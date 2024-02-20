package xueluoanping.fluiddrawerslegacy.handler;

import com.google.common.base.Preconditions;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.SoundActions;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidActionResult;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

// It's a patch for FluidUtil, because it can't specify the fluid
public class FluidUtilPatch {

    public static boolean interactWithFluidHandlerAndEmpty(@NotNull Player player, @NotNull InteractionHand hand, @NotNull IFluidHandler handler,FluidStack fluidStack) {
        Preconditions.checkNotNull(player);
        Preconditions.checkNotNull(hand);
        Preconditions.checkNotNull(handler);

        ItemStack heldItem = player.getItemInHand(hand);
        if (!heldItem.isEmpty()) {
            return player.getCapability(ForgeCapabilities.ITEM_HANDLER)
                    .map(playerInventory -> {
                        // FluidActionResult fluidActionResult = FluidUtil.tryFillContainerAndStow(heldItem, handler, playerInventory, Integer.MAX_VALUE, player, true);
                        // if (!fluidActionResult.isSuccess())
                        // {
                        FluidActionResult fluidActionResult = tryEmptyContainerAndStow(heldItem, handler, playerInventory, fluidStack, player, true);
                        // }
                        if (fluidActionResult.isSuccess()) {
                            player.setItemInHand(hand, fluidActionResult.getResult());
                            return true;
                        }
                        return false;
                    })
                    .orElse(false);
        }
        return false;
    }


    public static FluidActionResult tryEmptyContainerAndStow(@NotNull ItemStack container, IFluidHandler fluidDestination, IItemHandler inventory, FluidStack fluidStack, @Nullable Player player, boolean doDrain)
    {
        if (container.isEmpty())
        {
            return FluidActionResult.FAILURE;
        }

        if (player != null && player.getAbilities().instabuild)
        {
            // Note: FluidUtil just use amount to empty container
            FluidActionResult emptiedReal = tryEmptyContainer(container, fluidDestination, fluidStack, player, doDrain);
            if (emptiedReal.isSuccess())
            {
                return new FluidActionResult(container); // creative mode: item does not change
            }
        }
        else if (container.getCount() == 1) // don't need to stow anything, just fill and edit the container stack
        {
            // Note: FluidUtil just use amount to empty container
            FluidActionResult emptiedReal = tryEmptyContainer(container, fluidDestination, fluidStack, player, doDrain);
            if (emptiedReal.isSuccess())
            {
                return emptiedReal;
            }
        }
        else
        {
            // Note: FluidUtil just use amount to empty container
            FluidActionResult emptiedSimulated = tryEmptyContainer(container, fluidDestination, fluidStack, player, false);
            if (emptiedSimulated.isSuccess())
            {
                // check if we can give the itemStack to the inventory
                ItemStack remainder = ItemHandlerHelper.insertItemStacked(inventory, emptiedSimulated.getResult(), true);
                if (remainder.isEmpty() || player != null)
                {
                    // Note: FluidUtil just use amount to empty container
                    FluidActionResult emptiedReal = tryEmptyContainer(container, fluidDestination, fluidStack, player, doDrain);
                    remainder = ItemHandlerHelper.insertItemStacked(inventory, emptiedReal.getResult(), !doDrain);

                    // give it to the player or drop it at their feet
                    if (!remainder.isEmpty() && player != null && doDrain)
                    {
                        ItemHandlerHelper.giveItemToPlayer(player, remainder);
                    }

                    ItemStack containerCopy = container.copy();
                    containerCopy.shrink(1);
                    return new FluidActionResult(containerCopy);
                }
            }
        }

        return FluidActionResult.FAILURE;
    }


    // Note: FluidUtil just use amount to empty container
    @NotNull
    public static FluidActionResult tryEmptyContainer(@NotNull ItemStack container, IFluidHandler fluidDestination, FluidStack fluidStack, @Nullable Player player, boolean doDrain)
    {
        ItemStack containerCopy = ItemHandlerHelper.copyStackWithSize(container, 1); // do not modify the input
        return getFluidHandler(containerCopy)
                .map(containerFluidHandler -> {
                    // Note: FluidUtil just use amount to empty container
                    FluidStack transfer = FluidUtil.tryFluidTransfer(fluidDestination, containerFluidHandler, fluidStack, doDrain);
                    if (transfer.isEmpty())
                        return FluidActionResult.FAILURE;
                    if (!doDrain)
                    {
                        // We are acting on a COPY of the stack, so performing changes on the source is acceptable even if we are simulating.
                        // We need to perform the change otherwise the call to getContainer() will be incorrect.
                        containerFluidHandler.drain(transfer, IFluidHandler.FluidAction.EXECUTE);
                    }

                    if (doDrain && player != null)
                    {
                        SoundEvent soundevent = transfer.getFluid().getFluidType().getSound(transfer, SoundActions.BUCKET_EMPTY);

                        if (soundevent != null)
                        {
                            player.level().playSound(null, player.getX(), player.getY() + 0.5, player.getZ(), soundevent, SoundSource.BLOCKS, 1.0F, 1.0F);
                        }
                    }

                    ItemStack resultContainer = containerFluidHandler.getContainer();
                    return new FluidActionResult(resultContainer);
                })
                .orElse(FluidActionResult.FAILURE);
    }

    public static LazyOptional<IFluidHandlerItem> getFluidHandler(@NotNull ItemStack itemStack)
    {
        return itemStack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM);
    }

}
