package xueluoanping.fluiddrawerslegacy.compact.create;

import com.simibubi.create.Create;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.ModList;
import xueluoanping.fluiddrawerslegacy.block.tileentity.TileEntityFluidDrawer;

public class CreateHandler {
    public static boolean isCreateLoad() {
        ModList modList = ModList.get();
        return modList.isLoaded(Create.ID);
    }

    public static boolean interactWithPotion(TileEntityFluidDrawer tile, Player player, ItemStack heldStack) {
        return isCreateLoad() && CreatePotionHandler.interactWithPotion(tile, player, heldStack);
    }

    public static boolean interactWithBottle(TileEntityFluidDrawer tile, Player player, ItemStack heldStack) {
        return isCreateLoad() && CreatePotionHandler.interactWithBottle(tile, player, heldStack);
    }
}
