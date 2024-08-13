// package xueluoanping.fluiddrawerslegacy.capability;
//
// import com.jaquadro.minecraft.storagedrawers.block.tile.BlockEntityControllerIO;
// import net.minecraft.core.Direction;
// import net.neoforged.neoforge.capabilities.ICapabilityProvider;
//
// // import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
//
// import javax.annotation.Nonnull;
// import javax.annotation.Nullable;
//
// public class CapabilityProvider_FluidControllerProxy implements ICapabilityProvider {
//
//     private final BlockEntityControllerIO tile;
//     public CapabilityProvider_FluidControllerProxy(BlockEntityControllerIO tile) {
//         this.tile=tile;
//     }
//
//     @Nonnull
//     @Override
//     public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
//         // need it to help check if valid
//         boolean isvalid=tile.getController() != null && tile.getController().isValidSlave(tile.getBlockPos());
//         return cap == ForgeCapabilities.FLUID_HANDLER
//                 ? (isvalid?tile.getController().getCapability(cap, side):LazyOptional.empty())
//                 :LazyOptional.empty();
//     }
//
//     @Override
//     public @org.jetbrains.annotations.Nullable Object getCapability(Object object, Object context) {
//         return null;
//     }
// }
