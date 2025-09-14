package xueluoanping.fluiddrawerslegacy.block.framed;

import com.jaquadro.minecraft.storagedrawers.api.framing.FrameMaterial;
import com.jaquadro.minecraft.storagedrawers.api.framing.IFramedBlock;
import com.jaquadro.minecraft.storagedrawers.api.framing.IFramedBlockEntity;
import com.jaquadro.minecraft.storagedrawers.block.tile.util.FrameHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import xueluoanping.fluiddrawerslegacy.block.BlockFluidDrawer;
import xueluoanping.fluiddrawerslegacy.block.framed.blockentity.FramedBlockEntityFluidDrawer;

public class FramedBlockFluidDrawer extends BlockFluidDrawer implements IFramedBlock {
    public FramedBlockFluidDrawer(Properties properties, int slotCount, boolean half) {
        super(properties, slotCount, half);
    }

    @Override
    public IFramedBlockEntity getFramedBlockEntity(@NotNull Level level, @NotNull BlockPos blockPos) {
        return (IFramedBlockEntity)level.getBlockEntity(blockPos);
    }

    @Override
    public boolean supportsFrameMaterial(FrameMaterial frameMaterial) {
        return true;
    }

    @Override
    public ItemStack makeFramedItem(ItemStack source, ItemStack matSide, ItemStack matTrim, ItemStack matFront) {
        return FrameHelper.makeFramedItem(this, source, matSide, matTrim, matFront);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new FramedBlockEntityFluidDrawer(getSlotCount(), pos, state);
    }

}
