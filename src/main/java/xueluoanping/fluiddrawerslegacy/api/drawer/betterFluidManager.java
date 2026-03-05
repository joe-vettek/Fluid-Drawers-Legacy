package xueluoanping.fluiddrawerslegacy.api.drawer;

import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawerGroup;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import xueluoanping.fluiddrawerslegacy.FluidDrawersLegacyMod;
import xueluoanping.fluiddrawerslegacy.ModConstants;
import xueluoanping.fluiddrawerslegacy.block.blockentity.BlockEntityFluidDrawer;

import javax.annotation.Nonnull;
import java.util.*;

import static xueluoanping.fluiddrawerslegacy.ModConstants.DRAWER_GROUP_CAPABILITY;

public class betterFluidManager<T extends BlockEntity & IDrawerGroup> implements IFluidHandler {

    private final T tile;

    public betterFluidManager(T tile) {
        if (tile == null) {
            throw new RuntimeException("BlockEntity must implement IDrawerGroup");
        }
        this.tile = tile;
    }


    public List<BlockEntityFluidDrawer.FluidDrawerData> getFluidDrawerDataList() {
        try {
            IDrawerGroup handler = DRAWER_GROUP_CAPABILITY.getCapability(tile);
            if (handler != null) {
                int size = handler.getDrawerCount();
                if (size <= 0)
                    return List.of();

                List<BlockEntityFluidDrawer.FluidDrawerData> listNew = new ArrayList<>(size);
                for (int i = 0; i < size; i++) {
                    if (handler.getDrawer(i) instanceof BlockEntityFluidDrawer.FluidDrawerData fluidDrawerData) {
                        listNew.add(fluidDrawerData);
                    }
                }
                return listNew;
            }
        } catch (Exception e) {
            FluidDrawersLegacyMod.LOGGER.error("Failed to gather fluid drawers for manager at {}", tile.getBlockPos(), e);
        }

        return List.of();
    }

    // Add NBT mechanism to allow judgment based on NBT, using the isFluidEqual method (Jade is not currently supported there)
    // Add fluidMap memory mechanism and automatically remove 0 items.

    public List<FluidHolder> getFluidMap(List<BlockEntityFluidDrawer.FluidDrawerData> listNew) {

        Map<FluidStack, Long> fluidMap = new LinkedHashMap<>();

        listNew.forEach(ele -> {
            FluidStack fluidStack = ele.getTank().getFluid();

            int capacity = ele.getMaxTankCapacity();
            boolean isEmptyLockWithFluid = ele.isLock() && fluidStack.isEmpty() && !ele.getTank().getCacheFluid().isEmpty();

            if (isEmptyLockWithFluid) {
                fluidStack = ele.getTank().getCacheFluid();
                fluidStack.setAmount(0);
            } else if (fluidStack.isEmpty()) {
                fluidStack = FluidStack.EMPTY;
            }

            Long packed = fluidMap.get(fluidStack);
            if (packed != null) {
                int amount = (int) (packed >> 32) + fluidStack.getAmount();
                int cap = packed.intValue() + capacity;
                packed = ((long) amount << 32) | cap;
                fluidMap.put(fluidStack, packed);
            } else {
                packed = ((long) fluidStack.getAmount() << 32) | capacity;
                fluidMap.put(fluidStack, packed);
            }
        });

        List<FluidHolder> fluidHolderList = new ArrayList<>(fluidMap.size());
        List<FluidHolder> emptyHolderList = new ArrayList<>(fluidMap.size());
        fluidMap.forEach((fluidStack, packed) -> {
            int amount = (int) (packed >> 32);
            int cap = packed.intValue();
            FluidHolder fluidHolder = new FluidHolder(fluidStack, amount, cap);

            if (!fluidStack.isEmpty())
                fluidHolderList.add(fluidHolder);
            else
                emptyHolderList.add(fluidHolder);
        });
        fluidHolderList.addAll(emptyHolderList);

        return fluidHolderList;
    }


