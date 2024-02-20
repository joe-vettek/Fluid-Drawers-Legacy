package xueluoanping.fluiddrawerslegacy.compact.jade;


// import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityController;

import com.jaquadro.minecraft.storagedrawers.block.tile.BlockEntityController;
import com.jaquadro.minecraft.storagedrawers.block.tile.BlockEntitySlave;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.Fluids;
import snownee.jade.addon.universal.FluidStorageProvider;
import snownee.jade.api.*;
import snownee.jade.api.config.IPluginConfig;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
// import net.minecraft.network.chat.DisplayHelper.INSTANCEComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
// import snownee.jade.VanillaPlugin;
// import snownee.jade.addon.forge.ForgeCapabilityProvider;
import snownee.jade.api.ui.BoxStyle;
import snownee.jade.api.ui.IElementHelper;
import snownee.jade.api.ui.IProgressStyle;
import snownee.jade.api.view.FluidView;
import snownee.jade.overlay.DisplayHelper;
import xueluoanping.fluiddrawerslegacy.FluidDrawersLegacyMod;
import xueluoanping.fluiddrawerslegacy.ModTranslateKey;
import xueluoanping.fluiddrawerslegacy.config.ClientConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class ControllerProviderFixer implements IBlockComponentProvider, IServerDataProvider<BlockEntity> {
    static final ControllerProviderFixer INSTANCE = new ControllerProviderFixer();


    // TODO: reduce the code and it not need more (just keep hide)
    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
        JadeFluidHandler.resortTooltip(tooltip,accessor,config);
    }


    @Override
    public ResourceLocation getUid() {
        return FluidDrawersLegacyMod.rl("controller");
    }

    @Override
    public int getDefaultPriority() {
        return FluidStorageProvider.INSTANCE.getDefaultPriority() + 1000;
    }


    @Override
    public void appendServerData(CompoundTag compoundTag, ServerPlayer serverPlayer, Level level, BlockEntity blockEntity, boolean b) {
        if (!(blockEntity instanceof BlockEntityController)
                &&!(blockEntity instanceof BlockEntitySlave))
            return;
        JadeFluidHandler.appendServerDataIfWithNotEmpty(compoundTag,  blockEntity);
    }
}
