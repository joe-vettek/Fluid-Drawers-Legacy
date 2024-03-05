package xueluoanping.fluiddrawerslegacy.api;

import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawerGroup;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import xueluoanping.fluiddrawerslegacy.ModConstants;
import xueluoanping.fluiddrawerslegacy.block.blockentity.BlockEntityFluidDrawer;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.stream.Collectors;

import static xueluoanping.fluiddrawerslegacy.ModConstants.DRAWER_GROUP_CAPABILITY;

public class betterFluidManager<T extends BlockEntity & IDrawerGroup> implements IFluidHandler {
    private List<FluidStack> fluidRecord = new ArrayList<>();
    private FluidStack fluid = FluidStack.EMPTY;

    private final T tile;

    public betterFluidManager(T tile) {
        if (tile == null) {
            throw new RuntimeException("BlockEntity must implement IDrawerGroup");
        }
        this.tile = tile;
    }


    public void setFluid(FluidStack loadFluidStackFromNBT) {
        this.fluid = loadFluidStackFromNBT;
    }

    public CompoundTag writeToNBT(CompoundTag compoundTag) {
        return this.fluid.writeToNBT(compoundTag);
    }



    public int getDistance(BlockPos pos1, BlockPos pos2) {
        return Math.abs(pos1.getX() - pos2.getX()) + Math.abs(pos1.getY() - pos2.getY()) + Math.abs(pos1.getZ() - pos2.getZ());
    }


