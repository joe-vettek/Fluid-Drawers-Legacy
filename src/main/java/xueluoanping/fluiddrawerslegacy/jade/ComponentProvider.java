package xueluoanping.fluiddrawerslegacy.jade;



import com.jaquadro.minecraft.storagedrawers.block.tile.BlockEntityController;
import com.jaquadro.minecraft.storagedrawers.block.tile.BlockEntitySlave;
import mcp.mobius.waila.api.*;
import mcp.mobius.waila.api.config.IPluginConfig;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.fluids.FluidStack;

import java.util.*;

public class ComponentProvider implements IComponentProvider, IServerDataProvider<BlockEntity> {
    static final ComponentProvider INSTANCE = new ComponentProvider();


    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
        // ((BlockAccessorImpl) accessor).serverData
        //
        if ((accessor.getBlockEntity() instanceof BlockEntitySlave)) {
            var tag = accessor.getServerData().getList("jadeTanks", Tag.TAG_COMPOUND);
            for (Tag tag1 : tag) {
                var stack = FluidStack.loadFluidStackFromNBT((CompoundTag) tag1);
                DrawerCompenProvider.appendTank(tooltip, stack, ((CompoundTag) tag1).getInt("capacity"), FluidStack.EMPTY, false);
            }
        }
        JadeFluidHandler.resortTooltip(tooltip, accessor, config);
    }

    @Override
    public void appendServerData(CompoundTag compoundTag, ServerPlayer serverPlayer, Level level, BlockEntity blockEntity, boolean b) {
        // if (!(blockEntity instanceof TileEntityController)
        //         &&!(blockEntity instanceof TileEntitySlave))
        //     return;
        // JadeFluidHandler.appendServerDataIfWithNotEmpty(compoundTag,blockEntity);
    }
}
