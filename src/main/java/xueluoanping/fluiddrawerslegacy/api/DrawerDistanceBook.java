package xueluoanping.fluiddrawerslegacy.api;

import org.jetbrains.annotations.NotNull;
import xueluoanping.fluiddrawerslegacy.block.blockentity.BlockEntityFluidDrawer;

public  class DrawerDistanceBook implements Comparable<DrawerDistanceBook> {


    BlockEntityFluidDrawer.FluidDrawerData fluidDrawerData;
    private final int distance;

    public DrawerDistanceBook(BlockEntityFluidDrawer.FluidDrawerData fluidDrawerData, int d) {
        this.fluidDrawerData = fluidDrawerData;
        this.distance = d;
    }

    @Override
    public int compareTo(@NotNull DrawerDistanceBook o) {
        return this.distance - o.distance;
    }
}
