package xueluoanping.fluiddrawerslegacy;



import net.minecraft.core.NonNullList;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import xueluoanping.fluiddrawerslegacy.client.ClientSetup;
import xueluoanping.fluiddrawerslegacy.config.ClientConfig;
import xueluoanping.fluiddrawerslegacy.config.General;
import xueluoanping.fluiddrawerslegacy.handler.ControllerFluidCapabilityHandler;

import java.util.List;
//import xueluoanping.fluiddrawerslegacy.handler.ControllerFluidCapabilityHandler;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(FluidDrawersLegacyMod.MOD_ID)
public class FluidDrawersLegacyMod {
    public static final CreativeModeTab CREATIVE_TAB = new CreativeModeTab("fluiddrawers") {
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
        {
//            LOGGER.debug(x);
            LOGGER.info(x);
        }
    }

    public static void logger(Object... x) {

        if (General.bool.get()) {
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

    public FluidDrawersLegacyMod() {

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);

        MinecraftForge.EVENT_BUS.register(ControllerFluidCapabilityHandler.instance);
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, General.COMMON_CONFIG);
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, ClientConfig.CLIENT_CONFIG);
    }

}
