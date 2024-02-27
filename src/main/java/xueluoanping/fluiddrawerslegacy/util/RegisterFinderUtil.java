package xueluoanping.fluiddrawerslegacy.util;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.ForgeRegistries;

public class RegisterFinderUtil {


    public static Block getBlock(String s) {
        return getBlock(new ResourceLocation(s));
    }

    // BuiltInRegistries
    public static Block getBlock(ResourceLocation rs) {
        return ForgeRegistries.BLOCKS.getValue(rs);
    }

    public static Item getItem(String s) {
        return getItem(new ResourceLocation(s));
    }

    public static Item getItem(ResourceLocation rs) {
        return ForgeRegistries.ITEMS.getValue(rs);
    }

    public static Item getItem(String s, String s2) {
        return getItem(new ResourceLocation(s, s2));
    }

    public static ResourceLocation getItemKey(Item s) {
        return ForgeRegistries.ITEMS.getKey(s);
    }

    public static ResourceLocation getBlockKey(Block s) {
        return ForgeRegistries.BLOCKS.getKey(s);
    }
}