    public List<BlockEntityFluidDrawer.FluidDrawerData> getFluidDrawerDataList() {
        try {

            long startTime = System.currentTimeMillis();
            List<BlockEntityFluidDrawer.FluidDrawerData> listNew = new ArrayList<>();
            List<DrawerDistanceBook> listW = new ArrayList<>();
            // FluidDrawersLegacyMod.logger(tile.getCapability(DRAWER_GROUP_CAPABILITY, null).resolve().isPresent());
            if (tile.getCapability(DRAWER_GROUP_CAPABILITY, null).resolve().isPresent()) {
                // FluidDrawersLegacyMod.logger(tile.getCapability(DRAWER_GROUP_CAPABILITY, null).resolve().get().getDrawerCount()+"");
                IDrawerGroup handler = tile.getCapability(DRAWER_GROUP_CAPABILITY, null).resolve().get();
                int size = handler.getDrawerCount();
                for (int i = 0; i < size; i++) {

                    if (handler.getDrawer(i) instanceof BlockEntityFluidDrawer.FluidDrawerData fluidDrawerData) {
                        // listNew.add(fluidDrawerData);
                        int d = getDistance(tile.getBlockPos(), fluidDrawerData.getDrawerPos());
                        // dList.add(d);
                        listW.add(new DrawerDistanceBook(fluidDrawerData, d));
                    }
                }
                Collections.sort(listW);
                listNew = listW.stream().map(DrawerDistanceBook::fluidDrawerData).toList();

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

    public List<FluidHolder> getFluidMap(List<BlockEntityFluidDrawer.FluidDrawerData> listNew) {

        Map<FluidStack, List<Integer>> fluidMap = new LinkedHashMap<>();
        long startTime = System.currentTimeMillis();
        listNew.forEach(
                (ele) -> {
                    FluidStack fluidStack = ele.getTank().getFluid();

                    int capacity = ele.getMaxTankCapacity();
                    List<Integer> integerList = new ArrayList<>();
                    // indeed we not need to think about the fill because that's different
                    // Todo: add empty Lock
                    boolean isEmptyLockWithFluid = ele.isLock() && fluidStack.isEmpty() && !ele.getTank().getCacheFluid().isEmpty();
                    boolean notEmpty = fluidStack.getAmount() > 0 && fluidStack != FluidStack.EMPTY;
                    if (isEmptyLockWithFluid) {
                        fluidStack = ele.getTank().getCacheFluid();
                        fluidStack.setAmount(0);
                    }

                    // if (isEmptyLockWithFluid||notEmpty)
                    {
                        FluidStack fluidStackKey = fluidStack.copy();
                        // not 0, empty
                        if (notEmpty && !isEmptyLockWithFluid)
                            fluidStackKey.setAmount(1);
                        if (!notEmpty && !isEmptyLockWithFluid)
                            fluidStackKey = FluidStack.EMPTY;

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
        fluidRecord = new ArrayList<>(fluidMap.keySet());
        fluidRecord = fluidRecord.stream().distinct().collect(Collectors.toList());


        // must in the last position
        boolean removeEmptyKey = fluidRecord.removeIf(FluidStack::isEmpty);
        if (removeEmptyKey) {
            fluidRecord.add(FluidStack.EMPTY);
        }


        List<FluidHolder> fluidHolderList = new ArrayList<>();
        fluidRecord.forEach(fluidStack -> {
            FluidHolder holder = new FluidHolder(fluidStack,fluidMap.get(fluidStack).get(0),fluidMap.get(fluidStack).get(1));
            // holder.fluid = fluidStack;
            // holder.fluidAmount = fluidMap.get(fluidStack).get(0);
            // holder.tankCapacity = fluidMap.get(fluidStack).get(1);
            fluidHolderList.add(holder);
        });
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        // FluidDrawersLegacyMod.logger("Cost: " + duration + "Millis");
        return fluidHolderList;
    }

    // this function is used by others, so not need to lock it
    // note: there should use cacheFluid as a standard to check if allowed fill
    // while drain should use the fluid it has to assure no conflict problems
    protected int fillByOrder(List<BlockEntityFluidDrawer.FluidDrawerData> drawerDataList, List<Integer> priorityList, FluidStack resource, IFluidHandler.FluidAction action, int order) {
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
                        drawerDataList.get(i).getTank().fill(resource, IFluidHandler.FluidAction.EXECUTE);
                    return resource.getAmount();
                } else {
                    // avoid can't consume once
                    FluidStack fluidStack = resource;
                    fluidStack.setAmount(drawerDataList.get(i).getTank().getCapacity() -
                            drawerDataList.get(i).getTank().getFluid().getAmount());
                    if (action.execute())
                        drawerDataList.get(i).getTank().fill(fluidStack, IFluidHandler.FluidAction.EXECUTE);
                    return resource.getAmount() - fluidStack.getAmount();
                }
            }

        }

        return 0;
    }

    private int getFluidDrawerPriority(BlockEntityFluidDrawer.FluidDrawerData data) {
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
    public int fill(FluidStack resource, IFluidHandler.FluidAction action) {
        // FluidDrawersLegacyMod.logger("" + resource.writeToNBT(new CompoundTag()), action);
        List<BlockEntityFluidDrawer.FluidDrawerData> drawerDataList = getFluidDrawerDataList();
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
                    //
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
        List<BlockEntityFluidDrawer.FluidDrawerData> drawerDataList = getFluidDrawerDataList();
        return getFluidMap(drawerDataList).size();
    }

    // the following three function must be treated cautiously
    // because I'm not sure what would happens if return null


    @Nonnull
    @Override
    public FluidStack getFluidInTank(int tank) {
        List<BlockEntityFluidDrawer.FluidDrawerData> drawerDataList = getFluidDrawerDataList();
        FluidHolder fluidHolder = getFluidMap(drawerDataList).get(tank);
        // FluidStack stack = drawerDataList.get(tank).getTank().getFluid().copy();
        // long startTime=System.currentTimeMillis();
        // // for (int i = 0; i < 10000*1; i++) {
        // //     getFluidDrawerDataList();
        // //     // getFluidMap(getFluidDrawerDataList());
        // // }
        // long endTime = System.currentTimeMillis();
        // long duration = endTime - startTime;
        // FluidDrawersLegacyMod.logger("Cost: " + duration + "Millis");

        return new FluidStack(fluidHolder.fluid(), fluidHolder.fluidAmount());
    }

    @Override
    public int getTankCapacity(int tank) {
        List<BlockEntityFluidDrawer.FluidDrawerData> drawerDataList = getFluidDrawerDataList();
        FluidHolder fluidHolder = getFluidMap(drawerDataList).get(tank);
        // int amount = drawerDataList.get(tank).getTank().getCapacity();
        return fluidHolder.tankCapacity();
    }

    @Override
    public boolean isFluidValid(int tank, @Nonnull FluidStack stack) {
        List<BlockEntityFluidDrawer.FluidDrawerData> drawerDataList = getFluidDrawerDataList();
        FluidHolder fluidHolder = getFluidMap(drawerDataList).get(tank);
        // boolean result = drawerDataList.get(tank).getTank().isFluidValid(tank, stack);

        return stack.isFluidEqual(fluidHolder.fluid()) && stack.getAmount() + fluidHolder.fluidAmount() <= fluidHolder.tankCapacity();
    }


    // when action.execute ,can't give out the fluidstack ,or something bad would happen
    // note it's just a address ,so can't let others can change value directly
    @Nonnull
    @Override
    public FluidStack drain(FluidStack resource, IFluidHandler.FluidAction action) {
        //            FluidDrawersLegacyMod.logger("Drainresource" + resource.writeToNBT(new CompoundNBT()));
        // RebuildLock_drain0 = true;
        // FluidDrawersLegacyMod.logger(action, resource.writeToNBT(new CompoundTag()));

        List<BlockEntityFluidDrawer.FluidDrawerData> drawerDataList = getFluidDrawerDataList();

        // FluidDrawersLegacyMod.logger(getFluidMap(drawerDataList));
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
    public FluidStack drain(int maxDrain, IFluidHandler.FluidAction action) {
        //            FluidDrawersLegacyMod.LOGGER.info("Drainmmmm" + maxDrain);
        // RebuildLock_drain = true;
        // FluidDrawersLegacyMod.logger(action, maxDrain);
        List<BlockEntityFluidDrawer.FluidDrawerData> drawerDataList = getFluidDrawerDataList();
        List<FluidHolder> fluidHolder = getFluidMap(drawerDataList);

        FluidStack result = FluidStack.EMPTY;
        // FluidStack fluidType = FluidStack.EMPTY;
        // Strange , 0<0 ,but for will ingroe it.
        if (maxDrain > 0 && drawerDataList.size() > 0 && fluidHolder.size() > 0) {

            // tile.getLevel().dimensionTypeId
            if (tile.getLevel() instanceof ServerLevel) {
                // FluidDrawerControllerSave fluidDrawerControllerSave = FluidDrawerControllerSave.get(tile.getLevel());
                // this.fluid = fluidDrawerControllerSave.get(tile.getBlockPos());
                if (this.fluid.isEmpty() || !fluidRecord.contains(this.fluid))
                    this.fluid = fluidHolder.get(0).fluid();
                // fluidDrawerControllerSave.update(tile.getBlockPos(), this.fluid);
            }
            for (int i = 0; i < drawerDataList.size(); i++) {
                if (maxDrain <= 0) break;
                if (drawerDataList.get(i).getTank().getFluid().getFluid() == Fluids.EMPTY)
                    continue;
                if (drawerDataList.get(i).getTank().getFluid().getAmount() > 0) {
                    if (!result.isEmpty() && !result.isFluidEqual(drawerDataList.get(i).getTank().getFluid()))
                        continue;


                    // FluidStack temp = new FluidStack(!result.isEmpty() ? result : drawerDataList.get(i).getTank().getFluid(), maxDrain);
                    FluidStack temp = new FluidStack(!result.isEmpty() ? result : this.fluid, maxDrain);
                    temp = drawerDataList.get(i).getTank().drain(temp, action);
                    if (temp.getAmount() > 0) {
                        if (result == FluidStack.EMPTY)
                            result = temp;
                        else result.grow(temp.getAmount());
                        maxDrain -= temp.getAmount();
                    }
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

