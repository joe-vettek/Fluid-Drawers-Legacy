package xueluoanping.fluiddrawerslegacy.compact.jade;

import com.jaquadro.minecraft.storagedrawers.block.BlockTrim;
import com.jaquadro.minecraft.storagedrawers.block.tile.BlockEntitySlave;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import snownee.jade.addon.universal.FluidStorageProvider;
import snownee.jade.api.*;
import snownee.jade.api.config.IPluginConfig;
import snownee.jade.api.fluid.JadeFluidObject;
import snownee.jade.api.ui.BoxStyle;
import snownee.jade.api.ui.IElementHelper;
import snownee.jade.api.ui.IProgressStyle;
import snownee.jade.overlay.DisplayHelper;
import xueluoanping.fluiddrawerslegacy.FluidDrawersLegacyMod;
import xueluoanping.fluiddrawerslegacy.ModTranslateKey;
import xueluoanping.fluiddrawerslegacy.config.ClientConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class TrimProviderFixer implements IBlockComponentProvider{
    static final TrimProviderFixer INSTANCE = new TrimProviderFixer();

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig iPluginConfig) {
        tooltip.remove(Identifiers.UNIVERSAL_FLUID_STORAGE);
    }

    @Override
    public ResourceLocation getUid() {
        return FluidDrawersLegacyMod.rl("trim");
    }

    @Override
    public int getDefaultPriority() {
        return FluidStorageProvider.INSTANCE.getDefaultPriority()+1000;
    }

}
