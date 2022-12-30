package xueluoanping.fluiddrawerslegacy.custom;

import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityDrawers;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import xueluoanping.fluiddrawerslegacy.block.tileentity.TileEntityFluidDrawer;

import javax.annotation.Nonnull;

public class InventoryUpgrade implements IInventory {
    private static final int upgradeCapacity = 7;
    private TileEntityFluidDrawer tile;

    public InventoryUpgrade(TileEntityFluidDrawer tileEntity) {
        this.tile = tileEntity;
    }

    public int getContainerSize() {
        return 7;
    }

    public boolean isEmpty() {
        for(int i = 0; i < 7; ++i) {
            if (!this.tile.upgrades().getUpgrade(i).isEmpty()) {
                return false;
            }
        }

        return true;
    }

    @Nonnull
    public ItemStack getItem(int slot) {
        return this.tile.upgrades().getUpgrade(slot);
    }

    @Nonnull
    public ItemStack removeItem(int slot, int count) {
        ItemStack stack = this.tile.upgrades().getUpgrade(slot);
        if (count > 0) {
            this.tile.upgrades().setUpgrade(slot, ItemStack.EMPTY);
        }

        return stack;
    }

    @Nonnull
    public ItemStack removeItemNoUpdate(int slot) {
        return ItemStack.EMPTY;
    }

    public void setItem(int slot, @Nonnull ItemStack item) {
        this.tile.upgrades().setUpgrade(slot, item);
    }

    public int getMaxStackSize() {
        return 1;
    }

    public void setChanged() {
        this.tile.setChanged();
    }

    public boolean stillValid(PlayerEntity player) {
        BlockPos pos = this.tile.getBlockPos();
        if (this.tile.getLevel() != null && this.tile.getLevel().getBlockEntity(pos) == this.tile) {
            return !(player.distanceToSqr((double)pos.getX() + 0.5D, (double)pos.getY() + 0.5D, (double)pos.getZ() + 0.5D) > 64.0D);
        } else {
            return false;
        }
    }

    public void startOpen(PlayerEntity player) {
    }

    public void stopOpen(PlayerEntity player) {
    }

    public boolean canPlaceItem(int slot, @Nonnull ItemStack item) {
        return this.tile.upgrades().canAddUpgrade(item);
    }

    public void clearContent() {
    }

    public boolean canAddUpgrade(@Nonnull ItemStack item) {
        return this.tile.upgrades().canAddUpgrade(item);
    }

    public boolean canRemoveStorageUpgrade(int slot) {
        return this.tile.upgrades().canRemoveUpgrade(slot);
    }
}
