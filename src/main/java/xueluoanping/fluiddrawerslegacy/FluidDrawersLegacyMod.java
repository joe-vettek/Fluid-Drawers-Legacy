package xueluoanping.fluiddrawerslegacy;


import net.minecraft.item.*;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
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
import xueluoanping.fluiddrawerslegacy.event.ControllerFluidCapabilityHandler;
import xueluoanping.fluiddrawerslegacy.config.General;

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
