package xueluoanping.fluiddrawerslegacy.block.framed.blockentity;

import com.jaquadro.minecraft.storagedrawers.api.framing.IFramedBlockEntity;
import com.jaquadro.minecraft.storagedrawers.block.tile.tiledata.MaterialData;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.client.model.data.ModelProperty;
import org.jetbrains.annotations.NotNull;
import xueluoanping.fluiddrawerslegacy.block.blockentity.BlockEntityFluidDrawer;

public class FramedBlockEntityFluidDrawer extends BlockEntityFluidDrawer implements IFramedBlockEntity {
    public FramedBlockEntityFluidDrawer(int slotCount, BlockPos pos, BlockState state) {
        super(slotCount, pos, state);
        this.injectData(this.materialData);
    }
    private final MaterialData materialData = new MaterialData();

    @Override
    public MaterialData material() {
        return this.materialData;
    }

    @Override
    public CompoundTag writePortable(CompoundTag tag) {
        tag = super.writePortable(tag);
        return materialData.write(tag);
    }

    @Override
    public void requestModelDataUpdate() {
        super.requestModelDataUpdate();
    }

    public static final ModelProperty<MaterialData> AGE_PROPERTY = new ModelProperty<>();

    @Override
    public @NotNull ModelData getModelData() {
        return ModelData.builder().with(AGE_PROPERTY, material()).build();
    }
}
