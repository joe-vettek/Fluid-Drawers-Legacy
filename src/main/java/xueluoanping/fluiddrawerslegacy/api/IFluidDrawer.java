package xueluoanping.fluiddrawerslegacy.api;

import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

// we need to override the idrawer but not need the function storage item
public interface IFluidDrawer extends IDrawer {
    default @NotNull ItemStack getStoredItemPrototype() {
        return ItemStack.EMPTY;
    }

    default @NotNull IDrawer setStoredItem(@NotNull ItemStack var1) {
        return this;
    }

    @Override
    @NotNull
    default IDrawer setStoredItem(@NotNull ItemStack itemPrototype, int amount) {
        return this;
    }

    default int getStoredItemCount() {
        return 0;
    }

    default void setStoredItemCount(int var1) {
    }

    @Override
    default int adjustStoredItemCount(int amount) {
        // throw new UnsupportedOperationException("Not support ");
        return 0;
    }

    @Override
    default int getMaxCapacity() {
        return 0;
    }

    default int getMaxCapacity(@NotNull ItemStack var1) {
        return 0;
    }

    @Override
    default int getAcceptingMaxCapacity(@NotNull ItemStack itemPrototype) {
        return 0;
    }

    default int getRemainingCapacity() {
        return 0;
    }

    @Override
    default int getAcceptingRemainingCapacity() {
        return 0;
    }


    @Override
    default int getStoredItemStackSize() {
        return 0;
    }

    default boolean canItemBeStored(@NotNull ItemStack var1, Predicate<ItemStack> var2) {
        return false;
    }

    @Override
    default boolean canItemBeStored(@NotNull ItemStack itemPrototype) {
        return false;
    }

    default boolean canItemBeExtracted(@NotNull ItemStack var1, Predicate<ItemStack> var2) {
        return false;
    }

    @Override
    default boolean canItemBeExtracted(@NotNull ItemStack itemPrototype) {
        return false;
    }

    default boolean isEmpty() {
        return false;
    }

    IFluidTank getTank();


}
