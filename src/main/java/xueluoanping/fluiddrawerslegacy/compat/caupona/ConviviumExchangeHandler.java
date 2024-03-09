package xueluoanping.fluiddrawerslegacy.compat.caupona;

import com.khjxiaogu.convivium.CVMain;
import com.khjxiaogu.convivium.blocks.foods.BeverageItem;
import com.teammoeg.caupona.CPMain;
import com.teammoeg.caupona.util.Utils;
import net.minecraft.world.item.Items;
import net.minecraftforge.fluids.FluidStack;
import xueluoanping.fluiddrawerslegacy.api.exchange.ModExchangeHandler;
import xueluoanping.fluiddrawerslegacy.api.exchange.ExchangeHandlerAno;
import xueluoanping.fluiddrawerslegacy.api.exchange.FluidExchangeHandlerManager;
import xueluoanping.fluiddrawerslegacy.config.General;

@ExchangeHandlerAno(mods = {CPMain.MODID, CVMain.MODID})
public class ConviviumExchangeHandler implements ModExchangeHandler {
    @Override
    public void registerFluidItem(FluidExchangeHandlerManager.FluidItem manager) {
        if (General.cauponaSoupBowl.get()) {
            manager.registerFluidItem(
                    (item) -> item.getItem() instanceof BeverageItem? Utils.extractFluid(item):FluidStack.EMPTY,
                    (fluidStack) -> Items.GLASS_BOTTLE.getDefaultInstance());
        }
    }
}
