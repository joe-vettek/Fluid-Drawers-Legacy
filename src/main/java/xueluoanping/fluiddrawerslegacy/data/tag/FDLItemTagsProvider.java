package xueluoanping.fluiddrawerslegacy.data.tag;

import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.level.block.Block;
import xueluoanping.fluiddrawerslegacy.ModContents;

import java.util.concurrent.CompletableFuture;

public class FDLItemTagsProvider extends ItemTagsProvider {
    public FDLItemTagsProvider(PackOutput p_275343_, CompletableFuture<HolderLookup.Provider> p_275729_, CompletableFuture<TagsProvider.TagLookup<Block>> p_275322_) {
        super(p_275343_, p_275729_, p_275322_);
    }


    @Override
    protected void addTags(HolderLookup.Provider provider) {
        var tag= ItemTags.create(StorageDrawers.rl("drawers"));
        ModContents.DREntityBlockItems.getEntries().forEach(blockRegistryObject -> {
            this.tag(tag).add(blockRegistryObject.get());
        });
    }
}
