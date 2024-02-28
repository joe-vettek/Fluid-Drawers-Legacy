package xueluoanping.fluiddrawerslegacy.client.util;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.texture.OverlayTexture;

public class FluidUtil {
    public static void addVertex(VertexConsumer renderer, PoseStack stack, float x, float y, float z, float u, float v, int RGBA, float alpha, int brightness) {
        float red = ((RGBA >> 16) & 0xFF) / 255f;
        float green = ((RGBA >> 8) & 0xFF) / 255f;
        float blue = ((RGBA >> 0) & 0xFF) / 255f;
        //		renderer.vertex(stack.last().pose(), x, y, z).color(red, green, blue, alpha).uv(u, v).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(15728880)/*.lightmap(0, 240)*/.normal(stack.last().normal(), 0, 1.0F, 0).endVertex();
        int light1 = brightness & '\uffff';
        int light2 = brightness >> 16 & '\uffff';
        renderer.vertex(stack.last().pose(), x, y, z).color(red, green, blue, alpha).uv(u, v).uv2(light1, light2).overlayCoords(OverlayTexture.NO_OVERLAY).normal(stack.last().normal(), 0, 1.0F, 0).endVertex();
    }

}
