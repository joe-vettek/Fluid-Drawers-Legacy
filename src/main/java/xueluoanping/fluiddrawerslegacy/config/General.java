package xueluoanping.fluiddrawerslegacy.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class General {
    public static ForgeConfigSpec COMMON_CONFIG;
    public static ForgeConfigSpec.BooleanValue bool;
    public static ForgeConfigSpec.IntValue volume;
    public static ForgeConfigSpec.BooleanValue createPotion;
    static {
        ForgeConfigSpec.Builder COMMON_BUILDER = new ForgeConfigSpec.Builder();
        COMMON_BUILDER.comment("Debug settings").push("debugMode");
        bool = COMMON_BUILDER.comment("Set false to stop output log.").define("debugMode",false);
        COMMON_BUILDER.pop();

        COMMON_BUILDER.comment("General settings").push("general");
        volume=COMMON_BUILDER.comment("Set it to change volume.").defineInRange("volume",32000,4000,96000);
        COMMON_BUILDER.pop();
        COMMON_CONFIG = COMMON_BUILDER.build();

        COMMON_BUILDER.comment("Compat settings").push("create");
        createPotion=COMMON_BUILDER.comment("Allow players to use potion bottles to deposit potions directly into the fluid drawer in survival mode..").define("createPotionInteraction",true);
        COMMON_BUILDER.pop();
        COMMON_CONFIG = COMMON_BUILDER.build();
    }
}
