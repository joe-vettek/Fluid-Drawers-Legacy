package xueluoanping.fluiddrawerslegacy.client.gui;

import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.block.BlockDrawers;

import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityDrawers;
import com.jaquadro.minecraft.storagedrawers.client.renderer.StorageRenderItem;
import com.jaquadro.minecraft.storagedrawers.core.ModContainers;
import com.jaquadro.minecraft.storagedrawers.inventory.SlotUpgrade;
import com.jaquadro.minecraft.storagedrawers.item.ItemUpgrade;
import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
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

public class ContainerFluiDrawer extends Container {
    private static final int[][] slotCoordinates = new int[][]{{80, 36}};
    private IInventory upgradeInventory;

    private List<Slot> upgradeSlots;
    private List<Slot> playerSlots;
    private List<Slot> hotbarSlots;
    @OnlyIn(Dist.CLIENT)
    public StorageRenderItem activeRenderItem;
    private boolean isRemote;



    private TileEntityFluidDrawer tileEntityFluidDrawer;

    public ContainerFluiDrawer(int windowId, PlayerInventory playerInv, PacketBuffer data) {
        this(windowId, playerInv, getTileEntity(playerInv, data.readBlockPos()));
    }

    public static TileEntityFluidDrawer getTileEntity(PlayerInventory playerInv, BlockPos pos) {
        World world = playerInv.player.getCommandSenderWorld();
        TileEntity tile = world.getBlockEntity(pos);
        if (!(tile instanceof TileEntityFluidDrawer)) {
            StorageDrawers.log.error("Expected a drawers tile entity at " + pos.toString());
            return null;
        } else {
            return (TileEntityFluidDrawer) tile;
        }
    }
    public TileEntityFluidDrawer getTileEntityFluidDrawer() {
        return tileEntityFluidDrawer;
    }
    public ContainerFluiDrawer(int windowId, PlayerInventory playerInventory, TileEntityFluidDrawer tileEntity) {
        super(ModContents.containerType, windowId);
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

    protected int getStorageSlotX(int slot) {
        return slotCoordinates[slot][0];
    }

    protected int getStorageSlotY(int slot) {
        return slotCoordinates[slot][1];
    }

    public boolean stillValid(PlayerEntity player) {
        return this.upgradeInventory.stillValid(player);
    }


    public List<Slot> getUpgradeSlots() {
        return this.upgradeSlots;
    }

    @Override
    protected boolean moveItemStackTo(ItemStack p_75135_1_, int p_75135_2_, int p_75135_3_, boolean p_75135_4_) {

        return super.moveItemStackTo(p_75135_1_, p_75135_2_, p_75135_3_, p_75135_4_);
    }

    //    注意这里的index是slot的位置，本容器顺序为升级栏，玩家容器，玩家快捷栏
//    player客户端和服务端各出现一次，说明是先客户端调用，然后服务端调用
    @Nonnull
    @Override
    public ItemStack quickMoveStack(PlayerEntity player, int slotIndex) {
//        FluidDrawersLegacyMod.logger("hello" + player + slotIndex);
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

//            if (slotIndex < 7) {
//                if (upgradeSlots.get(slotIndex).getItem() != ItemStack.EMPTY) {
////                    player.addItem(upgradeSlots.get(slotIndex).getItem());
//                    if (this.moveItemStackTo(upgradeSlots.get(slotIndex).getItem(), 7, 43, false))
//                        return ItemStack.EMPTY;
//                }
//            } else if (7 <= slotIndex && slotIndex < 34) {
//                if (playerSlots.get(slotIndex - 7).getItem() != ItemStack.EMPTY) {
////                    FluidDrawersLegacyMod.logger("hello"+player + playerSlots.get(slotIndex).getItem());
//                    for (int i = 0; i < upgradeSlots.size(); i++) {
//                        if (upgradeSlots.get(i).getItem() == ItemStack.EMPTY) {
//
////                            upgradeSlots.get(i).set(playerSlots.get(slotIndex).getItem());
//
//                            if (!this.moveItemStackTo(playerSlots.get(slotIndex - 7).getItem(), 0, 7, false)) {
//                                FluidDrawersLegacyMod.logger("hello" + player + playerSlots.get(slotIndex - 7).getItem());
//                                return ItemStack.EMPTY;
//                            }
//
//                        }
//                    }
//                }
//            } else if (34 <= slotIndex && slotIndex < 43)
//                if (hotbarSlots.get(slotIndex - 34).getItem() != ItemStack.EMPTY) {
////                    FluidDrawersLegacyMod.logger("hello"+player + playerSlots.get(slotIndex).getItem());
//                    for (int i = 0; i < upgradeSlots.size(); i++) {
//                        if (upgradeSlots.get(i).getItem() == ItemStack.EMPTY) {
//
////                            upgradeSlots.get(i).set(playerSlots.get(slotIndex).getItem());
//
//                            if (!this.moveItemStackTo(hotbarSlots.get(slotIndex - 34).getItem(), 0, 7, false)) {
//                                FluidDrawersLegacyMod.logger("hello" + player + hotbarSlots.get(slotIndex - 34).getItem());
//                                return ItemStack.EMPTY;
//                            }
//
//                        }
//                    }
//                }
//
////            for (int i = 0; i < upgradeSlots.size(); i++) {
////                FluidDrawersLegacyMod.logger("hello"+player + (upgradeSlots.get(i).getItem().getItem()));
////            }
//
////            if(upgradeSlots.get(0).getItem()!= ItemStack.EMPTY)return upgradeSlots.get(0).getItem();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return ItemStack.EMPTY;
    }
}
