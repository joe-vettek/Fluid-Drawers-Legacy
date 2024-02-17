package xueluoanping.fluiddrawerslegacy.compact.create;

import com.simibubi.create.content.fluids.potion.PotionFluid;
import com.simibubi.create.content.fluids.potion.PotionFluidHandler;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import xueluoanping.fluiddrawerslegacy.block.tileentity.TileEntityFluidDrawer;

import java.util.concurrent.atomic.AtomicBoolean;

public class CreatePotionHandler {
    public static boolean interactWithPotion(TileEntityFluidDrawer tile, Player player, ItemStack heldStack) {
        AtomicBoolean result = new AtomicBoolean(false);
        tile.getCapability(ForgeCapabilities.FLUID_HANDLER, Direction.DOWN)
                .ifPresent(fluidHandler -> {

                    FluidStack fluidStack = PotionFluidHandler.getFluidFromPotionItem(heldStack);
                    if (fluidHandler.fill(fluidStack, IFluidHandler.FluidAction.SIMULATE) > 0) {
                        int amount = fluidHandler.fill(fluidStack, IFluidHandler.FluidAction.EXECUTE);
                        if (amount > 0) {
                            result.set(true);
                            if (!player.isCreative())
                                player.setItemInHand(InteractionHand.MAIN_HAND, Items.GLASS_BOTTLE.getDefaultInstance());
                        }
                    }
                });
        return result.get();
    }

    public static boolean interactWithBottle(TileEntityFluidDrawer tile, Player player, ItemStack heldStack) {
        AtomicBoolean result = new AtomicBoolean(false);
        tile.getCapability(ForgeCapabilities.FLUID_HANDLER, Direction.DOWN)
                .ifPresent(fluidHandler -> {

                    FluidStack fluidStack = fluidHandler.getFluidInTank(0).copy();
                    if (fluidStack.isEmpty()) return;
                    fluidStack.setAmount(250);
                    if (!fluidStack.isEmpty() && fluidStack.getFluid() instanceof PotionFluid && fluidHandler.drain(fluidStack, IFluidHandler.FluidAction.SIMULATE).getAmount() == 250) {

                        FluidStack outStack = fluidHandler.drain(fluidStack, IFluidHandler.FluidAction.EXECUTE);
                        if (outStack.getAmount() == 250) {
                            result.set(true);
                            if (!player.isCreative()) {
                                ItemStack itemStack = PotionFluidHandler.fillBottle(Items.GLASS_BOTTLE.getDefaultInstance(), outStack);
                                heldStack.shrink(1);
                                player.setItemInHand(InteractionHand.MAIN_HAND, heldStack);
                                player.addItem(itemStack);
                            }
                        }
                    }
                });
        return result.get();
    }
}
