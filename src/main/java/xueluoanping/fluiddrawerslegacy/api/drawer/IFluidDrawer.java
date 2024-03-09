package xueluoanping.fluiddrawerslegacy.api.drawer;

import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import xueluoanping.fluiddrawerslegacy.client.render.FluidAnimation;

import java.util.function.Predicate;

// we need to override the idrawer but not need the function storage item
public interface IFluidDrawer<T extends IFluidTank & IFluidHandler> extends IDrawer {
    @Deprecated(forRemoval = true)
    default @NotNull ItemStack getStoredItemPrototype() {
        return ItemStack.EMPTY;
    }

    @Deprecated(forRemoval = true)
    default @NotNull IDrawer setStoredItem(@NotNull ItemStack var1) {
        return this;
    }

    @Deprecated(forRemoval = true)
    @Override
    @NotNull
    default IDrawer setStoredItem(@NotNull ItemStack itemPrototype, int amount) {
        return this;
    }

    @Deprecated(forRemoval = true)
    default int getStoredItemCount() {
        return 0;
    }

    @Deprecated(forRemoval = true)
    default void setStoredItemCount(int var1) {
    }

    @Deprecated(forRemoval = true)
    @Override
    default int adjustStoredItemCount(int amount) {
        // throw new UnsupportedOperationException("Not support ");
        return 0;
    }



    @Deprecated(forRemoval = true)
    @Override
    default int getMaxCapacity() {
        return 0;
    }

    @Deprecated(forRemoval = true)
    default int getMaxCapacity(@NotNull ItemStack var1) {
        return 0;
    }

    @Deprecated(forRemoval = true)
    @Override
    default int getAcceptingMaxCapacity(@NotNull ItemStack itemPrototype) {
        return 0;
    }

    default int getRemainingCapacity() {
        return 0;
    }

    @Deprecated(forRemoval = true)
    @Override
    default int getAcceptingRemainingCapacity() {
        return 0;
    }

    @Deprecated(forRemoval = true)
    @Override
    default int getStoredItemStackSize() {
        return 0;
    }

    @Deprecated(forRemoval = true)
    default boolean canItemBeStored(@NotNull ItemStack var1, Predicate<ItemStack> var2) {
        return false;
    }

    @Deprecated(forRemoval = true)
    @Override
    default boolean canItemBeStored(@NotNull ItemStack itemPrototype) {
        return false;
    }

    @Deprecated(forRemoval = true)
    default boolean canItemBeExtracted(@NotNull ItemStack var1, Predicate<ItemStack> var2) {
        return false;
    }

    @Deprecated(forRemoval = true)
    @Override
    default boolean canItemBeExtracted(@NotNull ItemStack itemPrototype) {
        return false;
    }

    @Deprecated(forRemoval = true)
    default boolean isEmpty() {
        return false;
    }

    T getTank();

    FluidStack getCacheFluid();

    FluidAnimation getFluidAnimation();

}
