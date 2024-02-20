package xueluoanping.fluiddrawerslegacy.compat.jade;


// import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityController;

import com.jaquadro.minecraft.storagedrawers.block.tile.BlockEntityController;
import com.jaquadro.minecraft.storagedrawers.block.tile.BlockEntitySlave;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.addon.universal.FluidStorageProvider;
import snownee.jade.api.*;
import snownee.jade.api.config.IPluginConfig;
import net.minecraft.nbt.CompoundTag;
// import net.minecraft.network.chat.DisplayHelper.INSTANCEComponent;
// import snownee.jade.VanillaPlugin;
// import snownee.jade.addon.forge.ForgeCapabilityProvider;
import xueluoanping.fluiddrawerslegacy.FluidDrawersLegacyMod;

public class ControllerProviderFixer implements IBlockComponentProvider, IServerDataProvider<BlockAccessor> {
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
    public void appendServerData(CompoundTag compoundTag, BlockAccessor accessor) {
        // FluidDrawersLegacyMod.logger(compoundTag);
        if (!(accessor.getBlockEntity() instanceof BlockEntityController)
                &&!(accessor.getBlockEntity() instanceof BlockEntitySlave))
            return;
        JadeFluidHandler.appendServerDataIfWithNotEmpty(compoundTag,accessor);
    }
}
