package xueluoanping.fluiddrawerslegacy.block.tileentity;

import com.jaquadro.minecraft.storagedrawers.api.event.DrawerPopulatedEvent;
import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawer;
import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawerAttributes;
import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawerAttributesModifiable;
import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawerGroup;
import com.jaquadro.minecraft.storagedrawers.api.storage.attribute.LockAttribute;
import com.jaquadro.minecraft.storagedrawers.block.tile.ChamTileEntity;
import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityDrawers;
import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityDrawersStandard;
import com.jaquadro.minecraft.storagedrawers.block.tile.tiledata.StandardDrawerGroup;
import com.jaquadro.minecraft.storagedrawers.block.tile.tiledata.UpgradeData;
import com.jaquadro.minecraft.storagedrawers.capabilities.BasicDrawerAttributes;
import com.jaquadro.minecraft.storagedrawers.config.CommonConfig;
import com.jaquadro.minecraft.storagedrawers.core.ModItems;
import com.jaquadro.minecraft.storagedrawers.item.ItemUpgrade;
import com.jaquadro.minecraft.storagedrawers.item.ItemUpgradeStorage;
import com.jaquadro.minecraft.storagedrawers.network.CountUpdateMessage;
import com.jaquadro.minecraft.storagedrawers.network.MessageHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.ModelDataManager;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.data.ModelDataMap;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.registries.ForgeRegistries;
import xueluoanping.fluiddrawerslegacy.FluidDrawersLegacyMod;
import xueluoanping.fluiddrawerslegacy.ModConstants;
import xueluoanping.fluiddrawerslegacy.ModContents;
import xueluoanping.fluiddrawerslegacy.config.General;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.concurrent.atomic.AtomicInteger;

public class TileEntityFluidDrawer extends ChamTileEntity implements IDrawerGroup {

    private BasicDrawerAttributes drawerAttributes = new DrawerAttributes();

    private GroupData groupData = new GroupData(1);

    private UpgradeData upgradeData = new TileEntityFluidDrawer.DrawerUpgradeData();
    private final LazyOptional<?> capabilityGroup = LazyOptional.of(this::getGroup);
    //    public static int Capacity = 32000;


    public TileEntityFluidDrawer() {
        super(ModContents.tankTileEntityType);
        groupData.setCapabilityProvider(this);
        injectPortableData(groupData);
        this.upgradeData.setDrawerAttributes(this.drawerAttributes);
        this.injectPortableData(this.upgradeData);
    }

    private static int getCapacityStandard() {
        return General.volume.get();
    }


    public IDrawerGroup getGroup() {
        return groupData;
    }


    protected void onAttributeChanged() {
        this.requestModelDataUpdate();
        groupData.syncAttributes();
    }

    public boolean hasNoFluid() {
        return upgrades().write(new CompoundNBT()).toString().contains("storagedrawers:void_upgrade") || groupData.tank.getFluidInTank(0).getAmount() == 0;
    }

    public FluidStack getTankFLuid() {
        return groupData.tank.getFluidInTank(0);
    }

    public void inventoryChanged() {
        super.setChanged();
        if (level != null) {
            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
        }
    }

    @Override
    public void readPortable(CompoundNBT nbt) {
        super.readPortable(nbt);
        if (nbt.contains("Lock")) {
            EnumSet<LockAttribute> attrs = LockAttribute.getEnumSet(nbt.getByte("Lock"));
            if (attrs != null) {
                drawerAttributes.setItemLocked(LockAttribute.LOCK_EMPTY, attrs.contains(LockAttribute.LOCK_EMPTY));
                drawerAttributes.setItemLocked(LockAttribute.LOCK_POPULATED, attrs.contains(LockAttribute.LOCK_POPULATED));
            }
        } else {
            drawerAttributes.setItemLocked(LockAttribute.LOCK_EMPTY, false);
            drawerAttributes.setItemLocked(LockAttribute.LOCK_POPULATED, false);
        }

        if (nbt.contains("Shr"))
            drawerAttributes.setIsConcealed(nbt.getBoolean("Shr"));
        else
            drawerAttributes.setIsConcealed(false);


        if (nbt.contains("Qua"))
            drawerAttributes.setIsShowingQuantity(nbt.getBoolean("Qua"));
        else
            drawerAttributes.setIsShowingQuantity(false);
    }

