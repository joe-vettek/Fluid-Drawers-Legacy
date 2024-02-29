package xueluoanping.fluiddrawerslegacy.client.util;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.fluids.FluidStack;
import xueluoanping.fluiddrawerslegacy.client.render.XYZ;

import java.util.ArrayList;

import static net.minecraft.world.inventory.InventoryMenu.BLOCK_ATLAS;

public class TankRenderUtil {
    public static void addVertex(VertexConsumer renderer, PoseStack stack, float x, float y, float z, float u, float v, int RGBA, float alpha, int brightness) {
        float red = ((RGBA >> 16) & 0xFF) / 255f;
        float green = ((RGBA >> 8) & 0xFF) / 255f;
        float blue = ((RGBA >> 0) & 0xFF) / 255f;
        //		renderer.vertex(stack.last().pose(), x, y, z).color(red, green, blue, alpha).uv(u, v).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(15728880)/*.lightmap(0, 240)*/.normal(stack.last().normal(), 0, 1.0F, 0).endVertex();
        int light1 = brightness & '\uffff';
        int light2 = brightness >> 16 & '\uffff';
        renderer.vertex(stack.last().pose(), x, y, z).color(red, green, blue, alpha).uv(u, v).uv2(light1, light2).overlayCoords(OverlayTexture.NO_OVERLAY).normal(stack.last().normal(), 0, 1.0F, 0).endVertex();
    }

