package xueluoanping.fluiddrawerslegacy.capability;

import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawerGroup;
import com.jaquadro.minecraft.storagedrawers.block.tile.BlockEntityController;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
// import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import xueluoanping.fluiddrawerslegacy.FluidDrawersLegacyMod;
import xueluoanping.fluiddrawerslegacy.ModConstants;
import xueluoanping.fluiddrawerslegacy.block.tileentity.TileEntityFluidDrawer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static xueluoanping.fluiddrawerslegacy.ModConstants.DRAWER_GROUP_CAPABILITY;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CapabilityProvider_FluidDrawerController implements ICapabilityProvider {

    public final betterFluidHandler tank;
    private final LazyOptional<betterFluidHandler> tankHandler;
    public static final int Capacity = 32000;
    public static BlockPos tilePos = null;
    final BlockEntityController tile;
    // private List<TileEntityFluidDrawer.StandardDrawerData> drawerDataList = new ArrayList<>();
    private List<FluidStack> fluidRecord = new ArrayList<>();

    public CapabilityProvider_FluidDrawerController(final BlockEntityController tile) {
        this.tile = tile;
        tank = createFuildHandler();
        tankHandler = LazyOptional.of(() -> tank);
        tilePos = tile.getBlockPos();

    }


    public List<TileEntityFluidDrawer.StandardDrawerData> getFluidDrawerDataList() {
        try {
            long startTime = System.currentTimeMillis();
            List<TileEntityFluidDrawer.StandardDrawerData> listNew = new ArrayList<>();
            // FluidDrawersLegacyMod.logger(tile.getCapability(DRAWER_GROUP_CAPABILITY, null).resolve().isPresent());
            if (tile.getCapability(DRAWER_GROUP_CAPABILITY, null).resolve().isPresent()) {
                // FluidDrawersLegacyMod.logger(tile.getCapability(DRAWER_GROUP_CAPABILITY, null).resolve().get().getDrawerCount()+"");
                IDrawerGroup handler = tile.getCapability(DRAWER_GROUP_CAPABILITY, null).resolve().get();
                int size = handler.getDrawerCount();
                for (int i = 0; i < size; i++) {

                    if (handler.getDrawer(i) instanceof TileEntityFluidDrawer.StandardDrawerData) {
                        listNew.add((TileEntityFluidDrawer.StandardDrawerData) handler.getDrawer(i));
                    }
                }
                // for(int j=0;j<100000;j++){
                //     for (int i=0;i<size;i++){
                //         List<TileEntityFluidDrawer.StandardDrawerData> list2=new ArrayList<>();
                //         if (handler.getDrawer(i) instanceof TileEntityFluidDrawer.StandardDrawerData) {
                //             list2.add((TileEntityFluidDrawer.StandardDrawerData) handler.getDrawer(i));
                //         }
                //     }
                // }
                long endTime = System.currentTimeMillis();
                long duration = endTime - startTime;
                // FluidDrawersLegacyMod.logger("Cost: " + duration + "Millis");

                return listNew;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new ArrayList<>();
    }

    // Add NBT mechanism to allow judgment based on NBT, using the isFluidEqual method (Jade is not currently supported there)
    // Add fluidMap memory mechanism and automatically remove 0 items.

    public class FluidHolder {
        FluidStack fluid = FluidStack.EMPTY;
        int fluidAmount = 0;
        int tankCapacity = 0;

        @Override
        public String toString() {
            return ForgeRegistries.FLUIDS.getKey(fluid.getFluid()) + ":" + fluidAmount + "/" + tankCapacity;
        }
    }

    public List<FluidHolder> getFluidMap(List<TileEntityFluidDrawer.StandardDrawerData> listNew) {
        Map<FluidStack, List<Integer>> fluidMap = new HashMap<>();
        long startTime = System.currentTimeMillis();
        listNew.forEach(
                (ele) -> {
                    FluidStack fluidStack = ele.getTank().getFluid();

                    int capacity = ele.getMaxCapacity();
                    List<Integer> integerList = new ArrayList<>();
                    // indeed we not need to think about the fill because that's different
                    if (fluidStack.getAmount() > 0 && fluidStack != FluidStack.EMPTY) {
                        FluidStack fluidStackKey = fluidStack.copy();
                        // not 0, empty
                        fluidStackKey.setAmount(1);
                        if (fluidMap.containsKey(fluidStackKey)) {
                            integerList = fluidMap.get(fluidStackKey);
                            integerList.set(0, integerList.get(0) + fluidStack.getAmount());
                            integerList.set(1, integerList.get(1) + capacity);
                            fluidMap.replace(fluidStackKey, fluidMap.get(fluidStackKey), integerList);
                        } else {
                            integerList.add(fluidStack.getAmount());
                            integerList.add(capacity);
                            fluidMap.put(fluidStackKey, integerList);
                        }
                    }
                }
        );

        fluidRecord.removeIf(a -> fluidMap.keySet().stream().noneMatch(b -> b.equals(a)));
        fluidRecord.addAll(fluidMap.keySet());
        fluidRecord = fluidRecord.stream().distinct().collect(Collectors.toList());
        List<FluidHolder> fluidHolderList = new ArrayList<>();
        fluidRecord.forEach(fluidStack -> {
            FluidHolder holder = new FluidHolder();
            holder.fluid = fluidStack;
            holder.fluidAmount = fluidMap.get(fluidStack).get(0);
            holder.tankCapacity = fluidMap.get(fluidStack).get(1);
            fluidHolderList.add(holder);
        });
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        // FluidDrawersLegacyMod.logger("Cost: " + duration + "Millis");
        return fluidHolderList;
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


    private betterFluidHandler createFuildHandler() {
        return new betterFluidHandler(Capacity) {
        };
    }


    public class betterFluidHandler extends FluidTank {

        public betterFluidHandler(int capacity) {
            super(capacity);
        }

        public CompoundTag serializeNBT() {
            return writeToNBT(new CompoundTag());
        }

        public void deserializeNBT(CompoundTag tank) {
            readFromNBT(tank);
        }


        // this function is used by others, so not need to lock it
        // note: there should use cacheFluid as a standard to check if allowed fill
        // while drain should use the fluid it has to assure no conflict problems
        protected int fillByOrder(List<TileEntityFluidDrawer.StandardDrawerData> drawerDataList, List<Integer> priorityList, FluidStack resource, FluidAction action, int order) {
            if (drawerDataList.size() == 0)
                return 0;
            for (int i = 0; i < drawerDataList.size(); i++) {
                // reject invalid
                // only find valid and same order
                if (priorityList.get(i) != order)
                    continue;

                // when locked, need to check cache, or not necessary
                FluidStack tankCacheFluid = drawerDataList.get(i).getTank().getCacheFluid();
                if (drawerDataList.get(i).isLock()) {
                    if (!tankCacheFluid.isEmpty() && !tankCacheFluid.isFluidEqual(resource)) {
                        continue;
                    }
                }

                FluidStack tankFluid = drawerDataList.get(i).getTank().getFluid();
                if (tankFluid.isFluidEqual(resource) || tankFluid.isEmpty()) {
                    if (resource.getAmount() + drawerDataList.get(i).getTank().getFluid().getAmount()
                            <= drawerDataList.get(i).getTank().getCapacity()) {
                        if (action.execute())
                            drawerDataList.get(i).getTank().fill(resource, FluidAction.EXECUTE);
                        return resource.getAmount();
                    } else {
                        // avoid can't consume once
                        FluidStack fluidStack = resource;
                        fluidStack.setAmount(drawerDataList.get(i).getTank().getCapacity() -
                                drawerDataList.get(i).getTank().getFluid().getAmount());
                        if (action.execute())
                            drawerDataList.get(i).getTank().fill(fluidStack, FluidAction.EXECUTE);
                        return resource.getAmount() - fluidStack.getAmount();
                    }
                }

            }

            return 0;
        }

        private int getFluidDrawerPriority(TileEntityFluidDrawer.StandardDrawerData data) {
            if (data.getTank().isFull())
                return ModConstants.PRI_DISABLED;
            if (data.getTank().isEmpty()) {
                if (data.isLock())
                    return ModConstants.PRI_LOCKED_EMPTY;
                else
                    return ModConstants.PRI_EMPTY;
            } else {
                if (!data.isLock()) {
                    if (data.isVoid())
                        return ModConstants.PRI_VOID;
                    else
                        return ModConstants.PRI_NORMAL;
                } else {
                    if (data.isVoid())
                        return ModConstants.PRI_LOCKED_VOID;
                        //  not delete else if ,or will handle anything else
                    else if (data.isLock())
                        return ModConstants.PRI_LOCKED;
                }
            }
            return -1;
        }

        @Override
        public int fill(FluidStack resource, FluidAction action) {
            FluidDrawersLegacyMod.logger("" + resource.writeToNBT(new CompoundTag()), action);
            List<TileEntityFluidDrawer.StandardDrawerData> drawerDataList = getFluidDrawerDataList();
            int result = 0;
            int amount = resource.getAmount();
            final int amountF = amount;
            // if (amountF <= 0)
            //     return 0;
            if (amountF > 0) {
                int i = 0;
                //            rember to clear it
                List<Integer> priorityList = new ArrayList<>();
                while (i < drawerDataList.size()) {
                    priorityList.add(getFluidDrawerPriority(drawerDataList.get(i)));
                    //                FluidDrawersLegacyMod.logger("priorityList"+priorityList.get(priorityList.size()-1));
                    i++;
                }

                if (drawerDataList.size() == priorityList.size() && drawerDataList.size() > 0)
                    for (int j = 0; j < ModConstants.PRI_DISABLED; j++) {
                        //                这里需要确认，到底是什么情况(这里是个备份，不担心）
                        //                FluidDrawersLegacyMod.logger(resource.getAmount()+"fillByOrder"+j);
                        amount -= fillByOrder(drawerDataList, priorityList, resource, action, j);
                        //                amount=resource.getAmount();
                        if (amount == 0) {
                            //  FluidDrawersLegacyMod.logger(resource.getAmount()+"break"+j);
                            result = amountF;
                            break;
                        }

                    }
                if (amount > 0) {
                    result = amountF - amount;
                }
            }

            // RebuildLock_fill = false;

            return result;
        }

        @Override
        public int getTanks() {
            List<TileEntityFluidDrawer.StandardDrawerData> drawerDataList = getFluidDrawerDataList();

            return getFluidMap(drawerDataList).size();
        }

        // the following three function must be treated cautiously
        // because I'm not sure what would happens if return null


        @Nonnull
        @Override
        public FluidStack getFluidInTank(int tank) {
            List<TileEntityFluidDrawer.StandardDrawerData> drawerDataList = getFluidDrawerDataList();
            FluidHolder fluidHolder = getFluidMap(drawerDataList).get(tank);
            // FluidStack stack = drawerDataList.get(tank).getTank().getFluid().copy();
            return new FluidStack(fluidHolder.fluid, fluidHolder.fluidAmount);
        }

        @Override
        public int getTankCapacity(int tank) {
            List<TileEntityFluidDrawer.StandardDrawerData> drawerDataList = getFluidDrawerDataList();
            FluidHolder fluidHolder = getFluidMap(drawerDataList).get(tank);
            // int amount = drawerDataList.get(tank).getTank().getCapacity();
            return fluidHolder.tankCapacity;
        }

        @Override
        public boolean isFluidValid(int tank, @Nonnull FluidStack stack) {
            List<TileEntityFluidDrawer.StandardDrawerData> drawerDataList = getFluidDrawerDataList();
            FluidHolder fluidHolder = getFluidMap(drawerDataList).get(tank);
            // boolean result = drawerDataList.get(tank).getTank().isFluidValid(tank, stack);

            return stack.isFluidEqual(fluidHolder.fluid) && stack.getAmount() + fluidHolder.fluidAmount <= fluidHolder.tankCapacity;
        }


        // when action.execute ,can't give out the fluidstack ,or something bad would happen
        // note it's just a address ,so can't let others can change value directly
        @Nonnull
        @Override
        public FluidStack drain(FluidStack resource, FluidAction action) {
            //            FluidDrawersLegacyMod.logger("Drainresource" + resource.writeToNBT(new CompoundNBT()));
            // RebuildLock_drain0 = true;
            FluidDrawersLegacyMod.logger(action, resource.writeToNBT(new CompoundTag()));

            List<TileEntityFluidDrawer.StandardDrawerData> drawerDataList = getFluidDrawerDataList();
            FluidDrawersLegacyMod.logger(getFluidMap(drawerDataList));
            FluidStack result = FluidStack.EMPTY;
            FluidStack resourceCopy = resource.copy();
            // if (action.execute()) return result;
            if (!resourceCopy.isEmpty() && resourceCopy.getAmount() > 0) {
                for (int i = 0; i < drawerDataList.size(); i++) {
                    if (resourceCopy.getAmount() <= 0) break;
                    int drawerFluidAmount = drawerDataList.get(i).getTank().getFluid().getAmount();
                    if (drawerDataList.get(i).getTank().getFluid().isEmpty())
                        continue;
                    if (drawerDataList.get(i).getTank().getFluid().isFluidEqual(resourceCopy) && drawerFluidAmount > 0) {
                        // FluidStack temp = new FluidStack(drawerFluid, Math.min(drawerFluidAmount, resourceCopy.getAmount()));
                        //
                        // // FluidStack temp =
                        // if (action.execute())
                        FluidStack temp = drawerDataList.get(i).getTank().drain(resourceCopy, action);
                        if (temp.getAmount() > 0) {
                            if (result == FluidStack.EMPTY)
                                result = temp;
                            else result.grow(temp.getAmount());
                            resourceCopy.shrink(temp.getAmount());
                            // break;
                        }
                    }
                }
            }
            // RebuildLock_drain0 = false;

            return result;
        }

        @Nonnull
        @Override
        public FluidStack drain(int maxDrain, FluidAction action) {
            //            FluidDrawersLegacyMod.LOGGER.info("Drainmmmm" + maxDrain);
            // RebuildLock_drain = true;
            // FluidDrawersLegacyMod.logger(action, maxDrain);
            List<TileEntityFluidDrawer.StandardDrawerData> drawerDataList = getFluidDrawerDataList();
            FluidStack result = FluidStack.EMPTY;
            // FluidStack fluidType = FluidStack.EMPTY;
            // Strange , 0<0 ,but for will ingroe it.
            if (maxDrain > 0 && drawerDataList.size() > 0)
                for (int i = 0; i < drawerDataList.size(); i++) {
                    if (maxDrain <= 0) break;
                    if (drawerDataList.get(i).getTank().getFluid().getFluid() == Fluids.EMPTY)
                        continue;
                    if (drawerDataList.get(i).getTank().getFluid().getAmount() > 0) {
                        if (!result.isEmpty() && !result.isFluidEqual(drawerDataList.get(i).getTank().getFluid()))
                            continue;


                        FluidStack temp = new FluidStack(!result.isEmpty() ? result : drawerDataList.get(i).getTank().getFluid(), maxDrain);
                        temp = drawerDataList.get(i).getTank().drain(temp, action).copy();
                        if (temp.getAmount() > 0) {
                            if (result == FluidStack.EMPTY)
                                result = temp;
                            else result.grow(temp.getAmount());
                            maxDrain -= temp.getAmount();
                        }
                    }

                }

            // if (result.getAmount() <= 0) {
            //     // RebuildLock_drain = false;
            //     result= FluidStack.EMPTY;
            // }
            // RebuildLock_drain = false;

            return result;
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
