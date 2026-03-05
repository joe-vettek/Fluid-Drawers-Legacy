package xueluoanping.fluiddrawerslegacy.block.blockentity;

import com.jaquadro.minecraft.storagedrawers.api.storage.*;
import com.jaquadro.minecraft.storagedrawers.api.storage.attribute.LockAttribute;
import com.jaquadro.minecraft.storagedrawers.block.tile.BaseBlockEntity;
// import com.jaquadro.minecraft.storagedrawers.block.tile.ChamTileEntity;
// import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityDrawers;
// import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityDrawersStandard;
import com.jaquadro.minecraft.storagedrawers.block.tile.BlockEntityController;
import com.jaquadro.minecraft.storagedrawers.block.tile.tiledata.BlockEntityDataShim;
import com.jaquadro.minecraft.storagedrawers.block.tile.tiledata.ControllerData;
import com.jaquadro.minecraft.storagedrawers.block.tile.tiledata.UpgradeData;
import com.jaquadro.minecraft.storagedrawers.capabilities.BasicDrawerAttributes;
import com.jaquadro.minecraft.storagedrawers.config.ModCommonConfig;
import com.jaquadro.minecraft.storagedrawers.core.ModItems;
import com.jaquadro.minecraft.storagedrawers.item.ItemUpgradeRemote;
import com.jaquadro.minecraft.storagedrawers.item.ItemUpgradeStorage;
import com.texelsaurus.minecraft.chameleon.capabilities.ChameleonCapability;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.common.capabilities.Capability;

import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xueluoanping.fluiddrawerslegacy.ModContents;
import xueluoanping.fluiddrawerslegacy.api.drawer.IFluidDrawerGroup;
import xueluoanping.fluiddrawerslegacy.api.drawer.betterFluidManager;
import xueluoanping.fluiddrawerslegacy.api.drawer.IFluidDrawer;
import xueluoanping.fluiddrawerslegacy.client.render.FluidAnimation;
import xueluoanping.fluiddrawerslegacy.config.General;
import xueluoanping.fluiddrawerslegacy.util.RegisterFinderUtil;

import javax.annotation.Nonnull;
import java.util.EnumSet;

@SuppressWarnings("unused")
public class BlockEntityFluidDrawer extends BaseBlockEntity implements INetworked, IFluidDrawerGroup {

    private final BasicDrawerAttributes drawerAttributes = new DrawerAttributes();

    private final FluidGroupData fluidGroupData;
    private final UpgradeData upgradeData = new BlockEntityFluidDrawer.DrawerUpgradeData();
    private final LazyOptional<?> capabilityGroup = LazyOptional.of(this::getGroup);

    private final ControllerData controllerData = new ControllerData();

    public FluidAnimation fluidAnimation = new FluidAnimation();


    @SuppressWarnings("OptionalGetWithoutIsPresent")
    public BlockEntityFluidDrawer(int slotCount, BlockPos pos, BlockState state) {
        super(ModContents.DRBlockEntities.getEntries().stream().filter(
                blockEntityTypeRegistryObject -> RegisterFinderUtil.getBlockKey(state.getBlock()).equals(blockEntityTypeRegistryObject.getId())
        ).findFirst().get().get(), pos, state);
        this.fluidGroupData = new FluidGroupData(slotCount, this);

        this.injectPortableData(fluidGroupData);

        this.upgradeData.setDrawerAttributes(this.drawerAttributes);
        this.injectPortableData(this.upgradeData);
        injectPortableData(controllerData);
    }

    private void checkBoundController() {

        BlockEntityController controller = controllerData.getController(this);
        ItemStack remote = upgradeData.getRemoteUpgrade();
        if (remote == null && controller != null) {
            controller.invalidateRemoteNode(this);
            controllerData.bind(null);
            return;
        }

        if (remote != null && remote.getItem() instanceof ItemUpgradeRemote itemRemote) {
            BlockEntityController upgradeController = ItemUpgradeRemote.getBoundController(remote, level);
            if (controller != null && controller != upgradeController)
                controller.invalidateRemoteNode(this);

            if (upgradeController != null) {
                controllerData.bind(upgradeController);
                if (!upgradeController.addRemoteNode(this))
                    controllerData.bind(null);
            }

            if (itemRemote.isBound() && controllerData.getController(this) == null)
                upgradeData.unbindRemoteUpgrade();
        }
    }

    @Override
    public boolean supportsDirectControllerLink() {
        return true;
    }

    @Override
    public IControlGroup getBoundControlGroup() {
        return controllerData.getController(this);
    }

