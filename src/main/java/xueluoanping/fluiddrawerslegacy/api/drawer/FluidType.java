package xueluoanping.fluiddrawerslegacy.api.drawer;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public record FluidType(Fluid fluid, @Nullable CompoundTag tag) {
    public static FluidType of(FluidStack fluidStack) {
        return new FluidType(fluidStack.getFluid(), fluidStack.getTag());
    }

    @Override
    public int hashCode() {
        int code = 1;
        code = 31 * code + fluid().hashCode();
        if (tag != null)
            code = 31 * code + tag.hashCode();
        return code;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof FluidType)) {
            return false;
        }
        return isFluidEqual((FluidType) o);
    }

    public boolean isEmpty() {
        return fluid == Fluids.EMPTY;
    }


    public boolean isFluidEqual(@NotNull FluidType other) {
        return fluid() == other.fluid() && isFluidStackTagEqual(other);
    }

    private boolean isFluidStackTagEqual(FluidType other) {
        return tag == null ? other.tag == null : other.tag != null && tag.equals(other.tag);
    }


    public static boolean isFluidEqual(FluidStack stack, @NotNull FluidType other) {
        return stack.getFluid() == other.fluid() && isFluidStackTagEqual(stack, other);
    }

    public static boolean isFluidStackTagEqual(FluidStack stack, FluidType other) {
        CompoundTag tag = stack.getTag();
        return tag == null ? other.tag == null : other.tag != null && tag.equals(other.tag);
    }
}
