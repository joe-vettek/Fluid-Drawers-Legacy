package xueluoanping.fluiddrawerslegacy.client.model;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Direction;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

public class FluidDrawerBakedModel implements IBakedModel {
    private IBakedModel existingModel;

    public FluidDrawerBakedModel(IBakedModel existingModel) {
        this.existingModel = existingModel;
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState p_200117_1_, @Nullable Direction p_200117_2_, Random p_200117_3_) {
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
    public ItemOverrideList getOverrides() {
        return this.existingModel.getOverrides();
    }

    @Override
    public IBakedModel handlePerspective(ItemCameraTransforms.TransformType cameraTransformType, MatrixStack mat) {


        if (cameraTransformType == ItemCameraTransforms.TransformType.NONE ||
                cameraTransformType ==ItemCameraTransforms.TransformType.FIXED)
            return this.existingModel.handlePerspective(cameraTransformType, mat);
        return this;
    }
}