    private int getFluidDrawerPriority(BlockEntityFluidDrawer.FluidDrawerData data) {
        if (data.getTank().isFull())
            return data.isVoid() ? ModConstants.PRI_VOID : ModConstants.PRI_DISABLED;
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
                    // not delete else if ,or will handle anything else
                else if (data.isLock())
                    return ModConstants.PRI_LOCKED;
            }
        }
        return -1;
    }

    @Override
    public int fill(FluidStack resource, IFluidHandler.FluidAction action) {
        FluidStack remaining = resource.copy();

        if (!resource.isEmpty()) {
            List<BlockEntityFluidDrawer.FluidDrawerData> drawerDataList = getFluidDrawerDataList();

            for (BlockEntityFluidDrawer.FluidDrawerData drawer: drawerDataList) {
                if (remaining.isEmpty())
                    break;

                int priority = getFluidDrawerPriority(drawer);
                if (priority >= 0 && priority != ModConstants.PRI_DISABLED) {
                    FluidStack tankFluid = drawer.getTank().getFluid();
                    if (!tankFluid.isEmpty() && !tankFluid.isFluidEqual(resource))
                        continue;

                    int filled = drawer.getTank().fill(remaining, action);
                    remaining.shrink(filled);
                }
            }
        }

        return resource.getAmount() - remaining.getAmount();
    }

    @Override
    public int getTanks() {
        List<BlockEntityFluidDrawer.FluidDrawerData> drawerDataList = getFluidDrawerDataList();
        return getFluidMap(drawerDataList).size();
    }

    // the following three function must be treated cautiously
    // because I'm not sure what would happen if return null

    @Nonnull
    @Override
    public FluidStack getFluidInTank(int tank) {
        List<BlockEntityFluidDrawer.FluidDrawerData> drawerDataList = getFluidDrawerDataList();
        List<FluidHolder> fluidHolderList = getFluidMap(drawerDataList);
        if (fluidHolderList.size() > tank) {
            FluidHolder holder = fluidHolderList.get(tank);
            return new FluidStack(holder.fluid(), holder.fluidAmount());
        } else {
            return FluidStack.EMPTY;
        }
    }

    @Override
    public int getTankCapacity(int tank) {
        List<BlockEntityFluidDrawer.FluidDrawerData> drawerDataList = getFluidDrawerDataList();
        List<FluidHolder> fluidHolderList = getFluidMap(drawerDataList);
        if (fluidHolderList.size() > tank) {
            return fluidHolderList.get(tank).tankCapacity();
        } else {
            return 0;
        }
    }

    @Override
    public boolean isFluidValid(int tank, @Nonnull FluidStack stack) {
        List<BlockEntityFluidDrawer.FluidDrawerData> drawerDataList = getFluidDrawerDataList();
        List<FluidHolder> fluidHolderList = getFluidMap(drawerDataList);
        if (fluidHolderList.size() > tank) {
            FluidHolder holder = fluidHolderList.get(tank);
            return stack.isFluidEqual(holder.fluid()) &&
                    stack.getAmount() + holder.fluidAmount() <= holder.tankCapacity();
        } else {
            return false;
        }
    }


    // when action.execute, can't give out the fluid stack, or something bad would happen
    // note it's just an address, so can't let others can change value directly
    @Nonnull
    @Override
    public FluidStack drain(FluidStack resource, IFluidHandler.FluidAction action) {
        if (resource.isEmpty()) {
            return FluidStack.EMPTY;
        }

        List<BlockEntityFluidDrawer.FluidDrawerData> drawerDataList = getFluidDrawerDataList();

        int toDrain = resource.getAmount();
        int drained = 0;

        for (int i = 0; i < drawerDataList.size() && toDrain > 0; i++) {
            BlockEntityFluidDrawer.betterFluidHandler handler = drawerDataList.get(i).getTank();

            if (handler.getFluid().isFluidEqual(resource)) {
                FluidStack temp = handler.drain(toDrain, action);
                if (!temp.isEmpty()) {
                    toDrain -= temp.getAmount();
                    drained += temp.getAmount();
                }
            }
        }

        return new FluidStack(resource, drained);
    }

    @Nonnull
    @Override
    public FluidStack drain(int maxDrain, IFluidHandler.FluidAction action) {

        List<BlockEntityFluidDrawer.FluidDrawerData> drawerDataList = getFluidDrawerDataList();

        FluidStack result = FluidStack.EMPTY;
        for (int i = 0; i < drawerDataList.size() && maxDrain > 0; i++) {
            BlockEntityFluidDrawer.betterFluidHandler handler = drawerDataList.get(i).getTank();

            if (handler.getFluid().isEmpty())
                continue;
            if (!result.isEmpty() && !handler.getFluid().isFluidEqual(result))
                continue;

            FluidStack temp = handler.drain(maxDrain, action);

            if (temp.getAmount() > 0) {
                if (result.isEmpty())
                    result = temp;
                else result.grow(temp.getAmount());
                maxDrain -= temp.getAmount();
            }
        }

        return result;
    }
}

