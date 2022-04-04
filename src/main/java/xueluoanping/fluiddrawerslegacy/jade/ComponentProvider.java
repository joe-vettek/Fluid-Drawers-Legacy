package xueluoanping.fluiddrawerslegacy.jade;


import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityController;
import mcp.mobius.waila.api.*;
import mcp.mobius.waila.api.config.IPluginConfig;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import snownee.jade.VanillaPlugin;
import snownee.jade.addon.forge.ForgeCapabilityProvider;
import xueluoanping.fluiddrawerslegacy.ModTranslateKey;
import xueluoanping.fluiddrawerslegacy.config.ClientConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class ComponentProvider implements IComponentProvider, IServerDataProvider<BlockEntity> {
    static final ComponentProvider INSTANCE = new ComponentProvider();


    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
        tooltip.remove(VanillaPlugin.FORGE_FLUID);
        if (!(accessor.getBlockEntity() instanceof TileEntityController))
            return;

        if (accessor.getServerData().contains("jadeTanks")) {
//     10 或许是常量？ 不能用9，会读不出来
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
                                integerList.set(0, integerList.get(0) + fluidStack.getAmount());
                                integerList.set(1, integerList.get(1) + capacity);
                                fluidMap.replace(fluidStack.getFluid(), fluidMap.get(fluidStack.getFluid()), integerList);
                            } else {
                                integerList.add(fluidStack.getAmount());
                                integerList.add(capacity);
                                fluidMap.put(fluidStack.getFluid(), integerList);
                            }
                        }
                    }
            );
            AtomicInteger i = new AtomicInteger();
            fluidMap.forEach((fluid, integerList) -> {
                i.getAndIncrement();
                if (!accessor.getPlayer().isShiftKeyDown()
                        && i.get() < ClientConfig.showlimit.get())
                    ForgeCapabilityProvider.appendTank(tooltip, new FluidStack(fluid, integerList.get(0)), integerList.get(1));
                else if (accessor.getPlayer().isShiftKeyDown())
                    ForgeCapabilityProvider.appendTank(tooltip, new FluidStack(fluid, integerList.get(0)), integerList.get(1));

            });
            if (i.get() >= ClientConfig.showlimit.get()&&!accessor.getPlayer().isShiftKeyDown())
                tooltip.add(new TextComponent(ModTranslateKey.getWailaHide()));
        }

    }

    @Override
    public void appendServerData(CompoundTag compoundTag, ServerPlayer serverPlayer, Level level, BlockEntity blockEntity, boolean b) {

    }
}
