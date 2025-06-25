package xueluoanping.fluiddrawerslegacy.block.blockentity;

import com.jaquadro.minecraft.storagedrawers.api.capabilities.IDrawerCapabilityProvider;
import com.jaquadro.minecraft.storagedrawers.api.storage.*;
import com.jaquadro.minecraft.storagedrawers.api.storage.attribute.LockAttribute;
import com.jaquadro.minecraft.storagedrawers.block.tile.BaseBlockEntity;
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
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;

// import net.minecraftforge.fluids.capability.CapabilityFluidHandler;

import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.common.util.INBTSerializable;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.NotNull;
import xueluoanping.fluiddrawerslegacy.ModContents;
import xueluoanping.fluiddrawerslegacy.api.drawer.IFluidDrawerGroup;
import xueluoanping.fluiddrawerslegacy.api.drawer.betterFluidManager;
import xueluoanping.fluiddrawerslegacy.api.drawer.IFluidDrawer;
import xueluoanping.fluiddrawerslegacy.client.render.FluidAnimation;
import xueluoanping.fluiddrawerslegacy.config.General;
import xueluoanping.fluiddrawerslegacy.util.RegisterFinderUtil;

import javax.annotation.Nonnull;
import java.util.EnumSet;

public class BlockEntityFluidDrawer extends BaseBlockEntity implements IFluidDrawerGroup, IDrawerCapabilityProvider, INetworked {

    private final BasicDrawerAttributes drawerAttributes = new DrawerAttributes();

    private final FluidGroupData fluidGroupData;
    private final UpgradeData upgradeData = new BlockEntityFluidDrawer.DrawerUpgradeData();
    private final ControllerData controllerData = new ControllerData();

    // private final LazyOptional<?> capabilityGroup = LazyOptional.of(this::getGroup);
    //    public static int Capacity = 32000;

    public FluidAnimation fluidAnimation = new FluidAnimation();


    public BlockEntityFluidDrawer(int slotCount, BlockPos pos, BlockState state) {
        super(ModContents.DRBlockEntities.getEntries().stream().filter(blockEntityTypeRegistryObject -> blockEntityTypeRegistryObject.getId().equals(RegisterFinderUtil.getBlockKey(state.getBlock()))).findFirst().get().get(), pos, state);
        this.fluidGroupData = new FluidGroupData(slotCount, this);

        // this.fluidGroupData.setCapabilityProvider(this);
        this.injectPortableData(fluidGroupData);

        this.upgradeData.setDrawerAttributes(this.drawerAttributes);
        this.injectPortableData(this.upgradeData);
        this. injectPortableData(this.controllerData);
        //        FluidDrawersLegacyMod.logger("create tile");
    }

