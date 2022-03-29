package xueluoanping.fluiddrawerslegacy.plugins.jade;

import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawerGroup;
import com.jaquadro.minecraft.storagedrawers.block.BlockController;
import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityController;
import mcp.mobius.waila.addons.minecraft.HUDHandlerFurnace;
import mcp.mobius.waila.addons.minecraft.PluginMinecraft;
import mcp.mobius.waila.api.IComponentProvider;
import mcp.mobius.waila.api.IDataAccessor;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.api.IServerDataProvider;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import mcp.mobius.waila.api.RenderableTextComponent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.fluids.FluidStack;
import org.codehaus.plexus.util.CachedMap;
import xueluoanping.fluiddrawerslegacy.FluidDrawersLegacyMod;
import xueluoanping.fluiddrawerslegacy.block.tileentity.TileEntityFluidDrawer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ComponentProvider implements IComponentProvider, IServerDataProvider<TileEntity> {
    static final ComponentProvider INSTANCE = new ComponentProvider();
    @CapabilityInject(IDrawerGroup.class)
    static Capability<IDrawerGroup> DRAWER_GROUP_CAPABILITY = null;


    @Override
    public void appendBody(List<ITextComponent> tooltip, IDataAccessor accessor, IPluginConfig config) {
        if (!(accessor.getTileEntity() instanceof TileEntityController) || !accessor.getPlayer().isShiftKeyDown())
            return;

        if (accessor.getServerData().contains("recordFD")) {
//     10 或许是常量？
            ListNBT list = accessor.getServerData().getList("recordFD", 10);
            Map<Fluid, Integer> fluidMap = new HashMap<>();
            list.forEach(
                    (ele) -> {
                        FluidStack fluidStack = FluidStack.loadFluidStackFromNBT((CompoundNBT) ele);
                        if (fluidStack.getAmount() > 0 && fluidStack != FluidStack.EMPTY) {
                            if (fluidMap.containsKey(fluidStack.getFluid()))
                                fluidMap.replace(fluidStack.getFluid(), fluidMap.get(fluidStack.getFluid()), fluidMap.get(fluidStack.getFluid()) + fluidStack.getAmount());
                            else fluidMap.put(fluidStack.getFluid(), fluidStack.getAmount());
                        }
                    }
            );
            fluidMap.forEach((fluid, amount) -> {
                tooltip.add(new TranslationTextComponent("statement.fluiddrawerslegacy.fluiddrawer1")
                        .append(String.valueOf(amount))
                        .append( "mB")
                        .append(new TranslationTextComponent("statement.fluiddrawerslegacy.fluiddrawer2"))
                        .append(new TranslationTextComponent(new FluidStack(fluid, amount).getTranslationKey())
                        ));
            });

        }

        IComponentProvider.super.appendBody(tooltip, accessor, config);

    }

    @Override
    public void appendServerData(CompoundNBT compoundNBT, ServerPlayerEntity serverPlayerEntity, World world, TileEntity tileEntity) {
        if (tileEntity instanceof TileEntityController) {

            final ListNBT list = new ListNBT();
            TileEntityController tile = (TileEntityController) tileEntity;
            tile.getCapability(DRAWER_GROUP_CAPABILITY, null)
                    .ifPresent(handler -> {
//                        FluidDrawersLegacyMod.logger(handler.getDrawerCount()+"");
                        for (int i = 0; i < handler.getDrawerCount(); i++) {
                            if (handler.getDrawer(i) instanceof TileEntityFluidDrawer.StandardDrawerData) {
                                list.add(((TileEntityFluidDrawer.StandardDrawerData) handler.getDrawer(i)).getTank().serializeNBT());
                            }

                        }
                    });

            compoundNBT.put("recordFD", list);
//            FluidDrawersLegacyMod.logger(list.toString());
        }
    }

    private static RenderableTextComponent getRenderable(ItemStack stack) {
        CompoundNBT tag;
        if (!stack.isEmpty()) {
            tag = new CompoundNBT();
            tag.putString("id", stack.getItem().getRegistryName().toString());
            tag.putInt("count", stack.getCount());
            if (stack.hasTag()) {
                tag.putString("nbt", stack.getOrCreateTag().toString());
            }

            return new RenderableTextComponent(JadeCompact.RENDER_Fluid, tag);
        } else {
            tag = new CompoundNBT();
            tag.putInt("width", 18);
            return new RenderableTextComponent(JadeCompact.RENDER_Fluid, tag);
        }
    }
}
