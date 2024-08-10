package xueluoanping.fluiddrawerslegacy.api.exchange;

import com.jaquadro.minecraft.storagedrawers.block.tile.BlockEntityController;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforgespi.language.IModInfo;
import net.neoforged.neoforgespi.language.ModFileScanData;
import xueluoanping.fluiddrawerslegacy.FluidDrawersLegacyMod;
import xueluoanping.fluiddrawerslegacy.handler.FluidDrawerHandler;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
public class FluidExchangeHandlerManager {
    // public static List<ModExchangeHandler> handlers = new ArrayList<>();
    public static FluidItem FluidManager = new FluidItem();

    @SubscribeEvent
    @SuppressWarnings("unchecked")
    // refer to jade
    public static void get(FMLLoadCompleteEvent event) {
        List<String> classNames = ModList.get().getAllScanData().stream().flatMap(($) -> $.getAnnotations().stream()).filter(($) -> {
            if (!$.annotationType().getClassName().equals(ExchangeHandlerAno.class.getName())) {
                return false;
            } else {
                List<String> required = (ArrayList<String>) $.annotationData().getOrDefault("mods", new ArrayList<>());
                return new HashSet<>(ModList.get().getMods().stream().map(IModInfo::getModId).toList()).containsAll(required);
            }
        }).map(ModFileScanData.AnnotationData::memberName).toList();

        for (String className : classNames) {
            FluidDrawersLegacyMod.logger("Start loading modHandler at " + className);
            try {
                Class<?> clazz = Class.forName(className);
                if (ModExchangeHandler.class.isAssignableFrom(clazz)) {
                    ModExchangeHandler plugin = (ModExchangeHandler) clazz.getDeclaredConstructor().newInstance();
                    // handlers.add(plugin);
                    plugin.registerFluidItem(FluidManager);
                }
            } catch (Throwable var7) {
                FluidDrawersLegacyMod.logger("Error loading modHandler at " + className, var7);
            }
        }
    }

    public static boolean tryHandleByMod(IFluidHandler tile, Player player, InteractionHand hand) {
        ItemStack heldStack = player.getItemInHand(hand);
        try {
            for (FluidItemHolder handler : FluidManager.handlers) {
                FluidStack fluidStack = handler.getFluidByItem.apply(heldStack);
                if (fluidStack.isEmpty()) continue;
                ItemStack itemStack = handler.getItemByFluid.apply(fluidStack);
                if (itemStack.isEmpty()) continue;
                if (FluidDrawerHandler.rightClickInPut(tile, player, fluidStack, itemStack, hand))
                    return true;
            }
            for (FluidContainerHolder handler : FluidManager.outHandlers) {
                if (handler.applyItem.test(heldStack))
                    if (FluidDrawerHandler.rightClickOuput(tile, player, handler.checkFluidAmount, handler.getItemByFluid, heldStack, hand))
                        return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean mayConsume(Player player, InteractionHand hand) {
        ItemStack heldStack = player.getItemInHand(hand);
        try {
            for (FluidItemHolder handler : FluidManager.handlers) {
                FluidStack fluidStack = handler.getFluidByItem.apply(heldStack);
                if (!fluidStack.isEmpty())
                    return true;
            }
            for (FluidContainerHolder handler : FluidManager.outHandlers) {
                if (handler.applyItem.test(heldStack))
                    return true;
            }

            if (heldStack.getCapability(Capabilities.FluidHandler.ITEM)!=null)
                return true;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static ArrayList<FluidStack> getFluidInItemContainer(ItemStack heldStack) {
        ArrayList<FluidStack> fluidStacksList = new ArrayList<>();

        try {
            for (FluidItemHolder handler : FluidManager.handlers) {
                FluidStack fluidStack = handler.getFluidByItem.apply(heldStack);
                if (fluidStack.isEmpty()) continue;
                fluidStacksList.add(fluidStack);
                break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (heldStack.getCapability(Capabilities.FluidHandler.ITEM)!=null) {
          Optional.ofNullable(  heldStack.getCapability(Capabilities.FluidHandler.ITEM))
                    .ifPresent((itemFluidHandler) -> {
                        int size = itemFluidHandler.getTanks();
                        for (int i = 0; i < size; i++) {
                            if (!itemFluidHandler.getFluidInTank(i).isEmpty())
                                fluidStacksList.add(itemFluidHandler.getFluidInTank(i));
                        }
                    });
        }
        return fluidStacksList;
    }

    public static boolean tryHandleClickInputByMod(BlockEntityController tile, Player player, InteractionHand hand) {
        ItemStack heldStack = player.getItemInHand(hand);
        if (tile.getLevel().getCapability(Capabilities.FluidHandler.BLOCK,tile.getBlockPos(),null)!=null)
            try {
                var fluidM = tile.getLevel().getCapability(Capabilities.FluidHandler.BLOCK,tile.getBlockPos(),null);
                for (FluidItemHolder handler : FluidManager.handlers) {
                    FluidStack fluidStack = handler.getFluidByItem.apply(heldStack);
                    if (fluidStack.isEmpty()) continue;
                    ItemStack itemStack = handler.getItemByFluid.apply(fluidStack);
                    if (itemStack.isEmpty()) continue;
                    if (FluidDrawerHandler.rightClickInPut(fluidM, player, fluidStack, itemStack, hand))
                        return true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        return false;
    }


    public static class FluidItem {

        public List<FluidItemHolder> handlers = new ArrayList<>();
        public List<FluidContainerHolder> outHandlers = new ArrayList<>();

        // if need to set the fluid handler item
        public void registerFluidItem(Function<ItemStack, FluidStack> getFluidByItem, Function<FluidStack, ItemStack> getItemByFluid) {
            handlers.add(new FluidItemHolder(getFluidByItem, getItemByFluid));
        }

        public void registerFluidContainer(Predicate<ItemStack> applyItem, Function<FluidStack, Integer> checkFluidAmount, Function<FluidStack, ItemStack> getItemByFluid) {
            outHandlers.add(new FluidContainerHolder(applyItem, checkFluidAmount, getItemByFluid));
        }
    }

    public static record FluidItemHolder(Function<ItemStack, FluidStack> getFluidByItem,
                                         Function<FluidStack, ItemStack> getItemByFluid) {
    }

    public static record FluidContainerHolder(Predicate<ItemStack> applyItem,
                                              Function<FluidStack, Integer> checkFluidAmount,
                                              Function<FluidStack, ItemStack> getItemByFluid) {
    }


}
