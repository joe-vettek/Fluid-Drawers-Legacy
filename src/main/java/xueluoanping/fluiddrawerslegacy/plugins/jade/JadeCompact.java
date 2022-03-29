package xueluoanping.fluiddrawerslegacy.plugins.jade;

import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawerGroup;
import com.jaquadro.minecraft.storagedrawers.api.storage.attribute.LockAttribute;
import com.jaquadro.minecraft.storagedrawers.block.BlockSlave;
import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityController;
import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntitySlave;
import mcp.mobius.waila.api.IRegistrar;
import mcp.mobius.waila.api.IWailaPlugin;
import mcp.mobius.waila.api.TooltipPosition;
import mcp.mobius.waila.api.WailaPlugin;
import mcp.mobius.waila.api.event.WailaTooltipEvent;
import net.minecraft.client.resources.I18n;
import net.minecraft.fluid.Fluids;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import xueluoanping.fluiddrawerslegacy.FluidDrawersLegacyMod;
import xueluoanping.fluiddrawerslegacy.block.BlockFluidDrawer;
import xueluoanping.fluiddrawerslegacy.block.tileentity.TileEntityFluidDrawer;

@WailaPlugin
public class JadeCompact implements IWailaPlugin {
    static final ResourceLocation RENDER_Fluid = new ResourceLocation(FluidDrawersLegacyMod.MOD_ID, "tank");
    @CapabilityInject(IDrawerGroup.class)
    static Capability<IDrawerGroup> DRAWER_GROUP_CAPABILITY = null;

    public JadeCompact() {
        MinecraftForge.EVENT_BUS.addListener(this::overrideGrass);
    }

    public void overrideGrass(WailaTooltipEvent event) {

        if (event.getAccessor().getBlock() instanceof BlockFluidDrawer) {
            TileEntity tileEntity = event.getAccessor().getWorld().getBlockEntity(event.getAccessor().getPosition());
//            event.getCurrentTip().set(0, new StringTextComponent("§1R§2e§3d§4s§5t§6o§7n§8e §9C§ar§be§ca§dt§ei§fo§1n§2s")
//                    .append(ItemFluidDrawer.getNameStatic
//                            (BlockFluidDrawer.getHarvestItem
//                                    (ModContents.fluiddrawer.defaultBlockState(), event.getAccessor().getWorld(), event.getAccessor().getPosition()))));
            if (!event.getAccessor().getPlayer().isShiftKeyDown())
                return;
            if (tileEntity instanceof TileEntityFluidDrawer) {
                TileEntityFluidDrawer tile = (TileEntityFluidDrawer) tileEntity;
                tile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null)
                        .ifPresent(handler -> {
                            FluidStack fluidStack = handler.getFluidInTank(0);

                            String string = "Contains 3200mb of water";
//                            String userInfo = String.format(string, name, amount);
                            if (fluidStack.getAmount() > 0) {
                                if (tile.upgrades().hasVendingUpgrade()) fluidStack.setAmount(Integer.MAX_VALUE);
                                ITextComponent tail = event.getCurrentTip().get(event.getCurrentTip().size() - 1);
                                event.getCurrentTip().set(event.getCurrentTip().size() - 1, new TranslationTextComponent("statement.fluiddrawerslegacy.fluiddrawer1")
                                        .append(String.valueOf(fluidStack.getAmount()))
                                        .append("/" + tile.getEffectiveCapacity() + "mB")
                                        .append(new TranslationTextComponent("statement.fluiddrawerslegacy.fluiddrawer2"))
                                        .append(new TranslationTextComponent(fluidStack.getTranslationKey())
                                        ));
                                if (tile.getDrawerAttributes().isItemLocked(LockAttribute.LOCK_EMPTY))
                                    event.getCurrentTip().add(new TranslationTextComponent(I18n.get("tooltip.storagedrawers.waila.locked")));

                                event.getCurrentTip().add(tail);
                            } else {

                                if(!tile.getDrawerAttributes().isItemLocked(LockAttribute.LOCK_EMPTY))return;

                                TileEntityFluidDrawer.betterFluidHandler betterFluidHandler = (TileEntityFluidDrawer.betterFluidHandler) handler;
                                if (betterFluidHandler.getCacheFluid() != Fluids.EMPTY) {
                                    ITextComponent tail = event.getCurrentTip().get(event.getCurrentTip().size() - 1);
                                    event.getCurrentTip().set(event.getCurrentTip().size() - 1,
                                            new TranslationTextComponent(new FluidStack(betterFluidHandler.getCacheFluid(), 1).getTranslationKey())
                                    );
                                    event.getCurrentTip().add(new TranslationTextComponent(I18n.get("tooltip.storagedrawers.waila.locked")));
                                    event.getCurrentTip().add(tail);
                                }
                            }
                        });
            }
        }

    }

    @Override
    public void register(IRegistrar registrar) {
        registrar.addConfig(new ResourceLocation(FluidDrawersLegacyMod.MOD_ID, "display.content"), true);
        registrar.registerComponentProvider(ComponentProvider.INSTANCE, TooltipPosition.BODY, TileEntityController.class);
        registrar.registerBlockDataProvider(ComponentProvider.INSTANCE, TileEntityController.class);

        registrar.registerComponentProvider(SlaveCompoentProvider.INSTANCE, TooltipPosition.BODY, TileEntitySlave.class);
        registrar.registerBlockDataProvider(SlaveCompoentProvider.INSTANCE, TileEntitySlave.class);
    }


}
