package xueluoanping.fluiddrawerslegacy.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class ClientConfig {
    public static ForgeConfigSpec CLIENT_CONFIG;
    public static ForgeConfigSpec.IntValue distance;

    static {
        ForgeConfigSpec.Builder CLIENT_BUILDER = new ForgeConfigSpec.Builder();
        CLIENT_BUILDER.comment("Client settings").push("Renderer");
        distance = CLIENT_BUILDER.comment("Set the label renderer distance, -1 stands for wireless distance.").defineInRange("distance",-1,-1,1024);
        CLIENT_BUILDER.pop();
        CLIENT_CONFIG = CLIENT_BUILDER.build();
    }
}
