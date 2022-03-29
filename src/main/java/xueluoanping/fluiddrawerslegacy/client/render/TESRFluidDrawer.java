package xueluoanping.fluiddrawerslegacy.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.fluid.Fluids;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import xueluoanping.fluiddrawerslegacy.block.tileentity.TileEntityFluidDrawer;

import static net.minecraft.client.renderer.tileentity.BellTileEntityRenderer.BELL_RESOURCE_LOCATION;

// Thanks to WaterSource
public class TESRFluidDrawer extends TileEntityRenderer<TileEntityFluidDrawer> {
    private final ModelRenderer bellBody = new ModelRenderer(32, 32, 0, 0);

    public TESRFluidDrawer(TileEntityRendererDispatcher rendererDispatcherIn) {
        super(rendererDispatcherIn);
        this.bellBody.addBox(-3.0F, -6.0F, -3.0F, 6.0F, 7.0F, 6.0F);
        this.bellBody.setPos(8.0F, 12.0F, 8.0F);
        ModelRenderer modelrenderer = new ModelRenderer(32, 32, 0, 13);
        modelrenderer.addBox(4.0F, 4.0F, 4.0F, 8.0F, 2.0F, 8.0F);
        modelrenderer.setPos(-8.0F, -12.0F, -8.0F);
        this.bellBody.addChild(modelrenderer);
    }

    @Override
    public void render(TileEntityFluidDrawer tile, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn) {
//Only render in the world

        if (!tile.hasLevel())
            return;

//        if (!tile.hasNoFluid()) {
        // render the fluid
        matrixStackIn.pushPose();

        Minecraft mc = Minecraft.getInstance();
        long gameTime = mc.level.getGameTime();
        double animationTime = (double) gameTime + (double) partialTicks;
        renderFluid(tile, matrixStackIn, bufferIn, combinedLightIn, animationTime);
        matrixStackIn.popPose();

//        }
        render(partialTicks, matrixStackIn, bufferIn, combinedLightIn, combinedOverlayIn);
    }

    public void render(float p_112234_, MatrixStack poseStack, IRenderTypeBuffer bufferSource, int p_112237_, int p_112238_) {
        float f = (float) +p_112234_;
        float f1 = 0.5F;
        float f2 = 0.0F;

//		float f3 = Mth.sin(f / (float) Math.PI) / (4.0F + f / 3.0F);
//		f1 = -f3;
//		this.bellBody.xRot = f1;
//		this.bellBody.zRot = f2;
//		this.bellBody.y=this.bellBody.y+10;
        poseStack.scale(0.001f, 0.001f, 0.001f);
        IVertexBuilder ivertexbuilder = BELL_RESOURCE_LOCATION.buffer(bufferSource, RenderType::entitySolid);
        bellBody.render(poseStack, ivertexbuilder, p_112237_, p_112238_);
    }

    private void renderFluid(TileEntityFluidDrawer tile, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLight, double animationTime) {
//        FluidStack fluidStackDown = new FluidStack(Fluids.WATER, 30000);
        FluidStack fluidStackDown = null;
        final FluidStack[] fluidStack = new FluidStack[1];
        tile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null)
                .ifPresent(handler -> {
                    fluidStack[0] = handler.getFluidInTank(0);
//                    FluidDrawersLegacyMod.LOGGER.info(""+handler.getFluidInTank(0));
                });
        if (fluidStack[0] == null || fluidStack[0].getFluid() == Fluids.EMPTY) return;
        else fluidStackDown = fluidStack[0];

//        Minecraft mc = Minecraft.getInstance();
//        TextureAtlasSprite still = mc.getTextureAtlas(AtlasTexture.LOCATION_BLOCKS).apply(fluidStackDown.getFluid().getAttributes().getStillTexture());
        FluidAttributes attributes = fluidStackDown.getFluid().getAttributes();
        TextureAtlasSprite still = getBlockSprite(attributes.getStillTexture());
//        TextureAtlasSprite still = mc.getBlockRenderer().getBlockModelShaper().getTexture(fluidStackDown.getFluid().defaultFluidState().createLegacyBlock(), tile.getLevel(), tile.getBlockPos());
        int colorRGB = fluidStackDown.getFluid().getAttributes().getColor();

        int capacity = tile.getEffectiveCapacity();
        int amount = fluidStackDown.getAmount();
        if (capacity < amount) amount = capacity;
        float r=(float) amount / (float) capacity;
        if(tile.upgrades().hasVendingUpgrade())r=1f;
        float height = r * 0.875f;
        float vHeight = (still.getV1() - still.getV0()) * (1f - r);
        matrixStackIn.pushPose();
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
        matrixStackIn.popPose();


    }

    public static TextureAtlasSprite getBlockSprite(ResourceLocation sprite) {
        return Minecraft.getInstance().getModelManager().getAtlas(PlayerContainer.BLOCK_ATLAS).getSprite(sprite);
    }

    private void addVertex(IVertexBuilder renderer, MatrixStack stack, float x, float y, float z, float u, float v, int RGBA, float alpha, int brightness) {
        float red = ((RGBA >> 16) & 0xFF) / 255f;
        float green = ((RGBA >> 8) & 0xFF) / 255f;
        float blue = ((RGBA >> 0) & 0xFF) / 255f;
        renderer.vertex(stack.last().pose(), x, y, z).color(red, green, blue, alpha).uv(u, v).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(15728880)/*.lightmap(0, 240)*/.normal(stack.last().normal(), 0, 1.0F, 0).endVertex();
    }
}
