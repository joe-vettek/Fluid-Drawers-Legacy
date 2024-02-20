package xueluoanping.fluiddrawerslegacy.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class General {
    public static ForgeConfigSpec COMMON_CONFIG;
    public static ForgeConfigSpec.BooleanValue bool;
    public static ForgeConfigSpec.IntValue volume;
    public static ForgeConfigSpec.BooleanValue createPotion;
    public static ForgeConfigSpec.BooleanValue cauponaSoupBowl;
    static {
        ForgeConfigSpec.Builder COMMON_BUILDER = new ForgeConfigSpec.Builder();
        COMMON_BUILDER.comment("Debug settings").push("debugMode");
        bool = COMMON_BUILDER.comment("Set false to stop output log.").define("debugMode",false);
        COMMON_BUILDER.pop();

        COMMON_BUILDER.comment("General settings").push("general");
        volume=COMMON_BUILDER.comment("Set it to change volume.").defineInRange("volume",32000,4000,96000);
        COMMON_BUILDER.pop();
        // COMMON_CONFIG = COMMON_BUILDER.build();

        COMMON_BUILDER.comment("Compat settings").push("compat");
        createPotion=COMMON_BUILDER.comment("Allow players to use potion bottles to deposit potions directly in survival mode.").define("createPotionInteraction",true);
        // COMMON_BUILDER.pop();
        // COMMON_CONFIG = COMMON_BUILDER.build();

        // COMMON_BUILDER.comment("Compat settings").push("compat");
        cauponaSoupBowl=COMMON_BUILDER.comment("Allow players to use caupona soup bowl to deposit soup directly in survival mode.").define("cauponaSoupBowlInteraction",true);
        COMMON_BUILDER.pop();
        COMMON_CONFIG = COMMON_BUILDER.build();
    }
}
