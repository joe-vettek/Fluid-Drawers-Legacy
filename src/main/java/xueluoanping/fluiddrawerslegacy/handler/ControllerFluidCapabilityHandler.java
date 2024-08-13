package xueluoanping.fluiddrawerslegacy.handler;

import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawer;
import com.jaquadro.minecraft.storagedrawers.block.BlockController;
import com.jaquadro.minecraft.storagedrawers.block.tile.BlockEntityController;
import com.jaquadro.minecraft.storagedrawers.block.tile.BlockEntityDrawers;
import com.jaquadro.minecraft.storagedrawers.capabilities.CapabilityDrawerGroup;
import com.jaquadro.minecraft.storagedrawers.core.ModBlockEntities;
import com.jaquadro.minecraft.storagedrawers.core.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.registries.DeferredHolder;
import xueluoanping.fluiddrawerslegacy.FluidDrawersLegacyMod;
import xueluoanping.fluiddrawerslegacy.ModContents;
import xueluoanping.fluiddrawerslegacy.block.blockentity.BlockEntityFluidDrawer;
import xueluoanping.fluiddrawerslegacy.capability.CapabilityProvider_FluidDrawerController;
import xueluoanping.fluiddrawerslegacy.api.exchange.FluidExchangeHandlerManager;

import java.util.ArrayList;
import java.util.Optional;

import static xueluoanping.fluiddrawerslegacy.ModConstants.DRAWER_GROUP_CAPABILITY;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.GAME)
public class ControllerFluidCapabilityHandler {
    // public static final ControllerFluidCapabilityHandler instance = new ControllerFluidCapabilityHandler();
    // private static final ResourceLocation CAP_FLUID_CTRL = new ResourceLocation(FluidDrawersLegacyMod.MOD_ID, "fluid_ctrl");
    // private static final ResourceLocation CAP_FLUID_PROXY = new ResourceLocation(FluidDrawersLegacyMod.MOD_ID, "fluid_proxy");

    //    the Event need to detect in seconds after
    // If want to subscribe in class ,need static
    //    or not


    @SubscribeEvent
    public static void onInteractWithBlock(PlayerInteractEvent.RightClickBlock event) {
        Level world = event.getLevel();
        BlockPos pos = event.getPos();
        Direction face = event.getFace();
        BlockState state = world.getBlockState(pos);
        ItemStack stack = event.getItemStack();
        if (state.getBlock() != ModBlocks.CONTROLLER.get() || face != state.getValue(BlockController.FACING)) {
            return;
        }

        if (world.getBlockEntity(pos) instanceof BlockEntityController tile && event.getLevel().getCapability(Capabilities.FluidHandler.BLOCK, pos, null) != null) {
            ArrayList<FluidStack> fluidStacksList = FluidExchangeHandlerManager.getFluidInItemContainer(stack);
            if (fluidStacksList.isEmpty()) {
                return;
            }
            IFluidHandler fluidHandler = event.getLevel().getCapability(Capabilities.FluidHandler.BLOCK, pos, null);
            FluidStack fluidStack = FluidStack.EMPTY;
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
                    FluidExchangeHandlerManager.tryHandleClickInputByMod(tile, event.getEntity(), event.getHand())
                            || FluidUtilPatch.interactWithFluidHandlerAndEmpty(event.getEntity(), event.getHand(), fluidHandler, fluidStack))) {
                event.setCanceled(true);
                event.setCancellationResult(InteractionResult.SUCCESS);
                return;
            }

            Optional.ofNullable(tile.getCapability(CapabilityDrawerGroup.DRAWER_GROUP_CAPABILITY))
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
