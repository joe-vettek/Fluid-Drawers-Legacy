package xueluoanping.fluiddrawerslegacy.block.framed;

import com.jaquadro.minecraft.storagedrawers.util.WorldUtils;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import xueluoanping.fluiddrawerslegacy.block.ItemFluidDrawer;
import xueluoanping.fluiddrawerslegacy.block.framed.blockentity.FramedBlockEntityFluidDrawer;

public class FramedItemFluidDrawer extends ItemFluidDrawer {
    public FramedItemFluidDrawer(Block block, Properties properties) {
        super(block, properties);
    }

    protected boolean placeBlock(BlockPlaceContext context, BlockState state) {
        if (!super.placeBlock(context, state)) {
            return false;
        } else {
            FramedBlockEntityFluidDrawer blockEntity = WorldUtils.getBlockEntity(context.getLevel(), context.getClickedPos(), FramedBlockEntityFluidDrawer.class);
            ItemStack stack = context.getItemInHand();
            if (blockEntity != null && !stack.isEmpty()) {
                blockEntity.material().read(context.getItemInHand().getOrCreateTag());
            }

            return true;
        }
    }

}
