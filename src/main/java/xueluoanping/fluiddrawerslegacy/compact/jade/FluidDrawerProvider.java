package xueluoanping.fluiddrawerslegacy.compact.jade;

import com.jaquadro.minecraft.storagedrawers.api.storage.attribute.LockAttribute;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import snownee.jade.addon.universal.FluidStorageProvider;
import snownee.jade.api.*;

import snownee.jade.api.config.IPluginConfig;
import snownee.jade.api.fluid.JadeFluidObject;
import snownee.jade.api.ui.BoxStyle;
import snownee.jade.api.ui.IElementHelper;
import snownee.jade.api.ui.IProgressStyle;
import net.minecraft.client.resources.language.I18n;

import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;

// import snownee.jade.VanillaPlugin;
import snownee.jade.overlay.DisplayHelper;
import xueluoanping.fluiddrawerslegacy.FluidDrawersLegacyMod;
import xueluoanping.fluiddrawerslegacy.block.BlockFluidDrawer;
import xueluoanping.fluiddrawerslegacy.block.tileentity.TileEntityFluidDrawer;


public class FluidDrawerProvider implements IBlockComponentProvider {
    public static FluidDrawerProvider INSTANCE = new FluidDrawerProvider();

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
        tooltip.remove(Identifiers.UNIVERSAL_FLUID_STORAGE);
        tooltip.remove(Identifiers.UNIVERSAL_FLUID_STORAGE_DETAILED);

        if (accessor.getBlock() instanceof BlockFluidDrawer) {
            BlockEntity tileEntity = accessor.getLevel().getBlockEntity(((BlockAccessor) accessor).getPosition());
            if (tileEntity instanceof TileEntityFluidDrawer tile ) {
                tile.getCapability(ForgeCapabilities.FLUID_HANDLER, null)
                        .ifPresent(handler -> {
                            int capacity = tile.getTankEffectiveCapacity();

                            boolean isLocked = tile.getDrawerAttributes().isItemLocked(LockAttribute.LOCK_EMPTY);
                            TileEntityFluidDrawer.betterFluidHandler betterFluidHandler = (TileEntityFluidDrawer.betterFluidHandler) handler;
                            FluidStack fluidStack = betterFluidHandler.getFluid().copy();
                            Fluid cache = betterFluidHandler.getCacheFluid();

                            appendTank(tooltip, fluidStack, capacity, cache, isLocked);
                        });
            }
        }
    }

    public static void appendTank(ITooltip tooltip, FluidStack fluidStack, int capacity, Fluid cacheFluid, boolean isLocked) {
        if (capacity > 0) {
            IElementHelper helper = tooltip.getElementHelper();
            Component text;
            if (fluidStack.isEmpty()) {
                text =  Component.translatable("jade.fluid.empty");
                if (isLocked) {
                    // String amountText = DisplayHelper.INSTANCE.humanReadableNumber((double) fluidStack.getAmount(), "B", true);
                    text =  Component.translatable(I18n.get(new FluidStack(cacheFluid, 1).getTranslationKey()) + " 0B §e(" + I18n.get("tooltip.storagedrawers.waila.locked") + ") ");
                }
            } else {
                String amountText = DisplayHelper.INSTANCE.humanReadableNumber((double) fluidStack.getAmount(), "B", true);
                text = isLocked ?
                        Component.translatable("jade.fluid", fluidStack.getDisplayName(), amountText).append(" §e(" + I18n.get("tooltip.storagedrawers.waila.locked") + ") ") :
                        Component.translatable("jade.fluid", fluidStack.getDisplayName(), amountText);
            }

            IProgressStyle progressStyle = helper.progressStyle().overlay(helper.fluid(JadeFluidObject.of(fluidStack.getFluid(),fluidStack.getAmount())));
            // tooltip.add(helper.progress((float) fluidStack.getAmount() / (float) capacity, text, progressStyle, helper.borderStyle()).tag(VanillaPlugin.FORGE_FLUID));
            tooltip.add(helper.progress((float) fluidStack.getAmount() / (float) capacity, (Component)text, progressStyle, BoxStyle.DEFAULT, true));
        }
    }
    @Override
    public ResourceLocation getUid() {
        return FluidDrawersLegacyMod.rl("fluiddrawer");
    }

    @Override
    public int getDefaultPriority() {
        return FluidStorageProvider.INSTANCE.getDefaultPriority()+1000;
    }
}
