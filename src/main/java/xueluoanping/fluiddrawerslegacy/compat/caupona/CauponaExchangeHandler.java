package xueluoanping.fluiddrawerslegacy.compat.caupona;

import com.khjxiaogu.convivium.CVMain;
import com.teammoeg.caupona.item.StewItem;
import com.teammoeg.caupona.util.Utils;
import net.minecraft.world.item.Items;
import net.minecraftforge.fluids.FluidStack;
import xueluoanping.fluiddrawerslegacy.api.exchange.FluidExchangeHandlerManager;
import xueluoanping.fluiddrawerslegacy.api.exchange.ModExchangeHandler;
import xueluoanping.fluiddrawerslegacy.api.exchange.ExchangeHandlerAno;
import xueluoanping.fluiddrawerslegacy.config.General;

@ExchangeHandlerAno(mods = {CVMain.MODID})
public class CauponaExchangeHandler implements ModExchangeHandler {


    @Override
    public void registerFluidItem(FluidExchangeHandlerManager.FluidItem manager) {
        if (General.cauponaSoupBowl.get()) {
            manager.registerFluidItem((item) -> {
                if (item.getItem() instanceof StewItem) return Utils.extractFluid(item);
                else return FluidStack.EMPTY;
            }, (fluidStack) -> Items.BOWL.getDefaultInstance());
        }
    }


}
