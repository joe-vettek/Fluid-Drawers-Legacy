package xueluoanping.fluiddrawerslegacy.capability;

import com.jaquadro.minecraft.storagedrawers.block.tile.tiledata.BlockEntityDataShim;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import xueluoanping.fluiddrawerslegacy.FluidDrawersLegacyMod;

public class FluidDrawerControllerData extends BlockEntityDataShim {
    ICapabilityProvider capProvider;

    private static final String KEY="FluidDrainCache";
    // WeakReference
    public void setCapabilityProvider(ICapabilityProvider capProvider) {
        this.capProvider = capProvider;
    }

    @Override
    public void read(CompoundTag compoundTag) {
        FluidDrawersLegacyMod.logger("Load",KEY, compoundTag);
        if (capProvider instanceof CapabilityProvider_FluidDrawerController capabilityProviderFluidDrawerController) {
            if (compoundTag.contains(KEY))
                capabilityProviderFluidDrawerController.deserializeNBT(compoundTag.getCompound(KEY));
        }
    }

    @Override
    public CompoundTag write(CompoundTag compoundTag) {
        FluidDrawersLegacyMod.logger("Save",KEY, compoundTag);
        if (capProvider instanceof CapabilityProvider_FluidDrawerController capabilityProviderFluidDrawerController) {
            // return capabilityProviderFluidDrawerController.serializeNBT();
            compoundTag.put(KEY, capabilityProviderFluidDrawerController.serializeNBT());
        }
        return compoundTag;
    }

}
