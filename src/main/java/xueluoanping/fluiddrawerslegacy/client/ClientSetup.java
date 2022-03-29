package xueluoanping.fluiddrawerslegacy.client;

import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import xueluoanping.fluiddrawerslegacy.FluidDrawersLegacyMod;
import xueluoanping.fluiddrawerslegacy.ModContents;
import xueluoanping.fluiddrawerslegacy.client.gui.Screen;
import xueluoanping.fluiddrawerslegacy.client.model.BakedModelFluidDrawer;
import xueluoanping.fluiddrawerslegacy.client.render.TESRFluidDrawer;

import java.util.Map;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientSetup {


    // does the Glass Lantern render in the given layer (RenderType) - used as Predicate<RenderType> lambda for setRenderLayer
    public static boolean isGlassLanternValidLayer(RenderType layerToCheck) {
        return layerToCheck == RenderType.cutoutMipped() || layerToCheck == RenderType.translucent();
    }

    @SubscribeEvent
    public static void registerModels(ModelBakeEvent event) {
//            ClientRegistry.bindTileEntityRenderer(ModBlocks.Tile.STANDARD_DRAWERS_1, TileEntityDrawersRenderer::new);
    }

    @SubscribeEvent
    public static void onClientEvent(FMLClientSetupEvent event) {
        FluidDrawersLegacyMod.logger("Register Client");
        event.enqueueWork(() -> {
            RenderTypeLookup.setRenderLayer(ModContents.fluiddrawer, ClientSetup::isGlassLanternValidLayer);

            ClientRegistry.bindTileEntityRenderer(ModContents.tankTileEntityType, TESRFluidDrawer::new);
            ScreenManager.register(ModContents.containerType, Screen.Slot1::new);

        });
    }

    @SubscribeEvent
    public static void onModelBaked(ModelBakeEvent event) {
        Map<ResourceLocation, IBakedModel> modelRegistry = event.getModelRegistry();
        ModelResourceLocation location = new ModelResourceLocation(ModContents.itemBlock.getRegistryName(), "inventory");
        IBakedModel existingModel = modelRegistry.get(location);
        if (existingModel == null) {
            throw new RuntimeException("Did not find in registry");
        } else if (existingModel instanceof BakedModelFluidDrawer) {
            throw new RuntimeException("Tried to replace twice");
        } else {
            BakedModelFluidDrawer model = new BakedModelFluidDrawer(existingModel);
            event.getModelRegistry().put(location, model);
        }
    }
}
