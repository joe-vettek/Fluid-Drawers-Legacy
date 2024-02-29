package xueluoanping.fluiddrawerslegacy.block.blockentity;

import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawerAttributes;
import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawerAttributesModifiable;
import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawerGroup;
import com.jaquadro.minecraft.storagedrawers.api.storage.attribute.LockAttribute;
import com.jaquadro.minecraft.storagedrawers.block.tile.BaseBlockEntity;
// import com.jaquadro.minecraft.storagedrawers.block.tile.ChamTileEntity;
// import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityDrawers;
// import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityDrawersStandard;
import com.jaquadro.minecraft.storagedrawers.block.tile.tiledata.BlockEntityDataShim;
import com.jaquadro.minecraft.storagedrawers.block.tile.tiledata.UpgradeData;
import com.jaquadro.minecraft.storagedrawers.capabilities.BasicDrawerAttributes;
import com.jaquadro.minecraft.storagedrawers.config.CommonConfig;
import com.jaquadro.minecraft.storagedrawers.core.ModItems;
import com.jaquadro.minecraft.storagedrawers.item.ItemUpgradeStorage;
import com.jaquadro.minecraft.storagedrawers.network.CountUpdateMessage;
import com.jaquadro.minecraft.storagedrawers.network.MessageHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.common.capabilities.Capability;

import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
// import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;
import xueluoanping.fluiddrawerslegacy.ModConstants;
import xueluoanping.fluiddrawerslegacy.ModContents;
import xueluoanping.fluiddrawerslegacy.api.IFluidDrawerGroup;
import xueluoanping.fluiddrawerslegacy.api.betterFluidManager;
import xueluoanping.fluiddrawerslegacy.api.IFluidDrawer;
import xueluoanping.fluiddrawerslegacy.client.render.FluidAnimation;
import xueluoanping.fluiddrawerslegacy.config.General;
import xueluoanping.fluiddrawerslegacy.util.RegisterFinderUtil;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.EnumSet;

public class BlockEntityFluidDrawer extends BaseBlockEntity implements IFluidDrawerGroup {

    private BasicDrawerAttributes drawerAttributes = new DrawerAttributes();

    private FluidGroupData fluidGroupData;
    private final UpgradeData upgradeData = new BlockEntityFluidDrawer.DrawerUpgradeData();
    private final LazyOptional<?> capabilityGroup = LazyOptional.of(this::getGroup);
    //    public static int Capacity = 32000;

    public FluidAnimation fluidAnimation = new FluidAnimation();


