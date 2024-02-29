package xueluoanping.fluiddrawerslegacy.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
// import com.mojang.math.Quaternion;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fluids.FluidStack;
import xueluoanping.fluiddrawerslegacy.block.blockentity.BlockEntityFluidDrawer;
import xueluoanping.fluiddrawerslegacy.client.util.TankHolder;
import xueluoanping.fluiddrawerslegacy.client.util.TankRenderUtil;


import java.util.ArrayList;


public class FluidDrawerItemStackTileEntityRenderer extends BlockEntityWithoutLevelRenderer {

    private final BlockEntityRenderDispatcher blockEntityRenderDispatcher;
    public FluidDrawerItemStackTileEntityRenderer(BlockEntityRenderDispatcher renderDispatcher, EntityModelSet modelSet) {
        super(renderDispatcher, modelSet);
        this.blockEntityRenderDispatcher = renderDispatcher;
    }

    @Override
    public void renderByItem(ItemStack stack, ItemDisplayContext transformType, PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlay) {
//        FluidDrawersLegacyMod.logger(stack.getOrCreateTag().toString());

        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
        BakedModel ibakedmodel = itemRenderer.getModel(stack,(Level) null, (LivingEntity) null, 0);
        matrixStackIn.pushPose();
        matrixStackIn = rotateMatrix(matrixStackIn, transformType);
//        FluidDrawersLegacyMod.LOGGER.info(transformType+""+matrixStackIn.last().pose().toString());
        renderFluid(stack, matrixStackIn, bufferIn, combinedLightIn, 0);
        matrixStackIn.translate(0.5F, 0.5F, 0.5F);
        itemRenderer.render(stack, ItemDisplayContext.NONE, false, matrixStackIn, bufferIn, combinedLightIn, combinedOverlay, ibakedmodel.applyTransform(ItemDisplayContext.NONE,matrixStackIn,false));
        // BlockRenderDispatcher renderer = Minecraft.getInstance().getBlockRenderer();
        // renderer.renderSingleBlock(((BlockItem) stack.getItem()).getBlock().defaultBlockState(), matrixStackIn, bufferIn, combinedLightIn, combinedOverlay, ModelData.EMPTY, null);
        matrixStackIn.popPose();

    }

    //    Not Smart Method ,but have tested
//    translate need n/16 better ,etc 2.5/16,3/16
    private PoseStack rotateMatrix(PoseStack matrixStackIn, ItemDisplayContext transformType) {
        if (transformType == ItemDisplayContext.GUI) {
            matrixStackIn.translate(0.9375F, 0.21875F, 0F);
//            FluidDrawersLegacyMod.LOGGER.info(transformType+"00"+matrixStackIn.last().pose().toString());
//             matrixStackIn.mulPose(new Quaternion(30, 225, 0, true));

            matrixStackIn.mulPose(XYZ.deg_to_rad(30, 225, 0 ));
            // matrixStackIn.mulPose(XYZ.deg_to_rad(30, 225, 0));

//            FluidDrawersLegacyMod.LOGGER.info(transformType+"11"+matrixStackIn.last().pose().toString());
            matrixStackIn.scale(0.625f, 0.625f, 0.625f);
//            FluidDrawersLegacyMod.LOGGER.info(transformType+"22"+matrixStackIn.last().pose().toString());
        }
        if (transformType == ItemDisplayContext.GROUND) {
            matrixStackIn.translate(0.375, 0.375, 0.375);
            matrixStackIn.scale(0.25f, 0.25f, 0.25f);
        }
//        if (transformType == ItemCameraTransforms.TransformType.FIXED) {
//            matrixStackIn.translate(0.225F, 0.225F, 0.25F);
//            matrixStackIn.scale(0.5f, 0.5f, 0.5f);
//        }
        if (transformType == ItemDisplayContext.THIRD_PERSON_RIGHT_HAND) {
            // matrixStackIn.mulPose(new Quaternion(75, 45, 0, true));
            matrixStackIn.mulPose(XYZ.deg_to_rad(75, 45, 0));
            matrixStackIn.translate(0.51625, 0.46875, -0.1875);
            matrixStackIn.scale(0.375f, 0.375f, 0.375f);
        }
        if (transformType == ItemDisplayContext.THIRD_PERSON_LEFT_HAND) {
            // matrixStackIn.mulPose(new Quaternion(75, 45, 0, true));
            matrixStackIn.mulPose(XYZ.deg_to_rad(75, 45, 0));
            matrixStackIn.translate(0.51625, 0.46875, -0.1875);
            matrixStackIn.scale(0.375f, 0.375f, 0.375f);
        }
        if (transformType == ItemDisplayContext.FIRST_PERSON_RIGHT_HAND) {
            matrixStackIn.translate(0.40625, -0.1875, 0);
            matrixStackIn.mulPose(XYZ.deg_to_rad(0, 45, 0));
            // matrixStackIn.mulPose(new Quaternion(0, 45, 0, true));
            matrixStackIn.scale(0.675f, 0.675f, 0.675f);
        }
        if (transformType == ItemDisplayContext.FIRST_PERSON_LEFT_HAND) {
            matrixStackIn.translate(0.59375, -0.1875, 0);
            matrixStackIn.mulPose(XYZ.deg_to_rad(0, 225, 0));
            // matrixStackIn.mulPose(new Quaterniond(0, 225, 0, true));
            matrixStackIn.scale(0.675f, 0.675f, 0.675f);
        }
        return matrixStackIn;
    }

    private void renderFluid(ItemStack stack, PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLight, double animationTime) {
        if (!stack.getOrCreateTag().contains("tank")&&!stack.getOrCreateTag().contains("tanks"))
            return;
        FluidStack fluidStackDown = new FluidStack(Fluids.EMPTY, 0);
        if (stack.getOrCreateTag().contains("tank")) {
            ListTag tanklist = new ListTag();
            tanklist.add(stack.getOrCreateTag().getCompound("tank"));
            stack.getOrCreateTag().put("tanks",tanklist);
        }
        var flist = new ArrayList<TankHolder>();
        if (stack.getOrCreateTag().contains("tanks")) {
            for (Tag tank : stack.getOrCreateTag().getList("tanks", ListTag.TAG_COMPOUND)) {
                FluidStack fluidStack = FluidStack.loadFluidStackFromNBT((CompoundTag) tank);
                int capacity = BlockEntityFluidDrawer.calculateTankCapacityFromStack(stack);
                if (!fluidStack.isEmpty()&& stack.getOrCreateTag().toString().contains("storagedrawers:creative_vending_upgrade"))
                    fluidStack.setAmount(capacity);
                flist.add(TankRenderUtil.of(fluidStack,capacity));
            }
        }
        TankRenderUtil.renderFluid(flist, matrixStackIn, bufferIn, combinedLight, animationTime);
    }

}
