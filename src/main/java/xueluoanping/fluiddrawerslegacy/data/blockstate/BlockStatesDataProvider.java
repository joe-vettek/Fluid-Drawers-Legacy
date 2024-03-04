package xueluoanping.fluiddrawerslegacy.data.blockstate;

import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;
import xueluoanping.fluiddrawerslegacy.FluidDrawersLegacyMod;
import xueluoanping.fluiddrawerslegacy.ModContents;
import xueluoanping.fluiddrawerslegacy.util.RegisterFinderUtil;

import javax.annotation.Nullable;
import java.util.List;

public class BlockStatesDataProvider extends BlockStateProvider {


	public BlockStatesDataProvider(PackOutput gen, ExistingFileHelper exFileHelper) {

		super(gen, FluidDrawersLegacyMod.MOD_ID, exFileHelper);
	}

	@Override
	protected void registerStatesAndModels() {
		// simpleBlock(BlockRegister.bamboo_root.get());
		for (RegistryObject<Block> entry : ModContents.DREntityBlocks.getEntries()) {
			getVariantBuilder(entry.get()).forAllStatesExcept(state -> ConfiguredModel.builder()
					.modelFile(models().getExistingFile(resourceBlock(entry.getId().getPath())))
					// .modelFile(models().withExistingParent(blockName(entry.get()), resourceBlock(entry.getId().getPath())))
					.rotationY(getRotateYByFacing(state))
					.build());
		}

	}



	// Thanks vectorwing，great work
	// I am not proud of this method... But hey, it's runData. Only I shall have to deal with it.
	public void customStageBlock(Block block, @Nullable ResourceLocation parent, String textureKey, IntegerProperty ageProperty, List<Integer> suffixes, Property<?>... ignored) {
		getVariantBuilder(block)
				.forAllStatesExcept(state -> {
					int ageSuffix = state.getValue(ageProperty);
					String stageName = blockName(block) + "_stage_";
					stageName += suffixes.isEmpty() ? ageSuffix : suffixes.get(Math.min(suffixes.size() - 1, ageSuffix));
					// Cuisine.logger(stageName);
					if (parent == null) {
						return ConfiguredModel.builder()
								.modelFile(models().cross(stageName, resourceBlock(stageName))).build();
					}
					return ConfiguredModel.builder()
							.modelFile(models().singleTexture(stageName, parent, textureKey, resourceBlock(stageName))).build();
				}, ignored);
	}

	private String blockName(Block block) {
		return RegisterFinderUtil.getBlockKey(block).getPath();
	}

	public static ResourceLocation resourceBlock(String path) {
		return new ResourceLocation(FluidDrawersLegacyMod.MOD_ID, "block/" + path);
	}

	public ResourceLocation resourceVanillaBlock(String path) {
		return new ResourceLocation("block/" + path);
	}

	public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

	public static int getRotateYByFacing(BlockState state) {
		switch (state.getValue(FACING)) {
			case EAST -> {
				return 90;
			}
			case SOUTH -> {
				return 180;
			}
			case WEST -> {
				return 270;
			}
			default -> {
				return 0;
			}
		}
	}

}
