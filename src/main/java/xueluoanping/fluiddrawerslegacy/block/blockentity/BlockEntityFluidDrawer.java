package xueluoanping.fluiddrawerslegacy.block.blockentity;

import com.jaquadro.minecraft.storagedrawers.api.event.DrawerPopulatedEvent;
import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawer;
import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawerAttributes;
import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawerAttributesModifiable;
import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawerGroup;
import com.jaquadro.minecraft.storagedrawers.api.storage.attribute.LockAttribute;
import com.jaquadro.minecraft.storagedrawers.block.tile.ChamTileEntity;
import com.jaquadro.minecraft.storagedrawers.block.tile.tiledata.StandardDrawerGroup;
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
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;

import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import xueluoanping.fluiddrawerslegacy.ModConstants;
import xueluoanping.fluiddrawerslegacy.ModContents;
import xueluoanping.fluiddrawerslegacy.config.General;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.EnumSet;

public class BlockEntityFluidDrawer extends ChamTileEntity implements IDrawerGroup {

    private BasicDrawerAttributes drawerAttributes = new DrawerAttributes();

    private GroupData groupData = new GroupData(1);
    private UpgradeData upgradeData = new BlockEntityFluidDrawer.DrawerUpgradeData();
    private final LazyOptional<?> capabilityGroup = LazyOptional.of(this::getGroup);
    //    public static int Capacity = 32000;

    private int lastFluidAmount = 0;
    private int cacheFluidAmount = 0;
    private double lastAnimationTime = 0d;
    private boolean cutStartAnimation = false;

    public BlockEntityFluidDrawer(BlockPos pos, BlockState state) {
        super(ModContents.tankTileEntityType, pos, state);
        this.groupData.setCapabilityProvider(this);
        this.injectPortableData(groupData);

        this.upgradeData.setDrawerAttributes(this.drawerAttributes);
        this.injectPortableData(this.upgradeData);
//        FluidDrawersLegacyMod.logger("create tile");
    }

    private static int getCapacityStandard() {
        return General.volume.get();
    }

//    @Override
    public IDrawerGroup getGroup() {
        return this.groupData;
    }

//    @Override
    protected void onAttributeChanged() {
//        super.onAttributeChanged();
        this.requestModelDataUpdate();
        groupData.syncAttributes();
    }

    public boolean hasNoFluid() {
        return upgrades().write(new CompoundTag()).toString().contains("storagedrawers:void_upgrade") || groupData.tank.getFluidInTank(0).getAmount() == 0;
    }

    public FluidStack getTankFLuid() {
        return groupData.tank.getFluidInTank(0);
    }

