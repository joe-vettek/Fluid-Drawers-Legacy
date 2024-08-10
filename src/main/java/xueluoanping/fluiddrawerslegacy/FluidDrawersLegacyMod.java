package xueluoanping.fluiddrawerslegacy;


import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import xueluoanping.fluiddrawerslegacy.config.ClientConfig;
import xueluoanping.fluiddrawerslegacy.config.General;
import xueluoanping.fluiddrawerslegacy.data.start;
import xueluoanping.fluiddrawerslegacy.handler.ControllerFluidCapabilityHandler;

import java.util.List;
// import xueluoanping.fluiddrawerslegacy.handler.ControllerFluidCapabilityHandler;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(FluidDrawersLegacyMod.MOD_ID)
public class FluidDrawersLegacyMod {
    public static final String MOD_ID = "fluiddrawerslegacy";
    // Directly reference a log4j logger.
    public static final Logger LOGGER = LogManager.getLogger(FluidDrawersLegacyMod.MOD_ID);

    public static void logger(String x) {
        if (!FMLEnvironment.production || General.bool.get()) {
//            LOGGER.debug(x);
            LOGGER.info(x);
        }
    }

    public static void logger(Object... x) {

        if (!FMLEnvironment.production || General.bool.get()) {
            StringBuilder output = new StringBuilder();

            for (Object i : x) {
                if (i == null) output.append(", ").append("null");
                else if (i.getClass().isArray()) {
                    output.append(", [");
                    for (Object c : (int[]) i) {
                        output.append(c).append(",");
                    }
                    output.append("]");
                } else if (i instanceof List) {
                    output.append(", [");
                    for (Object c : (List) i) {
                        output.append(c);
                    }
                    output.append("]");
                } else
                    output.append(", ").append(i);
            }
            LOGGER.info(output.substring(1));
        }

    }


    public FluidDrawersLegacyMod(IEventBus modEventBus, ModContainer modContainer) {

        modEventBus.register(ControllerFluidCapabilityHandler.instance);
        modEventBus.addListener(this::gatherData);

        // Register the Deferred Register to the mod event bus so blocks get registered
        ModContents.DREntityBlocks.register(modEventBus);
        ModContents.DREntityBlockItems.register(modEventBus);
        ModContents.DRBlockEntities.register(modEventBus);
        ModContents.DRBlockEntities.register(modEventBus);
        // Register ourselves for server and other game events we are interested in.
        // Note that this is necessary if and only if we want *this* class (ExampleMod) to respond directly to events.
        // Do not add this line if there are no @SubscribeEvent-annotated functions in this class, like onServerStarting() below.
        // NeoForge.EVENT_BUS.register(this);

        // Register the item to a creative tab
        // modContainer.addListener(this::gatherData);
        // modContainer.addListener(this::FMLCommonSetup);
        // Register our mod's ModConfigSpec so that FML can create and load the config file for us
        modContainer.registerConfig(ModConfig.Type.COMMON, General.COMMON_CONFIG);
        modContainer.registerConfig(ModConfig.Type.CLIENT, ClientConfig.CLIENT_CONFIG);
        ModContents.init();
    }


    public static ResourceLocation rl(String id) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, id);
    }

    public void gatherData(final GatherDataEvent event) {
        start.dataGen(event);
    }
}
