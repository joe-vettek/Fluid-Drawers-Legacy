package xueluoanping.fluiddrawerslegacy.client.render;

import com.jaquadro.minecraft.storagedrawers.api.storage.attribute.LockAttribute;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3d;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.client.resources.model.Material;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import xueluoanping.fluiddrawerslegacy.block.BlockFluidDrawer;
import xueluoanping.fluiddrawerslegacy.block.blockentity.BlockEntityFluidDrawer;
import xueluoanping.fluiddrawerslegacy.config.ClientConfig;
import xueluoanping.fluiddrawerslegacy.util.MathUtil;

import static net.minecraft.world.inventory.InventoryMenu.BLOCK_ATLAS;

// Thanks to WaterSource
public class TESRFluidDrawer implements BlockEntityRenderer<BlockEntityFluidDrawer> {

    public static final Material BELL_RESOURCE_LOCATION = new Material(TextureAtlas.LOCATION_BLOCKS, new ResourceLocation("entity/bell/bell_body"));
    private static final String BELL_BODY = "bell_body";
    private final ModelPart bellBody;
    private final Font font;

    public TESRFluidDrawer(BlockEntityRendererProvider.Context pContext) {
        ModelPart modelpart = pContext.bakeLayer(ModelLayers.BELL);
        this.bellBody = modelpart.getChild("bell_body");
        this.font = pContext.getFont();
    }


    @Override
    public void render(BlockEntityFluidDrawer tile, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlay) {
//        FluidDrawersLegacyMod.logger(""+tile.getBlockState());
        if (!tile.hasLevel())
            return;

        if (tile.upgrades().hasIlluminationUpgrade()) combinedLightIn = 15728880;

        matrixStackIn.pushPose();

        Minecraft mc = Minecraft.getInstance();
        long gameTime = mc.level.getGameTime();
        double animationTime = (double) gameTime + (double) partialTicks;
        renderFluid(tile, matrixStackIn, bufferIn, combinedLightIn, animationTime);
        matrixStackIn.popPose();

        BlockEntityFluidDrawer.betterFluidHandler betterFluidHandler = (BlockEntityFluidDrawer.betterFluidHandler) tile.getTank();
        matrixStackIn.pushPose();

        if (betterFluidHandler.getCacheFluid() != Fluids.EMPTY &&
                tile.getDrawerAttributes().isConcealed()) {

            FluidStack fluidStackDown = new FluidStack(betterFluidHandler.getCacheFluid(), 1);
            Font fontRenderer = this.font;
            LocalPlayer player = Minecraft.getInstance().player;
            handleMatrixAngle(matrixStackIn, player, tile.getBlockPos(),tile.getBlockState().getValue(BlockFluidDrawer.FACING));
            matrixStackIn.scale(0.007f, 0.007f, 0.007f);
            MultiBufferSource.BufferSource txtBuffer = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());
            int textWidth = fontRenderer.width(I18n.get(fluidStackDown.getTranslationKey()));
            fontRenderer.drawInBatch(fluidStackDown.getDisplayName().getString()
                    , (float) (-textWidth) / 2.0F, -9F, 0xFFFFFF, false, matrixStackIn.last().pose(), txtBuffer, false, 0, combinedLightIn);
            txtBuffer.endBatch();

        }
        matrixStackIn.popPose();

        matrixStackIn.pushPose();

