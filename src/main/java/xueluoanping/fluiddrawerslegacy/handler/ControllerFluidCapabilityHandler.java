package xueluoanping.fluiddrawerslegacy.handler;

import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawer;
import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawerGroup;
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
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import xueluoanping.fluiddrawerslegacy.FluidDrawersLegacyMod;
import xueluoanping.fluiddrawerslegacy.block.blockentity.BlockEntityFluidDrawer;
import xueluoanping.fluiddrawerslegacy.capability.CapabilityProvider_FluidControllerProxy;
import xueluoanping.fluiddrawerslegacy.capability.CapabilityProvider_FluidDrawerController;
import xueluoanping.fluiddrawerslegacy.api.exchange.FluidExchangeHandlerManager;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.Map;

import static xueluoanping.fluiddrawerslegacy.ModConstants.DRAWER_GROUP_CAPABILITY;

@SuppressWarnings("removal")
public class ControllerFluidCapabilityHandler {
    public static final ControllerFluidCapabilityHandler instance = new ControllerFluidCapabilityHandler();
    private static final ResourceLocation CAP_FLUID_CTRL = new ResourceLocation(FluidDrawersLegacyMod.MOD_ID, "fluid_ctrl");
    private static final ResourceLocation CAP_FLUID_PROXY = new ResourceLocation(FluidDrawersLegacyMod.MOD_ID, "fluid_proxy");

    // public static final Map<Level, Map<BlockEntity, ICapabilityProvider>> LEVEL_MAP_IDENTITY_HASH_MAP = new IdentityHashMap<>();

    //    the Event need to detect in seconds after
    // If want to subscribe in class ,need static
    //    or not
    @SubscribeEvent
    public void onTileCapabilities(AttachCapabilitiesEvent<BlockEntity> event) {
        // FluidDrawersLegacyMod.logger(event.getObject().getLevel());
        BlockEntity tile = event.getObject();
        if (tile instanceof BlockEntityController) {
            CapabilityProvider_FluidDrawerController capabilityProviderFluidDrawerController = new CapabilityProvider_FluidDrawerController((BlockEntityController) tile);
            event.addCapability(CAP_FLUID_CTRL, capabilityProviderFluidDrawerController);
            // Map<BlockEntity, ICapabilityProvider> map = LEVEL_MAP_IDENTITY_HASH_MAP.computeIfAbsent(tile.getLevel(), (e) -> new IdentityHashMap<>());
            // map.put(tile, capabilityProviderFluidDrawerController);
        } else if (tile instanceof BlockEntitySlave) {
            CapabilityProvider_FluidControllerProxy capabilityProviderFluidControllerProxy = new CapabilityProvider_FluidControllerProxy((BlockEntitySlave) tile);
            event.addCapability(CAP_FLUID_PROXY, capabilityProviderFluidControllerProxy);
            // Map<BlockEntity, ICapabilityProvider> map = LEVEL_MAP_IDENTITY_HASH_MAP.computeIfAbsent(tile.getLevel(), (e) -> new IdentityHashMap<>());
            // map.put(tile, capabilityProviderFluidControllerProxy);
        }
    }

    // @SubscribeEvent
    // public void onTileCapabilities(TickEvent.LevelTickEvent event) {
    //     Map<BlockEntity, ICapabilityProvider> map = LEVEL_MAP_IDENTITY_HASH_MAP.get(event.level);
    //     if (map != null) {
    //         map.entrySet().removeIf(next -> next.getKey().isRemoved());
    //     }
    // }
    //
    // @SubscribeEvent
    // public void onLevelUnload(LevelEvent.Unload event) {
    //     if (event.getLevel() instanceof Level level)
    //         LEVEL_MAP_IDENTITY_HASH_MAP.remove(level);
    // }

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
            ArrayList<FluidStack> fluidStacksList = FluidExchangeHandlerManager.getFluidInItemContainer(stack);
            if (fluidStacksList.size() == 0) {
                return;
            }
            IFluidHandler fluidHandler = tile.getCapability(ForgeCapabilities.FLUID_HANDLER).resolve().get();
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

            IDrawerGroup handler = DRAWER_GROUP_CAPABILITY.getCapability(tile);
            if (handler != null) {
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
            }
        }


    }


}
