package xueluoanping.fluiddrawerslegacy.block.drawer.IFluidDrawe;

import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

public interface IFluidDrawer extends IDrawer {
    default @NotNull ItemStack getStoredItemPrototype(){
        return ItemStack.EMPTY;
    };

    default @NotNull IDrawer setStoredItem(@NotNull ItemStack var1){
        return null;
    };


    default  int getStoredItemCount(){return 0;};

    default void setStoredItemCount(int var1){};

    default int getMaxCapacity(@NotNull ItemStack var1){return 0;};


    default int getRemainingCapacity(){return 0;};


    default boolean canItemBeStored(@NotNull ItemStack var1, Predicate<ItemStack> var2){return false;};


    default boolean canItemBeExtracted(@NotNull ItemStack var1, Predicate<ItemStack> var2){return false;};


    boolean isEmpty();

    IFluidTank getTank();


}
