package xueluoanping.fluiddrawerslegacy.client.model;


import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Random;

public class BakedModelFluidDrawer implements BakedModel {
    private BakedModel existingModel;

    public BakedModelFluidDrawer(BakedModel existingModel) {
        this.existingModel = existingModel;
    }

    public BakedModel self()
    {
        return this;
    }

    // @Override
    // public @NotNull List<BakedQuad> getQuads(@org.jetbrains.annotations.Nullable BlockState state, @org.jetbrains.annotations.Nullable Direction side, @NotNull RandomSource rand, @NotNull ModelData data, @org.jetbrains.annotations.Nullable RenderType renderType) {
    //     throw new AssertionError("IForgeBakedModel::getQuads should never be called, only IForgeBakedModel::getQuads");
    // }

    // @Override
    // public List<BakedQuad> getQuads(@Nullable BlockState p_200117_1_, @Nullable Direction p_200117_2_, Random p_200117_3_) {
    //     throw new AssertionError("IForgeBakedModel::getQuads should never be called, only IForgeBakedModel::getQuads");
    // }

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState p_235039_, @Nullable Direction p_235040_, RandomSource p_235041_) {
        throw new AssertionError("IForgeBakedModel::getQuads should never be called, only IForgeBakedModel::getQuads");
    }

    @Override
    public boolean useAmbientOcclusion() {
        return this.existingModel.useAmbientOcclusion();
    }

    @Override
    public boolean isGui3d() {
        return this.existingModel.isGui3d();
    }

    @Override
    public boolean usesBlockLight() {
        return this.existingModel.usesBlockLight();
    }

    @Override
    public boolean isCustomRenderer() {
        return true;
    }

    @Override
    public TextureAtlasSprite getParticleIcon() {
        return this.existingModel.getParticleIcon();
    }

    @Override
    public ItemOverrides getOverrides() {
        return this.existingModel.getOverrides();
    }

    @Override
    public BakedModel applyTransform(ItemDisplayContext transformType, PoseStack poseStack, boolean applyLeftHandTransform) {

        if (transformType == ItemDisplayContext.NONE ||
                transformType ==ItemDisplayContext.FIXED)
            return this.existingModel.applyTransform(transformType, poseStack,applyLeftHandTransform);
        // return this;
        return BakedModel.super.applyTransform(transformType, poseStack, applyLeftHandTransform);
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

