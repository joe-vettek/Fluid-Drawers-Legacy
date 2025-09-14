package xueluoanping.fluiddrawerslegacy.data.blockstate;

import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;
import xueluoanping.fluiddrawerslegacy.FluidDrawersLegacyMod;
import xueluoanping.fluiddrawerslegacy.ModContents;
import xueluoanping.fluiddrawerslegacy.block.framed.FramedBlockFluidDrawer;
import xueluoanping.fluiddrawerslegacy.util.RegisterFinderUtil;

public class ItemModelProvider extends net.minecraftforge.client.model.generators.ItemModelProvider {


    public static final String GENERATED = "item/generated";
    public static final String HANDHELD = "item/handheld";

    public ItemModelProvider(PackOutput generator, ExistingFileHelper existingFileHelper) {
        super(generator, FluidDrawersLegacyMod.MOD_ID, existingFileHelper);
    }


    @Override
    protected void registerModels() {
        for (RegistryObject<Item> entry : ModContents.DREntityBlockItems.getEntries()) {
            registerExistingCuisineBlockItem(entry);
        }

    }

    private void registerExistingCuisineBlockItem(RegistryObject<Item> registryObject) {
        Block block = Block.byItem(registryObject.get());
        block = block instanceof FramedBlockFluidDrawer framedBlockFluidDrawer ? ModContents.getReverse(framedBlockFluidDrawer) : block;

        withExistingParent(resourceItem(RegisterFinderUtil.getItemKey(registryObject.get()).getPath()).getPath(),
                BlockStatesDataProvider.resourceBlock(RegisterFinderUtil.getBlockKey(block).getPath()));
    }

    private String itemName(Item item) {
        return RegisterFinderUtil.getItemKey(item).getPath();
    }

    @SuppressWarnings("removal")
    public ResourceLocation resourceItem(String path) {
        return new ResourceLocation(FluidDrawersLegacyMod.MOD_ID, "item/" + path);
    }
}
