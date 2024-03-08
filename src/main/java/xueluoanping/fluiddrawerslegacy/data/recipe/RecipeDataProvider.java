package xueluoanping.fluiddrawerslegacy.data.recipe;

import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.Tags;
import xueluoanping.fluiddrawerslegacy.FluidDrawersLegacyMod;
import xueluoanping.fluiddrawerslegacy.util.RegisterFinderUtil;

import java.util.function.Consumer;


public class RecipeDataProvider extends RecipeProvider {
    public RecipeDataProvider(PackOutput generator) {
        super(generator);
    }

    @Override
    protected void buildRecipes(Consumer<FinishedRecipe> consumer) {

        buildFluidDrawerRecipe(consumer, "", 1, "///", " X ", "///");
        buildFluidDrawerRecipe(consumer, "_2", 2, "/X/", "///", "/X/");
        buildFluidDrawerRecipe(consumer, "_4", 4, "X/X", "///", "X/X");
        buildFluidDrawerRecipeHalf(consumer, "_half", 1, "///", " X ", "///");
        buildFluidDrawerRecipeHalf(consumer, "_2_half", 2, "/X/", "///", "/X/");
        buildFluidDrawerRecipeHalf(consumer, "_4_half", 4, "X/X", "///", "X/X");

    }
    private void buildFluidDrawerRecipe(Consumer<FinishedRecipe> consumer, String countString, int count, String... pattern) {
        var a = ShapedRecipeBuilder.shaped(RecipeCategory.MISC, getDrawerWith(countString), count);
        for (String s : pattern) {
            a = a.pattern(s);
        }
        a.define('/', Tags.Items.GLASS)
                .define('X', Items.BUCKET)
                .unlockedBy("has_bucket", has(Items.BUCKET))
                .save(consumer);
    }
    private void buildFluidDrawerRecipeHalf(Consumer<FinishedRecipe> consumer, String countString, int count, String... pattern) {
        var a = ShapedRecipeBuilder.shaped(RecipeCategory.MISC, getDrawerWith(countString), count);
        for (String s : pattern) {
            a = a.pattern(s);
        }
        a.define('/', Tags.Items.GLASS_PANES)
                .define('X', Items.BUCKET)
                .unlockedBy("has_bucket", has(Items.BUCKET))
                .save(consumer);
    }

    private Item getDrawerWith(String countString) {
        return RegisterFinderUtil.getItem(FluidDrawersLegacyMod.rl("fluiddrawer" + countString));
    }

}
