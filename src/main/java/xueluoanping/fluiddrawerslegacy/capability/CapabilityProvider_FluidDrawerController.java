package xueluoanping.fluiddrawerslegacy.capability;

import com.jaquadro.minecraft.storagedrawers.block.tile.BlockEntityController;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
// import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fml.common.Mod;
import xueluoanping.fluiddrawerslegacy.api.betterFluidManager;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CapabilityProvider_FluidDrawerController implements ICapabilityProvider, INBTSerializable<CompoundTag> {

    public final betterFluidManager<BlockEntityController> tank;
    private final LazyOptional<betterFluidManager<BlockEntityController>> tankHandler;
    public static BlockPos tilePos = null;
    final BlockEntityController tile;
    // private List<TileEntityFluidDrawer.StandardDrawerData> drawerDataList = new ArrayList<>();

    public CapabilityProvider_FluidDrawerController(final BlockEntityController tile) {
        this.tile = tile;
        tank = createFuildHandler();
        tankHandler = LazyOptional.of(() -> tank);
        tilePos = tile.getBlockPos();
        FluidDrawerControllerData fluidDrawerControllerData = new FluidDrawerControllerData();
        fluidDrawerControllerData.setCapabilityProvider(this);
        this.tile.injectData(fluidDrawerControllerData);
        // tank.setFluid(FluidDrawerControllerSave.fluidDrawerControllerSave.);
    }

    @Override
    public CompoundTag serializeNBT() {
        return tank.writeToNBT(new CompoundTag());
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        // FluidDrawersLegacyMod.logger(22554, nbt);
        // if (nbt.contains("Fluid"))
        tank.setFluid(FluidStack.loadFluidStackFromNBT(nbt));
    }

    public void invalidate() {
        tankHandler.invalidate();
    }



    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.FLUID_HANDLER) {
            //            FluidDrawersLegacyMod.LOGGER.info("hello" + tile);
            return tankHandler.cast();
        }
        return LazyOptional.empty();
    }


    private betterFluidManager<BlockEntityController> createFuildHandler() {
        return new betterFluidManager<>(tile);
    }



    //    private static class FluidSlotRecord implements Comparable<FluidSlotRecord> {
    //
    //        private static final IDrawerAttributes EMPTY_ATTRS = new EmptyDrawerAttributes();
    //        private static final int PRI_LOCKED = 0;
    //        private static final int PRI_LOCKED_VOID = 1;
    //        private static final int PRI_NORMAL = 2;
    //        private static final int PRI_VOID = 3;
    //        private static final int PRI_EMPTY = 4;
    //        private static final int PRI_LOCKED_EMPTY = 5;
    //
    //        private final FluidDrawerGroup group;
    //        private final int slot;
    //        private final BlockPos pos;
    //        private final int priority;
    //
    //        int index;
    //
    //        FluidSlotRecord(FluidDrawerGroup group, int slot, BlockPos pos) {
    //            this.group = group;
    //            this.slot = slot;
    //            this.pos = pos;
    //            this.priority = computePriority();
    //        }
    //
    //        int computePriority() {
    //            FluidDrawer drawer = group.getFluidDrawer(slot);
    //            return 0;
    ////            IDrawerAttributes attrs = group.hasCapability(CapabilityDrawerAttributes.DRAWER_ATTRIBUTES_CAPABILITY, null)
    ////  ? Objects.requireNonNull(group.getCapability(CapabilityDrawerAttributes.DRAWER_ATTRIBUTES_CAPABILITY, null))
    ////  : EMPTY_ATTRS;
    ////            if (drawer.isEmpty()) {
    ////                return attrs.isItemLocked(LockAttribute.LOCK_EMPTY) ? PRI_LOCKED_EMPTY : PRI_EMPTY;
    ////            } else if (attrs.isVoid()) {
    ////                return attrs.isItemLocked(LockAttribute.LOCK_POPULATED) ? PRI_LOCKED_VOID : PRI_VOID;
    ////            } else {
    ////                return attrs.isItemLocked(LockAttribute.LOCK_POPULATED) ? PRI_LOCKED : PRI_NORMAL;
    ////            }
    //        }
    //
    //        FluidDrawer getDrawer() {
    //            return group.getFluidDrawer(slot);
    //        }
    //
    //        boolean isDrawerValid() {
    //            return group.isFluidDrawerGroupValid();
    //        }
    //
    //        @Override
    //        public int compareTo(FluidSlotRecord other) {
    //            int diff = priority - other.priority;
    //            return diff != 0 ? diff : pos.compareTo(other.pos);
    //        }
    //
    //    }

    // @SubscribeEvent
    // public static void StopForSave(LevelEvent.Unload event) {
    //     if (timerList.size() == 0)
    //         return;
    //     for (int i = 0; i < timerList.size(); i++) {
    //         timerList.get(i).cancel();
    //         FluidDrawersLegacyMod.logger("TRY CLOSE" + i);
    //     }
    //     timerList.clear();
    // }


    // use to find all nodes
    // public void searchNode(Level world, BlockPos originPos, BlockPos basePos) {
    //
    //     if (!world.hasChunk(originPos.getX() >> 4, originPos.getZ() >> 4))
    //         return;
    //     // above
    //     BlockPos p1 = originPos.above();
    //     if (withinRange(p1, basePos))
    //         if (testBlock(world.getBlockState(p1).getBlock())) {
    //             FluidDrawersLegacyMod.logger(world.getBlockState(p1) + p1.toString() +
    //                     (Math.abs(p1.getY() - basePos.getY()) + Math.abs(p1.getY() - basePos.getY()) + Math.abs(p1.getZ() - basePos.getZ())));
    //             searchNode(world, p1, basePos);
    //         }
    //     //        east
    //     BlockPos p2 = originPos.east();
    //     if (withinRange(p2, basePos)) {
    //         if (testBlock(world.getBlockState(p2).getBlock())) {
    //             FluidDrawersLegacyMod.logger(world.getBlockState(p2).toString());
    //
    //             searchNode(world, p2, basePos);
    //         }
    //     }
    //     //        south
    //     BlockPos p3 = originPos.south();
    //     if (withinRange(p3, basePos)) {
    //         if (testBlock(world.getBlockState(p3).getBlock())) {
    //             FluidDrawersLegacyMod.logger(world.getBlockState(p3).toString());
    //             searchNode(world, p3, basePos);
    //         }
    //     }
    //     //        west
    //     BlockPos p4 = originPos.west();
    //     if (withinRange(p4, basePos)) {
    //         if (testBlock(world.getBlockState(p4).getBlock())) {
    //             FluidDrawersLegacyMod.logger(world.getBlockState(p4).toString());
    //             searchNode(world, p4, basePos);
    //         }
    //     }
    //     //        north
    //     BlockPos p5 = originPos.north();
    //     if (withinRange(p5, basePos)) {
    //         if (testBlock(world.getBlockState(p5).getBlock())) {
    //             FluidDrawersLegacyMod.logger(world.getBlockState(p5).toString());
    //             searchNode(world, p5, basePos);
    //         }
    //     }
    //     //        below
    //     BlockPos p6 = originPos.below();
    //     if (withinRange(p6, basePos)) {
    //         if (testBlock(world.getBlockState(p6).getBlock())) {
    //             FluidDrawersLegacyMod.logger(world.getBlockState(p6).toString());
    //             searchNode(world, p6, basePos);
    //         }
    //     }
    // }
    //
    // private boolean testBlock(Block block) {
    //     if (block instanceof INetworked)
    //         return true;
    //     return false;
    // }

    // private boolean withinRange(BlockPos checkPos, BlockPos basePos) {
    //     if (posStack.search(checkPos) != -1)
    //         return false;
    //     posStack.push(checkPos);
    //     if (checkPos.getY() > 256 || checkPos.getY() < 0)
    //         return false;
    //     if (Math.max(Math.abs(checkPos.getY() - basePos.getY()),
    //             Math.max(
    //                     Math.abs(checkPos.getY() - basePos.getY())
    //                     , Math.abs(checkPos.getZ() - basePos.getZ())))
    //             < (CommonConfig.GENERAL.controllerRange.get() / 2 + 1))
    //         return true;
    //     return false;
    // }

}
