package xueluoanping.fluiddrawerslegacy.capability;

import com.jaquadro.minecraft.storagedrawers.block.tile.BlockEntityController;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.common.Mod;
import xueluoanping.fluiddrawerslegacy.api.drawer.betterFluidManager;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CapabilityProvider_FluidDrawerController implements ICapabilityProvider {

    public final betterFluidManager<BlockEntityController> tank;
    private final LazyOptional<betterFluidManager<BlockEntityController>> tankHandler;
    public static BlockPos tilePos = null;
    final BlockEntityController tile;

    public CapabilityProvider_FluidDrawerController(final BlockEntityController tile) {
        this.tile = tile;
        tank = createFluidHandler();
        tankHandler = LazyOptional.of(() -> tank);
        tilePos = tile.getBlockPos();
        FluidDrawerControllerData fluidDrawerControllerData = new FluidDrawerControllerData();
        fluidDrawerControllerData.setCapabilityProvider(this);
        this.tile.injectData(fluidDrawerControllerData);
    }

    public void invalidate() {
        tankHandler.invalidate();
    }


    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.FLUID_HANDLER) {
            return tankHandler.cast();
        }
        return LazyOptional.empty();
    }


    private betterFluidManager<BlockEntityController> createFluidHandler() {
        return new betterFluidManager<>(tile);
    }
}