    @Override
    public CompoundNBT writePortable(CompoundNBT tag) {
        EnumSet<LockAttribute> attrs = EnumSet.noneOf(LockAttribute.class);
        if (drawerAttributes.isItemLocked(LockAttribute.LOCK_EMPTY))
            attrs.add(LockAttribute.LOCK_EMPTY);
        if (drawerAttributes.isItemLocked(LockAttribute.LOCK_POPULATED))
            attrs.add(LockAttribute.LOCK_POPULATED);
        if (!attrs.isEmpty()) {
            tag.putByte("Lock", (byte) LockAttribute.getBitfield(attrs));
        }

        if (drawerAttributes.isConcealed())
            tag.putBoolean("Shr", true);

        if (drawerAttributes.isShowingQuantity())
            tag.putBoolean("Qua", true);
        return super.writePortable(tag);
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, @Nullable Direction facing) {
        if (capability == ModConstants.DRAWER_GROUP_CAPABILITY) {
            return this.capabilityGroup.cast();
        } else {
            LazyOptional<T> cap = this.getGroup().getCapability(capability, facing);
            return cap.isPresent() ? cap : super.getCapability(capability, facing);
        }
    }

    public void setChanged() {
        if (this.isRedstone() && this.getLevel() != null) {
            this.getLevel().updateNeighborsAt(this.getBlockPos(), this.getBlockState().getBlock());
            this.getLevel().updateNeighborsAt(this.getBlockPos().below(), this.getBlockState().getBlock());
        }

        super.setChanged();
    }

    protected void syncClientCount(int slot, int count) {
        if (this.getLevel() == null || !this.getLevel().isClientSide) {
            PacketDistributor.TargetPoint point = new PacketDistributor.TargetPoint((double)this.getBlockPos().getX(), (double)this.getBlockPos().getY(), (double)this.getBlockPos().getZ(), 500.0D, this.getLevel().dimension());
            MessageHandler.INSTANCE.send(PacketDistributor.NEAR.with(() -> {
                return point;
            }), new CountUpdateMessage(this.getBlockPos(), slot, count));
        }
    }

    @OnlyIn(Dist.CLIENT)
    public void clientUpdateCount(int slot, int count) {
        if (this.getLevel().isClientSide) {
            Minecraft.getInstance().tell(() -> {
                this.clientUpdateCountAsync(slot, count);
            });
        }
    }

    @OnlyIn(Dist.CLIENT)
    private void clientUpdateCountAsync(int slot, int count) {
        IDrawer drawer = this.getDrawer(slot);
        if (drawer.isEnabled() && drawer.getStoredItemCount() != count) {
            drawer.setStoredItemCount(count);
        }

    }

    public boolean dataPacketRequiresRenderUpdate() {
        return true;
    }

    public IDrawerAttributes getDrawerAttributes() {
        return this.drawerAttributes;
    }

    public IFluidHandler getTank() {
        return this.groupData.tank;
    }

    public int getEffectiveCapacity() {
        //FluidDrawersLegacyMod.LOGGER.info(""+getLevel()+upgrades().write(new CompoundNBT()).toString().contains("storagedrawers:creative_vending_upgrade"));
        if (upgrades().write(new CompoundNBT()).toString().contains("storagedrawers:creative_vending_upgrade") || upgrades().hasUnlimitedUpgrade())
            return Integer.MAX_VALUE;
        if (upgrades().hasOneStackUpgrade())
            return getCapacityStandard() / 32;
        return getCapacityStandard() * upgrades().getStorageMultiplier();
    }

