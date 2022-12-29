package xueluoanping.fluiddrawerslegacy.capability;

import com.jaquadro.minecraft.storagedrawers.api.storage.*;
import com.jaquadro.minecraft.storagedrawers.block.BlockController;
import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityController;
import com.jaquadro.minecraft.storagedrawers.config.CommonConfig;
import net.minecraft.block.Block;
import net.minecraft.fluid.Fluids;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.fml.common.Mod;
import xueluoanping.fluiddrawerslegacy.FluidDrawersLegacyMod;
import xueluoanping.fluiddrawerslegacy.ModConstants;
import xueluoanping.fluiddrawerslegacy.block.tileentity.TileEntityFluidDrawer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CapabilityProvider_FluidDrawerController implements ICapabilityProvider {

    public final betterFluidHandler tank;
    private final LazyOptional<betterFluidHandler> tankHandler;
    public static final int Capacity = 32000;
    public static BlockPos tilePos = null;
    private Stack<BlockPos> posStack = new Stack<>();
    @CapabilityInject(IDrawerGroup.class)
    static Capability<IDrawerGroup> DRAWER_GROUP_CAPABILITY = null;
    final TileEntityController tile;
    private static final List<Timer> timerList = new ArrayList<>();
    private final List<TileEntityFluidDrawer.StandardDrawerData> drawerDataList = new ArrayList<>();
    private final List<Integer> priorityList = new ArrayList<>();
    private boolean RebuildLock = false;

    public CapabilityProvider_FluidDrawerController(final TileEntityController tile) {
        this.tile = tile;
        tank = createFuildHandler();
        tankHandler = LazyOptional.of(() -> tank);
        tilePos = tile.getBlockPos();

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            public void run() {
                // if null, then cancel
//                    sometimes maybe a empty tile
                if (!RebuildLock) {
                    drawerDataList.clear();}
                if (tile == null) {
                    return;
                }


                if (((IDrawerGroup) tile).getDrawerCount() == 0) {
//                    FluidDrawersLegacyMod.logger("Not Found Drawer in " + tile.getLevel() + tile.getBlockPos() + ", Stop Now");
//                    FluidDrawersLegacyMod.logger(""+ timerList.size());
                    if (tile.isRemoved() || tile.getLevel().isClientSide()) {
//                        FluidDrawersLegacyMod.logger("Not Run in" + tile.getLevel());
//                        System.gc();
                        timer.cancel();
                    }
                    return;
                }
//                优先处理不加载的区块
//                FluidDrawersLegacyMod.logger("Chunk " + tile.getLevel().hasChunk(tile.getBlockPos().getX() >> 4, tile.getBlockPos().getZ() >> 4) );
                if (!tile.getLevel().hasChunk(tile.getBlockPos().getX() >> 4, tile.getBlockPos().getZ() >> 4)) {
                    FluidDrawersLegacyMod.logger("Not Chunk Cancel " + tile.getLevel() + tile.getBlockPos());
                    timer.cancel();
                    return;
                }
                //                    问题在于如何处置服务器上被移除的entity的计时器
//                说明，必须要延时处理，否则坐标是0，0，0
                if (!(tile.getLevel().getBlockState(tile.getBlockPos()).getBlock() instanceof BlockController)) {
//                    FluidDrawersLegacyMod.logger("null Cancel" + tile.getLevel());
                    timer.cancel();
                    return;
                }
//                    long start = System.currentTimeMillis();
//                    searchNode(tile.getLevel(), tile.getBlockPos(), tile.getBlockPos());

                posStack = new Stack<>();
//                    FluidDrawersLegacyMod.LOGGER.info(System.currentTimeMillis() - start);

                tile.getCapability(DRAWER_GROUP_CAPABILITY, null)
                        .ifPresent((handler -> {
                            if (handler.isGroupValid() && handler.getDrawerCount() > 0) {

                                if (!RebuildLock) {
                                    drawerDataList.clear();
                                    for (int i = 0; i < handler.getDrawerCount(); i++)
                                        if (handler.getDrawer(i) instanceof TileEntityFluidDrawer.StandardDrawerData) {
                                            drawerDataList.add((TileEntityFluidDrawer.StandardDrawerData) handler.getDrawer(i));
                                        }
                                }
                            }
                        }));

            }
        }, 7000, 5000);// 这里百毫秒
        timerList.add(timer);
    }

    // use to find all nodes
    public void searchNode(World world, BlockPos originPos, BlockPos basePos) {

        if (!world.hasChunk(originPos.getX() >> 4, originPos.getZ() >> 4))
            return;
//above
        BlockPos p1 = originPos.above();
        if (withinRange(p1, basePos))
            if (testBlock(world.getBlockState(p1).getBlock())) {
                FluidDrawersLegacyMod.logger(world.getBlockState(p1) + p1.toString() +
                        (Math.abs(p1.getY() - basePos.getY()) + Math.abs(p1.getY() - basePos.getY()) + Math.abs(p1.getZ() - basePos.getZ())));
                searchNode(world, p1, basePos);
            }
//        east
        BlockPos p2 = originPos.east();
        if (withinRange(p2, basePos)) {
            if (testBlock(world.getBlockState(p2).getBlock())) {
                FluidDrawersLegacyMod.logger(world.getBlockState(p2).toString());

                searchNode(world, p2, basePos);
            }
        }
//        south
        BlockPos p3 = originPos.south();
        if (withinRange(p3, basePos)) {
            if (testBlock(world.getBlockState(p3).getBlock())) {
                FluidDrawersLegacyMod.logger(world.getBlockState(p3).toString());
                searchNode(world, p3, basePos);
            }
        }
//        west
        BlockPos p4 = originPos.west();
        if (withinRange(p4, basePos)) {
            if (testBlock(world.getBlockState(p4).getBlock())) {
                FluidDrawersLegacyMod.logger(world.getBlockState(p4).toString());
                searchNode(world, p4, basePos);
            }
        }
//        north
        BlockPos p5 = originPos.north();
        if (withinRange(p5, basePos)) {
            if (testBlock(world.getBlockState(p5).getBlock())) {
                FluidDrawersLegacyMod.logger(world.getBlockState(p5).toString());
                searchNode(world, p5, basePos);
            }
        }
//        below
        BlockPos p6 = originPos.below();
        if (withinRange(p6, basePos)) {
            if (testBlock(world.getBlockState(p6).getBlock())) {
                FluidDrawersLegacyMod.logger(world.getBlockState(p6).toString());
                searchNode(world, p6, basePos);
            }
        }
    }

    private boolean testBlock(Block block) {
        if (block instanceof INetworked)
            return true;
        return false;
    }

    private boolean withinRange(BlockPos checkPos, BlockPos basePos) {
        if (posStack.search(checkPos) != -1)
            return false;
        posStack.push(checkPos);
        if (checkPos.getY() > 256 || checkPos.getY() < 0)
            return false;
        if (Math.max(Math.abs(checkPos.getY() - basePos.getY()),
                Math.max(
                        Math.abs(checkPos.getY() - basePos.getY())
                        , Math.abs(checkPos.getZ() - basePos.getZ())))
                < (CommonConfig.GENERAL.controllerRange.get() / 2 + 1))
            return true;
        return false;
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
//            FluidDrawersLegacyMod.LOGGER.info("hello" + tile);
            return tankHandler.cast();
        }
        return LazyOptional.empty();
    }


    private betterFluidHandler createFuildHandler() {
        return new betterFluidHandler(Capacity) {
        };
    }

    private void cancelLock() {
        this.RebuildLock = false;
    }

    private void setupLock() {
        this.RebuildLock = true;
    }

    public class betterFluidHandler extends FluidTank {

        public betterFluidHandler(int capacity) {
            super(capacity);
        }

        public CompoundNBT serializeNBT() {
            return writeToNBT(new CompoundNBT());
        }

        public void deserializeNBT(CompoundNBT tank) {
            readFromNBT(tank);
        }


        protected int fillByOrder(FluidStack resource, FluidAction action, int order) {
            if (drawerDataList.size() == 0) return 0;
            for (int i = 0; i < drawerDataList.size(); i++) {
//                reject invalid
                //                only find valid and same order
                if (priorityList.get(i) != order) continue;
// when locked, need to check cache, or not necessary
                
                // if drawer is empty and locked, no need to continue checking
                if( drawerDataList.get(i).getTank().getCacheFluid() == Fluids.EMPTY 
                        && drawerDataList.get(i).isLock())
                    continue;
                
                // if drawer not empty and fluids are different, no need to check for lock nor continue checking
                if ( drawerDataList.get(i).getTank().getCacheFluid() != Fluids.EMPTY
                        && drawerDataList.get(i).getTank().getCacheFluid().getFluid() != resource.getFluid())
                    continue;
                
                if ( drawerDataList.get(i).getTank().getCacheFluid().getFluid() == resource.getFluid()
                        || drawerDataList.get(i).getTank().getCacheFluid() == Fluids.EMPTY ) 
                {
                    if (resource.getAmount() + drawerDataList.get(i).getTank().getFluid().getAmount()
                            <= drawerDataList.get(i).getTank().getCapacity()) {
                        if (action.execute()) drawerDataList.get(i).getTank().fill(resource, FluidAction.EXECUTE);
                        return resource.getAmount();
                    } else {
//                      avoid can't consume once
                        FluidStack fluidStack = resource;
                        fluidStack.setAmount(drawerDataList.get(i).getTank().getCapacity() -
                                drawerDataList.get(i).getTank().getFluid().getAmount());
                        if (action.execute()) drawerDataList.get(i).getTank().fill(fluidStack, FluidAction.EXECUTE);
                        return resource.getAmount() - fluidStack.getAmount();
                    }
                }

            }

            return 0;
        }

        private int getFluidDrawerPriority(TileEntityFluidDrawer.StandardDrawerData data) {
            if (data.getTank().isFull()) return ModConstants.PRI_DISABLED;
            if (data.getTank().isEmpty()) {
                if (data.isLock()) return ModConstants.PRI_LOCKED_EMPTY;
                else return ModConstants.PRI_EMPTY;
            } else {
                if (!data.isLock()) {
                    if (data.isVoid()) return ModConstants.PRI_VOID;
                    else return ModConstants.PRI_NORMAL;
                } else {
                    if (data.isVoid()) return ModConstants.PRI_LOCKED_VOID;
//                    not delete else if ,or will handle anything else
                    else if (data.isLock()) return ModConstants.PRI_LOCKED;
                }
            }
            return -1;
        }

        @Override
        public int fill(FluidStack resource, FluidAction action) {
            //                Exception caught during firing event: Index 3 out of bounds for length 0
//                避免发生这种事情
//                在重置之间要锁住

//            FluidDrawersLegacyMod.logger("" + resource.writeToNBT(new CompoundNBT()) + RebuildLock);
            setupLock();
            int result = 0;
            int amount = resource.getAmount();
            final int amountF = amount;
            if (amountF <= 0) return 0;
            int i = 0;
//            rember to clear it
            priorityList.clear();
            while (i < drawerDataList.size()) {
                priorityList.add(getFluidDrawerPriority(drawerDataList.get(i)));
//                FluidDrawersLegacyMod.logger("priorityList"+priorityList.get(priorityList.size()-1));
                i++;
            }
            FluidDrawersLegacyMod.logger(resource.getAmount()+"fillByOrder"+priorityList.size());
            if (drawerDataList.size() == priorityList.size() && drawerDataList.size() > 0)
                for (int j = 0; j < ModConstants.PRI_DISABLED; j++) {
//                这里需要确认，到底是什么情况(这里是个备份，不担心）
                FluidDrawersLegacyMod.logger(resource.getAmount()+"fillByOrder"+j);
                    amount -= fillByOrder(resource, action, j);
//                amount=resource.getAmount();
                    if (amount == 0) {
                    FluidDrawersLegacyMod.logger(resource.getAmount()+"break"+j);
                        result = amountF;
                        break;
                    }

                }
            if (amount > 0) {
                result = amountF - amount;
            }

            cancelLock();
            return result;
        }

        @Override
        public int getTanks() {
            return CapabilityProvider_FluidDrawerController.this.drawerDataList.size();
        }

        @Nonnull
        @Override
        public FluidStack getFluidInTank(int tank) {
            return CapabilityProvider_FluidDrawerController.this.drawerDataList.get(tank).getTank().getFluid();
        }

        @Override
        public int getTankCapacity(int tank) {
            return CapabilityProvider_FluidDrawerController.this.drawerDataList.get(tank).getTank().getCapacity();
        }

        @Override
        public boolean isFluidValid(int tank, @Nonnull FluidStack stack) {
            return CapabilityProvider_FluidDrawerController.this.drawerDataList.get(tank).getTank().isFluidValid(0, stack);
        }


        @Nonnull
        @Override
        public FluidStack drain(FluidStack resource, FluidAction action) {
//            FluidDrawersLegacyMod.logger("Drainresource" + resource.writeToNBT(new CompoundNBT()));
            RebuildLock = true;

            if (resource.isEmpty() || resource.getAmount() <= 0) {
                RebuildLock = false;
                return FluidStack.EMPTY;
            }
            for (int i = 0; i < drawerDataList.size(); i++) {
                if (i >= drawerDataList.size())
                    break;
                if (drawerDataList.get(i).getTank().getCacheFluid() == Fluids.EMPTY)
                    continue;
                if (drawerDataList.get(i).getTank().getCacheFluid().getFluid() == resource.getFluid()
                        && drawerDataList.get(i).getTank().getFluid().getAmount() > 0) {
                    if (resource.getAmount() < drawerDataList.get(i).getTank().getFluid().getAmount()) {
                        FluidStack fluid = new FluidStack(drawerDataList.get(i).getTank().getCacheFluid().getFluid(), resource.getAmount());
                        if (action.execute()) {
                            drawerDataList.get(i).getTank().drain(fluid, FluidAction.EXECUTE);
                        }
                        RebuildLock = false;
                        return fluid;
                    } else {
//                        需要避免一个容器不能完全提供
                        FluidStack fluid = new FluidStack(drawerDataList.get(i).getTank().getCacheFluid().getFluid(), drawerDataList.get(i).getTank().getFluid().getAmount());
                        if (action.execute()) {
                            drawerDataList.get(i).getTank().drain(fluid, FluidAction.EXECUTE);
                        }
                        RebuildLock = false;
                        return fluid;
                    }
                }
            }
            RebuildLock = false;
            return FluidStack.EMPTY;
        }

        @Nonnull
        @Override
        public FluidStack drain(int maxDrain, FluidAction action) {

//            FluidDrawersLegacyMod.LOGGER.info("Drainmmmm" + maxDrain);
            RebuildLock = true;
            if (maxDrain <= 0) {
                RebuildLock = false;
                return FluidStack.EMPTY;
            }
            FluidStack fluid = FluidStack.EMPTY;
            for (int i = 0; i < drawerDataList.size(); i++) {
                if (drawerDataList.get(i).getTank().getCacheFluid() == FluidStack.EMPTY)
                    continue;
//                FluidDrawersLegacyMod.LOGGER.info("Drainmmmm" + i+drawerDataList.get(i).getTank().getFluidAmount());
                if (drawerDataList.get(i).getTank().getFluidAmount() >= maxDrain) {
                    fluid = new FluidStack(drawerDataList.get(i).getTank().getCacheFluid().getFluid(), maxDrain);
                    if (action.execute()) {
                        drawerDataList.get(i).getTank().drain(fluid, FluidAction.EXECUTE);
                    }
                    if (fluid.getAmount() > 0) {
                        break;
                    }
                } else {
                    fluid = new FluidStack(drawerDataList.get(i).getTank().getCacheFluid().getFluid(), drawerDataList.get(i).getTank().getFluidAmount());
                    if (action.execute()) {
                        drawerDataList.get(i).getTank().drain(fluid, FluidAction.EXECUTE);
                    }
                    if (fluid.getAmount() > 0) {
                        break;
                    }
                }
            }

            if (fluid.getAmount() <= 0) {
                RebuildLock = false;
                return FluidStack.EMPTY;
            }
            RebuildLock = false;
            return fluid;
        }


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
////                    ? Objects.requireNonNull(group.getCapability(CapabilityDrawerAttributes.DRAWER_ATTRIBUTES_CAPABILITY, null))
////                    : EMPTY_ATTRS;
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

    @SubscribeEvent
    public static void StopForSave(WorldEvent.Unload event) {
        if (timerList.size() == 0) return;
        for (int i = 0; i < timerList.size(); i++) {
            timerList.get(i).cancel();
            FluidDrawersLegacyMod.logger("TRY CLOSE" + i);
        }
        timerList.clear();
    }


}
