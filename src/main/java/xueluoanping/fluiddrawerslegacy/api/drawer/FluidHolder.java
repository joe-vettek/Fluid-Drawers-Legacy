package xueluoanping.fluiddrawerslegacy.api.drawer;

import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.fluids.FluidStack;


public record FluidHolder(FluidStack fluid, int fluidAmount, int tankCapacity) {

    @Override
    public String toString() {
        // fluid.getComponents().toString().
        return fluid.getComponents() + ":" + fluidAmount + "/" + tankCapacity;
    }
}