    public static int calcultaeCapacitybyStack(ItemStack stack) {
        if (!stack.getOrCreateTag().contains("Upgrades"))
            return getCapacityStandard();
        else {
            int i = 0;
            //            use 8 instead of 7, maybe 1 =null?
            UpgradeData upgradeData0 = new UpgradeData(8);

            ListNBT list = stack.getOrCreateTag().getList("Upgrades", Constants.NBT.TAG_COMPOUND);
            if (list.size() > 0)
                for (int j = 0; j < list.size(); j++) {
                    CompoundNBT id = (CompoundNBT) list.get(j);
                    String ids = id.get("id").getAsString();
                    String[] idGroup = ids.split(":");
                    ResourceLocation res = new ResourceLocation(idGroup[0], idGroup[1]);
                    if (ForgeRegistries.ITEMS.containsKey(res)) {
                        upgradeData0.addUpgrade(ForgeRegistries.ITEMS.getValue(res).getItem().getDefaultInstance());
                        if (ForgeRegistries.ITEMS.getValue(res).getItem() instanceof ItemUpgradeStorage) {
                            int level = ((ItemUpgradeStorage) ForgeRegistries.ITEMS.getValue(res).getItem()).level.getLevel();
                            i += CommonConfig.UPGRADES.getLevelMult(level);
                        }
                        //                    if(ForgeRegistries.ITEMS.getValue(res).getItem() ==ModItems.ONE_STACK_UPGRADE) return getCapacityStandard() / 32;
                    }
                }

            //            i += upgradeData0.getStorageMultiplier();
            i = i == 0 ? 1 : i;
            if (upgradeData0.write(new CompoundNBT()).toString().contains("storagedrawers:creative_vending_upgrade") || upgradeData0.write(new CompoundNBT()).toString().contains("storagedrawers:creative_storage_upgrade"))
                return Integer.MAX_VALUE;
            if (upgradeData0.write(new CompoundNBT()).toString().contains("storagedrawers:one_stack_upgrade"))
                return getCapacityStandard() / 32;
            //            FluidDrawersLegacyMod.logger(""+upgradeData0.getStorageMultiplier());
            return upgradeData0.hasVendingUpgrade() ? Integer.MAX_VALUE : getCapacityStandard() * i;
        }
    }


    public int getDrawerCapacity() {
        return 0;
    }


    public int getEffectiveDrawerCapacity() {
        return 0;
    }

    public UpgradeData upgrades() {
        return this.upgradeData;
    }

    @Override
    public int getDrawerCount() {
        return 0;
    }

    @Nonnull
    @Override
    public IDrawer getDrawer(int slot) {
        return this.getGroup().getDrawer(slot);
    }

    @Nonnull
    @Override
    public int[] getAccessibleDrawerSlots() {
        return new int[0];
    }

    public int getRedstoneLevel() {
        //        FluidDrawersLegacyMod.logger(getLevel().toString()+this.isRedstone());
        return (int) (((float) getTankFLuid().getAmount() / (float) getEffectiveCapacity()) * 15);
    }


    public boolean isRedstone() {
        return upgrades().getRedstoneType() != null;
    }

    public class GroupData extends StandardDrawerGroup {

        private final LazyOptional<?> attributesHandler = LazyOptional.of(TileEntityFluidDrawer.this::getDrawerAttributes);
        public final betterFluidHandler tank;
        private final LazyOptional<betterFluidHandler> tankHandler;
        public DrawerData drawerData;

        public GroupData(int slotCount) {
            super(slotCount);
            tank = createFuildHandler();
            tankHandler = LazyOptional.of(() -> tank);

        }


