package xueluoanping.fluiddrawerslegacy.data;

import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;

import java.util.function.Consumer;


public class RecipeDataProvider extends RecipeProvider {
	public RecipeDataProvider(PackOutput generator) {
		super(generator);
	}

	@Override
	protected void buildRecipes(Consumer<FinishedRecipe> consumer) {
	}

}
