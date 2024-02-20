package xueluoanping.fluiddrawerslegacy.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraftforge.fluids.FluidStack;
import xueluoanping.fluiddrawerslegacy.FluidDrawersLegacyMod;
import xueluoanping.fluiddrawerslegacy.block.tileentity.TileEntityFluidDrawer;


public class FluidDrawerItemStackTileEntityRenderer extends ItemStackTileEntityRenderer {
    @Override
    public void renderByItem(ItemStack stack, ItemCameraTransforms.TransformType transformType, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlay) {
        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
        IBakedModel ibakedmodel = itemRenderer.getModel(stack, null, null);
        matrixStackIn.pushPose();
        matrixStackIn = rotateMatrix(matrixStackIn, transformType);
//        FluidDrawersLegacyMod.LOGGER.info(transformType+""+matrixStackIn.last().pose().toString());
        renderFluid(stack, matrixStackIn, bufferIn, combinedLightIn, 0);
        matrixStackIn.translate(0.5F, 0.5F, 0.5F);
        itemRenderer.render(stack, ItemCameraTransforms.TransformType.NONE, false, matrixStackIn, bufferIn, combinedLightIn, combinedOverlay, ibakedmodel.getBakedModel());
        matrixStackIn.popPose();

    }

    //    Not Smart Method ,but have tested
//    translate need n/16 better ,etc 2.5/16,3/16
    private MatrixStack rotateMatrix(MatrixStack matrixStackIn, ItemCameraTransforms.TransformType transformType) {
        if (transformType == ItemCameraTransforms.TransformType.GUI) {
            matrixStackIn.translate(0.9375F, 0.21875F, 0F);
//            FluidDrawersLegacyMod.LOGGER.info(transformType+"00"+matrixStackIn.last().pose().toString());
            matrixStackIn.mulPose(new Quaternion(30, 225, 0, true));
//            FluidDrawersLegacyMod.LOGGER.info(transformType+"11"+matrixStackIn.last().pose().toString());
            matrixStackIn.scale(0.625f, 0.625f, 0.625f);
//            FluidDrawersLegacyMod.LOGGER.info(transformType+"22"+matrixStackIn.last().pose().toString());
        }
        if (transformType == ItemCameraTransforms.TransformType.GROUND) {
            matrixStackIn.translate(0.375, 0.375, 0.375);
            matrixStackIn.scale(0.25f, 0.25f, 0.25f);
        }
//        if (transformType == ItemCameraTransforms.TransformType.FIXED) {
//            matrixStackIn.translate(0.225F, 0.225F, 0.25F);
//            matrixStackIn.scale(0.5f, 0.5f, 0.5f);
//        }
        if (transformType == ItemCameraTransforms.TransformType.THIRD_PERSON_RIGHT_HAND) {
            matrixStackIn.mulPose(new Quaternion(75, 45, 0, true));
            matrixStackIn.translate(0.51625, 0.46875, -0.1875);
            matrixStackIn.scale(0.375f, 0.375f, 0.375f);
        }
        if (transformType == ItemCameraTransforms.TransformType.THIRD_PERSON_LEFT_HAND) {
            matrixStackIn.mulPose(new Quaternion(75, 45, 0, true));
            matrixStackIn.translate(0.51625, 0.46875, -0.1875);
            matrixStackIn.scale(0.375f, 0.375f, 0.375f);
        }
        if (transformType == ItemCameraTransforms.TransformType.FIRST_PERSON_RIGHT_HAND) {
            matrixStackIn.translate(0.40625, -0.1875, 0);
            matrixStackIn.mulPose(new Quaternion(0, 45, 0, true));
            matrixStackIn.scale(0.675f, 0.675f, 0.675f);
        }
        if (transformType == ItemCameraTransforms.TransformType.FIRST_PERSON_LEFT_HAND) {
            matrixStackIn.translate(0.59375, -0.1875, 0);
            matrixStackIn.mulPose(new Quaternion(0, 225, 0, true));
            matrixStackIn.scale(0.675f, 0.675f, 0.675f);
        }
        return matrixStackIn;
    }

