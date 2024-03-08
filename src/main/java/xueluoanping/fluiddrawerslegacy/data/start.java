package xueluoanping.fluiddrawerslegacy.data;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import xueluoanping.fluiddrawerslegacy.FluidDrawersLegacyMod;
import xueluoanping.fluiddrawerslegacy.data.blockstate.BlockStatesDataProvider;
import xueluoanping.fluiddrawerslegacy.data.blockstate.ItemModelProvider;
import xueluoanping.fluiddrawerslegacy.data.lang.Lang_EN;
import xueluoanping.fluiddrawerslegacy.data.lang.Lang_ZH;
import xueluoanping.fluiddrawerslegacy.data.loot.LFTLootTableProvider;
import xueluoanping.fluiddrawerslegacy.data.recipe.RecipeDataProvider;
import xueluoanping.fluiddrawerslegacy.data.tag.FDLItemTagsProvider;
import xueluoanping.fluiddrawerslegacy.data.tag.TagsDataProvider;

import java.util.concurrent.CompletableFuture;


public class start {
    public final static String MODID = FluidDrawersLegacyMod.MOD_ID;

    public static void dataGen(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        ExistingFileHelper helper = event.getExistingFileHelper();
        PackOutput packOutput = generator.getPackOutput();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();
        if (event.includeServer()) {
            FluidDrawersLegacyMod.logger("Generate We Data!!!");

            generator.addProvider(event.includeServer(),new RecipeDataProvider(packOutput));
            TagsDataProvider blockTags = new TagsDataProvider(packOutput,lookupProvider, MODID, helper);
            generator.addProvider(event.includeServer(),blockTags);
            generator.addProvider(event.includeServer(),new FDLItemTagsProvider(packOutput, lookupProvider, blockTags.contentsGetter()));

            generator.addProvider(event.includeServer(),new LFTLootTableProvider(packOutput));
            // generator.addProvider(new GLMProvider(generator, MODID));

            // generator.addProvider(event.includeServer(),new Lang_EN(packOutput, helper));
            // generator.addProvider(event.includeServer(),new Lang_ZH(packOutput, helper));

            // generator.addProvider(new SimpleMP(generator));

        }
        if (event.includeClient()) {
            generator.addProvider(event.includeClient(),new BlockStatesDataProvider(packOutput,helper));
            generator.addProvider(event.includeClient(),new ItemModelProvider(packOutput,helper));
            generator.addProvider(event.includeClient(),new Lang_EN(packOutput,helper));
            generator.addProvider(event.includeClient(),new Lang_ZH(packOutput,helper));
        }


    }
}