        if (tile.getDrawerAttributes().isItemLocked(LockAttribute.LOCK_EMPTY)) {
            if (betterFluidHandler.getCacheFluid() != Fluids.EMPTY) {
                FluidStack fluidStackDown = new FluidStack(betterFluidHandler.getCacheFluid(), 1000);
                Font fontRenderer = this.font;
//                matrixStackIn.translate(0.5, 0.15, 1);


                MultiBufferSource.BufferSource txtBuffer = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());

                String label = "(" + I18n.get("tooltip.storagedrawers.waila.locked") + ")";
                int textWidth = fontRenderer.width(label);
                LocalPlayer player = Minecraft.getInstance().player;
                handleMatrixAngle(matrixStackIn, player, tile.getBlockPos(),tile.getBlockState().getValue(BlockFluidDrawer.FACING));
//                FluidDrawersLegacyMod.logger(vector3d + ""+d);
                matrixStackIn.scale(0.007f, 0.007f, 0.007f);

                fontRenderer.drawInBatch(label
                        , (float) (-textWidth) / 2.0F, 0F, 0xFFFFFF, false, matrixStackIn.last().pose(), txtBuffer, false, 0, combinedLightIn);
                txtBuffer.endBatch();
//                fontRenderer.draw(matrixStackIn, I18n.get(fluidStackDown.getTranslationKey()), 0F, 0F, 0xFFFFF);
            }

        }
        matrixStackIn.popPose();

        matrixStackIn.pushPose();

        if (tile.getDrawerAttributes().isShowingQuantity()) {
            if (betterFluidHandler.getCacheFluid() != Fluids.EMPTY) {

                FluidStack fluidStackDown = betterFluidHandler.getFluid();
                Font fontRenderer = this.font;
//                matrixStackIn.translate(0.5, 0.15, 1);


                MultiBufferSource.BufferSource txtBuffer = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());
                int amount = fluidStackDown.getAmount();
                String label = String.valueOf(amount) + "mB";
                int textWidth = fontRenderer.width(label);
                LocalPlayer player = Minecraft.getInstance().player;
                handleMatrixAngle(matrixStackIn, player, tile.getBlockPos(),tile.getBlockState().getValue(BlockFluidDrawer.FACING));
//                FluidDrawersLegacyMod.logger(vector3d + ""+d);
                matrixStackIn.scale(0.007f, 0.007f, 0.007f);

                fontRenderer.drawInBatch(label
                        , (float) (-textWidth) / 2.0F, -18F, 0xFFFFFF, false, matrixStackIn.last().pose(), txtBuffer, false, 0, combinedLightIn);
                txtBuffer.endBatch();
//                fontRenderer.draw(matrixStackIn, I18n.get(fluidStackDown.getTranslationKey()), 0F, 0F, 0xFFFFF);
            }

        }
        matrixStackIn.popPose();
        render(partialTicks, matrixStackIn, bufferIn, combinedLightIn, combinedOverlay);
    }


    private void handleMatrixAngle(PoseStack matrixStackIn, LocalPlayer player, BlockPos pos,Direction d) {
        Vector3d vector3d = new Vector3d(player.getPosition(1.0f).x() - pos.getX() - 0.5
                , player.getPosition(0f).y() - pos.getY()
                , player.getPosition(0f).z() - pos.getZ() - 0.5);


//        Direction d = Direction.getNearest(vector3d.x, vector3d.y, vector3d.z);

        if (d == Direction.DOWN || d == Direction.UP) {
            if (vector3d.x > 0 && Math.abs(vector3d.x) > Math.abs(vector3d.z)) d = Direction.EAST;
            if (vector3d.x < 0 && Math.abs(vector3d.x) > Math.abs(vector3d.z)) d = Direction.WEST;
            if (vector3d.x > 0 && Math.abs(vector3d.x) < Math.abs(vector3d.z)) d = Direction.SOUTH;
            if (vector3d.x < 0 && Math.abs(vector3d.x) < Math.abs(vector3d.z)) d = Direction.NORTH;
        }
        if (ClientConfig.distance.get() != -1) {
            if (MathUtil.calDistanceSelf(vector3d) > ClientConfig.distance.get())
                d = Direction.DOWN;
        }
        switch (d) {
            case SOUTH:
                matrixStackIn.translate(0.5, 0.15, 1);
                matrixStackIn.mulPose(new Quaternion(0, 180, 180, true));
                break;
            case NORTH:
                matrixStackIn.mulPose(new Quaternion(0, 0, 180, true));
                matrixStackIn.translate(-0.5, -0.15, 0);
                break;
            case EAST:
                matrixStackIn.mulPose(new Quaternion(0, 270, 180, true));
                matrixStackIn.translate(-0.5, -0.15, -1);
                break;
            case WEST:
                matrixStackIn.mulPose(new Quaternion(0, 90, 180, true));
                matrixStackIn.translate(0.5, -0.15, 0);
                break;
            default:
                matrixStackIn.scale(0.01f, 0.01f, 0.01f);
                break;
        }
    }

    public void render(float p_112234_, PoseStack poseStack, MultiBufferSource bufferSource, int p_112237_, int p_112238_) {
        poseStack.scale(0.001f, 0.001f, 0.001f);
        VertexConsumer vertexconsumer = BELL_RESOURCE_LOCATION.buffer(bufferSource, RenderType::entitySolid);
        this.bellBody.render(poseStack, vertexconsumer, p_112237_, p_112238_);
    }

    private void renderFluid(BlockEntityFluidDrawer tile, PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLight, double animationTime) {
//        FluidStack fluidStackDown = new FluidStack(Fluids.WATER, 30000);
        FluidStack fluidStackDown = null;
        final FluidStack[] fluidStack = new FluidStack[1];
        tile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null)
                .ifPresent(handler -> {
                    fluidStack[0] = handler.getFluidInTank(0);
//                    FluidDrawersLegacyMod.LOGGER.info(""+handler.getFluidInTank(0));
                });
        if (fluidStack[0] == null || (fluidStack[0].getFluid() == Fluids.EMPTY) && !tile.getDrawerAttributes().isItemLocked(LockAttribute.LOCK_EMPTY))
            return;
        else fluidStackDown = fluidStack[0];
