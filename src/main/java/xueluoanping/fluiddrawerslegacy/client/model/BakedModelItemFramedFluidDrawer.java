package xueluoanping.fluiddrawerslegacy.client.model;


import com.jaquadro.minecraft.storagedrawers.block.tile.tiledata.MaterialData;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.ChunkRenderTypeSet;
import net.minecraftforge.client.model.BakedModelWrapper;
import net.minecraftforge.client.model.data.ModelData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xueluoanping.fluiddrawerslegacy.block.framed.blockentity.FramedBlockEntityFluidDrawer;

import java.util.ArrayList;
import java.util.List;

public class BakedModelItemFramedFluidDrawer extends BakedModelFramedFluidDrawer {

    private final MaterialData materialData;
    private final RenderType renderType;

    public BakedModelItemFramedFluidDrawer(MaterialData materialData, RenderType renderType, BakedModel existingModel) {
        super(existingModel);
        this.materialData = materialData;
        this.renderType = renderType;
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, RandomSource rand) {
        List<BakedQuad> quads = super.getQuads(state, side, rand);
        List<BakedQuad> bakedQuads = makeBakedQuads(ModelData.EMPTY, renderType, materialData, quads);
        if (bakedQuads != null) return bakedQuads;
        return quads;
    }

    @Override
    public BakedModel applyTransform(ItemDisplayContext transformType, PoseStack poseStack, boolean applyLeftHandTransform) {
        getTransforms().getTransform(transformType).apply(applyLeftHandTransform, poseStack);
        return this;
    }

    @Override
    public List<BakedModel> getRenderPasses(ItemStack itemStack, boolean fabulous) {
        return List.of(this);
    }


    @Override
    public List<RenderType> getRenderTypes(ItemStack itemStack, boolean fabulous) {
        return super.getRenderTypes(itemStack, fabulous);
    }
}

