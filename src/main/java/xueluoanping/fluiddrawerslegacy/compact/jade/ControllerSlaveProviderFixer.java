package xueluoanping.fluiddrawerslegacy.compact.jade;

import com.jaquadro.minecraft.storagedrawers.block.tile.BlockEntityController;
import com.jaquadro.minecraft.storagedrawers.block.tile.BlockEntitySlave;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.capability.IFluidHandler;
import snownee.jade.addon.universal.FluidStorageProvider;
import snownee.jade.api.*;

import snownee.jade.api.config.IPluginConfig;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;

import net.minecraftforge.fluids.FluidStack;

import snownee.jade.api.fluid.JadeFluidObject;
import snownee.jade.api.ui.BoxStyle;
import snownee.jade.api.ui.IElementHelper;
import snownee.jade.api.ui.IProgressStyle;
import snownee.jade.overlay.DisplayHelper;
import xueluoanping.fluiddrawerslegacy.FluidDrawersLegacyMod;
import xueluoanping.fluiddrawerslegacy.ModTranslateKey;
import xueluoanping.fluiddrawerslegacy.capability.CapabilityProvider_FluidDrawerController;
import xueluoanping.fluiddrawerslegacy.config.ClientConfig;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class ControllerSlaveProviderFixer implements IBlockComponentProvider, IServerDataProvider<BlockAccessor> {
    static final ControllerSlaveProviderFixer INSTANCE = new ControllerSlaveProviderFixer();


    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig iPluginConfig) {
        tooltip.remove(Identifiers.UNIVERSAL_FLUID_STORAGE);
        if (!(accessor.getBlockEntity() instanceof BlockEntitySlave))
            return;

        // FluidDrawersLegacyMod.logger(accessor.getServerData().toString());
        if (accessor.getServerData().contains("JadeFluidStorage")) {
            ListTag list =
                    ((CompoundTag) accessor.getServerData().getList("JadeFluidStorage", CompoundTag.TAG_COMPOUND).get(0))
                            .getList("Views", CompoundTag.TAG_COMPOUND);
            // FluidDrawersLegacyMod.logger(((CompoundTag)list.get(0)).getList("Views", CompoundTag.TAG_COMPOUND).toString());

            Map<FluidStack, List<Integer>> fluidMap = new LinkedHashMap<>();
            list.forEach(
                    (ele) -> {
                        // add this to add
                        ((CompoundTag) ele).putString("FluidName", ((CompoundTag) ele).getString("fluid"));
                        ((CompoundTag) ele).putInt("Amount", ((CompoundTag) ele).getInt("amount"));
                        if (((CompoundTag) ele).contains("tag"))
                            ((CompoundTag) ele).put("Tag", ((CompoundTag) ele).getCompound("tag"));

                        FluidStack fluidStack = FluidStack.loadFluidStackFromNBT((CompoundTag) ele);
                        int capacity = ((CompoundTag) ele).getInt("capacity");
                        List<Integer> integerList = new ArrayList<>();
                        // if (fluidStack.getAmount() > 0 && fluidStack != FluidStack.EMPTY)
                        {
                            FluidStack fluidStackKey = fluidStack.copy();
                            // not 0, empty
                            if (fluidStack.getAmount() > 0 && fluidStack != FluidStack.EMPTY)
                                fluidStackKey.setAmount(1);

                            // FluidDrawersLegacyMod.logger(fluidStack.writeToNBT(new CompoundTag()));
                            if (fluidMap.containsKey(fluidStackKey)) {
                                integerList = fluidMap.get(fluidStackKey);
                                integerList.set(0, integerList.get(0) + fluidStack.getAmount());
                                integerList.set(1, integerList.get(1) + capacity);
                                fluidMap.replace(fluidStackKey, fluidMap.get(fluidStackKey), integerList);
                            } else {
                                integerList.add(fluidStack.getAmount());
                                integerList.add(capacity);
                                fluidMap.put(fluidStackKey, integerList);
                            }
                        }
                    }
            );
            if(fluidMap.containsKey(FluidStack.EMPTY)){
                List<Integer> integerList = fluidMap.remove(FluidStack.EMPTY);
                fluidMap.put(FluidStack.EMPTY,integerList);

            }
            AtomicInteger i = new AtomicInteger();
            fluidMap.forEach((fluid, integerList) -> {
                i.getAndIncrement();
                if (accessor.getPlayer().isShiftKeyDown() ||
                        (!accessor.getPlayer().isShiftKeyDown()
                                && i.get() < ClientConfig.showlimit.get())) {
                    IElementHelper helper = tooltip.getElementHelper();
                    FluidStack fluidStack = new FluidStack(fluid, integerList.get(0));
                    IProgressStyle progressStyle = helper.progressStyle().overlay(helper.fluid(JadeFluidObject.of(fluid.getFluid(), integerList.get(0), fluid.getTag())));
                    String amountText = DisplayHelper.INSTANCE.humanReadableNumber((double) fluidStack.getAmount(), "B", true);
                    String capacityText = DisplayHelper.INSTANCE.humanReadableNumber((double) integerList.get(1), "B", true);

                    MutableComponent text = !fluidStack.isEmpty()
                            ? Component.translatable("jade.fluid", fluidStack.getDisplayName(), amountText)
                            : Component.translatable("tooltip.jade.empty");

                    if (accessor.getPlayer().isShiftKeyDown()&&!fluidStack.isEmpty() )
                        text.append("§7 / " + capacityText);
                    else if (fluidStack.isEmpty()) {
                        text.append("§7 " + capacityText);
                    }
                    tooltip.add(helper.progress((float) fluidStack.getAmount() / (float) integerList.get(1), text, progressStyle, BoxStyle.DEFAULT, true));

                    // FluidStorageProvider.append(tooltip, new FluidStack(fluid, integerList.get(0)), integerList.get(1));
                }
            });
            if (i.get() >= ClientConfig.showlimit.get() && !accessor.getPlayer().isShiftKeyDown())
                tooltip.add(Component.translatable(ModTranslateKey.getWailaHide()));


        }
    }


    @Override
    public ResourceLocation getUid() {
        return FluidDrawersLegacyMod.rl("controller_slave");
    }

    @Override
    public int getDefaultPriority() {
        return FluidStorageProvider.INSTANCE.getDefaultPriority() + 1000;
    }

    @Override
    public void appendServerData(CompoundTag compoundTag, BlockAccessor accessor) {
        if (!(accessor.getBlockEntity() instanceof BlockEntitySlave tile))
            return;
        if (compoundTag.contains("JadeFluidStorage")) {
            ListTag list =
                    ((CompoundTag) compoundTag.getList("JadeFluidStorage", CompoundTag.TAG_COMPOUND).get(0))
                            .getList("Views", CompoundTag.TAG_COMPOUND);

            Optional<IFluidHandler> a = accessor.getBlockEntity().getCapability(ForgeCapabilities.FLUID_HANDLER).resolve();
            if (a.isPresent()) {
                IFluidHandler iFluidHandler = a.get();
                if (iFluidHandler instanceof CapabilityProvider_FluidDrawerController.betterFluidHandler handler) {
                    int tanks = handler.getTanks();
                    // FluidDrawersLegacyMod.logger(tanks);
                    if (tanks > 0) {
                        if (handler.getFluidInTank(tanks - 1).isEmpty()) {
                            CompoundTag compoundTag1 = new CompoundTag();
                            compoundTag1.putString("fluid", BuiltInRegistries.FLUID.getKey(Fluids.EMPTY).toString());
                            compoundTag1.putLong("amount", 0);
                            compoundTag1.putLong("capacity", handler.getTankCapacity(tanks - 1));
                            // compoundTag.put("emptyCapacity",compoundTag1);
                            list.add(0,compoundTag1);

                            // list.add(compoundTag1);
                        }


                    }
                }
            }
        }
    }
}
