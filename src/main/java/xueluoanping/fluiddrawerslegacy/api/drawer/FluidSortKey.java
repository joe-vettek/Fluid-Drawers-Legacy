package xueluoanping.fluiddrawerslegacy.api.drawer;

import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidStack;


public record FluidSortKey(Holder<Fluid> fluid, DataComponentPatch dataComponentPatch) {

    public static final FluidSortKey EMPTY = of(FluidStack.EMPTY);

    public static FluidSortKey of(FluidStack fluidStackKey) {
        return new FluidSortKey(fluidStackKey.getFluid().builtInRegistryHolder(),
                fluidStackKey.getComponentsPatch()
        );
    }

    public boolean isEmpty() {
        return false;
    }

    public FluidStack to() {
        return to(1);
    }

    public FluidStack to(int amount) {
        if (fluid == Fluids.EMPTY.builtInRegistryHolder()) {
            return FluidStack.EMPTY;
        }
        FluidStack stack = new FluidStack(fluid, amount);
        for (var entry : dataComponentPatch.entrySet()) {
            if (entry.getValue().isPresent())
                stack.set((DataComponentType) entry.getKey(), entry.getValue().get());
        }
        return stack;
    }
}