    public static void renderFluid(ArrayList<TankHolder> fluidStacks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLight, double animationTime) {

        int count=fluidStacks.size();
        int slot=0;
        for (TankHolder holder : fluidStacks) {
            var fluidStackDown=holder.fluidStackDown();
            int capacity= holder.capacity();
            slot++;
            if (fluidStackDown.isEmpty() && capacity > 0) continue;

            Minecraft mc = Minecraft.getInstance();
            TextureAtlasSprite still = mc.getTextureAtlas(BLOCK_ATLAS).apply(IClientFluidTypeExtensions.of(fluidStackDown.getFluid()).getStillTexture(fluidStackDown));

            RenderSystem.setShaderTexture(0, BLOCK_ATLAS);
            int colorRGB = IClientFluidTypeExtensions.of(fluidStackDown.getFluid()).getTintColor(fluidStackDown);

            int amount = fluidStackDown.getAmount();
//             int amount = tile.fluidAnimation.getAndUpdateLastFluidAmount(tile.getTankFLuid().getAmount(), animationTime);
            // FluidDrawersLegacyMod.logger(""+animationTime);
            if (capacity < amount) amount = capacity;

            float r = (float) amount / (float) capacity;

            float maxHeight = 0.87f;
            float height = r * maxHeight;
            float width = 0.872f;
            float didw = 0.0625f;
            float didh = 0.0625f;
            float x0 = 0.064f;
            float y0 = 0.064f;
            float z0 = 0.064f;
            float x1 = 0.9360f;
            float y1 = 0.064f;
            float z1 = 0.9360f;

            float u0=still.getU0();
            float u1=still.getU1();
            float du=u1-u0;
            float v0=still.getV0();
            float v1=still.getV1();
            float dv=v1-v0;

            float uHeight = (still.getU1() - still.getU0()) * (1f - r);
            float uHeight2 = (still.getU1() - still.getU0()) * r;


            if (count == 4) {
                int orderY = 0;
                int orderX = 0;
                switch (slot) {
                    case 1 -> {
                        orderX = 0;
                        orderY = 1;
                    }
                    case 2 -> {
                        orderX = 1;
                        orderY = 1;
                    }
                    case 3 -> {
                        orderX = 0;
                        orderY = 0;
                    }
                    case 4 -> {
                        orderX = 1;
                        orderY = 0;
                    }
                }
                // x0 += (1-orderX) * (didw + width/2);
                // x1 += (1 - orderX) * (didw) + (orderX - 1) * width / 2;
                x0 = orderX == 0 ? x0 + (width + didw) / 2 : x0;
                x1 = orderX == 0 ? x1 : x1 - (width + didw) / 2;

                y0 += orderY * (didh + maxHeight / 2);
                y1 += y0 + height / 2 + (orderY - 1) * didh;

                // u0= orderY == 0 ? u0 : u0+du/2;
                // u1= orderY == 0 ? u1-du/2 : u1;
                //
                // v0= orderX == 1 ? v0 : v0+dv/2;
                // v1= orderX == 1 ? v1-dv/2 : v1;
                // uHeight=uHeight/2;

            } else if (count == 2) {
                int orderY = slot == 1 ? 1 : 0;
                y0 += orderY * (didh + maxHeight / 2);
                y1 += y0 + height / 2 + (orderY - 1) * didh;
                // uHeight=uHeight/2;
                //
                // u0= slot == 2 ? u0 : u0+du/2;
                // u1= slot == 2 ? u1-du/2 : u1;
            } else {
                y1 += y0 + height;
            }


            matrixStackIn.pushPose();
            GlStateManager._disableCull();
            VertexConsumer buffer = bufferIn.getBuffer(RenderType.translucent());
            // matrixStackIn.translate(0,0,0);
            // matrixStackIn.mulPose(XYZ.deg_to_rad(0, 90, 0));

            //
            // 1,0 should convart

            // Bottom
            addVertex(buffer, matrixStackIn, x1, y0, z1, u1, v1, colorRGB, 1.0f, combinedLight);
            addVertex(buffer, matrixStackIn, x0, y0, z1, u1, v0, colorRGB, 1.0f, combinedLight);
            addVertex(buffer, matrixStackIn, x0, y0, z0, u0, v0, colorRGB, 1.0f, combinedLight);
            addVertex(buffer, matrixStackIn, x1, y0, z0, u0, v1, colorRGB, 1.0f, combinedLight);

            // Top
            addVertex(buffer, matrixStackIn, x0, y1, z0, u0, v0, colorRGB, 1.0f, combinedLight);
            addVertex(buffer, matrixStackIn, x0, y1, z1, u1, v0, colorRGB, 1.0f, combinedLight);
            addVertex(buffer, matrixStackIn, x1, y1, z1, u1, v1, colorRGB, 1.0f, combinedLight);
            addVertex(buffer, matrixStackIn, x1, y1, z0, u0, v1, colorRGB, 1.0f, combinedLight);

            // Front
            addVertex(buffer, matrixStackIn, x0, y0, z0, u0+uHeight, v0, colorRGB, 1.0f, combinedLight);
            addVertex(buffer, matrixStackIn, x0, y1, z0, u1, v0, colorRGB, 1.0f, combinedLight);
            addVertex(buffer, matrixStackIn, x1, y1, z0, u1, v1, colorRGB, 1.0f, combinedLight);
            addVertex(buffer, matrixStackIn, x1, y0, z0, u0+uHeight, v1, colorRGB, 1.0f, combinedLight);

            // Right(for block)
            addVertex(buffer, matrixStackIn, x1, y0, z0, u0+uHeight, v0, colorRGB, 1.0f, combinedLight);
            addVertex(buffer, matrixStackIn, x1, y1, z0, u1, v0, colorRGB, 1.0f, combinedLight);
            addVertex(buffer, matrixStackIn, x1, y1, z1, u1, v1, colorRGB, 1.0f, combinedLight);
            addVertex(buffer, matrixStackIn, x1, y0, z1, u0+uHeight, v1, colorRGB, 1.0f, combinedLight);

            // Behind
            addVertex(buffer, matrixStackIn, x0, y0, z1, u1-uHeight, v1, colorRGB, 1.0f, combinedLight);
            addVertex(buffer, matrixStackIn, x1, y0, z1, u1-uHeight, v0, colorRGB, 1.0f, combinedLight);
            addVertex(buffer, matrixStackIn, x1, y1, z1, u0, v0, colorRGB, 1.0f, combinedLight);
            addVertex(buffer, matrixStackIn, x0, y1, z1, u0, v1, colorRGB, 1.0f, combinedLight);

            // Left(for block)
            addVertex(buffer, matrixStackIn, x0, y0, z0, u0+uHeight, v0, colorRGB, 1.0f, combinedLight);
            addVertex(buffer, matrixStackIn, x0, y0, z1, u0+uHeight, v1, colorRGB, 1.0f, combinedLight);
            addVertex(buffer, matrixStackIn, x0, y1, z1, u1, v1, colorRGB, 1.0f, combinedLight);
            addVertex(buffer, matrixStackIn, x0, y1, z0, u1, v0, colorRGB, 1.0f, combinedLight);

            // addVertex(buffer, matrixStackIn, 0.064f, 0.064f + height, 0.064f, still.getU0(), still.getV0(), colorRGB, 1.0f, combinedLight);
            // addVertex(buffer, matrixStackIn, 0.064f, 0.064f + height, 0.9360f, still.getU1(), still.getV0(), colorRGB, 1.0f, combinedLight);
            // addVertex(buffer, matrixStackIn, 0.9360f, 0.064f + height, 0.9360f, still.getU1(), still.getV1(), colorRGB, 1.0f, combinedLight);
            // addVertex(buffer, matrixStackIn, 0.9360f, 0.064f + height, 0.064f, still.getU0(), still.getV1(), colorRGB, 1.0f, combinedLight);
            //
            // addVertex(buffer, matrixStackIn, 0.9360f, 0.064f + height, 0.064f, still.getU0(), still.getV0() + vHeight, colorRGB, 1.0f, combinedLight);
            // addVertex(buffer, matrixStackIn, 0.9360f, 0.064f + height, 0.9360f, still.getU1(), still.getV0() + vHeight, colorRGB, 1.0f, combinedLight);
            // addVertex(buffer, matrixStackIn, 0.9360f, 0.064f, 0.9360f, still.getU1(), still.getV1(), colorRGB, 1.0f, combinedLight);
            // addVertex(buffer, matrixStackIn, 0.9360f, 0.064f, 0.064f, still.getU0(), still.getV1(), colorRGB, 1.0f, combinedLight);
            //
            // addVertex(buffer, matrixStackIn, 0.064f, 0.064f, 0.064f, still.getU0(), still.getV0(), colorRGB, 1.0f, combinedLight);
            // addVertex(buffer, matrixStackIn, 0.064f, 0.064f, 0.9360f, still.getU1(), still.getV0(), colorRGB, 1.0f, combinedLight);
            // addVertex(buffer, matrixStackIn, 0.064f, 0.064f + height, 0.9360f, still.getU1(), still.getV1() - vHeight, colorRGB, 1.0f, combinedLight);
            // addVertex(buffer, matrixStackIn, 0.064f, 0.064f + height, 0.064f, still.getU0(), still.getV1() - vHeight, colorRGB, 1.0f, combinedLight);
            //
            // addVertex(buffer, matrixStackIn, 0.064f, 0.064f + height, 0.064f, still.getU0(), still.getV0() + vHeight, colorRGB, 1.0f, combinedLight);
            // addVertex(buffer, matrixStackIn, 0.9360f, 0.064f + height, 0.064f, still.getU1(), still.getV0() + vHeight, colorRGB, 1.0f, combinedLight);
            // addVertex(buffer, matrixStackIn, 0.9360f, 0.064f, 0.064f, still.getU1(), still.getV1(), colorRGB, 1.0f, combinedLight);
            // addVertex(buffer, matrixStackIn, 0.064f, 0.064f, 0.064f, still.getU0(), still.getV1(), colorRGB, 1.0f, combinedLight);
            //
            //
            // addVertex(buffer, matrixStackIn, 0.064f, 0.064f, 0.9360f, still.getU0(), still.getV0() + vHeight, colorRGB, 1.0f, combinedLight);
            // addVertex(buffer, matrixStackIn, 0.9360f, 0.064f, 0.9360f, still.getU1(), still.getV0() + vHeight, colorRGB, 1.0f, combinedLight);
            // addVertex(buffer, matrixStackIn, 0.9360f, 0.064f + height, 0.9360f, still.getU1(), still.getV1(), colorRGB, 1.0f, combinedLight);
            // addVertex(buffer, matrixStackIn, 0.064f, 0.064f + height, 0.9360f, still.getU0(), still.getV1(), colorRGB, 1.0f, combinedLight);


            GlStateManager._enableCull();
            matrixStackIn.popPose();
        }
    }

    public static TankHolder of(FluidStack stack, int cap) {
        return new TankHolder(stack, cap);
    }
}
