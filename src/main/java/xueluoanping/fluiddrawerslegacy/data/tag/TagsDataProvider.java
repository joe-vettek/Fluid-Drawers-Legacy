package xueluoanping.fluiddrawerslegacy.data.tag;

import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.Nullable;
import xueluoanping.fluiddrawerslegacy.ModContents;

import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class TagsDataProvider extends BlockTagsProvider {


    public TagsDataProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, String modId, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, modId, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        var tag= BlockTags.create(StorageDrawers.rl("drawers"));
        ModContents.DREntityBlocks.getEntries().forEach(blockRegistryObject -> {
            this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(blockRegistryObject.get());
            this.tag(tag).add(blockRegistryObject.get());
        });
    }
}