    private void renderFluid(ItemStack stack, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLight, double animationTime) {
        if (!stack.getOrCreateTag().contains("tank"))
            return;
        FluidStack fluidStackDown = new FluidStack(Fluids.EMPTY, 0);
        fluidStackDown = FluidStack.loadFluidStackFromNBT((CompoundNBT) stack.getOrCreateTag().get("tank"));
        if (fluidStackDown.getAmount() == 0)
            return;
        if(stack.getOrCreateTag().toString().contains("storagedrawers:creative_vending_upgrade"))fluidStackDown.setAmount(Integer.MAX_VALUE);

        Minecraft mc = Minecraft.getInstance();
        TextureAtlasSprite still = mc.getTextureAtlas(AtlasTexture.LOCATION_BLOCKS).apply(fluidStackDown.getFluid().getAttributes().getStillTexture(fluidStackDown));
//        TextureAtlasSprite still = mc.getBlockRenderer().getBlockModelShaper().getTexture(fluidStackDown.getFluid().defaultFluidState().createLegacyBlock(), stack.getFrame().level, new BlockPos(0,0,0));
        int colorRGB = fluidStackDown.getFluid().getAttributes().getColor(fluidStackDown);
        int capacity = TileEntityFluidDrawer.calcultaeCapacitybyStack(stack);
        int amount = fluidStackDown.getAmount();
        if (capacity < amount) amount = capacity;
        float height = (float) amount / (float) capacity * 0.875f;
        float vHeight = (still.getV1() - still.getV0()) * (1f - (float) amount / (float) capacity);
//        float height = (float) fluidStackDown.getAmount() / (float) TileEntityFluidDrawer.calcultaeCapacitybyStack(stack) * 0.75f;
//        float vHeight = (still.getV1() - still.getV0()) * (1f - (float) fluidStackDown.getAmount() / (float) TileEntityFluidDrawer.calcultaeCapacitybyStack(stack));
//        matrixStackIn.pushPose();
        GlStateManager._disableCull();
        IVertexBuilder buffer = bufferIn.getBuffer(RenderType.translucent());

        addVertex(buffer, matrixStackIn, 0.064f, 0.064f, 0.064f, still.getU0(), still.getV0(), colorRGB, 1.0f, combinedLight);
        addVertex(buffer, matrixStackIn, 0.064f, 0.064f, 0.9360f, still.getU1(), still.getV0(), colorRGB, 1.0f, combinedLight);
        addVertex(buffer, matrixStackIn, 0.9360f, 0.064f, 0.9360f, still.getU1(), still.getV1(), colorRGB, 1.0f, combinedLight);
        addVertex(buffer, matrixStackIn, 0.9360f, 0.064f, 0.064f, still.getU0(), still.getV1(), colorRGB, 1.0f, combinedLight);

        addVertex(buffer, matrixStackIn, 0.064f, 0.064f + height, 0.064f, still.getU0(), still.getV0(), colorRGB, 1.0f, combinedLight);
        addVertex(buffer, matrixStackIn, 0.064f, 0.064f + height, 0.9360f, still.getU1(), still.getV0(), colorRGB, 1.0f, combinedLight);
        addVertex(buffer, matrixStackIn, 0.9360f, 0.064f + height, 0.9360f, still.getU1(), still.getV1(), colorRGB, 1.0f, combinedLight);
        addVertex(buffer, matrixStackIn, 0.9360f, 0.064f + height, 0.064f, still.getU0(), still.getV1(), colorRGB, 1.0f, combinedLight);

        addVertex(buffer, matrixStackIn, 0.9360f, 0.064f + height, 0.064f, still.getU0(), still.getV0() + vHeight, colorRGB, 1.0f, combinedLight);
        addVertex(buffer, matrixStackIn, 0.9360f, 0.064f + height, 0.9360f, still.getU1(), still.getV0() + vHeight, colorRGB, 1.0f, combinedLight);
        addVertex(buffer, matrixStackIn, 0.9360f, 0.064f, 0.9360f, still.getU1(), still.getV1(), colorRGB, 1.0f, combinedLight);
        addVertex(buffer, matrixStackIn, 0.9360f, 0.064f, 0.064f, still.getU0(), still.getV1(), colorRGB, 1.0f, combinedLight);

        addVertex(buffer, matrixStackIn, 0.064f, 0.064f, 0.064f, still.getU0(), still.getV0(), colorRGB, 1.0f, combinedLight);
        addVertex(buffer, matrixStackIn, 0.064f, 0.064f, 0.9360f, still.getU1(), still.getV0(), colorRGB, 1.0f, combinedLight);
        addVertex(buffer, matrixStackIn, 0.064f, 0.064f + height, 0.9360f, still.getU1(), still.getV1() - vHeight, colorRGB, 1.0f, combinedLight);
        addVertex(buffer, matrixStackIn, 0.064f, 0.064f + height, 0.064f, still.getU0(), still.getV1() - vHeight, colorRGB, 1.0f, combinedLight);

        addVertex(buffer, matrixStackIn, 0.064f, 0.064f + height, 0.064f, still.getU0(), still.getV0() + vHeight, colorRGB, 1.0f, combinedLight);
        addVertex(buffer, matrixStackIn, 0.9360f, 0.064f + height, 0.064f, still.getU1(), still.getV0() + vHeight, colorRGB, 1.0f, combinedLight);
        addVertex(buffer, matrixStackIn, 0.9360f, 0.064f, 0.064f, still.getU1(), still.getV1(), colorRGB, 1.0f, combinedLight);
        addVertex(buffer, matrixStackIn, 0.064f, 0.064f, 0.064f, still.getU0(), still.getV1(), colorRGB, 1.0f, combinedLight);


        addVertex(buffer, matrixStackIn, 0.064f, 0.064f, 0.9360f, still.getU0(), still.getV0() + vHeight, colorRGB, 1.0f, combinedLight);
        addVertex(buffer, matrixStackIn, 0.9360f, 0.064f, 0.9360f, still.getU1(), still.getV0() + vHeight, colorRGB, 1.0f, combinedLight);
        addVertex(buffer, matrixStackIn, 0.9360f, 0.064f + height, 0.9360f, still.getU1(), still.getV1(), colorRGB, 1.0f, combinedLight);
        addVertex(buffer, matrixStackIn, 0.064f, 0.064f + height, 0.9360f, still.getU0(), still.getV1(), colorRGB, 1.0f, combinedLight);


        GlStateManager._enableCull();
//        matrixStackIn.popPose();


    }


    private void addVertex(IVertexBuilder renderer, MatrixStack stack, float x, float y, float z, float u, float v, int RGBA, float alpha, int brightness) {
        float red = ((RGBA >> 16) & 0xFF) / 255f;
        float green = ((RGBA >> 8) & 0xFF) / 255f;
        float blue = ((RGBA >> 0) & 0xFF) / 255f;
        renderer.vertex(stack.last().pose(), x, y, z).color(red, green, blue, alpha).uv(u, v).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(15728880)/*.lightmap(0, 240)*/.normal(stack.last().normal(), 0, 1.0F, 0).endVertex();
    }

}
