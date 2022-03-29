package xueluoanping.fluiddrawerslegacy;


import net.minecraft.item.*;
import net.minecraft.util.NonNullList;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
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

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, General.COMMON_CONFIG);

    }


}
