package xueluoanping.fluiddrawerslegacy.compact.jade;

import com.jaquadro.minecraft.storagedrawers.block.tile.BlockEntitySlave;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.addon.universal.FluidStorageProvider;
import snownee.jade.api.*;

import snownee.jade.api.config.IPluginConfig;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;

import snownee.jade.api.fluid.JadeFluidObject;
import snownee.jade.api.ui.BoxStyle;
import snownee.jade.api.ui.IElementHelper;
import snownee.jade.api.ui.IProgressStyle;
import snownee.jade.overlay.DisplayHelper;
import xueluoanping.fluiddrawerslegacy.FluidDrawersLegacyMod;
import xueluoanping.fluiddrawerslegacy.ModTranslateKey;
import xueluoanping.fluiddrawerslegacy.config.ClientConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
public class ControllerSlaveProviderFixer implements IBlockComponentProvider, IServerDataProvider<BlockAccessor>{
    static final ControllerSlaveProviderFixer INSTANCE = new ControllerSlaveProviderFixer();


    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig iPluginConfig) {
        tooltip.remove(Identifiers.UNIVERSAL_FLUID_STORAGE);
        if (!(accessor.getBlockEntity() instanceof BlockEntitySlave))
            return;
       // FluidDrawersLegacyMod.logger(accessor.getServerData().toString());
        if (accessor.getServerData().contains("JadeFluidStorage")) {
            ListTag list =
            ((CompoundTag)accessor.getServerData().getList("JadeFluidStorage", CompoundTag.TAG_COMPOUND).get(0))
                    .getList("Views", CompoundTag.TAG_COMPOUND);
            // FluidDrawersLegacyMod.logger(((CompoundTag)list.get(0)).getList("Views", CompoundTag.TAG_COMPOUND).toString());

            Map<FluidStack, List<Integer>> fluidMap = new HashMap<>();
            list.forEach(
                    (ele) -> {
                        ((CompoundTag) ele).putString("FluidName",((CompoundTag) ele).getString("fluid"));
                        ((CompoundTag) ele).putInt("Amount",((CompoundTag) ele).getInt("amount"));
                        FluidStack fluidStack = FluidStack.loadFluidStackFromNBT((CompoundTag) ele);
                        int capacity = ((CompoundTag) ele).getInt("capacity");
                        List<Integer> integerList = new ArrayList<>();
                        if (fluidStack.getAmount() > 0 && fluidStack != FluidStack.EMPTY) {
                            FluidStack fluidStackKey = fluidStack.copy();
                            // not 0, empty
                            fluidStackKey.setAmount(1);
                            if (fluidMap.containsKey(fluidStackKey)) {
                                integerList = fluidMap.get(fluidStackKey);
                                integerList.set(0,integerList.get(0)+fluidStack.getAmount());
                                integerList.set(1,integerList.get(1)+capacity);
                                fluidMap.replace(fluidStackKey, fluidMap.get(fluidStackKey), integerList);
                            } else {
                                integerList.add(fluidStack.getAmount());
                                integerList.add(capacity);
                                fluidMap.put(fluidStackKey,integerList);
                            }
                        }
                    }
            );
            AtomicInteger i = new AtomicInteger();
            fluidMap.forEach((fluid, integerList) -> {
                i.getAndIncrement();
                if (accessor.getPlayer().isShiftKeyDown()||
                        (!accessor.getPlayer().isShiftKeyDown()
                                && i.get() < ClientConfig.showlimit.get()))
                {
                    IElementHelper helper = tooltip.getElementHelper();
                    FluidStack fluidStack= new FluidStack(fluid, integerList.get(0));
                    IProgressStyle progressStyle = helper.progressStyle().overlay(helper.fluid(JadeFluidObject.of(fluid.getFluid(), integerList.get(0),fluid.getTag())));
                    String amountText = DisplayHelper.INSTANCE.humanReadableNumber((double) fluidStack.getAmount(), "B", true);

                    Component text=Component.translatable("jade.fluid", fluidStack.getDisplayName(), amountText);
                    tooltip.add(helper.progress((float) fluidStack.getAmount() / (float) integerList.get(1), (Component) text, progressStyle, BoxStyle.DEFAULT, true));

                    // FluidStorageProvider.append(tooltip, new FluidStack(fluid, integerList.get(0)), integerList.get(1));
                }
            });
            if (i.get() >= ClientConfig.showlimit.get() &&!accessor.getPlayer().isShiftKeyDown())
                tooltip.add(Component.translatable(ModTranslateKey.getWailaHide()));


        }
    }


    @Override
    public ResourceLocation getUid() {
        return FluidDrawersLegacyMod.rl("controller_slave");
    }

    @Override
    public int getDefaultPriority() {
        return FluidStorageProvider.INSTANCE.getDefaultPriority()+1000;
    }

    @Override
    public void appendServerData(CompoundTag compoundTag, BlockAccessor blockAccessor) {

    }
}
