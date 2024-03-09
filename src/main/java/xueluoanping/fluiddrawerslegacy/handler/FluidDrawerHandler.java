package xueluoanping.fluiddrawerslegacy.handler;

import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.SoundActions;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;

public class FluidDrawerHandler {

    public static boolean rightClickInPut(IFluidHandler fluidHandler, Player player, FluidStack extractFluid, ItemStack resultStack, InteractionHand mainHand) {
        AtomicBoolean result = new AtomicBoolean(false);
        {
            FluidStack fluidStack = extractFluid.copy();
            if (!fluidStack.isEmpty() && fluidHandler.fill(fluidStack, IFluidHandler.FluidAction.SIMULATE) == extractFluid.getAmount()) {
                int amount = fluidHandler.fill(fluidStack, IFluidHandler.FluidAction.EXECUTE);
                if (amount == fluidStack.getAmount()) {
                    result.set(true);
                    playSound(player,fluidStack,true);
                    if (!player.isCreative())
                        player.setItemInHand(mainHand, resultStack);
                }
            }
        }
        return result.get();
    }

    public static boolean rightClickOuput(IFluidHandler fluidHandler, Player player, Function<FluidStack, Integer> testIfValidAndSet, Function<FluidStack, ItemStack> getItemByFluid, ItemStack heldStack, InteractionHand mainHand) {
        AtomicBoolean result = new AtomicBoolean(false);
        {
            FluidStack fluidStack = fluidHandler.getFluidInTank(0).copy();
            if (fluidStack.isEmpty()) return false;
            // fluidStack.setAmount(250);
            int amount = testIfValidAndSet.apply(fluidStack);
            fluidStack.setAmount(amount);
            if (!fluidStack.isEmpty() && fluidHandler.drain(fluidStack, IFluidHandler.FluidAction.SIMULATE).getAmount() == amount) {

                FluidStack outStack = fluidHandler.drain(fluidStack, IFluidHandler.FluidAction.EXECUTE);
                if (outStack.getAmount() == amount) {
                    result.set(true);
                    playSound(player,outStack,false);
                    if (!player.isCreative()) {
                        ItemStack itemStack = getItemByFluid.apply(outStack);
                        heldStack.shrink(1);
                        player.setItemInHand(InteractionHand.MAIN_HAND, heldStack);
                        if (!heldStack.isEmpty())
                            player.addItem(itemStack);
                        else player.setItemInHand(InteractionHand.MAIN_HAND, itemStack);
                    }
                }
            }
        }
        return result.get();
    }

    public static boolean playSound(Player player, FluidStack transfer,boolean doDrain) {
        if (player != null) {
            SoundEvent soundevent = transfer.getFluid().getFluidType().getSound(transfer,doDrain? SoundActions.BUCKET_EMPTY: SoundActions.BUCKET_FILL);

            if (soundevent != null) {
                player.level().playSound(null, player.getX(), player.getY() + 0.5, player.getZ(), soundevent, SoundSource.BLOCKS, 1.0F, 1.0F);
                return true;
            }
        }
        return false;
    }
}
