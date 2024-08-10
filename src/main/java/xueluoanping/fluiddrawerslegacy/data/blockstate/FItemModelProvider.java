package xueluoanping.fluiddrawerslegacy.data.blockstate;

import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredHolder;
import xueluoanping.fluiddrawerslegacy.FluidDrawersLegacyMod;
import xueluoanping.fluiddrawerslegacy.ModContents;
import xueluoanping.fluiddrawerslegacy.util.RegisterFinderUtil;

public class FItemModelProvider extends ItemModelProvider {


	public static final String GENERATED = "item/generated";
	public static final String HANDHELD = "item/handheld";

	public FItemModelProvider(PackOutput generator, ExistingFileHelper existingFileHelper) {
		super(generator, FluidDrawersLegacyMod.MOD_ID, existingFileHelper);
	}


	@Override
	protected void registerModels() {
		for (DeferredHolder<Item, ? extends Item> entry : ModContents.DREntityBlockItems.getEntries()) {
			registerExistingCuisineBlockItem(entry);
		}

	}

	private void registerExistingCuisineBlockItem(DeferredHolder<Item, ? extends Item> registryObject) {
		withExistingParent(resourceItem( RegisterFinderUtil.getItemKey(registryObject.get()).getPath()).getPath(),
				BlockStatesDataProvider.resourceBlock(RegisterFinderUtil.getBlockKey(Block.byItem(registryObject.get())).getPath()));

	}

	private String itemName(Item item) {
		return RegisterFinderUtil.getItemKey(item).getPath();
	}

	public ResourceLocation resourceItem(String path) {
		return FluidDrawersLegacyMod.rl("item/" + path);
	}



}
