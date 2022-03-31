package xueluoanping.fluiddrawerslegacy.handler;

import com.jaquadro.minecraft.storagedrawers.block.BlockController;
import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityController;
import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntitySlave;
import com.jaquadro.minecraft.storagedrawers.core.ModBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BucketItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fml.common.Mod;
import xueluoanping.fluiddrawerslegacy.FluidDrawersLegacyMod;
import xueluoanping.fluiddrawerslegacy.capability.CapabilityProvider_FluidControllerProxy;
import xueluoanping.fluiddrawerslegacy.capability.CapabilityProvider_FluidDrawerController;


import javax.annotation.Nullable;
import java.util.concurrent.atomic.AtomicBoolean;


public class ControllerFluidCapabilityHandler {
    public static final ControllerFluidCapabilityHandler instance = new ControllerFluidCapabilityHandler();
    private static final ResourceLocation CAP_FLUID_CTRL = new ResourceLocation(FluidDrawersLegacyMod.MOD_ID, "fluid_ctrl");
    private static final ResourceLocation CAP_FLUID_PROXY = new ResourceLocation(FluidDrawersLegacyMod.MOD_ID, "fluid_proxy");

    //    the Event need to detect in seconds after
    //If want to subscribe in class ,need static
//    or not
    @SubscribeEvent
    public void onTileCapabilities(AttachCapabilitiesEvent<TileEntity> event) {
        TileEntity tile = event.getObject();
        if (tile instanceof TileEntityController
        ) {
            event.addCapability(CAP_FLUID_CTRL, new CapabilityProvider_FluidDrawerController((TileEntityController) tile));
        } else if (tile instanceof TileEntitySlave) {
            event.addCapability(CAP_FLUID_PROXY, new CapabilityProvider_FluidControllerProxy((TileEntitySlave) tile));
        }

    }

    @SubscribeEvent
    public void onInteractWithBlock(PlayerInteractEvent.RightClickBlock event) {
        World world = event.getWorld();
        BlockPos pos = event.getPos();
        Direction face = event.getFace();
        BlockState state = world.getBlockState(pos);
        if (state.getBlock() != ModBlocks.CONTROLLER || face != state.getValue(BlockController.FACING)) {
            return;
        }
//        FluidDrawersLegacyMod.logger("Try Interact in " + world);
        if (handleTankInteraction(world.getBlockEntity(pos), face, event.getPlayer(), event.getHand()).get()) {
            event.setCanceled(true);
            event.setCancellationResult(ActionResultType.SUCCESS);
        }
    }

    public static AtomicBoolean handleTankInteraction(@Nullable TileEntity tile, @Nullable Direction face,
                                                      PlayerEntity player, Hand hand) {
        AtomicBoolean result = new AtomicBoolean(false);
        if (tile.getLevel().isClientSide()) return new AtomicBoolean(false);
        if (!(tile instanceof TileEntityController) || !tile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, face).isPresent()) {
            return result;
        }
        ItemStack heldStack = player.getItemInHand(hand);
        if (heldStack.getItem() instanceof BucketItem) {
            BucketItem bucketItem = (BucketItem) heldStack.getItem();
            if (bucketItem.getFluid() == Fluids.EMPTY)
                return result;
            tile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, face).ifPresent(
                    (handler) -> {
                       if  (FluidAttributes.BUCKET_VOLUME==
                                handler.fill(new FluidStack(bucketItem.getFluid(), FluidAttributes.BUCKET_VOLUME)
                                        , IFluidHandler.FluidAction.EXECUTE))
                           if(!player.isCreative())
                           player.setItemInHand(hand, heldStack.getContainerItem());
                       result.set(true);
                    }
            );
            return result;
        }
        tile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, face).ifPresent(
                (handler) -> {
//                    FluidDrawersLegacyMod.LOGGER.info(""+heldStack);
                    CapabilityProvider_FluidDrawerController.betterFluidHandler betterFluidHandler =
                            (CapabilityProvider_FluidDrawerController.betterFluidHandler) handler;
                    heldStack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY)
                            .ifPresent((itemFluidHandler) -> {
//                                FluidDrawersLegacyMod.logger(""+itemFluidHandler.getFluidInTank(0).writeToNBT(new CompoundNBT()));
//                                if(itemFluidHandler.drain(1, IFluidHandler.FluidAction.SIMULATE).getAmount()>0)
                                    if(betterFluidHandler.fill(itemFluidHandler.getFluidInTank(0), IFluidHandler.FluidAction.EXECUTE)>0)
                                        result.set(true);
//                                FluidDrawersLegacyMod.logger(""+itemFluidHandler.getFluidInTank(0).writeToNBT(new CompoundNBT())+betterFluidHandler.fill(itemFluidHandler.getFluidInTank(0), IFluidHandler.FluidAction.EXECUTE));
                            });

                });



        return result;
    }




}
