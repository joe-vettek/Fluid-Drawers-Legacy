package xueluoanping.fluiddrawerslegacy.compat.vanlia;

import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidType;
import xueluoanping.fluiddrawerslegacy.api.exchange.ExchangeHandlerAno;
import xueluoanping.fluiddrawerslegacy.api.exchange.ModExchangeHandler;
import xueluoanping.fluiddrawerslegacy.api.exchange.FluidExchangeHandlerManager;


@ExchangeHandlerAno
public class BucketExchanger implements ModExchangeHandler {

    @Override
    public void registerFluidItem(FluidExchangeHandlerManager.FluidItem manager) {
        // if (General.createPotion.get())
        {
            manager.registerFluidItem(
                    (item) -> item.getItem() instanceof BucketItem bucketItem &&bucketItem.getFluid()!= Fluids.EMPTY ? new FluidStack(bucketItem.getFluid(), FluidType.BUCKET_VOLUME) : FluidStack.EMPTY,
                    (fluidStack) -> Items.BUCKET.getDefaultInstance());
            manager.registerFluidContainer((item) -> item.getItem() == Items.BUCKET,
                    (fluid) -> fluid.getFluid().getBucket()==Items.AIR?0:FluidType.BUCKET_VOLUME,
                    (outStack) -> outStack.getFluid().getBucket().getDefaultInstance());
        }
    }
}
