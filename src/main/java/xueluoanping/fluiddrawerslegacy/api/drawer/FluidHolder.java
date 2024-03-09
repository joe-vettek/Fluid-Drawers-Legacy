package xueluoanping.fluiddrawerslegacy.api.drawer;

import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;

public record FluidHolder( FluidStack fluid, int fluidAmount,int tankCapacity) {

    @Override
    public String toString() {
        return fluid.writeToNBT(new CompoundTag()) + ":" + fluidAmount + "/" + tankCapacity;
    }
}
