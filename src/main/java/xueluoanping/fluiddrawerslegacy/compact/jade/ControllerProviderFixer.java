package xueluoanping.fluiddrawerslegacy.compact.jade;


// import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityController;

import com.jaquadro.minecraft.storagedrawers.api.storage.attribute.LockAttribute;
import com.jaquadro.minecraft.storagedrawers.block.tile.BlockEntityController;
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
// import net.minecraft.network.chat.DisplayHelper.INSTANCEComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
// import snownee.jade.VanillaPlugin;
// import snownee.jade.addon.forge.ForgeCapabilityProvider;
import snownee.jade.api.fluid.JadeFluidObject;
import snownee.jade.api.ui.BoxStyle;
import snownee.jade.api.ui.IElement;
import snownee.jade.api.ui.IElementHelper;
import snownee.jade.api.ui.IProgressStyle;
import snownee.jade.api.view.FluidView;
import snownee.jade.overlay.DisplayHelper;
import xueluoanping.fluiddrawerslegacy.FluidDrawersLegacyMod;
import xueluoanping.fluiddrawerslegacy.ModTranslateKey;
import xueluoanping.fluiddrawerslegacy.block.tileentity.TileEntityFluidDrawer;
import xueluoanping.fluiddrawerslegacy.capability.CapabilityProvider_FluidDrawerController;
import xueluoanping.fluiddrawerslegacy.config.ClientConfig;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class ControllerProviderFixer implements IBlockComponentProvider, IServerDataProvider<BlockAccessor> {
    static final ControllerProviderFixer INSTANCE = new ControllerProviderFixer();


    // TODO: reduce the code and it not need more (just keep hide)
    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
        // tooltip.remove(Identifiers.UNIVERSAL_FLUID_STORAGE);
        // if (!(accessor.getBlockEntity() instanceof BlockEntityController tile))
        //     return;
        //
        //
        // // FluidDrawersLegacyMod.logger(accessor.getServerData()+"");
        // if (accessor.getServerData().contains("JadeFluidStorage")) {
        //     ListTag list =
        //             ((CompoundTag) accessor.getServerData().getList("JadeFluidStorage", CompoundTag.TAG_COMPOUND).get(0))
        //                     .getList("Views", CompoundTag.TAG_COMPOUND);
        //
        //     Map<FluidStack, List<Integer>> fluidMap = new LinkedHashMap<>();
        //     list.forEach(
        //             (ele) -> {
        //                 ((CompoundTag) ele).putString("FluidName", ((CompoundTag) ele).getString("fluid"));
        //                 ((CompoundTag) ele).putInt("Amount", ((CompoundTag) ele).getInt("amount"));
        //                 if (((CompoundTag) ele).contains("tag"))
        //                     ((CompoundTag) ele).put("Tag", ((CompoundTag) ele).getCompound("tag"));
        //
        //                 FluidStack fluidStack = FluidStack.loadFluidStackFromNBT((CompoundTag) ele);
        //                 int capacity = ((CompoundTag) ele).getInt("capacity");
        //                 List<Integer> integerList = new ArrayList<>();
        //                 // FluidDrawersLegacyMod.logger(fluidStack.writeToNBT(new CompoundTag()));
        //                 // FluidDrawersLegacyMod.logger(fluidStack.getDisplayName().toString());
        //                 // if (fluidStack.getAmount() > 0 && fluidStack != FluidStack.EMPTY)
        //                 {
        //                     FluidStack fluidStackKey = fluidStack.copy();
        //                     // not 0, empty
        //                     if (fluidStack.getAmount() > 0 && fluidStack != FluidStack.EMPTY)
        //                         fluidStackKey.setAmount(1);
        //                     if (fluidMap.containsKey(fluidStackKey)) {
        //                         integerList = fluidMap.get(fluidStackKey);
        //                         integerList.set(0, integerList.get(0) + fluidStack.getAmount());
        //                         integerList.set(1, integerList.get(1) + capacity);
        //                         fluidMap.replace(fluidStackKey, fluidMap.get(fluidStackKey), integerList);
        //                     } else {
        //                         integerList.add(fluidStack.getAmount());
        //                         integerList.add(capacity);
        //                         fluidMap.put(fluidStackKey, integerList);
        //                     }
        //                 }
        //             }
        //     );
        //     if(fluidMap.containsKey(FluidStack.EMPTY)){
        //         List<Integer> integerList = fluidMap.remove(FluidStack.EMPTY);
        //         fluidMap.put(FluidStack.EMPTY,integerList);
        //
        //     }
        //     AtomicInteger i = new AtomicInteger();
        //     fluidMap.forEach((fluid, integerList) -> {
        //         i.getAndIncrement();
        //         if (accessor.getPlayer().isShiftKeyDown() ||
        //                 (!accessor.getPlayer().isShiftKeyDown()
        //                         && i.get() < ClientConfig.showlimit.get())) {
        //             IElementHelper helper = tooltip.getElementHelper();
        //             FluidStack fluidStack = new FluidStack(fluid, integerList.get(0));
        //             IProgressStyle progressStyle = helper.progressStyle().overlay(helper.fluid(JadeFluidObject.of(fluid.getFluid(), integerList.get(0), fluid.getTag())));
        //             String amountText = DisplayHelper.INSTANCE.humanReadableNumber((double) fluidStack.getAmount(), "B", true);
        //             String capacityText = DisplayHelper.INSTANCE.humanReadableNumber((double) integerList.get(1), "B", true);
        //
        //             MutableComponent text = !fluidStack.isEmpty() ? Component.translatable("jade.fluid", fluidStack.getDisplayName(), amountText) : Component.translatable("tooltip.jade.empty");
        //             if (accessor.getPlayer().isShiftKeyDown()&&!fluidStack.isEmpty() )
        //                 text.append("§7 / " + capacityText);
        //             else if (fluidStack.isEmpty()) {
        //                 text.append("§7 " + capacityText);
        //             }
        //             tooltip.add(helper.progress((float) fluidStack.getAmount() / (float) integerList.get(1), (Component) text, progressStyle, BoxStyle.DEFAULT, true));
        //
        //             // FluidStorageProvider.append(tooltip, new FluidStack(fluid, integerList.get(0)), integerList.get(1));
        //         }
        //     });
        //     if (i.get() >= ClientConfig.showlimit.get() && !accessor.getPlayer().isShiftKeyDown())
        //         tooltip.add(Component.translatable(ModTranslateKey.getWailaHide()));
        //
        // }

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


    @Override
    public ResourceLocation getUid() {
        return FluidDrawersLegacyMod.rl("controller");
    }

    @Override
    public int getDefaultPriority() {
        return FluidStorageProvider.INSTANCE.getDefaultPriority() + 1000;
    }

    @Override
    public void appendServerData(CompoundTag compoundTag, BlockAccessor accessor) {
        // FluidDrawersLegacyMod.logger(compoundTag);
        if (!(accessor.getBlockEntity() instanceof BlockEntityController tile))
            return;
        if (compoundTag.contains("JadeFluidStorage")) {
            ListTag list =
                    ((CompoundTag) compoundTag.getList("JadeFluidStorage", CompoundTag.TAG_COMPOUND).get(0))
                            .getList("Views", CompoundTag.TAG_COMPOUND);
            // list.clear();
            if(list.size()<=1)return;

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
                            list.addTag(list.size(),compoundTag1);
                            // list.add(compoundTag1);
                        }
                    }
                    // for (int i = 0; i < handler.getTanks(); i++) {
                    //     CompoundTag compoundTag1 = new CompoundTag();
                    //     FluidStack stack = handler.getFluidInTank(i);
                    //     compoundTag1.putString("fluid", BuiltInRegistries.FLUID.getKey(stack.getFluid()).toString());
                    //     compoundTag1.putLong("amount", stack.getAmount());
                    //     if (stack.hasTag())
                    //         compoundTag1.put("tag", stack.getTag());
                    //     compoundTag1.putLong("capacity", handler.getTankCapacity(i));
                    //     list.addTag(list.size(), compoundTag1);
                    // }
                }
            }
        }

    }
}
