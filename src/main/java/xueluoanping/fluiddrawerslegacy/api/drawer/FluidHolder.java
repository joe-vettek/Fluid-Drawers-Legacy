package xueluoanping.fluiddrawerslegacy.api.drawer;

public record FluidHolder(FluidType fluid, int fluidAmount, int tankCapacity) {

    //@Override
    //public String toString() {
    //    return fluid.writeToNBT(new CompoundTag()) + ":" + fluidAmount + "/" + tankCapacity;
    //}
}
