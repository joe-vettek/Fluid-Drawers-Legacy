package xueluoanping.fluiddrawerslegacy.compat.create;

import com.simibubi.create.Create;
import com.simibubi.create.content.fluids.potion.PotionFluid;
import com.simibubi.create.content.fluids.potion.PotionFluidHandler;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.PotionItem;
import net.minecraftforge.fluids.FluidStack;
import xueluoanping.fluiddrawerslegacy.compat.ModExchangeHandler;
import xueluoanping.fluiddrawerslegacy.compat.ExchangeHandlerAno;
import xueluoanping.fluiddrawerslegacy.compat.ModHandlerManager;
import xueluoanping.fluiddrawerslegacy.config.General;

@ExchangeHandlerAno(mods = {Create.ID})
public class CreateExchangeHandler implements ModExchangeHandler {

    @Override
    public void registerFluidItem(ModHandlerManager.FluidItem manager) {
        if (General.createPotion.get()) {
            manager.registerFluidItem(
                    (item) -> item.getItem() instanceof PotionItem? PotionFluidHandler.getFluidFromPotionItem(item):FluidStack.EMPTY,
                    (fluidStack) -> Items.GLASS_BOTTLE.getDefaultInstance());
            manager.registerFluidContainer((item) -> item.getItem() == Items.GLASS_BOTTLE,
                    (fluid) -> fluid.getFluid() instanceof PotionFluid?250:0,
                    (outStack) -> PotionFluidHandler.fillBottle(Items.GLASS_BOTTLE.getDefaultInstance(), outStack));
        }
    }
}
