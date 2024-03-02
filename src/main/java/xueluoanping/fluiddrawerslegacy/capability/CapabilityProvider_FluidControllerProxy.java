package xueluoanping.fluiddrawerslegacy.capability;

import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntitySlave;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;

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
        boolean isvalid=tile.getController() != null && tile.getController().isValidSlave(tile.getBlockPos());
        return cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY
                ? (isvalid?tile.getController().getCapability(cap, side):LazyOptional.empty())
                :LazyOptional.empty();
    }

}
