package xueluoanping.fluiddrawerslegacy.client.render;

import com.jaquadro.minecraft.storagedrawers.api.storage.attribute.LockAttribute;
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
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.client.resources.model.Material;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fluids.FluidStack;
// import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import org.joml.Vector3d;
import xueluoanping.fluiddrawerslegacy.block.BlockFluidDrawer;
import xueluoanping.fluiddrawerslegacy.block.blockentity.BlockEntityFluidDrawer;
import xueluoanping.fluiddrawerslegacy.client.util.TankHolder;
import xueluoanping.fluiddrawerslegacy.client.util.TankRenderUtil;
import xueluoanping.fluiddrawerslegacy.config.ClientConfig;
import xueluoanping.fluiddrawerslegacy.util.MathUtil;

import java.awt.*;
import java.util.ArrayList;

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

        // BlockEntityFluidDrawer.betterFluidHandler betterFluidHandler = null;
        // FluidStack fluidStackDown = FluidStack.EMPTY;
        // int capacity = 0;
        // boolean isLocked = tile.getDrawerAttributes().isItemLocked(LockAttribute.LOCK_EMPTY);
        var dlist = tile.getTank().getFluidDrawerDataList();
        int count = dlist.size();
        int slot = 0;
        for (BlockEntityFluidDrawer.FluidDrawerData data : dlist) {
            slot++;
            var betterFluidHandler = data.getTank();
            if (betterFluidHandler.getCacheFluid().getRawFluid() != Fluids.EMPTY &&
                    tile.getDrawerAttributes().isConcealed()) {
                FluidStack fluidStackDown = new FluidStack(betterFluidHandler.getCacheFluid(), 1);
                var label = fluidStackDown.getDisplayName().getString();
                drawText(0, label, slot, count, tile, matrixStackIn, combinedLightIn);
            }

            if (tile.getDrawerAttributes().isItemLocked(LockAttribute.LOCK_EMPTY)) {
                if (betterFluidHandler.getCacheFluid().getRawFluid() != Fluids.EMPTY) {
                    String label = "(" + I18n.get("tooltip.storagedrawers.waila.locked") + ")";
                    drawText(1, label, slot, count, tile, matrixStackIn, combinedLightIn);
                }
            }

            if (tile.getDrawerAttributes().isShowingQuantity()) {
                if (betterFluidHandler.getCacheFluid().getRawFluid() != Fluids.EMPTY) {
                    FluidStack fluidStackDown = betterFluidHandler.getFluid();
                    int amount = fluidStackDown.getAmount();
                    String label = amount + "mB";
                    drawText(2, label, slot, count, tile, matrixStackIn, combinedLightIn);
                }

            }
        }


        render(partialTicks, matrixStackIn, bufferIn, combinedLightIn, combinedOverlay);
    }

    private void drawText(int line, String label, int slot, int count, BlockEntityFluidDrawer tile, PoseStack matrixStackIn, int combinedLightIn) {
        matrixStackIn.pushPose();

        Font fontRenderer = this.font;
        MultiBufferSource.BufferSource txtBuffer = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());
        int textWidth = fontRenderer.width(label);
        var lh = font.lineHeight;

        LocalPlayer player = Minecraft.getInstance().player;

        var d = tile.getBlockState().getValue(BlockFluidDrawer.FACING);
        handleMatrixAngle(matrixStackIn, player, tile.getBlockPos(), d);
        float x = 0;
        float y = 0;
        float z = 0;
        float scale_x = 0.007f;
        float scale_y = 0.007f;
        float scale_z = 0.007f;

        float extraHeight = 0f;

        if (tile.isHalf()) {
            z += 0.55f;
        }
        if (count > 1) {
            scale_x /= 2;
            scale_y /= 2;
            scale_z /= 2;
            if (count == 4) {
                if (slot == 1) {
                    x += -0.25;
                    y += -0.39;
                }
                if (slot == 2) {
                    x += 0.25;
                    y += -0.39;
                }
                if (slot == 3) {
                    x += -0.25;
                    y += 0.1;
                }
                if (slot == 4) {
                    x += 0.25;
                    y += 0.1;
                }
                if (textWidth > 80) {
                    var r = textWidth / 80d;
                    scale_x /= r;
                    scale_y /= r;
                    scale_z /= r;
                    extraHeight = (float) ((lh) * (r - 1));
                }
            }
            if (count == 2) {
                if (slot == 1) {
                    y += -0.39;
                }
                if (slot == 2) {
                    y += 0.08;
                }
            }

        }
        matrixStackIn.translate(x, y, z);
        matrixStackIn.scale(scale_x, scale_y, scale_z);
        fontRenderer.drawInBatch(label
                , (float) (-textWidth) / 2.0F, -18F - lh * 1.2f * line - 1.2f*extraHeight, 0xFFFFFF, false, matrixStackIn.last().pose(), txtBuffer, Font.DisplayMode.NORMAL, 0, combinedLightIn);
        txtBuffer.endBatch();

        matrixStackIn.popPose();
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

        TankRenderUtil.renderFluid(flist, matrixStackIn, bufferIn, combinedLight, animationTime, tile.isHalf(), tile.getBlockState().getValue(BlockFluidDrawer.FACING));
    }

}
