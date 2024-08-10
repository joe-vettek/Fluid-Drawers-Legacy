package xueluoanping.fluiddrawerslegacy.client;


// import com.jaquadro.minecraft.storagedrawers.client.renderer.TileEntityDrawersRenderer;
import com.jaquadro.minecraft.storagedrawers.core.ModBlocks;
import com.mojang.blaze3d.platform.ScreenManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;

import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.jetbrains.annotations.NotNull;
import xueluoanping.fluiddrawerslegacy.FluidDrawersLegacyMod;
import xueluoanping.fluiddrawerslegacy.ModContents;
import xueluoanping.fluiddrawerslegacy.block.blockentity.BlockEntityFluidDrawer;
import xueluoanping.fluiddrawerslegacy.client.gui.Screen;
import xueluoanping.fluiddrawerslegacy.client.model.BakedModelFluidDrawer;
import xueluoanping.fluiddrawerslegacy.client.render.FluidDrawerItemStackTileEntityRenderer;
import xueluoanping.fluiddrawerslegacy.client.render.TESRFluidDrawer;

import java.util.Map;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientSetup {


    // does the Glass Lantern render in the given layer (RenderType) - used as Predicate<RenderType> lambda for setRenderLayer
    public static boolean isGlassLanternValidLayer(RenderType layerToCheck) {
        return layerToCheck == RenderType.cutoutMipped() || layerToCheck == RenderType.translucent();
    }

    @SubscribeEvent
    public static void onClientEvent(RegisterClientExtensionsEvent event) {
        for (DeferredHolder<Item, ? extends Item> entry : ModContents.DREntityBlockItems.getEntries()) {
            event.registerItem(new IClientItemExtensions() {
                @Override
                public @NotNull BlockEntityWithoutLevelRenderer getCustomRenderer() {
                    return new FluidDrawerItemStackTileEntityRenderer(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());

                }
            },entry.value().asItem());

        }
    }

    @SubscribeEvent
    public static void onClientEvent(FMLClientSetupEvent event) {
        FluidDrawersLegacyMod.logger("Register Client");
        event.enqueueWork(() -> {
            // ItemBlockRenderTypes.setRenderLayer(ModContents.fluiddrawer.get(), ClientSetup::isGlassLanternValidLayer);
            MenuScreens.register(ModContents.containerType.get(), Screen.Slot1::new);
        });
    }

//    注意static是单次，比如启动类，没有比如右击事件
    @SubscribeEvent
    public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
        // FluidDrawersLegacyMod.logger("Register Renderer");
        ModContents.DRBlockEntities.getEntries().forEach((reg) -> {
            event.registerBlockEntityRenderer((BlockEntityType<BlockEntityFluidDrawer>)reg.get(),
                    TESRFluidDrawer::new);
        });
    }


    @SubscribeEvent
    public static void onModelBaked(ModelEvent.ModifyBakingResult event) {
        Map<ModelResourceLocation, BakedModel> modelRegistry = event.getModels();

        ModContents.DREntityBlockItems.getEntries().forEach((reg) -> {
            ModelResourceLocation location = new ModelResourceLocation(reg.getId(), "inventory");
            BakedModel existingModel = modelRegistry.get(location);
            if (existingModel == null) {
                throw new RuntimeException("Did not find in registry");
            } else if (existingModel instanceof BakedModelFluidDrawer) {
                throw new RuntimeException("Tried to replace twice");
            } else {
                BakedModelFluidDrawer model = new BakedModelFluidDrawer(existingModel);
                modelRegistry.put(location, model);
            }
        });
    }
}
