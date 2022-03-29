package xueluoanping.fluiddrawerslegacy.client.model;


import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockDisplayReader;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.data.ModelDataMap;
import net.minecraftforge.client.model.data.ModelProperty;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

/*
* not use,just as a termplate
* */
public class FluidDrawerBlockBakedModel implements IBakedModel {
    private IBakedModel existingModel;
    public static ModelProperty<BlockState> COPIED_BLOCK = new ModelProperty<>();

    public FluidDrawerBlockBakedModel(IBakedModel existingModel) {
        this.existingModel = existingModel;
    }

    @Nonnull
    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @Nonnull Random rand, @Nonnull IModelData extraData) {
        IBakedModel renderModel = existingModel;
        if (extraData.hasProperty(COPIED_BLOCK)) {
            BlockState copiedBlock = extraData.getData(COPIED_BLOCK);
            if (copiedBlock != null) {
                Minecraft mc = Minecraft.getInstance();
                BlockRendererDispatcher blockRendererDispatcher = mc.getBlockRenderer();
                renderModel = blockRendererDispatcher.getBlockModel(copiedBlock);
            }
        }
        return renderModel.getQuads(state, side, rand, extraData);
    }
    @Override
    public IModelData getModelData(IBlockDisplayReader world, BlockPos pos, BlockState state, IModelData tileData) {
        BlockState downBlockState = world.getBlockState(pos.below());
        ModelDataMap modelDataMap = new ModelDataMap.Builder().withInitial(COPIED_BLOCK, null).build();

        if (downBlockState.getBlock() == Blocks.AIR ) {
            return modelDataMap;
        }
        modelDataMap.setData(COPIED_BLOCK, downBlockState);
        return modelDataMap;
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


}

