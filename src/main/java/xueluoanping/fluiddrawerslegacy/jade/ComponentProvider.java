package xueluoanping.fluiddrawerslegacy.jade;


import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityController;
import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntitySlave;
import mcp.mobius.waila.api.*;
import mcp.mobius.waila.api.config.IPluginConfig;
import mcp.mobius.waila.api.ui.IElement;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import snownee.jade.VanillaPlugin;
import snownee.jade.addon.forge.ForgeCapabilityProvider;
import xueluoanping.fluiddrawerslegacy.ModTranslateKey;
import xueluoanping.fluiddrawerslegacy.capability.CapabilityProvider_FluidDrawerController;
import xueluoanping.fluiddrawerslegacy.config.ClientConfig;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class ComponentProvider implements IComponentProvider, IServerDataProvider<BlockEntity> {
    static final ComponentProvider INSTANCE = new ComponentProvider();


    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
        // ((BlockAccessorImpl) accessor).serverData
        //
        if ((accessor.getBlockEntity() instanceof TileEntitySlave)) {
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
