package xueluoanping.fluiddrawerslegacy;


import com.jaquadro.minecraft.storagedrawers.integration.TheOneProbe;
import com.mojang.datafixers.types.Type;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.item.*;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import xueluoanping.fluiddrawerslegacy.api.capability.CapabilityProvider_FluidDrawerController;
import xueluoanping.fluiddrawerslegacy.block.BlockFluidDrawer;
import xueluoanping.fluiddrawerslegacy.block.ItemFluidDrawer;
import xueluoanping.fluiddrawerslegacy.api.event.ControllerFluidCapabilityHandler;
import xueluoanping.fluiddrawerslegacy.block.tileentity.TileEntityFluidDrawer;
import xueluoanping.fluiddrawerslegacy.client.model.FluidDrawerBakedModel;
import xueluoanping.fluiddrawerslegacy.client.render.TESRFluidDrawer;
import xueluoanping.fluiddrawerslegacy.config.General;


import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(FluidDrawersLegacyMod.MOD_ID)
public class FluidDrawersLegacyMod {
    public static final ItemGroup CREATIVE_TAB = new ItemGroup("fluiddrawers") {
        @Override
        public ItemStack makeIcon() {
            return ModContents.itemBlock.getDefaultInstance();
        }

        @Override
        public void fillItemList(NonNullList<ItemStack> itemStackNonNullList) {
//            itemStackNonNullList.add(0, RegistryEvents.fluiddrawer.asItem().getDefaultInstance());
            super.fillItemList(itemStackNonNullList);
        }
    };
    public static final String MOD_ID = "fluiddrawerslegacy";


    // Directly reference a log4j logger.
    public static final Logger LOGGER = LogManager.getLogger(FluidDrawersLegacyMod.MOD_ID);
    private static boolean DebugMode = false;

    public static void logger(String x) {
        if (General.bool.get())
            LOGGER.info(x);
    }

    public FluidDrawersLegacyMod() {
        // Register the setup method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        // Register the enqueueIMC method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::enqueueIMC);
        // Register the processIMC method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::processIMC);
        // Register the doClientStuff method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::doClientStuff);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
//        MinecraftForge.EVENT_BUS.register(ModContents.instance);
        MinecraftForge.EVENT_BUS.register(ControllerFluidCapabilityHandler.instance);
//        MinecraftForge.EVENT_BUS.register(CapabilityProvider_FluidDrawerController.instance);
//        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onModQueueEvent);
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, General.COMMON_CONFIG);

    }

    private void setup(final FMLCommonSetupEvent event) {
        // some preinit code
//        LOGGER.info("HELLO FROM PREINIT");
//        LOGGER.info("DIRT BLOCK >> {}", Blocks.DIRT.getRegistryName());


    }

    private void doClientStuff(final FMLClientSetupEvent event) {
        // do something that can only be done on the client
//        LOGGER.info("Got game settings {}", event.getMinecraftSupplier().get().options);
    }

    private void enqueueIMC(final InterModEnqueueEvent event) {
        // some example code to dispatch IMC to another mod
//        InterModComms.sendTo("examplemod", "helloworld", () -> {
//            LOGGER.info("Hello world from the MDK");
//            return "Hello world";
//        });
    }

    private void processIMC(final InterModProcessEvent event) {
        // some example code to receive and process InterModComms from other mods
//        LOGGER.info("Got IMC {}", event.getIMCStream().
//                map(m -> m.getMessageSupplier().get()).
//                collect(Collectors.toList()));
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(FMLServerStartingEvent event) {
        // do something when the server starts
//        LOGGER.info("HELLO from server starting");

    }

//    @SuppressWarnings("Convert2MethodRef")  // otherwise the class loader gets upset if TheOneProbe is not loaded
//    private void onModQueueEvent(final InterModEnqueueEvent event) {
//        InterModComms.sendTo("theoneprobe", "getTheOneProbe", () -> new OneProbe());
//    }

}
