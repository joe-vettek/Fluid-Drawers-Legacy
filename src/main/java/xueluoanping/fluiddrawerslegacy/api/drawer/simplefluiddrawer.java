package xueluoanping.fluiddrawerslegacy.api.drawer;

import net.minecraft.fluid.Fluids;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nullable;

public class simplefluiddrawer implements FluidDrawer{
    @Nullable
    @Override
    public FluidStack getStoredFluid() {
        return new FluidStack(Fluids.WATER,3000);
    }

    @Override
    public FluidDrawer setStoredFluid(@Nullable FluidStack fluid) {
        return this;
    }

    @Override
    public int getMaxCapacity(@Nullable FluidStack fluid) {
        return 3000;
    }

    @Override
    public int getRemainingCapacity() {
        return 1000;
    }

    @Override
    public boolean canFluidBeStored(@Nullable FluidStack fluid) {
        return true;
    }

    @Override
    public boolean canFluidBeExtracted(@Nullable FluidStack fluid) {
        return true;
    }
}
