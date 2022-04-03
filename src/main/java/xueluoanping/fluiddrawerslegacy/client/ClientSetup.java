package xueluoanping.fluiddrawerslegacy.client;


import com.jaquadro.minecraft.storagedrawers.client.renderer.TileEntityDrawersRenderer;
import com.jaquadro.minecraft.storagedrawers.core.ModBlocks;
import com.mojang.blaze3d.platform.ScreenManager;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;

import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.ClientRegistry;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
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
    public static void onClientEvent(FMLClientSetupEvent event) {
        FluidDrawersLegacyMod.logger("Register Client");
        event.enqueueWork(() -> {
            ItemBlockRenderTypes.setRenderLayer(ModContents.fluiddrawer, ClientSetup::isGlassLanternValidLayer);
            MenuScreens.register(ModContents.containerType, Screen.Slot1::new);
        });
    }

//    注意static是单次，比如启动类，没有比如右击事件
    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
        FluidDrawersLegacyMod.logger("Register Renderer");
        event.registerBlockEntityRenderer(ModContents.tankTileEntityType, TESRFluidDrawer::new);
    }


    @SubscribeEvent
    public static void onModelBaked(ModelBakeEvent event) {
        Map<ResourceLocation, BakedModel> modelRegistry = event.getModelRegistry();
        ModelResourceLocation location = new ModelResourceLocation(ModContents.itemBlock.getRegistryName(), "inventory");
        BakedModel existingModel = modelRegistry.get(location);
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
