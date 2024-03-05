package xueluoanping.fluiddrawerslegacy.api;

import org.jetbrains.annotations.NotNull;
import xueluoanping.fluiddrawerslegacy.block.blockentity.BlockEntityFluidDrawer;

public record DrawerDistanceBook(BlockEntityFluidDrawer.FluidDrawerData fluidDrawerData, int distance)
        implements Comparable<DrawerDistanceBook> {

    @Override
    public int compareTo(@NotNull DrawerDistanceBook o) {
        return this.distance() - o.distance();
    }
}
