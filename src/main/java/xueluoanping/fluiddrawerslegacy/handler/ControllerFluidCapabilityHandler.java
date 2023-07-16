package xueluoanping.fluiddrawerslegacy.handler;

import com.jaquadro.minecraft.storagedrawers.block.BlockController;
import com.jaquadro.minecraft.storagedrawers.block.tile.BlockEntityController;
import com.jaquadro.minecraft.storagedrawers.block.tile.BlockEntitySlave;
import com.jaquadro.minecraft.storagedrawers.core.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
// import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidType;
// import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
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
    public void onTileCapabilities(AttachCapabilitiesEvent<BlockEntity> event) {
        BlockEntity tile = event.getObject();
        if (tile instanceof BlockEntityController
        ) {
            event.addCapability(CAP_FLUID_CTRL, new CapabilityProvider_FluidDrawerController((BlockEntityController) tile));
        } else if (tile instanceof BlockEntitySlave) {
            event.addCapability(CAP_FLUID_PROXY, new CapabilityProvider_FluidControllerProxy((BlockEntitySlave) tile));
        }

    }

    @SubscribeEvent
    public void onInteractWithBlock(PlayerInteractEvent.RightClickBlock event) {
        Level world = event.getLevel();
        BlockPos pos = event.getPos();
        Direction face = event.getFace();
        BlockState state = world.getBlockState(pos);
        if (state.getBlock() != ModBlocks.CONTROLLER.get() || face != state.getValue(BlockController.FACING)) {
            return;
        }
//        FluidDrawersLegacyMod.logger("Try Interact in " + world);
        if (handleTankInteraction(world.getBlockEntity(pos), face, event.getEntity(), event.getHand()).get()) {
            event.setCanceled(true);
            event.setCancellationResult(InteractionResult.SUCCESS);
        }
    }

    public static AtomicBoolean handleTankInteraction(@Nullable BlockEntity tile, @Nullable Direction face,
                                                      Player player, InteractionHand hand) {
        AtomicBoolean result = new AtomicBoolean(false);
        if (tile.getLevel().isClientSide()) return new AtomicBoolean(false);
        if (!(tile instanceof BlockEntityController) || !tile.getCapability(ForgeCapabilities.FLUID_HANDLER, face).isPresent()) {
            return result;
        }
        ItemStack heldStack = player.getItemInHand(hand);
        if (heldStack.getItem() instanceof BucketItem) {
            BucketItem bucketItem = (BucketItem) heldStack.getItem();
            if (bucketItem.getFluid() == Fluids.EMPTY)
                return result;
            tile.getCapability(ForgeCapabilities.FLUID_HANDLER, face).ifPresent(
                    (handler) -> {
                       if  (FluidType.BUCKET_VOLUME==
                                handler.fill(new FluidStack(bucketItem.getFluid(), FluidType.BUCKET_VOLUME)
                                        , IFluidHandler.FluidAction.EXECUTE))
                           if(!player.isCreative())
                           player.setItemInHand(hand, heldStack.getCraftingRemainingItem());
                       result.set(true);
                    }
            );
            return result;
        }
        tile.getCapability(ForgeCapabilities.FLUID_HANDLER, face).ifPresent(
                (handler) -> {
//                    FluidDrawersLegacyMod.LOGGER.info(""+heldStack);
                    CapabilityProvider_FluidDrawerController.betterFluidHandler betterFluidHandler =
                            (CapabilityProvider_FluidDrawerController.betterFluidHandler) handler;
                    heldStack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM)
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