//        FluidDrawersLegacyMod.logger(((TileEntityFluidDrawer.betterFluidHandler) tile.getTank()).getCacheFluid().getDisplayName().toString());

        if (tile.getDrawerAttributes().isItemLocked(LockAttribute.LOCK_EMPTY)) {
            BlockEntityFluidDrawer.betterFluidHandler betterFluidHandler = (BlockEntityFluidDrawer.betterFluidHandler) tile.getTank();
            if (fluidStackDown.getAmount() <= 0)
                if (betterFluidHandler.getCacheFluid() != Fluids.EMPTY) {

                    fluidStackDown = new FluidStack(betterFluidHandler.getCacheFluid(), 1000);
//                    FluidDrawersLegacyMod.logger(fluidStackDown.getTranslationKey());
//                FontRenderer fontRenderer = this.renderer.getFont();
//                matrixStackIn.mulPose(new Quaternion(0, 0, 0, true));
//                fontRenderer.draw(matrixStackIn, I18n.get(fluidStackDown.getTranslationKey()),0F, 0F, 0xFFFFF);
                } else {
                    return;
                }
        }
//        Minecraft mc = Minecraft.getInstance();
//        TextureAtlasSprite still = mc.getTextureAtlas(AtlasTexture.LOCATION_BLOCKS).apply(fluidStackDown.getFluid().getAttributes().getStillTexture());
        FluidAttributes attributes = fluidStackDown.getFluid().getAttributes();
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        TextureAtlasSprite still = mc.getTextureAtlas(BLOCK_ATLAS).apply(fluidStackDown.getFluid().getAttributes().getStillTexture());

//        TextureAtlasSprite still = mc.getBlockRenderer().getBlockModelShaper().getTexture(fluidStackDown.getFluid().defaultFluidState().createLegacyBlock(), tile.getLevel(), tile.getBlockPos());
        RenderSystem.setShaderTexture(0, BLOCK_ATLAS);
//        TextureAtlasSprite still = mc.getBlockRenderer().getBlockModelShaper().getTexture(fluidStackDown.getFluid().defaultFluidState().createLegacyBlock(), tile.getLevel(), tile.getBlockPos());
        int colorRGB = fluidStackDown.getFluid().getAttributes().getColor(fluidStackDown);

        int capacity = tile.getTankEffectiveCapacity();
        // int amount = fluidStackDown.getAmount();
        int amount=tile.getAndUpdateLastFluidAmount(animationTime);

        if (capacity < amount) amount = capacity;
        float r = (float) amount / (float) capacity;
        if (tile.upgrades().hasVendingUpgrade()) r = 1f;
        float height = r * 0.875f;
        float vHeight = (still.getV1() - still.getV0()) * (1f - r);
        matrixStackIn.pushPose();
        GlStateManager._disableCull();
        VertexConsumer buffer = bufferIn.getBuffer(RenderType.translucent());


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
        matrixStackIn.popPose();


    }

    public static TextureAtlasSprite getBlockSprite(ResourceLocation sprite) {
        return Minecraft.getInstance().getModelManager().getAtlas(BLOCK_ATLAS).getSprite(sprite);
    }

    private void addVertex(VertexConsumer renderer, PoseStack stack, float x, float y, float z, float u, float v, int RGBA, float alpha, int brightness) {
        float red = ((RGBA >> 16) & 0xFF) / 255f;
        float green = ((RGBA >> 8) & 0xFF) / 255f;
        float blue = ((RGBA >> 0) & 0xFF) / 255f;
        //		renderer.vertex(stack.last().pose(), x, y, z).color(red, green, blue, alpha).uv(u, v).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(15728880)/*.lightmap(0, 240)*/.normal(stack.last().normal(), 0, 1.0F, 0).endVertex();
        int light1 = brightness & '\uffff';
        int light2 = brightness >> 16 & '\uffff';
        renderer.vertex(stack.last().pose(), x, y, z).color(red, green, blue, alpha).uv(u, v).uv2(light1, light2).overlayCoords(OverlayTexture.NO_OVERLAY).normal(stack.last().normal(), 0, 1.0F, 0).endVertex();
    }


}
