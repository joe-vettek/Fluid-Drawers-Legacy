package xueluoanping.fluiddrawerslegacy.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class ClientConfig {
    public static ForgeConfigSpec CLIENT_CONFIG;
    public static ForgeConfigSpec.IntValue distance;
    public static ForgeConfigSpec.IntValue showlimit;
    static {
        ForgeConfigSpec.Builder CLIENT_BUILDER = new ForgeConfigSpec.Builder();
        CLIENT_BUILDER.comment("Client settings").push("Renderer");
        distance = CLIENT_BUILDER.comment("Set the label renderer distance, -1 stands for unlimited distance.").defineInRange("distance",-1,-1,1024);
        CLIENT_BUILDER.pop();

        CLIENT_BUILDER.comment("Waila settings").push("Display");
        showlimit = CLIENT_BUILDER.comment("Set the fluid show amounts limit with controller and slave.").defineInRange("showlimit",9,3,24);
        CLIENT_BUILDER.pop();
        CLIENT_CONFIG = CLIENT_BUILDER.build();
    }
}
