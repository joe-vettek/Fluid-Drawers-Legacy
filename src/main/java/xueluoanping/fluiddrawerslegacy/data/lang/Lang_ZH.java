package xueluoanping.fluiddrawerslegacy.data.lang;


import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.ExistingFileHelper;
import xueluoanping.fluiddrawerslegacy.FluidDrawersLegacyMod;

import java.util.List;

import static xueluoanping.fluiddrawerslegacy.util.RegisterFinderUtil.*;

public class Lang_ZH extends LangHelper {
    public Lang_ZH(PackOutput gen, ExistingFileHelper helper) {
        super(gen, helper, FluidDrawersLegacyMod.MOD_ID, "zh_cn");
    }


    @Override
    protected void addTranslations() {
        add(FluidDrawersLegacyMod.MOD_ID, "储液抽屉：遗产");
        add("itemGroup.fluiddrawers", "储液抽屉：遗产");

        for (List<String> strings : List.of(List.of("", ""), List.of("_2", "1x2"), List.of("_4", "2x2"))) {
            addDrawer(strings.get(0),strings.get(1));
            addDrawer2(strings.get(0),strings.get(1));
        }
		
		addWailaHint( "§o<..按住shift以查看更多..>");
        addSlot("§7%s号格子: 装有%s的%s");
    }



    @Override
    public void addDrawer(String countString, String hint) {
        super.addDrawer(countString, (hint + " 流体抽屉").strip());
        super.addFramedDrawer(countString,  (hint + " 镶框流体抽屉").strip());
    }

    public void addDrawer2(String countString, String hint) {
        super.addDrawer(countString + "_half", (hint + " 小型流体抽屉").strip());
        super.addFramedDrawer(countString + "_half", (hint+ " 小型镶框流体抽屉").strip());
    }
}