        private TileEntityFluidDrawer.betterFluidHandler createFuildHandler() {
            return new TileEntityFluidDrawer.betterFluidHandler(General.volume.get()) {
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
            return TileEntityFluidDrawer.this.isGroupValid();
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
        public CompoundNBT write(CompoundNBT tag) {

            tag.put("tank", tank.serializeNBT());
            //            FluidDrawersLegacyMod.LOGGER.info(tank.serializeNBT());
            CompoundNBT nbt = super.write(tag);


            //            inventoryChanged();
            //            If want to camouflage, pay attention to setting the capacity first, but we don't need it.
            return nbt;
        }


        @Override
        public void read(CompoundNBT nbt) {
            upgrades().read(nbt);

            if (nbt.contains("tank", Constants.NBT.TAG_COMPOUND)) {
                tank.deserializeNBT((CompoundNBT) nbt.get("tank"));
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
            return TileEntityFluidDrawer.this.groupData.tank;
        }

        //        public int getCapacity() {
        //            return getCapacityStandard() * upgrades().getStorageMultiplier();
        //        }

        @Override
        protected int getStackCapacity() {
            return upgrades().getStorageMultiplier() * getEffectiveDrawerCapacity();
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
            return TileEntityFluidDrawer.this.getDrawerAttributes().isItemLocked(LockAttribute.LOCK_EMPTY);
        }

        public boolean isVoid() {
            return upgrades().serializeNBT().toString().contains("void");
        }

        public BlockPos getDrawerPos() {
            return TileEntityFluidDrawer.this.getBlockPos();
        }

        @Override
        public int getMaxCapacity() {
            return getEffectiveCapacity();
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


        @Override
        public FluidStack getFluid() {
            if (upgrades().hasVendingUpgrade() && this.fluid.getFluid() != Fluids.EMPTY) {
//                FluidStack stack = fluid.copy();
//                stack.setAmount(Integer.MAX_VALUE);
                return new FluidStack(super.getFluid().getFluid(), Integer.MAX_VALUE);
            }
            return super.getFluid();
        }

        public CompoundNBT serializeNBT() {
            // resize capacity when sending message
            if (this.getCapacity() != TileEntityFluidDrawer.this.getEffectiveCapacity())
                this.setCapacity(TileEntityFluidDrawer.this.getEffectiveCapacity());
            CompoundNBT nbt = new CompoundNBT();
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
            nbt.put("cache", cacheFluid.writeToNBT(new CompoundNBT()));
            return writeToNBT(nbt);
        }

        public void deserializeNBT(CompoundNBT tank) {
            if (this.getCapacity() != TileEntityFluidDrawer.this.getEffectiveCapacity())
                this.setCapacity(TileEntityFluidDrawer.this.getEffectiveCapacity());
            if (tank.contains("cache")) {
                FluidStack cacheTempStack = FluidStack.loadFluidStackFromNBT(tank.getCompound("cache"));
                // String[] x = tank.getString("cache").split(":");
                // ResourceLocation res = new ResourceLocation(x[0], x[1]);
                //
                // Fluid fluid = ForgeRegistries.FLUIDS.getValue(res);

                setCacheFluid(cacheTempStack);
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
                    && upgrades().write(new CompoundNBT()).toString().contains("storagedrawers:void_upgrade")) {
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

            TileEntityFluidDrawer.this.onAttributeChanged();
            if (TileEntityFluidDrawer.this.getLevel() != null && !TileEntityFluidDrawer.this.getLevel().isClientSide) {
                TileEntityFluidDrawer.this.setChanged();
                TileEntityFluidDrawer.this.markBlockForUpdate();
            }

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
                if (upgrade.getItem() == ModItems.FILL_LEVEL_UPGRADE.getItem()) return false;
                else if (upgrade.getItem() == ModItems.ONE_STACK_UPGRADE.getItem()) {
                    if (upgrades().hasOneStackUpgrade())
                        return false;

                    if (TileEntityFluidDrawer.this.getTank().getFluidInTank(0).getAmount()
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
                    int effectiveStorageMult = TileEntityFluidDrawer.this.upgrades().getStorageMultiplier();
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
            if (TileEntityFluidDrawer.this.getLevel() != null && !TileEntityFluidDrawer.this.getLevel().isClientSide) {
                TileEntityFluidDrawer.this.setChanged();
                TileEntityFluidDrawer.this.markBlockForUpdate();
            }

        }
    }
}
