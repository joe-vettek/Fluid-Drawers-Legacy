package xueluoanping.fluiddrawerslegacy.client.model;


import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.BakedModelWrapper;
import net.minecraftforge.client.model.data.ModelData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BakedModelFluidDrawer extends BakedModelWrapper<BakedModel> {

    public BakedModelFluidDrawer(BakedModel existingModel) {
        super(existingModel);
    }

    @Override
    public boolean isCustomRenderer() {
        return true;
    }

    @Override
    public @NotNull List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @NotNull RandomSource rand, @NotNull ModelData extraData, @Nullable RenderType renderType) {
        return super.getQuads(state, side, rand, extraData, renderType);
    }

    @Override
    public BakedModel applyTransform(ItemDisplayContext transformType, PoseStack poseStack, boolean applyLeftHandTransform) {
        if (transformType == ItemDisplayContext.NONE ||
                transformType ==ItemDisplayContext.FIXED)
            return this.originalModel.applyTransform(transformType, poseStack,applyLeftHandTransform);
        getTransforms().getTransform(transformType).apply(applyLeftHandTransform, poseStack);
        return this;
    }


    // @Override
    // public BakedModel handlePerspective(ItemTransforms.TransformType cameraTransformType, PoseStack mat) {
    //
    //     if (cameraTransformType == ItemTransforms.TransformType.NONE ||
    //             cameraTransformType ==ItemTransforms.TransformType.FIXED)
    //         return this.existingModel.(cameraTransformType, mat);
    //     return this;
    // }
}

