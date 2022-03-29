package xueluoanping.fluiddrawerslegacy.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;

import javax.annotation.Nullable;

/** Class to add one level of static indirection to client only lookups */
public class SafeClientAccess {
  /** Gets the currently pressed key for tooltips, returns UNKNOWN on a server */
  public static TooltipKey getTooltipKey() {
    if (FMLEnvironment.dist == Dist.CLIENT) {
      return ClientOnly.getPressedKey();
    }
    return TooltipKey.UNKNOWN;
  }

  /** Gets the client player entity, or null on a server */
  @Nullable
  public static PlayerEntity getPlayer() {
    if (FMLEnvironment.dist == Dist.CLIENT) {
      return ClientOnly.getClientPlayer();
    }
    return null;
  }

  /** This class is only loaded on the client, so is safe to reference client only methods */
  private static class ClientOnly {
    /** Gets the currently pressed key modifier for tooltips */
    public static TooltipKey getPressedKey() {
      if (Screen.hasShiftDown()) {
        return TooltipKey.SHIFT;
      }
      if (Screen.hasControlDown()) {
        return TooltipKey.CONTROL;
      }
      if (Screen.hasAltDown()) {
        return TooltipKey.ALT;
      }
      return TooltipKey.NORMAL;
    }

    /** Gets the client player instance */
    @Nullable
    public static PlayerEntity getClientPlayer() {
      return Minecraft.getInstance().player;
    }
  }
}
