package xueluoanping.fluiddrawerslegacy.capability;

import com.jaquadro.minecraft.storagedrawers.block.tile.tiledata.BlockEntityDataShim;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import xueluoanping.fluiddrawerslegacy.FluidDrawersLegacyMod;


public class FluidDrawerControllerData extends BlockEntityDataShim {
    ICapabilityProvider capProvider;

    public void setCapabilityProvider(ICapabilityProvider capProvider) {
        this.capProvider = capProvider;
    }

    @Override
    public void read(CompoundTag compoundTag) {
        FluidDrawersLegacyMod.logger("Load Drawer", compoundTag);
    }

    @Override
    public CompoundTag write(CompoundTag compoundTag) {
        FluidDrawersLegacyMod.logger("Save Drawer", compoundTag);
        return compoundTag;
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        if (capProvider instanceof CapabilityProvider_FluidDrawerController capabilityProviderFluidDrawerController) {
            capabilityProviderFluidDrawerController.invalidate();
        }
    }
}
