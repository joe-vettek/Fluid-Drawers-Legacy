package xueluoanping.fluiddrawerslegacy.data.lang;


import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.ExistingFileHelper;
import xueluoanping.fluiddrawerslegacy.FluidDrawersLegacyMod;

import static xueluoanping.fluiddrawerslegacy.util.RegisterFinderUtil.*;

public class Lang_ZH extends LangHelper {
    public Lang_ZH(PackOutput gen, ExistingFileHelper helper) {
        super(gen, helper, FluidDrawersLegacyMod.MOD_ID, "zh_cn");
    }


    @Override
    protected void addTranslations() {
        add(FluidDrawersLegacyMod.MOD_ID, "储液抽屉：遗产");

        addDrawer("", "");
        addDrawer("_2", "1x2");
        addDrawer("_4", "2x2");
        addDrawer2("", "");
        addDrawer2("_2", "1x2");
        addDrawer2("_4", "2x2");
		
		addWailaHint( "§o<..按住shift以查看更多..>");
        addSlot("§7o%s号抽屉: 装有%s的%s");
    }



    @Override
    public void addDrawer(String countString, String hint) {
        super.addDrawer(countString, (hint + " 流体抽屉").strip());
    }

    public void addDrawer2(String countString, String hint) {
        super.addDrawer(countString + "_half", (hint + " 小型流体抽屉").strip());
    }
}
