package xueluoanping.fluiddrawerslegacy.block.tileentity;

import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawer;
import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawerAttributesModifiable;
import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawerGroup;
import com.jaquadro.minecraft.storagedrawers.api.storage.attribute.LockAttribute;
import com.jaquadro.minecraft.storagedrawers.block.tile.ChamTileEntity;
import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityDrawers;
import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityDrawersStandard;
import com.jaquadro.minecraft.storagedrawers.block.tile.tiledata.UpgradeData;
import com.jaquadro.minecraft.storagedrawers.capabilities.BasicDrawerAttributes;
import com.jaquadro.minecraft.storagedrawers.config.CommonConfig;
import com.jaquadro.minecraft.storagedrawers.core.ModBlocks;
import com.jaquadro.minecraft.storagedrawers.core.ModItems;
import com.jaquadro.minecraft.storagedrawers.item.ItemUpgradeStorage;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.LazyOptional;
import xueluoanping.fluiddrawerslegacy.FluidDrawersLegacyMod;
import xueluoanping.fluiddrawerslegacy.api.drawer.FluidDrawer;
import xueluoanping.fluiddrawerslegacy.api.drawer.FluidDrawerGroup;
import xueluoanping.fluiddrawerslegacy.api.drawer.simplefluiddrawer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class TileTankDrawer extends ChamTileEntity implements FluidDrawerGroup {

    private UpgradeData upgradeData = new TileTankDrawer.DrawerUpgradeData();
    private IDrawerAttributesModifiable drawerAttributes = new TileTankDrawer.DrawerAttributes();

    private boolean loading = true;

    public TileTankDrawer(TileEntityType<?> tileEntityTypeIn) {
        super(tileEntityTypeIn);
        this.upgradeData.setDrawerAttributes(this.drawerAttributes);
        this.injectPortableData(this.upgradeData);
    }


    @Override
    public int getFluidDrawerCount() {
        return 1;
    }

    @Override
    public FluidDrawer getFluidDrawer(int slot) {
        return new simplefluiddrawer();
    }

    @Override
    public int[] getAccessibleFluidDrawerSlots() {
        int[] aa = new int[1];
        aa[0] = 1;
        return new int[0];
    }


    public IDrawerGroup getGroup() {
        return null;
    }

    public boolean hasNoFluid() {
        return true;
    }


    protected void inventoryChanged() {
        super.setChanged();
        if (level != null)
            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), Constants.BlockFlags.BLOCK_UPDATE);
    }

    public static class Slot1 extends TileTankDrawer {
//        private TileEntityDrawersStandard.GroupData groupData = new TileEntityDrawersStandard.GroupData(1);

        public Slot1() {
            super(ModBlocks.Tile.STANDARD_DRAWERS_1);
//            this.groupData.setCapabilityProvider(this);
//            this.injectPortableData(this.groupData);
        }

        public IDrawerGroup getGroup() {
            return null;
        }

//        protected void onAttributeChanged() {
//            super.onAttributeChanged();
//            this.groupData.syncAttributes();
//        }
    }

    private class DrawerUpgradeData extends UpgradeData {
        DrawerUpgradeData() {
            super(7);
        }

        public boolean canAddUpgrade(@Nonnull ItemStack upgrade) {
            if (!super.canAddUpgrade(upgrade)) {
                return false;
            } else {
                if (upgrade.getItem() == ModItems.ONE_STACK_UPGRADE) {
                    int lostStackCapacity = TileTankDrawer.this.upgradeData.getStorageMultiplier() * (TileTankDrawer.this.getEffectiveDrawerCapacity() - 1);
                    if (!this.stackCapacityCheck(lostStackCapacity)) {
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
                    int storageMult = CommonConfig.UPGRADES.getLevelMult(storageLevel);
                    int effectiveStorageMult = TileTankDrawer.this.upgradeData.getStorageMultiplier();
                    if (effectiveStorageMult == storageMult) {
                        --storageMult;
                    }

                    int addedStackCapacity = storageMult * TileTankDrawer.this.getEffectiveDrawerCapacity();
                    if (!this.stackCapacityCheck(addedStackCapacity)) {
                        return false;
                    }
                }

                return true;
            }
        }

        private boolean stackCapacityCheck(int addedStackCapacity) {
            return true;
        }
    }

    private int getEffectiveDrawerCapacity() {
        return 0;
    }

    private class DrawerAttributes extends BasicDrawerAttributes {
        private DrawerAttributes() {
        }

        protected void onAttributeChanged() {
            if (!TileTankDrawer.this.loading && !TileTankDrawer.this.drawerAttributes.isItemLocked(LockAttribute.LOCK_POPULATED)) {
                for (int slot = 0; slot < TileTankDrawer.this.getGroup().getDrawerCount(); ++slot) {
                    if (TileTankDrawer.this.emptySlotCanBeCleared(slot)) {
                        IDrawer drawer = TileTankDrawer.this.getGroup().getDrawer(slot);
                        drawer.setStoredItem(ItemStack.EMPTY);
                    }
                }
            }

            TileTankDrawer.this.onAttributeChanged();
            if (TileTankDrawer.this.getLevel() != null && !TileTankDrawer.this.getLevel().isClientSide) {
                TileTankDrawer.this.setChanged();
                TileTankDrawer.this.markBlockForUpdate();
            }

        }
    }

    protected boolean emptySlotCanBeCleared(int slot) {
        IDrawer drawer = this.getGroup().getDrawer(slot);
        return !drawer.isEmpty() && drawer.getStoredItemCount() == 0;
    }

    protected void onAttributeChanged() {
        this.requestModelDataUpdate();
    }
}
