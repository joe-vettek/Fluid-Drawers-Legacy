package xueluoanping.fluiddrawerslegacy.jade;

import com.jaquadro.minecraft.storagedrawers.api.storage.attribute.LockAttribute;
import mcp.mobius.waila.api.BlockAccessor;
import mcp.mobius.waila.api.IComponentProvider;
import mcp.mobius.waila.api.ITooltip;
import mcp.mobius.waila.api.config.IPluginConfig;
import mcp.mobius.waila.api.ui.IElementHelper;
import mcp.mobius.waila.api.ui.IProgressStyle;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import snownee.jade.VanillaPlugin;
import xueluoanping.fluiddrawerslegacy.block.BlockFluidDrawer;
import xueluoanping.fluiddrawerslegacy.block.blockentity.BlockEntityFluidDrawer;


public class DrawerCompenProvider implements IComponentProvider {
    public static DrawerCompenProvider INSTANCE = new DrawerCompenProvider();

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
        tooltip.remove(VanillaPlugin.FORGE_FLUID);
        if (accessor.getBlock() instanceof BlockFluidDrawer) {
            BlockEntity tileEntity = accessor.getLevel().getBlockEntity(((BlockAccessor) accessor).getPosition());
            if (tileEntity instanceof BlockEntityFluidDrawer tile &&
                    config.get(VanillaPlugin.FORGE_FLUID)) {
                tile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null)
                        .ifPresent(handler -> {
                            int capacity = tile.getTankEffectiveCapacity();

                            boolean isLocked = tile.getDrawerAttributes().isItemLocked(LockAttribute.LOCK_EMPTY);
                            BlockEntityFluidDrawer.betterFluidHandler betterFluidHandler = (BlockEntityFluidDrawer.betterFluidHandler) handler;
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
            TranslatableComponent text;
            if (fluidStack.isEmpty()) {
                text = new TranslatableComponent("jade.fluid.empty");
                if (isLocked) {
                    String amountText = VanillaPlugin.getDisplayHelper().humanReadableNumber((double) fluidStack.getAmount(), "B", true);
                    text = new TranslatableComponent(I18n.get(new FluidStack(cacheFluid, 1).getTranslationKey()) + " 0B §e(" + I18n.get("tooltip.storagedrawers.waila.locked") + ") ");
                }
            } else {
                String amountText = VanillaPlugin.getDisplayHelper().humanReadableNumber((double) fluidStack.getAmount(), "B", true);
                text = isLocked ?
                        (TranslatableComponent) new TranslatableComponent("jade.fluid", fluidStack.getDisplayName(), amountText).append(" §e(" + I18n.get("tooltip.storagedrawers.waila.locked") + ") ") :
                        new TranslatableComponent("jade.fluid", fluidStack.getDisplayName(), amountText);
            }

            IProgressStyle progressStyle = helper.progressStyle().overlay(helper.fluid(fluidStack));
            tooltip.add(helper.progress((float) fluidStack.getAmount() / (float) capacity, text, progressStyle, helper.borderStyle()).tag(VanillaPlugin.FORGE_FLUID));
        }
    }

}