    public void inventoryChanged() {
        super.setChanged();
        if (level != null) {
            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), Block.UPDATE_ALL);
        }
    }

    @Override
    public int getDrawerCount() {
        return 0;
    }

    @NotNull
    @Override
    public IDrawer getDrawer(int i) {
        return new StandardDrawerData((StandardDrawerGroup) getGroup(),0);
    }

    @NotNull
    @Override
    public int[] getAccessibleDrawerSlots() {
        return new int[0];
    }

    public void setCutStartAnimation(boolean cutStartAnimation) {
        this.cutStartAnimation = cutStartAnimation;
    }

    public int getAndUpdateLastFluidAmount(double animationTime) {
        int expectFluidAmount = this.groupData.tank.getFluidAmount();
        if (expectFluidAmount != this.lastFluidAmount) {
            int fluidAmountChange = (expectFluidAmount - this.lastFluidAmount);
            boolean isFluidUpdate = expectFluidAmount != cacheFluidAmount;
            boolean hasEnoughFluidAmount = Math.abs(fluidAmountChange) > 200;
            boolean isTooQuickAnimation = isFluidUpdate && animationTime - this.lastAnimationTime < 3;
            // FluidDrawersLegacyMod.logger(lastFluidAmount+""+isFluidUpdate+"Fluid Update,"+isTooQuickAnimation+ "" + "," + this.lastAnimationTime);
            boolean shouldAnimation = hasEnoughFluidAmount && !isTooQuickAnimation && !cutStartAnimation;
            if (shouldAnimation) {
                // this.lastFluidAmount += fluidAmountChange > 0 ? 50 : -50;
                this.lastFluidAmount += fluidAmountChange * 0.125f;
            } else {
                this.lastFluidAmount = expectFluidAmount;
            }
            if (isFluidUpdate) {
                this.lastAnimationTime = animationTime;
            }
            cutStartAnimation=false;
        }
        this.cacheFluidAmount = expectFluidAmount;
        return lastFluidAmount;
    }

    @Nonnull
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, @Nullable Direction facing) {
        IDrawerGroup group = this.getGroup();
        if (capability == ModConstants.DRAWER_GROUP_CAPABILITY) {
            return this.capabilityGroup.cast();
        } else {
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
            PacketDistributor.TargetPoint point = new PacketDistributor.TargetPoint((double)this.getBlockPos().getX(), (double)this.getBlockPos().getY(), (double)this.getBlockPos().getZ(), 500.0D, this.getLevel().dimension());
            MessageHandler.INSTANCE.send(PacketDistributor.NEAR.with(() -> {
                return point;
            }), new CountUpdateMessage(this.getBlockPos(), slot, count));
        }
    }

    public IFluidHandler getTank() {
        return this.groupData.tank;
    }

    public int getTankEffectiveCapacity() {

        if (upgrades().hasVendingUpgrade() || upgrades().hasUnlimitedUpgrade())
            return Integer.MAX_VALUE;
        if (upgrades().hasOneStackUpgrade())
            return getCapacityStandard() / 32;

        return getCapacityStandard() * upgrades().getStorageMultiplier();
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
//    public int getDrawerCapacity() {
//        return 0;
//    }

//    @Override
//    public int getEffectiveDrawerCapacity() {
//        return 0;
//    }

    @Nonnull
//    @Override
//    public IDrawer getDrawer(int slot) {
//        return super.getDrawer(slot);
//    }

//    @Override
    public int getRedstoneLevel() {
//        FluidDrawersLegacyMod.logger(getLevel().toString()+this.isRedstone());
        return (int) (((float) getTankFLuid().getAmount() / (float) getTankEffectiveCapacity()) * 15);
    }

//    @Override
    public boolean isRedstone() {
        return upgrades().getRedstoneType() != null;
    }

//    @Override
    public UpgradeData upgrades() {
        return this.upgradeData;
    }

    public class GroupData extends StandardDrawerGroup {

        private final LazyOptional<?> attributesHandler = LazyOptional.of(BlockEntityFluidDrawer.this::getDrawerAttributes);
        public final betterFluidHandler tank;
        private final LazyOptional<betterFluidHandler> tankHandler;
        public DrawerData drawerData;

        public GroupData(int slotCount) {
            super(slotCount);
            tank = createFuildHandler();
            tankHandler = LazyOptional.of(() -> tank);

        }


        private betterFluidHandler createFuildHandler() {
            return new betterFluidHandler(General.volume.get()) {
            };
        }


        @Nonnull
        @Override
        protected DrawerData createDrawer(int slot) {
            drawerData = new StandardDrawerData(this, slot);
            return drawerData;
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
            if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
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
            tag.put("tank", tank.serializeNBT());


//            inventoryChanged();
            //            If want to camouflage, pay attention to setting the capacity first, but we don't need it.

            return super.write(tag);
        }


        @Override
        public void read(CompoundTag nbt) {

//            if(!getLevel().isClientSide())
//            FluidDrawersLegacyMod.logger(getLevel().isClientSide()+"");


//            upgrades must first,to adjust the capacity
            upgrades().read(nbt);
//            FluidDrawersLegacyMod.logger("read"+nbt.toString());
            if (nbt.contains("tank")) {
                tank.deserializeNBT((CompoundTag) nbt.get("tank"));
            }
//            inventoryChanged();

            super.read(nbt);
        }

        public boolean idVoidUpgrade() {
            return getDrawerAttributes().isVoid();
        }

    }

    public class StandardDrawerData extends StandardDrawerGroup.DrawerData {
        private int slot;
//        private FluidStack fluid = new FluidStack(Fluids.EMPTY, 0);

        public StandardDrawerData(StandardDrawerGroup group, int slot) {
            super(group);
            this.slot = slot;
        }

        public betterFluidHandler getTank() {
            return BlockEntityFluidDrawer.this.groupData.tank;
        }

//        public int getCapacity() {
//            return getCapacityStandard() * upgrades().getStorageMultiplier();
//        }

        @Override
        protected int getStackCapacity() {
//            return upgrades().getStorageMultiplier() * getEffectiveDrawerCapacity();
            return 0;
        }


        @Override
        protected void onItemChanged() {
            DrawerPopulatedEvent event = new DrawerPopulatedEvent(this);
            MinecraftForge.EVENT_BUS.post(event);

            if (getLevel() != null && !getLevel().isClientSide()) {
                setChanged();
                markBlockForUpdate();
            }
        }

        @Override
        protected void onAmountChanged() {
            if (getLevel() != null && !getLevel().isClientSide()) {
                syncClientCount(slot, getStoredItemCount());
                setChanged();

            }
        }

        public boolean isLock() {
            return BlockEntityFluidDrawer.this.getDrawerAttributes().isItemLocked(LockAttribute.LOCK_EMPTY);
        }

        public boolean isVoid() {
            return upgrades().serializeNBT().toString().contains("void");
        }

    }

    public class betterFluidHandler extends FluidTank {
        private Fluid cacheFluid = Fluids.EMPTY;

        public Fluid getCacheFluid() {
            return cacheFluid;
        }

        private void setCacheFluid(Fluid cacheFluid) {
            this.cacheFluid = cacheFluid;
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
                return new FluidStack(super.getFluid().getFluid(), Integer.MAX_VALUE);
            }
            return super.getFluid();
        }

        public CompoundTag serializeNBT() {
//            发送信息时调整容量大小
            if (this.getCapacity() != BlockEntityFluidDrawer.this.getTankEffectiveCapacity())
                this.setCapacity(BlockEntityFluidDrawer.this.getTankEffectiveCapacity());
            CompoundTag nbt = new CompoundTag();
            if (getCacheFluid() != Fluids.EMPTY &&
                    fluid.getFluid() != Fluids.EMPTY &&
                    getCacheFluid() != fluid.getFluid()) {
                setCacheFluid(getFluid().getFluid());

            }
            if (getCacheFluid() == Fluids.EMPTY &&
                    getFluid().getAmount() > 0) {
                setCacheFluid(getFluid().getFluid());

            }

            nbt.putString("cache", cacheFluid.getRegistryName().toString());
            return writeToNBT(nbt);
        }

        public void deserializeNBT(CompoundTag tank) {
            if (this.getCapacity() != BlockEntityFluidDrawer.this.getTankEffectiveCapacity())
                this.setCapacity(BlockEntityFluidDrawer.this.getTankEffectiveCapacity());
            if (tank.contains("cache")) {
                String[] x = tank.getString("cache").split(":");
                ResourceLocation res = new ResourceLocation(x[0], x[1]);

                Fluid fluid = ForgeRegistries.FLUIDS.getValue(res);

                setCacheFluid(fluid);
            }

//            FluidDrawersLegacyMod.LOGGER.info(cacheFluid.getRegistryName()+""+getLevel()+""+drawerAttributes.isItemLocked(LockAttribute.LOCK_EMPTY));
//            FluidStack fluid = FluidStack.loadFluidStackFromNBT(tank);
//            setFluid(fluid);
//            FluidDrawersLegacyMod.logger(fluid.getAmount()+"");
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
//            if(!getLevel().isClientSide())
//            FluidDrawersLegacyMod.logger(
//                    "server:"+getCacheFluid().getRegistryName()+";"
//                    +resource.getFluid().getRegistryName()
//                    +getDrawerAttributes().isItemLocked(LockAttribute.LOCK_EMPTY));
//            if(getLevel().isClientSide())
//                FluidDrawersLegacyMod.logger(
//                        "client:"+getCacheFluid().getRegistryName()+";"
//                                +resource.getFluid().getRegistryName()
//                                +getDrawerAttributes().isItemLocked(LockAttribute.LOCK_EMPTY));
            if (getDrawerAttributes().isItemLocked(LockAttribute.LOCK_EMPTY)) {
//                FluidDrawersLegacyMod.logger(getCacheFluid().getRegistryName().toString());
                if (getCacheFluid() != Fluids.EMPTY
                        && getCacheFluid() != resource.getFluid()) {
                    return 0;
                }
                if (getCacheFluid() == Fluids.EMPTY) {
                    if (resource.getAmount() > 0) {
                        setCacheFluid(resource.getFluid());
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

                    if (BlockEntityFluidDrawer.this.getTank().getFluidInTank(0).getAmount()
                            >= getCapacityStandard() / 32)
                        return false;
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
                    int amount = getTank().getFluidInTank(0).getAmount();
                    int capacity = getTank().getTankCapacity(0);
                    int standardCapacity = getCapacityStandard();
                    int afterCapacity = standardCapacity * (effectiveStorageMult - storageMult);
                    if (afterCapacity < amount) return false;

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
