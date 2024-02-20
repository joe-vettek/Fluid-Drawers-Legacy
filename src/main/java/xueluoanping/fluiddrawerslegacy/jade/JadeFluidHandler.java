package xueluoanping.fluiddrawerslegacy.jade;

import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityController;
import mcp.mobius.waila.api.BlockAccessor;
import mcp.mobius.waila.api.ITooltip;
import mcp.mobius.waila.api.config.IPluginConfig;
import mcp.mobius.waila.api.ui.IElement;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.registries.ForgeRegistries;
import snownee.jade.VanillaPlugin;
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

            Optional<IFluidHandler> a = accessor.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY).resolve();
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
        List<IElement> iElementList = tooltip.get(VanillaPlugin.FORGE_FLUID);
        if (iElementList.size() > ClientConfig.showlimit.get() && !accessor.getPlayer().isShiftKeyDown()) {
            List<IElement> noNeed = iElementList.subList(ClientConfig.showlimit.get(), iElementList.size());
            iElementList.removeAll(noNeed);
            tooltip.remove(VanillaPlugin.FORGE_FLUID);
            for (IElement iElement : iElementList) {
                tooltip.add(iElement);
            }
            tooltip.add(new TranslatableComponent(ModTranslateKey.getWailaHide()));
        }

    }
}