    public BlockEntityFluidDrawer(int slotCount, BlockPos pos, BlockState state) {
        super(ModContents.DRBlockEntities.getEntries().stream().filter(blockEntityTypeRegistryObject -> blockEntityTypeRegistryObject.getId().equals(RegisterFinderUtil.getBlockKey(state.getBlock()))).findFirst().get().get(), pos, state);
        this.fluidGroupData = new FluidGroupData(slotCount, this);

        // this.fluidGroupData.setCapabilityProvider(this);
        this.injectPortableData(fluidGroupData);

        this.upgradeData.setDrawerAttributes(this.drawerAttributes);
        this.injectPortableData(this.upgradeData);
//        FluidDrawersLegacyMod.logger("create tile");
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

    public FluidStack getTankFLuid() {
        return fluidGroupData.tank.getFluidInTank(0);
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


    @Nonnull
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, @Nullable Direction facing) {
        IDrawerGroup group = this.getGroup();
        if (capability == ModConstants.DRAWER_GROUP_CAPABILITY) {
            return this.capabilityGroup.cast();
        } else {
            if (getGroup() == null) {
                return super.getCapability(capability, facing);
            }
            LazyOptional<T> cap = this.getGroup().getCapability(capability, facing);
            return cap.isPresent() ? cap : super.getCapability(capability, facing);
        }
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

    protected void syncClientCount(int slot, int count) {
        if (this.getLevel() == null || !this.getLevel().isClientSide) {
            PacketDistributor.TargetPoint point = new PacketDistributor.TargetPoint((double) this.getBlockPos().getX(), (double) this.getBlockPos().getY(), (double) this.getBlockPos().getZ(), 500.0D, this.getLevel().dimension());
            MessageHandler.INSTANCE.send(PacketDistributor.NEAR.with(() -> {
                return point;
            }), new CountUpdateMessage(this.getBlockPos(), slot, count));
        }
    }

    public betterFluidManager<BlockEntityFluidDrawer> getTank() {
        return this.fluidGroupData.tank;
    }

    private static int getCapacityStandard() {
        return General.volume.get();
    }


    public int getCapacityEffective() {
        if (upgrades().hasVendingUpgrade() || upgrades().hasUnlimitedUpgrade())
            return Integer.MAX_VALUE;
        if (upgrades().hasOneStackUpgrade())
            return getCapacityStandard() / 32;
        return getCapacityStandard() * upgrades().getStorageMultiplier();
    }

    public int getCapacityTank() {
        return getCapacityEffective() / getDrawerCount();
    }

    public int getCapacityUsed() {
        int used = 0;
        for (FluidDrawerData data : fluidGroupData.slots) {
            used += data.getTank().getFluidAmount();
        }
        return used;
    }


    public static int calcultaeTankCapacitybyStack(ItemStack stack) {
        if (!stack.getOrCreateTag().contains("Upgrades"))
            return getCapacityStandard();
        else {
            int i = 1;
//            use 7 now, just need to setDrawerAttributes with a default one
            UpgradeData tmpData = new UpgradeData(7);
            tmpData.setDrawerAttributes(new IDrawerAttributesModifiable() {
            });
            CompoundTag tag = new CompoundTag();
            tag.put("Upgrades", stack.getOrCreateTag().get("Upgrades"));
            tmpData.deserializeNBT(tag);

//            FluidDrawersLegacyMod.logger(tmpData.hasVendingUpgrade()+"");
            i = tmpData.getStorageMultiplier();
            if (tmpData.hasVendingUpgrade() || tmpData.hasUnlimitedUpgrade())
                return Integer.MAX_VALUE;
            if (tmpData.hasOneStackUpgrade())
                return getCapacityStandard() / 32;
            return getCapacityStandard() * i;
        }
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
            tank = createFuildHandler(blockEntityFluidDrawer);
            tankHandler = LazyOptional.of(() -> tank);

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

        @Nonnull
        @Override
        public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, @Nullable Direction facing) {

            if (capability == ModConstants.DRAWER_ATTRIBUTES_CAPABILITY)
                return attributesHandler.cast();
            if (capability == ForgeCapabilities.FLUID_HANDLER) {
//                inventoryChanged();
                if (facing == null) {
//                    FluidDrawersLegacyMod.LOGGER.info(getLevel().toString() + facing+tank.serializeNBT());
                }
                return tankHandler.cast();

            }
//            return super.getCapability(capability, facing);
            return LazyOptional.empty();
        }


        @Override
        public CompoundTag write(CompoundTag tag) {

//            FluidDrawersLegacyMod.logger(tag.toString());
//             tag.put("tank", tank.serializeNBT());
            upgradeData.write(tag);
            // if (getDrawerCount() == 1)
            //     tag.put("tank", this.slots[0].serializeNBT());
            // else
            {
                ListTag tanklist = new ListTag();
                for (FluidDrawerData data : this.slots) {
                    tanklist.add(data.serializeNBT());
                }
                tag.put("tanks", tanklist);
            }
//            inventoryChanged();
            //            If want to camouflage, pay attention to setting the capacity first, but we don't need it.
            return tag;
        }


        @Override
        public void read(CompoundTag nbt) {
//            if(!getLevel().isClientSide())
//            FluidDrawersLegacyMod.logger(getLevel().isClientSide()+"");
//            upgrades must first,to adjust the capacity
            upgrades().read(nbt);
//            FluidDrawersLegacyMod.logger("read"+nbt.toString());

            if (nbt.contains("tank")) {
                this.slots[0].deserializeNBT(nbt.getCompound("tank"));
            } else if (nbt.contains("tanks")) {
                var tanklist=nbt.getList("tanks",ListTag.TAG_COMPOUND);
                for (int i = 0; i < tanklist.size(); i++) {
                    this.slots[i].deserializeNBT(tanklist.getCompound(i));
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
        public FluidAnimation fluidAnimation=new FluidAnimation();

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


        // @Override
        public int getMaxTankCapacity() {
            return getCapacityTank();
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
//                FluidStack stack = fluid.copy();
//                stack.setAmount(Integer.MAX_VALUE);
                return new FluidStack(super.getFluid(), Integer.MAX_VALUE);
            }
            return super.getFluid();
        }

        public CompoundTag serializeNBT() {
            // resize capacity when sending message
            if (this.getCapacity() != BlockEntityFluidDrawer.this.getCapacityTank())
                this.setCapacity(BlockEntityFluidDrawer.this.getCapacityTank());
            CompoundTag nbt = new CompoundTag();
            if (getCacheFluid().getRawFluid() != Fluids.EMPTY &&
                    fluid.getFluid() != Fluids.EMPTY &&
                    getCacheFluid().getRawFluid() != fluid.getFluid()) {
                setCacheFluid(getFluid());
            }
            if (getCacheFluid().getRawFluid() == Fluids.EMPTY &&
                    getFluid().getAmount() > 0) {
                setCacheFluid(getFluid());

            }

            // nbt.putString("cache", cacheFluid.getFluidType().toString());
            nbt.put("cache", cacheFluid.writeToNBT(new CompoundTag()));
            return writeToNBT(nbt);
        }

        public void deserializeNBT(CompoundTag tank) {
            if (this.getCapacity() != BlockEntityFluidDrawer.this.getCapacityTank())
                this.setCapacity(BlockEntityFluidDrawer.this.getCapacityTank());
            if (tank.contains("cache")) {
                FluidStack cacheTempStack = FluidStack.loadFluidStackFromNBT(tank.getCompound("cache"));
                setCacheFluid(cacheTempStack);
            }
            readFromNBT(tank);
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
                if (getCacheFluid().getRawFluid() != Fluids.EMPTY
                        && !getCacheFluid().isFluidEqual(resource)) {
                    return 0;
                }
                if (getCacheFluid().getRawFluid() == Fluids.EMPTY) {
                    if (resource.getAmount() > 0) {
                        if (action.execute())
                            setCacheFluid(resource);
                        return super.fill(resource, action);
                    } else return 0;

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
                    onContentsChanged();
                    return fluid.getAmount();
                }
                if (!fluid.isFluidEqual(resource)) {
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
        public FluidStack drain(int maxDrain, FluidAction action) {
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
                if (upgrade.getItem() == ModItems.FILL_LEVEL_UPGRADE.get()) return false;
                else if (upgrade.getItem() == ModItems.ONE_STACK_UPGRADE.get()) {
                    if (upgrades().hasOneStackUpgrade())
                        return false;

                    for (int i = 0; i < getDrawerCount(); i++) {
                        if (getDrawer(i).getTank().getFluidAmount() >= getCapacityTank() / 32)
                            return false;
                    }

//                    int lostStackCapacity = getCapacityStandard() * upgrades().getStorageMultiplier();
//
//                    if (!this.stackCapacityCheck(lostStackCapacity)) {
//                        return false;
//                    }
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
                    int storageMult = CommonConfig.UPGRADES.getLevelMult(storageLevel);
                    int effectiveStorageMult = BlockEntityFluidDrawer.this.upgrades().getStorageMultiplier();
//                    单个物品特殊处理，
                    if (effectiveStorageMult == storageMult) {
                        --storageMult;
                    }

                    for (int i = 0; i < getDrawerCount(); i++) {
                        int amount = getDrawer(i).getTank().getFluidAmount();
                        int standardCapacity = getCapacityTank();
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
