package xueluoanping.fluiddrawerslegacy.data.recipe;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.Tags;
import xueluoanping.fluiddrawerslegacy.FluidDrawersLegacyMod;
import xueluoanping.fluiddrawerslegacy.util.RegisterFinderUtil;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;


public class RecipeDataProvider extends RecipeProvider {
    public RecipeDataProvider(PackOutput generator, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(generator, lookupProvider);
    }

    @Override
    protected void buildRecipes(RecipeOutput pRecipeOutput, HolderLookup.Provider holderLookup) {
        super.buildRecipes(pRecipeOutput, holderLookup);

        buildFluidDrawerRecipe(pRecipeOutput, "", 1, "///", " X ", "///");
        buildFluidDrawerRecipe(pRecipeOutput, "_2", 2, "/X/", "///", "/X/");
        buildFluidDrawerRecipe(pRecipeOutput, "_4", 4, "X/X", "///", "X/X");
        buildFluidDrawerRecipeHalf(pRecipeOutput, "_half", 1, "///", " X ", "///");
        buildFluidDrawerRecipeHalf(pRecipeOutput, "_2_half", 2, "/X/", "///", "/X/");
        buildFluidDrawerRecipeHalf(pRecipeOutput, "_4_half", 4, "X/X", "///", "X/X");

    }


    private void buildFluidDrawerRecipe(RecipeOutput pRecipeOutput, String countString, int count, String... pattern) {
        var a = ShapedRecipeBuilder.shaped(RecipeCategory.MISC, getDrawerWith(countString), count);
        for (String s : pattern) {
            a = a.pattern(s);
        }
        a.define('/', Tags.Items.GLASS_BLOCKS)
                .define('X', Items.BUCKET)
                .unlockedBy("has_bucket", has(Items.BUCKET))
                .save(pRecipeOutput);
    }

    private void buildFluidDrawerRecipeHalf(RecipeOutput pRecipeOutput, String countString, int count, String... pattern) {
        var a = ShapedRecipeBuilder.shaped(RecipeCategory.MISC, getDrawerWith(countString), count);
        for (String s : pattern) {
            a = a.pattern(s);
        }
        a.define('/', Tags.Items.GLASS_PANES)
                .define('X', Items.BUCKET)
                .unlockedBy("has_bucket", has(Items.BUCKET))
                .save(pRecipeOutput);
    }

    private Item getDrawerWith(String countString) {
        return RegisterFinderUtil.getItem(FluidDrawersLegacyMod.rl("fluiddrawer" + countString));
    }

}
