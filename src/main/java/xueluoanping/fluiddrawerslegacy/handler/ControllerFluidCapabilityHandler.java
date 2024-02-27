package xueluoanping.fluiddrawerslegacy.handler;

import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawer;
import com.jaquadro.minecraft.storagedrawers.block.BlockController;
import com.jaquadro.minecraft.storagedrawers.block.tile.BlockEntityController;
import com.jaquadro.minecraft.storagedrawers.block.tile.BlockEntitySlave;
import com.jaquadro.minecraft.storagedrawers.core.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
// import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.FluidStack;
// import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import xueluoanping.fluiddrawerslegacy.FluidDrawersLegacyMod;
import xueluoanping.fluiddrawerslegacy.block.blockentity.BlockEntityFluidDrawer;
import xueluoanping.fluiddrawerslegacy.capability.CapabilityProvider_FluidControllerProxy;
import xueluoanping.fluiddrawerslegacy.capability.CapabilityProvider_FluidDrawerController;
import xueluoanping.fluiddrawerslegacy.compat.ModHandlerManager;

import java.util.ArrayList;

import static xueluoanping.fluiddrawerslegacy.ModConstants.DRAWER_GROUP_CAPABILITY;


public class ControllerFluidCapabilityHandler {
    public static final ControllerFluidCapabilityHandler instance = new ControllerFluidCapabilityHandler();
    private static final ResourceLocation CAP_FLUID_CTRL = new ResourceLocation(FluidDrawersLegacyMod.MOD_ID, "fluid_ctrl");
    private static final ResourceLocation CAP_FLUID_PROXY = new ResourceLocation(FluidDrawersLegacyMod.MOD_ID, "fluid_proxy");

    //    the Event need to detect in seconds after
    // If want to subscribe in class ,need static
    //    or not
    @SubscribeEvent
    public void onTileCapabilities(AttachCapabilitiesEvent<BlockEntity> event) {
        // FluidDrawersLegacyMod.logger(event.getObject().getLevel());
        BlockEntity tile = event.getObject();
        if (tile instanceof BlockEntityController) {
            event.addCapability(CAP_FLUID_CTRL, new CapabilityProvider_FluidDrawerController((BlockEntityController) tile));
            event.addListener(() -> {
                // listen the remove and if we need use a save
                // FluidDrawersLegacyMod.logger(tile.getBlockPos(),tile.getLevel());
                // if (tile.getLevel() instanceof ServerLevel)
                //     FluidDrawerControllerSave.get(tile.getLevel()).remove(tile.getBlockPos());
            });

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
        ItemStack stack = event.getItemStack();
        if (state.getBlock() != ModBlocks.CONTROLLER.get() || face != state.getValue(BlockController.FACING)) {
            return;
        }

        if (world.getBlockEntity(pos) instanceof BlockEntityController tile && tile.getCapability(ForgeCapabilities.FLUID_HANDLER).resolve().isPresent()) {
            ArrayList<FluidStack> fluidStacksList = ModHandlerManager.getFluidInItemContainer(stack);
            if (fluidStacksList.size() == 0) {
                return;
            }
            IFluidHandler fluidHandler=tile.getCapability(ForgeCapabilities.FLUID_HANDLER).resolve().get();
            FluidStack fluidStack =FluidStack.EMPTY;
            // 必须还要确保存在
            boolean isExist = false;
            for (FluidStack stack1 : fluidStacksList) {
                if (!fluidHandler.drain(stack1, IFluidHandler.FluidAction.SIMULATE).isEmpty()) {
                    isExist = true;
                    fluidStack = stack1;
                    break;
                }
            }

            if (isExist && (
                    ModHandlerManager.tryHandleClickInputByMod(tile,event.getEntity(),event.getHand())
                    ||FluidUtilPatch.interactWithFluidHandlerAndEmpty(event.getEntity(), event.getHand(), fluidHandler, fluidStack))) {
                event.setCanceled(true);
                event.setCancellationResult(InteractionResult.SUCCESS);
                return;
            }

            tile.getCapability(DRAWER_GROUP_CAPABILITY, null)
                    .ifPresent((handler -> {
                        if (handler.isGroupValid() && handler.getDrawerCount() > 0) {
                            for (int i = 0; i < handler.getDrawerCount(); i++) {
                                IDrawer drawer = handler.getDrawer(i);
                                if (!(drawer instanceof BlockEntityFluidDrawer.FluidDrawerData fluiddrawer)) {
                                    if (drawer.canItemBeStored(stack)
                                            && drawer.getStoredItemPrototype().is(stack.getItem()))
                                        break;
                                }
                            }

                        }
                    }));
        }


    }



    // public static AtomicBoolean handleTankInteraction(@Nullable BlockEntity tile, @Nullable Direction face,
    //                                                   Player player, InteractionHand hand) {
    //     AtomicBoolean result = new AtomicBoolean(false);
    //     if (tile.getLevel().isClientSide())
    //         return new AtomicBoolean(false);
    //     if (!(tile instanceof BlockEntityController) || !tile.getCapability(ForgeCapabilities.FLUID_HANDLER, face).isPresent()) {
    //         return result;
    //     }
    //     ItemStack heldStack = player.getItemInHand(hand);
    //     if (heldStack.getItem() instanceof BucketItem bucketItem) {
    //         if (bucketItem.getFluid() == Fluids.EMPTY)
    //             return result;
    //         tile.getCapability(ForgeCapabilities.FLUID_HANDLER, face).ifPresent(
    //                 (handler) -> {
    //                     if (FluidType.BUCKET_VOLUME ==
    //                             handler.fill(new FluidStack(bucketItem.getFluid(), FluidType.BUCKET_VOLUME)
    //                                     , IFluidHandler.FluidAction.EXECUTE))
    //                         if (!player.isCreative())
    //                             player.setItemInHand(hand, heldStack.getCraftingRemainingItem());
    //                     result.set(true);
    //                 }
    //         );
    //         return result;
    //     }
    //     tile.getCapability(ForgeCapabilities.FLUID_HANDLER, face).ifPresent(
    //             (handler) -> {
    //                 //                    FluidDrawersLegacyMod.LOGGER.info(""+heldStack);
    //                 betterFluidHandlerManager betterFluidHandler =
    //                         (betterFluidHandlerManager) handler;
    //                 heldStack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM)
    //                         .ifPresent((itemFluidHandler) -> {
    //                             //                                FluidDrawersLegacyMod.logger(""+itemFluidHandler.getFluidInTank(0).writeToNBT(new CompoundNBT()));
    //                             //                                if(itemFluidHandler.drain(1, IFluidHandler.FluidAction.SIMULATE).getAmount()>0)
    //                             if (betterFluidHandler.fill(itemFluidHandler.getFluidInTank(0), IFluidHandler.FluidAction.EXECUTE) > 0)
    //                                 result.set(true);
    //                             //                                FluidDrawersLegacyMod.logger(""+itemFluidHandler.getFluidInTank(0).writeToNBT(new CompoundNBT())+betterFluidHandler.fill(itemFluidHandler.getFluidInTank(0), IFluidHandler.FluidAction.EXECUTE));
    //                         });
    //
    //             });
    //
    //
    //     return result;
    // }


}
