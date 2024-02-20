package xueluoanping.fluiddrawerslegacy.compat.caupona;

import com.khjxiaogu.convivium.CVMain;
import com.teammoeg.caupona.item.StewItem;
import com.teammoeg.caupona.util.Utils;
import net.minecraft.world.item.Items;
import net.minecraftforge.fluids.FluidStack;
import xueluoanping.fluiddrawerslegacy.compat.ModExchangeHandler;
import xueluoanping.fluiddrawerslegacy.compat.ExchangeHandlerAno;
import xueluoanping.fluiddrawerslegacy.compat.ModHandlerManager;
import xueluoanping.fluiddrawerslegacy.config.General;

@ExchangeHandlerAno(mods = {CVMain.MODID})
public class CauponaExchangeHandler implements ModExchangeHandler {


    @Override
    public void registerFluidItem(ModHandlerManager.FluidItem manager) {
        if (General.cauponaSoupBowl.get()) {
            manager.registerFluidItem((item) -> {
                if (item.getItem() instanceof StewItem) return Utils.extractFluid(item);
                else return FluidStack.EMPTY;
            }, (fluidStack) -> Items.BOWL.getDefaultInstance());
        }
    }


}
