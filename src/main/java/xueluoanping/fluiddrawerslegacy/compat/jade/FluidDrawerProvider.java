package xueluoanping.fluiddrawerslegacy.compat.jade;

import com.jaquadro.minecraft.storagedrawers.api.storage.attribute.LockAttribute;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import snownee.jade.addon.universal.FluidStorageProvider;
import snownee.jade.api.*;

import snownee.jade.api.config.IPluginConfig;
import snownee.jade.api.fluid.JadeFluidObject;
import snownee.jade.api.ui.BoxStyle;
import snownee.jade.api.ui.IElementHelper;
import net.minecraft.client.resources.language.I18n;

import net.minecraft.world.level.block.entity.BlockEntity;

// import snownee.jade.VanillaPlugin;
import snownee.jade.api.ui.ProgressStyle;
import snownee.jade.overlay.DisplayHelper;
import xueluoanping.fluiddrawerslegacy.FluidDrawersLegacyMod;
import xueluoanping.fluiddrawerslegacy.api.drawer.BetterFluidManager;
import xueluoanping.fluiddrawerslegacy.block.BlockFluidDrawer;
import xueluoanping.fluiddrawerslegacy.block.blockentity.BlockEntityFluidDrawer;

import java.util.Optional;


public class FluidDrawerProvider implements IBlockComponentProvider {
    public static FluidDrawerProvider INSTANCE = new FluidDrawerProvider();

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
        tooltip.remove(JadeIds.UNIVERSAL_FLUID_STORAGE);
        tooltip.remove(JadeIds.UNIVERSAL_FLUID_STORAGE_DETAILED);

        if (accessor.getBlock() instanceof BlockFluidDrawer) {
            BlockEntity tileEntity = accessor.getLevel().getBlockEntity(accessor.getPosition());
            if (tileEntity instanceof BlockEntityFluidDrawer tile) {
               Optional.ofNullable( accessor.getLevel().getCapability(Capabilities.FluidHandler.BLOCK,accessor.getPosition(), null))
                        .ifPresent(handler -> {
                            int capacity = tile.getCapacityTankEffective();
                            boolean isLocked = tile.getDrawerAttributes().isItemLocked(LockAttribute.LOCK_EMPTY);
                            if (handler instanceof BetterFluidManager) {
                                var h = (BetterFluidManager<BlockEntityFluidDrawer>) handler;
                                for (BlockEntityFluidDrawer.FluidDrawerData data : h.getFluidDrawerDataList()) {
                                    BlockEntityFluidDrawer.betterFluidHandler betterFluidHandler = data.getTank();
                                    FluidStack fluidStack = betterFluidHandler.getFluid().copy();
                                    FluidStack cache = betterFluidHandler.getCacheFluid();
                                    appendTank(tooltip, accessor, fluidStack, capacity, cache, isLocked);
                                }
                            }


                        });
            }
        }
    }

    public static void appendTank(ITooltip tooltip, BlockAccessor accessor, FluidStack fluidStack, int capacity, FluidStack cacheFluid, boolean isLocked) {
        if (capacity > 0) {
            IElementHelper helper =IElementHelper.get();
            MutableComponent text;
            if (fluidStack.isEmpty()) {
                text = Component.translatable("jade.fluid.empty");
                String capacityText = DisplayHelper.INSTANCE.humanReadableNumber((double) capacity, "B", true);
                text.append("§7 " + capacityText);
                if (isLocked) {
                    // String amountText = DisplayHelper.INSTANCE.humanReadableNumber((double) fluidStack.getAmount(), "B", true);
                    text = Component.translatable(I18n.get(new FluidStack(cacheFluid.getFluidHolder(), 1).getHoverName().getString()) + " 0B §e(" + I18n.get("tooltip.storagedrawers.waila.locked") + ") ");
                }
            } else {
                String amountText = DisplayHelper.INSTANCE.humanReadableNumber((double) fluidStack.getAmount(), "B", true);
                text = isLocked ?
                        Component.translatable("jade.fluid", fluidStack.getHoverName(), amountText).append(" §e(" + I18n.get("tooltip.storagedrawers.waila.locked") + ") ") :
                        Component.translatable("jade.fluid", fluidStack.getHoverName(), amountText);
                if (accessor.getPlayer().isShiftKeyDown()) {
                    String capacityText = DisplayHelper.INSTANCE.humanReadableNumber((double) capacity, "B", true);
                    text.append("§7 / " + capacityText);
                }
            }

            ProgressStyle progressStyle = helper.progressStyle().overlay(helper.fluid(JadeFluidObject.of(fluidStack.getFluid(), fluidStack.getAmount(), fluidStack.getComponentsPatch())));
            // tooltip.add(helper.progress((float) fluidStack.getAmount() / (float) capacity, text, progressStyle, helper.borderStyle()).tag(VanillaPlugin.FORGE_FLUID));
            tooltip.add(helper.progress((float) fluidStack.getAmount() / (float) capacity, (Component) text, progressStyle, BoxStyle.getNestedBox(), true));
        }
    }

    @Override
    public ResourceLocation getUid() {
        return FluidDrawersLegacyMod.rl("fluiddrawer");
    }

    @Override
    public int getDefaultPriority() {
        return FluidStorageProvider.ForBlock.getBlock().getDefaultPriority() + 1000;
    }
}
