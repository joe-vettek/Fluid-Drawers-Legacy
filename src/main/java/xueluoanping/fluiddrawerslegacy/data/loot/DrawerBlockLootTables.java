package xueluoanping.fluiddrawerslegacy.data.loot;

import com.google.common.collect.Maps;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.CopyCustomDataFunction;
import net.minecraft.world.level.storage.loot.providers.nbt.ContextNbtProvider;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.neoforged.neoforge.registries.DeferredHolder;
import xueluoanping.fluiddrawerslegacy.FluidDrawersLegacyMod;
import xueluoanping.fluiddrawerslegacy.ModContents;
import xueluoanping.fluiddrawerslegacy.util.RegisterFinderUtil;


import java.util.*;
import java.util.stream.Collectors;

public class DrawerBlockLootTables extends BlockLootSubProvider {

    // I‘m not sure if I need a map for myself, but as you see the BlockLoot class have a same one
    // so while you add your block, and then you need to deal with the block you don't need
    // If you don't want to do some extra check, maybe my method is better,
    // so now you need override add,accept method yourself. Don't be lazy.
    private final Map<ResourceLocation, LootTable.Builder> map = Maps.newHashMap();


    public DrawerBlockLootTables(HolderLookup.Provider provider) {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags(), provider);
    }

    private Block getDrawerWith(String countString) {
        return RegisterFinderUtil.getBlock(FluidDrawersLegacyMod.rl("fluiddrawer" + countString));
    }
    // @Override
    // protected void add(Block block, LootTable.Builder builder) {
    //     this.map.put(block.getLootTable(), builder);
    //
    // }

    @Override
    protected void generate() {
        add(getDrawerWith(""), this::createSingleDrawerTable);
        add(getDrawerWith("_2"), this::createSingleDrawerTable);
        add(getDrawerWith("_4"), this::createSingleDrawerTable);
        add(getDrawerWith("_half"), this::createSingleDrawerTable);
        add(getDrawerWith("_2_half"), this::createSingleDrawerTable);
        add(getDrawerWith("_4_half"), this::createSingleDrawerTable);
    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
        return ModContents.DREntityBlocks.getEntries().stream().map(DeferredHolder::get).collect(Collectors.toList());
    }


    public LootTable.Builder createSingleDrawerTable(ItemLike item) {
        return LootTable.lootTable()
                .withPool(
                        this.applyExplosionCondition(item.asItem(), LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F))
                                .add(LootItem.lootTableItem(item))
                        )).apply(CopyCustomDataFunction
                        .copyData(ContextNbtProvider.BLOCK_ENTITY)
                        .copy("tanks", "tanks")
                        .copy("Upgrades", "Upgrades")
                        .copy("Lock", "Lock")
                        .copy("Shr", "Shr")
                        .copy("Qua", "Qua")
                );
    }

}
