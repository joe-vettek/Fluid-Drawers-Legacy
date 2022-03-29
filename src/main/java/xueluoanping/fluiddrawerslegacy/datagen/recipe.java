package xueluoanping.fluiddrawerslegacy.datagen;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.IFinishedRecipe;
import net.minecraftforge.common.data.ForgeRecipeProvider;

import java.util.function.Consumer;

public class recipe extends ForgeRecipeProvider {
    public recipe(DataGenerator generatorIn) {
        super(generatorIn);
    }

    @Override
    protected void buildShapelessRecipes(Consumer<IFinishedRecipe> consumer) {
        super.buildShapelessRecipes(consumer);
    }
}