    private void checkBoundController () {
        BlockEntityController controller = controllerData.getController(this);
        ItemStack remote = upgradeData.getRemoteUpgrade();
        if (remote == null && controller != null) {
            controller.invalidateRemoteNode(this);
            controllerData.bind(null);
            return;
        }

        if (remote != null && remote.getItem() instanceof ItemUpgradeRemote itemRemote) {
            BlockEntityController upgradeController = itemRemote.getBoundController(remote, level);
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

    // public void validateBoundController() {
    //     checkBoundController();
    // }

    @Override
    public boolean supportsDirectControllerLink () {
        return true;
    }

    @Override
    public IControlGroup getBoundControlGroup () {
        return controllerData.getController(this);
    }

    @Override
    public boolean canRecurseSearch () {
        ItemStack upgrade = upgradeData.getRemoteUpgrade();
        if (upgrade == null)
            return true;
        if (upgrade.getItem() instanceof ItemUpgradeRemote item)
            return item.isGroupUpgrade();
        return true;
    }

    @Override
    public void unbindControlGroup () {
        upgradeData.unbindRemoteUpgrade();
    }
    //    @Override
    public IDrawerGroup getGroup() {
        return this.fluidGroupData;
    }

    //    @Override
    protected void onAttributeChanged() {
        //        super.onAttributeChanged();
        this.requestModelDataUpdate();
        // fluidGroupData.syncAttributes();
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
    public IFluidDrawer getDrawer(int i) {
        return fluidGroupData.getDrawer(i);
    }

    @Override
    public int @NotNull [] getAccessibleDrawerSlots() {
        return new int[0];
    }


    // @Nonnull
    // public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, @Nullable Direction facing) {
    //     IDrawerGroup group = this.getGroup();
    //     if (capability == ModConstants.DRAWER_GROUP_CAPABILITY) {
    //         return this.capabilityGroup.cast();
    //     } else {
    //         if (getGroup() == null) {
    //             return super.getCapability(capability, facing);
    //         }
    //         LazyOptional<T> cap = this.getGroup().getCapability(capability, facing);
    //         return cap.isPresent() ? cap : super.getCapability(capability, facing);
    //     }
    // }

    public <T> T getCapability(@NotNull BlockCapability<T, Void> capability) {
        return this.level == null ? null : this.level.getCapability(capability, this.getBlockPos(), this.getBlockState(), this, null);
    }

    @Override
    public CompoundTag writePortable(HolderLookup.Provider provider, CompoundTag tag) {
        tag = super.writePortable(provider, tag);
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
    public void readPortable(HolderLookup.Provider provider, CompoundTag nbt) {
        super.readPortable(provider, nbt);

        if (nbt.contains("Lock")) {
            EnumSet<LockAttribute> attrs = LockAttribute.getEnumSet(nbt.getByte("Lock"));
            if (attrs != null) {
                this.drawerAttributes.setItemLocked(LockAttribute.LOCK_EMPTY, attrs.contains(LockAttribute.LOCK_EMPTY));
                this.drawerAttributes.setItemLocked(LockAttribute.LOCK_POPULATED, attrs.contains(LockAttribute.LOCK_POPULATED));
                //                    FluidDrawersLegacyMod.logger( attrs.contains(LockAttribute.LOCK_POPULATED)+""+ attrs.contains(LockAttribute.LOCK_EMPTY));
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

    //    @Override
    public IDrawerAttributes getDrawerAttributes() {
        return this.drawerAttributes;
    }

    // protected void syncClientCount(int slot, int count) {
    //     if (this.getLevel() == null || !this.getLevel().isClientSide) {
    //         PacketDistributor.TargetPoint point = new PacketDistributor.TargetPoint((double) this.getBlockPos().getX(), (double) this.getBlockPos().getY(), (double) this.getBlockPos().getZ(), 500.0D, this.getLevel().dimension());
    //         MessageHandler.INSTANCE.send(PacketDistributor.NEAR.with(() -> {
    //             return point;
    //         }), new CountUpdateMessage(this.getBlockPos(), slot, count));
    //     }
    // }


    @Override
    public <T> T getCapability(ChameleonCapability<T> capability) {
        return capability != null && this.level != null ?
                capability.getCapability(this.level, this.getBlockPos()) : null;
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


    public static int calculateTankCapacityFromStack(HolderLookup.Provider provider, ItemStack stack) {
        int tankCapacity = getVolume();
        var tag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        if (tag != null) {
            if (tag.contains("tanks")) {
                int size = tag.getList("tanks", ListTag.TAG_COMPOUND).size();
                if (size > 0)
                    tankCapacity /= size;
            }
            var up = new UpgradeData(7);
            // up.setDrawerAttributes(new IDrawerAttributesModifiable() {
            // });
            up.read(provider, tag);
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


    //    @Override
    public int getRedstoneLevel() {
        //        FluidDrawersLegacyMod.logger(getLevel().toString()+this.isRedstone());
        return (int) (((float) getCapacityUsed() / (float) getCapacityEffective()) * 15);
    }

    //    @Override
    public boolean isRedstone() {
        return upgrades().getRedstoneType() != null;
    }

    //    @Override
    public UpgradeData upgrades() {
        return this.upgradeData;
    }

    public boolean isHalf() {
        return getBlockState().getBlock().getDescriptionId().contains("half");
    }

    public class FluidGroupData extends BlockEntityDataShim implements IFluidDrawerGroup, IDrawerCapabilityProvider {

        // private final LazyOptional<?> attributesHandler = LazyOptional.of(BlockEntityFluidDrawer.this::getDrawerAttributes);
        public final betterFluidManager<BlockEntityFluidDrawer> tank;
        // private final LazyOptional<betterFluidManager<BlockEntityFluidDrawer>> tankHandler;
        private final FluidDrawerData[] slots;

        public FluidGroupData(int slotCount, BlockEntityFluidDrawer blockEntityFluidDrawer) {
            super();
            this.slots = new FluidDrawerData[slotCount];

            for (int i = 0; i < slotCount; ++i) {
                this.slots[i] = this.createDrawer(i);
            }
            tank = createFuildHandler(blockEntityFluidDrawer);
            // tankHandler = LazyOptional.of(() -> tank);
        }

        private betterFluidManager<BlockEntityFluidDrawer> createFuildHandler(BlockEntityFluidDrawer blockEntityFluidDrawer) {
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
        public @NotNull IFluidDrawer getDrawer(int i) {
            return this.slots[i];
        }

        @Override
        public int[] getAccessibleDrawerSlots() {
            return new int[0];
        }

        @Override
        public boolean isGroupValid() {
            //            return TileEntityFluidDrawer.this.isGroupValid();
            return !BlockEntityFluidDrawer.this.isRemoved();
        }

        @Override
        public <T> T getCapability(ChameleonCapability<T> capability) {
            return capability != null && BlockEntityFluidDrawer.this.level != null ?
                    capability.getCapability(BlockEntityFluidDrawer.this.level, BlockEntityFluidDrawer.this.getBlockPos()) : null;
        }



        @Override
        public CompoundTag write(HolderLookup.Provider provider, CompoundTag tag) {
            upgradeData.write(provider, tag);

            ListTag tanklist = new ListTag();
            for (FluidDrawerData data : this.slots) {
                tanklist.add(data.serializeNBT(provider));
            }
            tag.put("tanks", tanklist);

            //            inventoryChanged();
            //            If want to camouflage, pay attention to setting the capacity first, but we don't need it.
            return tag;
        }


        @Override
        public void read(HolderLookup.Provider provider, CompoundTag nbt) {
            //            if(!getLevel().isClientSide())
            //            FluidDrawersLegacyMod.logger(getLevel().isClientSide()+"");
            //            upgrades must first,to adjust the capacity
            upgrades().read(provider, nbt);
            //            FluidDrawersLegacyMod.logger("read"+nbt.toString());

            if (nbt.contains("tank")) {
                this.slots[0].deserializeNBT(provider, nbt.getCompound("tank"));
            } else if (nbt.contains("tanks")) {
                var tanklist = nbt.getList("tanks", ListTag.TAG_COMPOUND);
                for (int i = 0; i < tanklist.size(); i++) {
                    this.slots[i].deserializeNBT(provider, tanklist.getCompound(i));
                }
            }
            //            inventoryChanged();
        }

        public boolean idVoidUpgrade() {
            return getDrawerAttributes().isVoid();
        }
    }

    // IDrawer,
    public class FluidDrawerData implements IFluidDrawer<betterFluidHandler>, INBTSerializable<CompoundTag> {
        private int slot;
        //        private FluidStack fluid = new FluidStack(Fluids.EMPTY, 0);
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
            return upgrades().serializeNBT(level.registryAccess()).toString().contains("void");
        }


        // @Override
        public int getMaxTankCapacity() {
            return getCapacityTankEffective();
        }

        @Override
        public CompoundTag serializeNBT(HolderLookup.Provider provider) {
            return tank.serializeNBT(provider);
        }

        @Override
        public void deserializeNBT(HolderLookup.Provider provider, CompoundTag nbt) {
            tank.deserializeNBT(provider, nbt);
        }

        @Override
        public IDrawer copy() {
            return new FluidDrawerData(group, slot, tank.getCapacity());
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
                //                FluidStack stack = fluid.copy();
                //                stack.setAmount(Integer.MAX_VALUE);
                return new FluidStack(super.getFluid().getFluidHolder(), Integer.MAX_VALUE);
            }
            return super.getFluid();
        }

        public CompoundTag serializeNBT(HolderLookup.Provider provider) {
            // resize capacity when sending message
            if (this.getCapacity() != BlockEntityFluidDrawer.this.getCapacityTankEffective())
                this.setCapacity(BlockEntityFluidDrawer.this.getCapacityTankEffective());
            CompoundTag nbt = new CompoundTag();
            if (getCacheFluid().getFluid() != Fluids.EMPTY &&
                    fluid.getFluid() != Fluids.EMPTY &&
                    getCacheFluid().getFluid() != fluid.getFluid()) {
                setCacheFluid(getFluid());
            }
            if (getCacheFluid().getFluid() == Fluids.EMPTY &&
                    getFluid().getAmount() > 0) {
                setCacheFluid(getFluid());

            }

            // nbt.putString("cache", cacheFluid.getFluidType().toString());

            nbt.put("cache", cacheFluid.saveOptional(provider));
            return writeToNBT(provider, nbt);
        }

        public void deserializeNBT(HolderLookup.Provider provider, CompoundTag tank) {
            if (this.getCapacity() != BlockEntityFluidDrawer.this.getCapacityTankEffective())
                this.setCapacity(BlockEntityFluidDrawer.this.getCapacityTankEffective());
            if (tank.contains("cache")) {
                FluidStack cacheTempStack = FluidStack.parseOptional(provider, tank.getCompound("cache"));
                setCacheFluid(cacheTempStack);
            }
            readFromNBT(provider, tank);
        }

        //        need to override ,or not sync
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
                if (getCacheFluid().getFluid() != Fluids.EMPTY
                        && !FluidStack.isSameFluid(getCacheFluid(), resource)) {
                    return 0;
                }
                if (getCacheFluid().getFluid() == Fluids.EMPTY) {
                    if (resource.getAmount() > 0) {
                        if (action.execute())
                            setCacheFluid(resource);
                        return super.fill(resource, action);
                    } else
                        return 0;

                }
            }
            if ((this.getCapacity() - fluid.getAmount() - resource.getAmount()) < 0
                    && upgrades().write(level.registryAccess(), new CompoundTag()).toString().contains("storagedrawers:void_upgrade")) {
                if (resource.isEmpty() || !isFluidValid(resource)) {
                    return 0;
                }
                if (action.simulate()) {
                    return resource.getAmount();
                }
                if (fluid.isEmpty()) {
                    fluid = new FluidStack(resource.getFluidHolder(), Math.min(capacity, resource.getAmount()));
                    onContentsChanged();
                    return fluid.getAmount();
                }
                if (!FluidStack.isSameFluid(fluid, resource)) {
                    return 0;
                }
                fluid.setAmount(capacity);
                onContentsChanged();
                return resource.getAmount();
            }
            return super.fill(resource, action);
        }

        @Nonnull
        @Override
        public FluidStack drain(FluidStack resource, FluidAction action) {
            if (upgrades().hasVendingUpgrade())
                return resource.getFluid() == fluid.getFluid() ?
                        new FluidStack(fluid.getFluid(), resource.getAmount()) :
                        FluidStack.EMPTY;
            return super.drain(resource, action);
        }

        @Nonnull
        @Override
        public FluidStack drain(int maxDrain, IFluidHandler.FluidAction action) {
            if (upgrades().hasVendingUpgrade())
                return new FluidStack(fluid.getFluid(), maxDrain);
            return super.drain(maxDrain, action);
        }


    }


    //    extra attributes, not very useful
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
            boolean result = super.setItemLocked(attr, isLocked);
            return result;
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
                    int storageMult = ModCommonConfig.INSTANCE.UPGRADES.getLevelMult(storageLevel);
                    int effectiveStorageMult = BlockEntityFluidDrawer.this.upgrades().getStorageMultiplier();
                    //                    单个物品特殊处理，
                    if (effectiveStorageMult == storageMult) {
                        --storageMult;
                    }

                    for (int i = 0; i < getDrawerCount(); i++) {
                        int amount = getDrawer(i).getTank().getFluidAmount();
                        int standardCapacity = getCapacityTankStandard();
                        int afterCapacity = standardCapacity * (effectiveStorageMult - storageMult);
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

        //        private boolean stackCapacityCheck(int stackCapacity) {
        //            return false;
        //        }
        //
        //        @Override
        //        public int getStorageMultiplier() {
        ////            if(hasOneStackUpgrade())return super.getStorageMultiplier()/32;
        //            return super.getStorageMultiplier();
        //        }

    }
}
