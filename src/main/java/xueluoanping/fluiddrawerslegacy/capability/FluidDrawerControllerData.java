package xueluoanping.fluiddrawerslegacy.capability;


import com.jaquadro.minecraft.storagedrawers.block.tile.tiledata.TileDataShim;
import net.minecraft.nbt.CompoundNBT;
import xueluoanping.fluiddrawerslegacy.FluidDrawersLegacyMod;


public class FluidDrawerControllerData extends TileDataShim {
    CapabilityProvider_FluidDrawerController capProvider;

    private static final String KEY="FluidDrainCache";
    // WeakReference
    public void setCapabilityProvider(CapabilityProvider_FluidDrawerController capProvider) {
        this.capProvider = capProvider;
    }

    @Override
    public void read(CompoundNBT compoundTag) {
        FluidDrawersLegacyMod.logger("Load",KEY, compoundTag);
        if (capProvider instanceof CapabilityProvider_FluidDrawerController ) {
            if (compoundTag.contains(KEY))
                capProvider.deserializeNBT(compoundTag.getCompound(KEY));
        }
    }

    @Override
    public CompoundNBT write(CompoundNBT compoundTag) {
        FluidDrawersLegacyMod.logger("Save",KEY, compoundTag);
        if (capProvider instanceof CapabilityProvider_FluidDrawerController ) {
            // return capabilityProviderFluidDrawerController.serializeNBT();
            compoundTag.put(KEY, capProvider.serializeNBT());
        }
        return compoundTag;
    }

}
