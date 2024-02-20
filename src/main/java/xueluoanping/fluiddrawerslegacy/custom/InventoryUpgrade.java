package xueluoanping.fluiddrawerslegacy.custom;

// import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityDrawers;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import xueluoanping.fluiddrawerslegacy.block.blockentity.BlockEntityFluidDrawer;

import javax.annotation.Nonnull;

// need extend InventoryUpgrade
public class InventoryUpgrade extends com.jaquadro.minecraft.storagedrawers.inventory.InventoryUpgrade {
    private static final int upgradeCapacity = 7;
    private BlockEntityFluidDrawer tile;

    public InventoryUpgrade(BlockEntityFluidDrawer tileEntity) {
        super(null);
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

    public boolean stillValid(Player player) {
        BlockPos pos = this.tile.getBlockPos();
        if (this.tile.getLevel() != null && this.tile.getLevel().getBlockEntity(pos) == this.tile) {
            return !(player.distanceToSqr((double)pos.getX() + 0.5D, (double)pos.getY() + 0.5D, (double)pos.getZ() + 0.5D) > 64.0D);
        } else {
            return false;
        }
    }

    public void startOpen(Player player) {
    }

    public void stopOpen(Player player) {
    }

    public boolean canPlaceItem(int slot, @Nonnull ItemStack item) {
        return this.tile.upgrades().canAddUpgrade(item);
        // return true;
    }

    public void clearContent() {
    }

    public boolean canAddUpgrade(@Nonnull ItemStack item) {
        return this.tile.upgrades().canAddUpgrade(item);
    }

    public boolean canRemoveStorageUpgrade(int slot) {
        return this.tile.upgrades().canRemoveUpgrade(slot);
    }

    // new add in 1.20
    public boolean canSwapUpgrade(int slot, @NotNull ItemStack item) {
        return this.tile.upgrades().canSwapUpgrade(slot, item);
    }

}
