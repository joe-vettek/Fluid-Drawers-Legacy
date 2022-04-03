package xueluoanping.fluiddrawerslegacy.capability;

import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntitySlave;
import net.minecraft.core.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class CapabilityProvider_FluidControllerProxy implements  ICapabilityProvider {

    private final TileEntitySlave tile;
    public CapabilityProvider_FluidControllerProxy(TileEntitySlave tile) {
        this.tile=tile;
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        return cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY
                ? (tile.getController()!=null?tile.getController().getCapability(cap, side):LazyOptional.empty())
                :LazyOptional.empty();
    }

}