    @Override
    public boolean canRecurseSearch() {
        ItemStack upgrade = upgradeData.getRemoteUpgrade();
        if (upgrade == null)
            return true;

        if (upgrade.getItem() instanceof ItemUpgradeRemote item)
            return item.isGroupUpgrade();

        return true;
    }

    @Override
    public void unbindControlGroup() {
        upgradeData.unbindRemoteUpgrade();
    }


    public IDrawerGroup getGroup() {
        return this.fluidGroupData;
    }

    protected void onAttributeChanged() {
        this.requestModelDataUpdate();
    }

    public void inventoryChanged() {
        super.setChanged();
        if (level != null) {
            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), Block.UPDATE_ALL);
        }
    }

    @Override
    public int getDrawerCount() {
        return fluidGroupData.getDrawerCount();
    }

    @NotNull
    @Override
    public FluidDrawerData getDrawer(int i) {
        return fluidGroupData.getDrawer(i);
    }

    @Override
    public int @NotNull [] getAccessibleDrawerSlots() {
        return new int[0];
    }


    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> capability, @Nullable Direction side) {
        if (capability == ForgeCapabilities.FLUID_HANDLER) {
            return fluidGroupData.tankHandler.cast();
        }
        return super.getCapability(capability, side);
    }

    @Override
    public CompoundTag writePortable(CompoundTag tag) {
        tag = super.writePortable(tag);
        EnumSet<LockAttribute> attrs = EnumSet.noneOf(LockAttribute.class);
        if (this.drawerAttributes.isItemLocked(LockAttribute.LOCK_EMPTY))
            attrs.add(LockAttribute.LOCK_EMPTY);
        if (this.drawerAttributes.isItemLocked(LockAttribute.LOCK_POPULATED))
            attrs.add(LockAttribute.LOCK_POPULATED);
        if (!attrs.isEmpty()) {
            tag.putByte("Lock", (byte) LockAttribute.getBitfield(attrs));
        }
        if (this.drawerAttributes.isConcealed())
            tag.putBoolean("Shr", true);

        if (this.drawerAttributes.isShowingQuantity())
            tag.putBoolean("Qua", true);

        return tag;
    }

    @Override
    public void readPortable(CompoundTag nbt) {
        super.readPortable(nbt);

        if (nbt.contains("Lock")) {
            EnumSet<LockAttribute> attrs = LockAttribute.getEnumSet(nbt.getByte("Lock"));
            if (attrs != null) {
                this.drawerAttributes.setItemLocked(LockAttribute.LOCK_EMPTY, attrs.contains(LockAttribute.LOCK_EMPTY));
                this.drawerAttributes.setItemLocked(LockAttribute.LOCK_POPULATED, attrs.contains(LockAttribute.LOCK_POPULATED));
            }
        } else {
            this.drawerAttributes.setItemLocked(LockAttribute.LOCK_EMPTY, false);
            this.drawerAttributes.setItemLocked(LockAttribute.LOCK_POPULATED, false);
        }
        if (nbt.contains("Shr"))
            this.drawerAttributes.setIsConcealed(nbt.getBoolean("Shr"));
        else
            this.drawerAttributes.setIsConcealed(false);
        if (nbt.contains("Qua"))
            this.drawerAttributes.setIsShowingQuantity(nbt.getBoolean("Qua"));
        else
            this.drawerAttributes.setIsShowingQuantity(false);

    }

    // @Override
    public IDrawerAttributes getDrawerAttributes() {
        return this.drawerAttributes;
    }

    public betterFluidManager<BlockEntityFluidDrawer> getTank() {
        return this.fluidGroupData.tank;
    }

    private static int getVolume() {
        return General.volume.get();
    }

    private int getCapacityStandard() {
        return (int) (General.volume.get() * (isHalf() ? 0.5 : 1));
    }


    public int getCapacityEffective() {
        if (upgrades().hasVendingUpgrade() || upgrades().hasUnlimitedUpgrade())
            return Integer.MAX_VALUE;
        if (upgrades().hasOneStackUpgrade())
            return FluidType.BUCKET_VOLUME;
        return getCapacityStandard() * upgrades().getStorageMultiplier();
    }

    public int getCapacityTankEffective() {
        return getCapacityEffective() / getDrawerCount();
    }

    public int getCapacityTankStandard() {
        return getCapacityStandard() / getDrawerCount();
    }

    public int getCapacityUsed() {
        int used = 0;
        for (FluidDrawerData data : fluidGroupData.slots) {
            used += data.getTank().getFluidAmount();
        }
        return used;
    }


    public static int calculateTankCapacityFromStack(ItemStack stack) {
        int tankCapacity = getVolume();
        var tag = stack.getTag();
        if (tag != null) {
            if (tag.contains("tanks")) {
                int size = tag.getList("tanks", ListTag.TAG_COMPOUND).size();
                if (size > 0)
                    tankCapacity /= size;
            }
            var up = new UpgradeData(7);
            up.setDrawerAttributes(new IDrawerAttributesModifiable() {});
            up.read(tag);
            int mul = up.getStorageMultiplier();
            tankCapacity *= mul;
            if (stack.getItem().getDescriptionId().contains("half"))
                tankCapacity /= 2;
            if (up.hasVendingUpgrade() || up.hasUnlimitedUpgrade())
                tankCapacity = Integer.MAX_VALUE;
            if (up.hasOneStackUpgrade())
                tankCapacity = FluidType.BUCKET_VOLUME;
        }
        return tankCapacity;
    }



    public int getRedstoneLevel() {
        return (int) (((float) getCapacityUsed() / (float) getCapacityEffective()) * 15);
    }


    public boolean isRedstone() {
        return upgrades().getRedstoneType() != null;
    }


    public UpgradeData upgrades() {
        return this.upgradeData;
    }

    public boolean isHalf() {
        return getBlockState().getBlock().getDescriptionId().contains("half");
    }

    public class FluidGroupData extends BlockEntityDataShim implements IFluidDrawerGroup {

        private final LazyOptional<?> attributesHandler = LazyOptional.of(BlockEntityFluidDrawer.this::getDrawerAttributes);
        public final betterFluidManager<BlockEntityFluidDrawer> tank;
        private final LazyOptional<betterFluidManager<BlockEntityFluidDrawer>> tankHandler;
        private final FluidDrawerData[] slots;

        public FluidGroupData(int slotCount, BlockEntityFluidDrawer blockEntityFluidDrawer) {
            super();
            this.slots = new FluidDrawerData[slotCount];

            for (int i = 0; i < slotCount; ++i) {
                this.slots[i] = this.createDrawer(i);
            }
            tank = createFluidHandler(blockEntityFluidDrawer);
            tankHandler = LazyOptional.of(() -> tank);

        }

        private betterFluidManager<BlockEntityFluidDrawer> createFluidHandler(BlockEntityFluidDrawer blockEntityFluidDrawer) {
            return new betterFluidManager<>(blockEntityFluidDrawer);
        }


        protected FluidDrawerData createDrawer(int slot) {
            return new FluidDrawerData(this, slot, getCapacityStandard() / this.slots.length);
        }

        @Override
        public int getDrawerCount() {
            return this.slots.length;
        }

        @Override
        public @NotNull FluidDrawerData getDrawer(int i) {
            return this.slots[i];
        }

        @Override
        public int[] getAccessibleDrawerSlots() {
            return new int[0];
        }

        @Override
        public boolean isGroupValid() {
            return !BlockEntityFluidDrawer.this.isRemoved();
        }

        @Override
        public <T> T getCapability(ChameleonCapability<T> capability) {
            return capability != null && BlockEntityFluidDrawer.this.level != null ?
                    capability.getCapability(BlockEntityFluidDrawer.this.level, BlockEntityFluidDrawer.this.getBlockPos()) : null;
        }

        @Override
        public CompoundTag write(CompoundTag tag) {
            upgradeData.write(tag);

            ListTag tankList = new ListTag();
            for (FluidDrawerData data : this.slots) {
                tankList.add(data.serializeNBT());
            }
            tag.put("tanks", tankList);

            // inventoryChanged();
            // If want to camouflage, pay attention to setting the capacity first, but we don't need it.
            return tag;
        }


        @Override
        public void read(CompoundTag nbt) {
            // if(!getLevel().isClientSide())
            //     FluidDrawersLegacyMod.logger(getLevel().isClientSide()+"");
            // upgrades must first,to adjust the capacity
            upgrades().read(nbt);
            // FluidDrawersLegacyMod.logger("read"+nbt.toString());

            if (nbt.contains("tank")) {
                this.slots[0].deserializeNBT(nbt.getCompound("tank"));
            } else if (nbt.contains("tanks")) {
                var tankList = nbt.getList("tanks", ListTag.TAG_COMPOUND);
                for (int i = 0; i < tankList.size(); i++) {
                    this.slots[i].deserializeNBT(tankList.getCompound(i));
                }
            }
            // inventoryChanged();
        }

        public boolean idVoidUpgrade() {
            return getDrawerAttributes().isVoid();
        }

    }

    // IDrawer,
    public class FluidDrawerData implements IFluidDrawer<betterFluidHandler>, INBTSerializable<CompoundTag> {
        private final int slot;
        private final FluidGroupData group;
        private final betterFluidHandler tank;
        public FluidAnimation fluidAnimation = new FluidAnimation();

        public FluidDrawerData(FluidGroupData group, int slot, int ca) {
            super();
            this.group = group;
            this.slot = slot;
            this.tank = createFluidHandler(ca);
        }

        private betterFluidHandler createFluidHandler(int ca) {
            return new betterFluidHandler(ca);
        }

        public BlockPos getDrawerPos() {
            return getBlockPos();
        }

        public betterFluidHandler getTank() {
            return tank;
        }


        @Override
        public FluidStack getCacheFluid() {
            return getTank().getCacheFluid();
        }

        @Override
        public FluidAnimation getFluidAnimation() {
            return this.fluidAnimation;
        }


        public boolean isLock() {
            return BlockEntityFluidDrawer.this.getDrawerAttributes().isItemLocked(LockAttribute.LOCK_EMPTY);
        }

        public boolean isVoid() {
            return upgrades().serializeNBT().toString().contains("void");
        }


        public int getMaxTankCapacity() {
            return getCapacityTankEffective();
        }

        @Override
        public CompoundTag serializeNBT() {
            return tank.serializeNBT();
        }

        @Override
        public void deserializeNBT(CompoundTag nbt) {
            tank.deserializeNBT(nbt);
        }
    }

    public class betterFluidHandler extends FluidTank {
        private FluidStack cacheFluid = FluidStack.EMPTY;

        // not allow change here
        public FluidStack getCacheFluid() {
            return cacheFluid.copy();
        }

        private void setCacheFluid(FluidStack cacheFluid) {
            FluidStack cacheFluidCopy = cacheFluid.copy();
            if (!cacheFluidCopy.isEmpty())
                cacheFluidCopy.setAmount(1);
            this.cacheFluid = cacheFluidCopy;
        }

        public betterFluidHandler(int capacity) {
            super(capacity);
        }

        @NotNull
        @Override
        public FluidStack getFluid() {
            if (upgrades().hasVendingUpgrade() && this.fluid.getFluid() != Fluids.EMPTY) {
                return new FluidStack(super.getFluid(), Integer.MAX_VALUE);
            }
            return super.getFluid();
        }

        public CompoundTag serializeNBT() {
            // resize capacity when sending message
            if (this.getCapacity() != BlockEntityFluidDrawer.this.getCapacityTankEffective())
                this.setCapacity(BlockEntityFluidDrawer.this.getCapacityTankEffective());
            CompoundTag nbt = new CompoundTag();

            nbt.put("cache", cacheFluid.writeToNBT(new CompoundTag()));
            return writeToNBT(nbt);
        }

        public void deserializeNBT(CompoundTag tank) {
            if (this.getCapacity() != BlockEntityFluidDrawer.this.getCapacityTankEffective())
                this.setCapacity(BlockEntityFluidDrawer.this.getCapacityTankEffective());
            if (tank.contains("cache")) {
                FluidStack cacheTempStack = FluidStack.loadFluidStackFromNBT(tank.getCompound("cache"));
                setCacheFluid(cacheTempStack);
            }
            readFromNBT(tank);
        }

        // need to override ,or not sync
        @Override
        protected void onContentsChanged() {

            inventoryChanged();

            super.onContentsChanged();
        }

        public boolean isFull() {
            return this.getFluidAmount() == this.getCapacity();
        }

        @Override
        public int fill(FluidStack resource, FluidAction action) {

            if (upgrades().hasVendingUpgrade())
                return 0;
            if (getDrawerAttributes().isItemLocked(LockAttribute.LOCK_EMPTY)) {
                if (getCacheFluid().getRawFluid() == Fluids.EMPTY || !getCacheFluid().isFluidEqual(resource)) {
                    return 0;
                }
            }
            if ((this.getCapacity() - fluid.getAmount() - resource.getAmount()) < 0
                    && upgrades().write(new CompoundTag()).toString().contains("storagedrawers:void_upgrade")) {
                if (resource.isEmpty() || !isFluidValid(resource)) {
                    return 0;
                }
                if (action.simulate()) {
                    return resource.getAmount();
                }
                if (fluid.isEmpty()) {
                    fluid = new FluidStack(resource, Math.min(capacity, resource.getAmount()));
                    setCacheFluid(resource);
                    onContentsChanged();
                    return fluid.getAmount();
                }
                if (!fluid.isFluidEqual(resource)) {
                    return 0;
                }
                fluid.setAmount(capacity);
                onContentsChanged();
                return resource.getAmount();
            } else {
                boolean wasEmpty = fluid.isEmpty();
                int filled = super.fill(resource, action);
                if (wasEmpty && filled > 0) {
                    setCacheFluid(resource);
                }
                return filled;
            }
        }

        @Nonnull
        @Override
        public FluidStack drain(FluidStack resource, FluidAction action) {
            if (upgrades().hasVendingUpgrade())
                return resource.isFluidEqual(fluid) ?
                        new FluidStack(fluid.getFluid(), resource.getAmount()) :
                        FluidStack.EMPTY;
            return super.drain(resource, action);
        }

        @Nonnull
        @Override
        public FluidStack drain(int maxDrain, FluidAction action) {
            if (upgrades().hasVendingUpgrade())
                return new FluidStack(fluid.getFluid(), maxDrain);
            return super.drain(maxDrain, action);
        }
    }


    // extra attributes, not very useful
    private class DrawerAttributes extends BasicDrawerAttributes {
        private DrawerAttributes() {
        }


        protected void onAttributeChanged() {

            BlockEntityFluidDrawer.this.onAttributeChanged();
            if (BlockEntityFluidDrawer.this.getLevel() != null
                    && !BlockEntityFluidDrawer.this.getLevel().isClientSide) {
                BlockEntityFluidDrawer.this.setChanged();
                BlockEntityFluidDrawer.this.markBlockForUpdate();
            }

        }

        @Override
        public boolean isUnlimitedVending() {
            return upgrades().hasVendingUpgrade();
        }

        @Override
        public boolean setItemLocked(LockAttribute attr, boolean isLocked) {
            return super.setItemLocked(attr, isLocked);
        }
    }

    private class DrawerUpgradeData extends UpgradeData {
        DrawerUpgradeData() {
            super(7);
        }

        public boolean canAddUpgrade(@Nonnull ItemStack upgrade) {
            if (!super.canAddUpgrade(upgrade)) {
                return false;
            } else {
                if (upgrade.getItem() == ModItems.FILL_LEVEL_UPGRADE.get())
                    return false;
                else if (upgrade.getItem() == ModItems.ONE_STACK_UPGRADE.get()) {
                    if (upgrades().hasOneStackUpgrade())
                        return false;

                    for (int i = 0; i < getDrawerCount(); i++) {
                        var tank = getDrawer(i).getTank();
                        if (tank.getFluidAmount() >= tank.getCapacity() / 32)
                            return false;
                    }
                }

                return true;
            }
        }

        public boolean canRemoveUpgrade(int slot) {
            if (!super.canRemoveUpgrade(slot)) {
                return false;
            } else {

                ItemStack upgrade = this.getUpgrade(slot);
                if (upgrade.getItem() instanceof ItemUpgradeStorage) {
                    int storageLevel = ((ItemUpgradeStorage) upgrade.getItem()).level.getLevel();
                    int storageMulti = ModCommonConfig.INSTANCE.UPGRADES.getLevelMult(storageLevel);
                    int effectiveStorageMulti = BlockEntityFluidDrawer.this.upgrades().getStorageMultiplier();
                    // 单个物品特殊处理
                    if (effectiveStorageMulti == storageMulti) {
                        --storageMulti;
                    }

                    for (int i = 0; i < getDrawerCount(); i++) {
                        int amount = getDrawer(i).getTank().getFluidAmount();
                        int standardCapacity = getCapacityTankStandard();
                        int afterCapacity = standardCapacity * (effectiveStorageMulti - storageMulti);
                        if (afterCapacity < amount)
                            return false;
                    }
                }

                return true;
            }
        }

        protected void onUpgradeChanged(ItemStack oldUpgrade, ItemStack newUpgrade) {
            if (BlockEntityFluidDrawer.this.getLevel() != null && !BlockEntityFluidDrawer.this.getLevel().isClientSide) {
                checkBoundController();
                if (getBoundControlGroup() != null)
                    getBoundControlGroup().addRemoteNode(BlockEntityFluidDrawer.this);

                BlockEntityFluidDrawer.this.setChanged();
                BlockEntityFluidDrawer.this.markBlockForUpdate();
            }

        }
    }
}
