package xueluoanping.fluiddrawerslegacy.handler;

import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import xueluoanping.fluiddrawerslegacy.capability.FluidDrawerControllerSave;

// @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.DEDICATED_SERVER)
public class Levelhandler {
    public static Levelhandler instance = new Levelhandler();

    @SubscribeEvent
    public void onLevelLoad(LevelEvent.Load event) {
    }

    @SubscribeEvent
    public void onLevelSave(LevelEvent.Save event) {

    }
}
