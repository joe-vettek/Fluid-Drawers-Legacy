package xueluoanping.fluiddrawerslegacy.client.gui;

import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
// import com.jaquadro.minecraft.storagedrawers.client.renderer.StorageRenderItem;
import com.jaquadro.minecraft.storagedrawers.client.gui.StorageGuiGraphics;
import com.jaquadro.minecraft.storagedrawers.core.ModItems;
import com.jaquadro.minecraft.storagedrawers.inventory.SlotUpgrade;
import com.jaquadro.minecraft.storagedrawers.item.ItemUpgrade;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import xueluoanping.fluiddrawerslegacy.FluidDrawersLegacyMod;
import xueluoanping.fluiddrawerslegacy.ModContents;
import xueluoanping.fluiddrawerslegacy.block.tileentity.TileEntityFluidDrawer;
import xueluoanping.fluiddrawerslegacy.custom.InventoryUpgrade;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class ContainerFluiDrawer extends AbstractContainerMenu {
    private static final int[][] slotCoordinates = new int[][]{{80, 36}};
    private Container upgradeInventory;

    private List<Slot> upgradeSlots;
    private List<Slot> playerSlots;
    private List<Slot> hotbarSlots;
    @OnlyIn(Dist.CLIENT)
    public StorageGuiGraphics activeGuiGraphics;
    // public StorageRenderItem activeRenderItem;
    private boolean isRemote;



    private TileEntityFluidDrawer tileEntityFluidDrawer;

    public ContainerFluiDrawer(int windowId, Inventory playerInv, FriendlyByteBuf data) {
        this(ModContents.containerType.get(),windowId, playerInv, getTileEntity(playerInv, data.readBlockPos()));
    }

    public static TileEntityFluidDrawer getTileEntity(Inventory playerInv, BlockPos pos) {
        if (!(playerInv.player.getCommandSenderWorld().getBlockEntity(pos) instanceof TileEntityFluidDrawer tile)) {
            StorageDrawers.log.error("Expected a drawers tile entity at " + pos.toString());
            return null;
        } else {
            return  tile;
        }
    }


    public TileEntityFluidDrawer getTileEntityFluidDrawer() {
        return tileEntityFluidDrawer;
    }

    public ContainerFluiDrawer(@Nullable MenuType<?> type, int windowId, Inventory playerInventory, TileEntityFluidDrawer tileEntity) {
        super(type, windowId);
        this.tileEntityFluidDrawer=tileEntity;
        int drawerCount = 1;
        this.upgradeInventory = new InventoryUpgrade(tileEntity);

        int i;
        this.upgradeSlots = new ArrayList();
        for (i = 0; i < 7; ++i) {
            this.upgradeSlots.add(this.addSlot(new SlotUpgrade(this.upgradeInventory, i, 26 + i * 18, 86)));
        }

        this.playerSlots = new ArrayList();

        for (i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.playerSlots.add(this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 117 + i * 18)));
            }
        }

        this.hotbarSlots = new ArrayList();

        for (i = 0; i < 9; ++i) {
            this.hotbarSlots.add(this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 175)));
        }

        this.isRemote = playerInventory.player.getCommandSenderWorld().isClientSide();
    }

    public void setLastAccessedItem(ItemStack stack) {
        if (this.isRemote && this.activeGuiGraphics != null) {
            this.activeGuiGraphics.overrideStack = stack;
        }

    }

    protected int getStorageSlotX(int slot) {
        return slotCoordinates[slot][0];
    }

    protected int getStorageSlotY(int slot) {
        return slotCoordinates[slot][1];
    }



    public List<Slot> getUpgradeSlots() {
        return this.upgradeSlots;
    }



    //    注意这里的index是slot的位置，本容器顺序为升级栏，玩家容器，玩家快捷栏
//    player客户端和服务端各出现一次，说明是先客户端调用，然后服务端调用
    @Nonnull
    @Override
    public ItemStack quickMoveStack(Player player, int slotIndex) {
       // FluidDrawersLegacyMod.logger("hello" + player + slotIndex);
        try {
            ItemStack itemStack = ItemStack.EMPTY;
            Slot slot = (Slot) this.slots.get(slotIndex);

            int upgradeStart = ((Slot) this.upgradeSlots.get(0)).index;
            int upgradeEnd = ((Slot) this.upgradeSlots.get(this.upgradeSlots.size() - 1)).index + 1;
            int inventoryStart = ((Slot) this.playerSlots.get(0)).index;
            int hotbarStart = ((Slot) this.hotbarSlots.get(0)).index;
            int hotbarEnd = ((Slot) this.hotbarSlots.get(this.hotbarSlots.size() - 1)).index + 1;
            if (slot != null && slot.hasItem()) {
                ItemStack slotStack = slot.getItem();
                itemStack = slotStack.copy();
                if (slotIndex >= upgradeStart && slotIndex < upgradeEnd) {
                    if (!this.moveItemStackTo(slotStack, inventoryStart, hotbarEnd, true)) {
                        return ItemStack.EMPTY;
                    }

                    slot.onQuickCraft(slotStack, itemStack);
                } else if (slotIndex >= inventoryStart && slotIndex < hotbarEnd && !slotStack.isEmpty()) {
                    if (slotStack.getItem() instanceof ItemUpgrade) {
                        ItemStack slotStack1 = slotStack.copy();
                        slotStack1.setCount(1);
                        if (this.moveItemStackTo(slotStack1, upgradeStart, upgradeEnd, false)) {
                            slotStack.shrink(1);
                            if (slotStack.getCount() == 0) {
                                slot.set(ItemStack.EMPTY);
                            } else {
                                slot.setChanged();
                            }

                            slot.onTake(player, slotStack);
                            return ItemStack.EMPTY;
                        }

                        if (slotIndex >= inventoryStart && slotIndex < hotbarStart) {
                            if (!this.moveItemStackTo(slotStack, hotbarStart, hotbarEnd, false)) {
                                return ItemStack.EMPTY;
                            }
                        } else if (slotIndex >= hotbarStart && slotIndex < hotbarEnd && !this.moveItemStackTo(slotStack, inventoryStart, hotbarStart, false)) {
                            return ItemStack.EMPTY;
                        }
                    } else if (slotIndex >= inventoryStart && slotIndex < hotbarStart) {
                        if (!this.moveItemStackTo(slotStack, hotbarStart, hotbarEnd, false)) {
                            return ItemStack.EMPTY;
                        }
                    } else if (slotIndex >= hotbarStart && slotIndex < hotbarEnd && !this.moveItemStackTo(slotStack, inventoryStart, hotbarStart, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (!this.moveItemStackTo(slotStack, inventoryStart, hotbarEnd, false)) {
                    return ItemStack.EMPTY;
                }

                int slotStackSize = slotStack.getCount();
                if (slotStackSize == 0) {
                    slot.set(ItemStack.EMPTY);
                } else {
                    slot.setChanged();
                }

                if (slotStackSize == itemStack.getCount()) {
                    return ItemStack.EMPTY;
                }

                slot.onTake(player, slotStack);
            }
//            FluidDrawersLegacyMod.logger("hello" + itemStack);
            return itemStack;

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player player) {
        // this.upgradeInventory.setItem(0, ModItems.CONVERSION_UPGRADE.get().getDefaultInstance());
        return this.upgradeInventory.stillValid(player);
    }

}
