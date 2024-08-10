package xueluoanping.fluiddrawerslegacy.util;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class RegisterFinderUtil {


    public static Block getBlock(String s) {
        return getBlock(ResourceLocation.parse(s));
    }

    // BuiltInRegistries
    public static Block getBlock(ResourceLocation rs) {
        return BuiltInRegistries.BLOCK.get(rs);
    }

    public static Item getItem(String s) {
        return getItem(ResourceLocation.parse(s));
    }

    public static Item getItem(ResourceLocation rs) {
        return BuiltInRegistries.ITEM.get(rs);
    }

    public static Item getItem(String s, String s2) {
        return getItem(ResourceLocation.fromNamespaceAndPath(s, s2));
    }

    public static ResourceLocation getItemKey(Item s) {
        return BuiltInRegistries.ITEM.getKey(s);
    }

    public static ResourceLocation getBlockKey(Block s) {
        return BuiltInRegistries.BLOCK.getKey(s);
    }
}
