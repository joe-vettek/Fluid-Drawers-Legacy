package xueluoanping.fluiddrawerslegacy.client.model;


import com.jaquadro.minecraft.storagedrawers.block.tile.tiledata.MaterialData;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.ChunkRenderTypeSet;
import net.minecraftforge.client.model.BakedModelWrapper;
import net.minecraftforge.client.model.data.ModelData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xueluoanping.fluiddrawerslegacy.FluidDrawersLegacyMod;
import xueluoanping.fluiddrawerslegacy.block.framed.blockentity.FramedBlockEntityFluidDrawer;

import java.util.ArrayList;
import java.util.List;

import static net.minecraft.world.inventory.InventoryMenu.BLOCK_ATLAS;

public class BakedModelFramedFluidDrawer extends BakedModelWrapper<BakedModel> {

    public BakedModelFramedFluidDrawer(BakedModel existingModel) {
        super(existingModel);

    }

    @Override
    public @NotNull List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @NotNull RandomSource rand, @NotNull ModelData extraData, @Nullable RenderType renderType) {
        List<BakedQuad> quads = super.getQuads(state, side, rand, extraData, renderType);
        if (extraData.has(FramedBlockEntityFluidDrawer.AGE_PROPERTY)) {
            MaterialData materialData = extraData.get(FramedBlockEntityFluidDrawer.AGE_PROPERTY);
            List<BakedQuad> quads2 = makeBakedQuads(extraData, renderType, materialData, quads);
            if (quads2 != null) return quads2;
        }
        return quads;
    }

    protected static @Nullable List<BakedQuad> makeBakedQuads(@NotNull ModelData extraData, @Nullable RenderType renderType, MaterialData materialData, List<BakedQuad> quads) {
        if (materialData != null && !materialData.isEmpty()) {
            List<BakedQuad> quads2 = new ArrayList<>();
            boolean cutoutType = renderType != RenderType.translucent();
            for (BakedQuad quad : quads) {
                ResourceLocation name = quad.getSprite().contents().name();
                boolean cutout = true;
                if (!materialData.getSide().isEmpty()
                        && name.getPath().contains("block_side")) {
                    quad = new BakedQuadRetextured(quad, Minecraft.getInstance().getItemRenderer()
                            .getItemModelShaper().getItemModel(materialData.getSide()).getParticleIcon(extraData));
                } else if (!materialData.getFront().isEmpty()
                        && name.getPath().contains("front_empty")) {
                    cutout = false;
                    quad = new BakedQuadRetextured(quad, Minecraft.getInstance().getItemRenderer()
                            .getItemModelShaper().getItemModel(materialData.getFront()).getParticleIcon(extraData));
                } else if (!materialData.getTrim().isEmpty()
                        && (name.getPath().contains("side") || name.getPath().contains("top")
                        || name.getPath().contains("back"))
                ) {
                    quad = new BakedQuadRetextured(quad, Minecraft.getInstance().getItemRenderer()
                            .getItemModelShaper().getItemModel(materialData.getTrim()).getParticleIcon(extraData));
                }
                // if(!cutoutType){
                //     quads2.add(quad);
                // }
                // else {
                //     if(cutout)
                //     quads2.add(new BakedQuadRetextured(quad,Minecraft.getInstance().getTextureAtlas(BLOCK_ATLAS)
                //                             .apply(FluidDrawersLegacyMod.rl("block/front_empty"))));
                // }

                if (cutoutType == cutout)
                    quads2.add(quad);

                // else {
                //     if(cutout){
                //         quads2.add(new BakedQuadRetextured(quad,Minecraft.getInstance().getTextureAtlas(BLOCK_ATLAS)
                //                 .apply(R)));
                //     }
                // }
            }
            return quads2;
        }
        return null;
    }


    @Override
    public ChunkRenderTypeSet getRenderTypes(@NotNull BlockState state, @NotNull RandomSource rand, @NotNull ModelData extraData) {
        ChunkRenderTypeSet renderTypes = super.getRenderTypes(state, rand, extraData);
        if (extraData.has(FramedBlockEntityFluidDrawer.AGE_PROPERTY)) {
            MaterialData materialData = extraData.get(FramedBlockEntityFluidDrawer.AGE_PROPERTY);
            if (materialData != null && !materialData.isMatOpaque(materialData.getFront())) {
                // ChunkRenderTypeSet renderTypes1 = Minecraft.getInstance().getItemRenderer()
                //         .getItemModelShaper().getItemModel(materialData.getFront()).getRenderTypes(state, rand, extraData);
                return ChunkRenderTypeSet.of(
                        RenderType.translucent(), RenderType.cutout());
            }
        }
        return renderTypes;
    }

    @Override
    public BakedModel applyTransform(ItemDisplayContext transformType, PoseStack poseStack, boolean applyLeftHandTransform) {
        getTransforms().getTransform(transformType).apply(applyLeftHandTransform, poseStack);
        return this;
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, RandomSource rand) {
        return super.getQuads(state, side, rand);
    }

    @Override
    public List<BakedModel> getRenderPasses(ItemStack itemStack, boolean fabulous) {
        MaterialData materialData = new MaterialData();
        materialData.read(itemStack.getOrCreateTag());
        return materialData.isEmpty() ?
                List.of(this) : List.of(new BakedModelItemFramedFluidDrawer(
                        materialData, RenderType.translucent(), originalModel
                )
                , new BakedModelItemFramedFluidDrawer(
                        materialData, RenderType.cutout(), originalModel
                )
        );
    }


    @Override
    public List<RenderType> getRenderTypes(ItemStack itemStack, boolean fabulous) {
        MaterialData materialData = new MaterialData();
        materialData.read(itemStack.getOrCreateTag());
        return materialData.isEmpty() ?
                super.getRenderTypes(itemStack, fabulous) :
                List.of(
                        RenderType.translucent()
                        // ,
                        // RenderType.cutout()
                );
    }

    @Override
    public @NotNull TextureAtlasSprite getParticleIcon(@NotNull ModelData extraData) {
        if (extraData.has(FramedBlockEntityFluidDrawer.AGE_PROPERTY)) {
            MaterialData materialData = extraData.get(FramedBlockEntityFluidDrawer.AGE_PROPERTY);
            if (materialData != null && !materialData.isEmpty()) {
                if (!materialData.getSide().isEmpty()) {
                    return Minecraft.getInstance().getItemRenderer()
                            .getItemModelShaper().getItemModel(materialData.getSide())
                            .getParticleIcon(extraData);
                }
            }
        }
        return super.getParticleIcon(extraData);
    }
}

