package xueluoanping.fluiddrawerslegacy.compact.jade;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.registries.ForgeRegistries;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.ITooltip;
import snownee.jade.api.Identifiers;
import snownee.jade.api.config.IPluginConfig;
import snownee.jade.api.ui.IElement;
import xueluoanping.fluiddrawerslegacy.ModTranslateKey;
import xueluoanping.fluiddrawerslegacy.capability.CapabilityProvider_FluidDrawerController;
import xueluoanping.fluiddrawerslegacy.config.ClientConfig;

import java.util.List;
import java.util.Optional;

public class JadeFluidHandler {
    public static void appendServerDataIfWithNotEmpty(CompoundTag compoundTag, BlockEntity accessor) {
        if (compoundTag.contains("JadeFluidStorage")) {
            ListTag list =
                    ((CompoundTag) compoundTag.getList("JadeFluidStorage", CompoundTag.TAG_COMPOUND).get(0))
                            .getList("Views", CompoundTag.TAG_COMPOUND);
            if(list.size()<=1)return;

            Optional<IFluidHandler> a = accessor.getCapability(ForgeCapabilities.FLUID_HANDLER).resolve();
            if (a.isPresent()) {
                IFluidHandler iFluidHandler = a.get();
                if (iFluidHandler instanceof CapabilityProvider_FluidDrawerController.betterFluidHandler handler) {
                    int tanks = handler.getTanks();
                    // FluidDrawersLegacyMod.logger(tanks);
                    if (tanks > 0) {
                        if (handler.getFluidInTank(tanks - 1).isEmpty()) {
                            CompoundTag compoundTag1 = new CompoundTag();
                            compoundTag1.putString("fluid", ForgeRegistries.FLUIDS.getKey(Fluids.EMPTY).toString());
                            compoundTag1.putLong("amount", 0);
                            compoundTag1.putLong("capacity", handler.getTankCapacity(tanks - 1));
                            // compoundTag.put("emptyCapacity",compoundTag1);
                            list.addTag(list.size(), compoundTag1);

                            // list.add(compoundTag1);
                        }


                    }
                }
            }
        }
    }

    public static void resortTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
        List<IElement> iElementList = tooltip.get(Identifiers.UNIVERSAL_FLUID_STORAGE);
        if (iElementList.size() > ClientConfig.showlimit.get() && !accessor.getPlayer().isShiftKeyDown()) {
            List<IElement> noNeed = iElementList.subList(ClientConfig.showlimit.get(), iElementList.size());
            iElementList.removeAll(noNeed);
            tooltip.remove(Identifiers.UNIVERSAL_FLUID_STORAGE);
            for (IElement iElement : iElementList) {
                tooltip.add(iElement);
            }
            tooltip.add(Component.translatable(ModTranslateKey.getWailaHide()));
        }
    }
}
