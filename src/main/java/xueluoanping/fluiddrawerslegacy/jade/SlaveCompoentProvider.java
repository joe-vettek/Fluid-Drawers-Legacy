package xueluoanping.fluiddrawerslegacy.jade;

import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityController;
import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntitySlave;
import mcp.mobius.waila.api.BlockAccessor;
import mcp.mobius.waila.api.IComponentProvider;
import mcp.mobius.waila.api.IServerDataProvider;
import mcp.mobius.waila.api.ITooltip;
import mcp.mobius.waila.api.config.IPluginConfig;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import snownee.jade.VanillaPlugin;
import snownee.jade.addon.forge.ForgeCapabilityProvider;
import xueluoanping.fluiddrawerslegacy.FluidDrawersLegacyMod;
import xueluoanping.fluiddrawerslegacy.ModConstants;
import xueluoanping.fluiddrawerslegacy.block.tileentity.TileEntityFluidDrawer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SlaveCompoentProvider implements IComponentProvider, IServerDataProvider<BlockEntity> {
    static final SlaveCompoentProvider INSTANCE = new SlaveCompoentProvider();


    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig iPluginConfig) {
        tooltip.remove(VanillaPlugin.FORGE_FLUID);
        if (!(accessor.getBlockEntity() instanceof TileEntitySlave))
            return;
//        FluidDrawersLegacyMod.logger(accessor.getServerData().toString());
        if (accessor.getServerData().contains("jadeTanks")) {
            ListTag list = accessor.getServerData().getList("jadeTanks", CompoundTag.TAG_COMPOUND);
            Map<Fluid, List<Integer>> fluidMap = new HashMap<>();
            list.forEach(
                    (ele) -> {
                        FluidStack fluidStack = FluidStack.loadFluidStackFromNBT((CompoundTag) ele);
                        int capacity = ((CompoundTag) ele).getInt("capacity");
                        List<Integer> integerList = new ArrayList<>();
                        if (fluidStack.getAmount() > 0 && fluidStack != FluidStack.EMPTY) {
                            if (fluidMap.containsKey(fluidStack.getFluid())) {
                                integerList = fluidMap.get(fluidStack.getFluid());
                                integerList.set(0,integerList.get(0)+fluidStack.getAmount());
                                integerList.set(1,integerList.get(1)+capacity);
                                fluidMap.replace(fluidStack.getFluid(), fluidMap.get(fluidStack.getFluid()), integerList);
                            } else {
                                integerList.add(fluidStack.getAmount());
                                integerList.add(capacity);
                                fluidMap.put(fluidStack.getFluid(),integerList);
                            }
                        }
                    }
            );
            fluidMap.forEach((fluid, integerList) -> {
                ForgeCapabilityProvider.appendTank(tooltip,new FluidStack(fluid,integerList.get(0)),integerList.get(1));
            });

        }
    }

    @Override
    public void appendServerData(CompoundTag compoundTag, ServerPlayer serverPlayer, Level level, BlockEntity blockEntity, boolean b) {

    }
}
