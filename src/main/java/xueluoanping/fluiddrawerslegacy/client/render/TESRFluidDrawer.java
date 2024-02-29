package xueluoanping.fluiddrawerslegacy.client.render;

import com.jaquadro.minecraft.storagedrawers.api.storage.attribute.LockAttribute;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexConsumer;
// import com.mojang.math.Quaternion;
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
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.FluidStack;
// import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.FluidUtil;
import org.joml.Vector3d;
import xueluoanping.fluiddrawerslegacy.api.betterFluidManager;
import xueluoanping.fluiddrawerslegacy.block.BlockFluidDrawer;
import xueluoanping.fluiddrawerslegacy.block.blockentity.BlockEntityFluidDrawer;
import xueluoanping.fluiddrawerslegacy.client.gui.Screen;
import xueluoanping.fluiddrawerslegacy.client.util.TankHolder;
import xueluoanping.fluiddrawerslegacy.client.util.TankRenderUtil;
import xueluoanping.fluiddrawerslegacy.config.ClientConfig;
import xueluoanping.fluiddrawerslegacy.util.MathUtil;

import java.util.ArrayList;
import java.util.List;

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

        BlockEntityFluidDrawer.betterFluidHandler betterFluidHandler = null;
        // FluidStack fluidStackDown = FluidStack.EMPTY;
        // int capacity = 0;
        // boolean isLocked = tile.getDrawerAttributes().isItemLocked(LockAttribute.LOCK_EMPTY);
        for (BlockEntityFluidDrawer.FluidDrawerData data : tile.getTank().getFluidDrawerDataList()) {
            betterFluidHandler = data.getTank();
        }
        matrixStackIn.pushPose();

        if (betterFluidHandler.getCacheFluid().getRawFluid() != Fluids.EMPTY &&
                tile.getDrawerAttributes().isConcealed()) {

            FluidStack fluidStackDown = new FluidStack(betterFluidHandler.getCacheFluid(), 1);
            Font fontRenderer = this.font;
            LocalPlayer player = Minecraft.getInstance().player;
            handleMatrixAngle(matrixStackIn, player, tile.getBlockPos(), tile.getBlockState().getValue(BlockFluidDrawer.FACING));
            matrixStackIn.scale(0.007f, 0.007f, 0.007f);
            MultiBufferSource.BufferSource txtBuffer = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());
            int textWidth = fontRenderer.width(I18n.get(fluidStackDown.getTranslationKey()));
            fontRenderer.drawInBatch(fluidStackDown.getDisplayName().getString()
                    , (float) (-textWidth) / 2.0F, -9F, 0xFFFFFF, false,
                    matrixStackIn.last().pose(), txtBuffer, Font.DisplayMode.NORMAL, 0, combinedLightIn);
            txtBuffer.endBatch();

        }
        matrixStackIn.popPose();

        matrixStackIn.pushPose();

        if (tile.getDrawerAttributes().isItemLocked(LockAttribute.LOCK_EMPTY)) {
            if (betterFluidHandler.getCacheFluid().getRawFluid() != Fluids.EMPTY) {
                FluidStack fluidStackDown = new FluidStack(betterFluidHandler.getCacheFluid(), 1000);
                Font fontRenderer = this.font;
//                matrixStackIn.translate(0.5, 0.15, 1);


                MultiBufferSource.BufferSource txtBuffer = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());

                String label = "(" + I18n.get("tooltip.storagedrawers.waila.locked") + ")";
                int textWidth = fontRenderer.width(label);
                LocalPlayer player = Minecraft.getInstance().player;
                handleMatrixAngle(matrixStackIn, player, tile.getBlockPos(), tile.getBlockState().getValue(BlockFluidDrawer.FACING));
//                FluidDrawersLegacyMod.logger(vector3d + ""+d);
                matrixStackIn.scale(0.007f, 0.007f, 0.007f);

                fontRenderer.drawInBatch(label
                        , (float) (-textWidth) / 2.0F, 0F, 0xFFFFFF, false, matrixStackIn.last().pose(), txtBuffer, Font.DisplayMode.NORMAL, 0, combinedLightIn);
                txtBuffer.endBatch();
//                fontRenderer.draw(matrixStackIn, I18n.get(fluidStackDown.getTranslationKey()), 0F, 0F, 0xFFFFF);
            }

        }
        matrixStackIn.popPose();

        matrixStackIn.pushPose();

        if (tile.getDrawerAttributes().isShowingQuantity()) {
            if (betterFluidHandler.getCacheFluid().getRawFluid() != Fluids.EMPTY) {

                FluidStack fluidStackDown = betterFluidHandler.getFluid();
                Font fontRenderer = this.font;
//                matrixStackIn.translate(0.5, 0.15, 1);


                MultiBufferSource.BufferSource txtBuffer = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());
                int amount = fluidStackDown.getAmount();
                String label = String.valueOf(amount) + "mB";
                int textWidth = fontRenderer.width(label);
                LocalPlayer player = Minecraft.getInstance().player;
                handleMatrixAngle(matrixStackIn, player, tile.getBlockPos(), tile.getBlockState().getValue(BlockFluidDrawer.FACING));
//                FluidDrawersLegacyMod.logger(vector3d + ""+d);
                matrixStackIn.scale(0.007f, 0.007f, 0.007f);

                fontRenderer.drawInBatch(label
                        , (float) (-textWidth) / 2.0F, -18F, 0xFFFFFF, false, matrixStackIn.last().pose(), txtBuffer, Font.DisplayMode.NORMAL, 0, combinedLightIn);
                txtBuffer.endBatch();
//                fontRenderer.draw(matrixStackIn, I18n.get(fluidStackDown.getTranslationKey()), 0F, 0F, 0xFFFFF);
            }

        }
        matrixStackIn.popPose();
        render(partialTicks, matrixStackIn, bufferIn, combinedLightIn, combinedOverlay);
    }


    private void handleMatrixAngle(PoseStack matrixStackIn, LocalPlayer player, BlockPos pos, Direction d) {
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
                // matrixStackIn.mulPose(new Quaternion(0, 180, 180, true));
                matrixStackIn.mulPose(XYZ.deg_to_rad(0, 180, 180));
                break;
            case NORTH:
                // matrixStackIn.mulPose(new Quaternion(0, 0, 180, true));
                matrixStackIn.mulPose(XYZ.deg_to_rad(0, 0, 180));
                matrixStackIn.translate(-0.5, -0.15, 0);
                break;
            case EAST:
                // matrixStackIn.mulPose(new Quaternion(0, 270, 180, true));
                matrixStackIn.mulPose(XYZ.deg_to_rad(0, 270, 180));
                matrixStackIn.translate(-0.5, -0.15, -1);
                break;
            case WEST:
                // matrixStackIn.mulPose(new Quaternion(0, 90, 180, true));
                matrixStackIn.mulPose(XYZ.deg_to_rad(0, 90, 180));
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
        boolean isLocked = tile.getDrawerAttributes().isItemLocked(LockAttribute.LOCK_EMPTY);
        int count = tile.getDrawerCount();
        var flist = new ArrayList<TankHolder>();
        for (int i = 0; i < count; i++) {
            var data = (tile.getDrawer(i));
            var betterFluidHandler = data.getTank();
            var fluidStackDown = betterFluidHandler.getFluid().copy();
            var cache = data.getCacheFluid();
            int capacity = betterFluidHandler.getCapacity();

            if (!fluidStackDown.isEmpty())
                fluidStackDown.setAmount(data.getFluidAnimation().getAndUpdateLastFluidAmount(fluidStackDown.getAmount(), animationTime));

            if (isLocked && fluidStackDown.isEmpty() && !cache.isEmpty()) {
                fluidStackDown = new FluidStack(cache, 1000);
            }

            if (tile.upgrades().hasVendingUpgrade() && !fluidStackDown.isEmpty())
                fluidStackDown.setAmount(capacity);
            flist.add(TankRenderUtil.of(fluidStackDown, capacity));
        }
        TankRenderUtil.renderFluid(flist, matrixStackIn, bufferIn, combinedLight, animationTime);
    }

}
