package xueluoanping.fluiddrawerslegacy;


import net.minecraft.item.*;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import xueluoanping.fluiddrawerslegacy.config.General;
import xueluoanping.fluiddrawerslegacy.handler.ControllerFluidCapabilityHandler;

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
        MinecraftForge.EVENT_BUS.register(ControllerFluidCapabilityHandler.instance);
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, General.COMMON_CONFIG);

    }

}
