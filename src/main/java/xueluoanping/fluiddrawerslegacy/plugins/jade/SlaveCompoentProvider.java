package xueluoanping.fluiddrawerslegacy.plugins.jade;

import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityController;
import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntitySlave;
import mcp.mobius.waila.api.*;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import xueluoanping.fluiddrawerslegacy.block.tileentity.TileEntityFluidDrawer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SlaveCompoentProvider implements IComponentProvider, IServerDataProvider<TileEntity> {
    static final SlaveCompoentProvider INSTANCE = new SlaveCompoentProvider();



    @Override
    public void appendBody(List<ITextComponent> tooltip, IDataAccessor accessor, IPluginConfig config) {
        if (!(accessor.getTileEntity() instanceof TileEntitySlave) || !accessor.getPlayer().isShiftKeyDown())
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
        if (tileEntity instanceof TileEntitySlave) {

            final ListNBT list = new ListNBT();
            TileEntityController tile = ((TileEntitySlave) tileEntity).getController();

            if(tile==null)
                return;
            tile.getCapability(TileEntityFluidDrawer.DRAWER_GROUP_CAPABILITY, null)
                    .ifPresent(handler -> {
//                        FluidDrawersLegacyMod.logger(handler.getDrawerCount()+"");
                        for (int i = 0; i < handler.getDrawerCount(); i++) {
                            if (handler.getDrawer(i) instanceof TileEntityFluidDrawer.StandardDrawerData) {
                                list.add(((TileEntityFluidDrawer.StandardDrawerData) handler.getDrawer(i)).getTank().serializeNBT());
                            }

                        }
                    });
//            FluidDrawersLegacyMod.logger("aaa"+list);
            compoundNBT.put("recordFD", list);
//            FluidDrawersLegacyMod.logger(list.toString());
        }
    }

}
