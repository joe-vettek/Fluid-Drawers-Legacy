package xueluoanping.fluiddrawerslegacy.data.lang;

import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.ExistingFileHelper;

import xueluoanping.fluiddrawerslegacy.FluidDrawersLegacyMod;

import java.util.List;

public class Lang_EN extends LangHelper {
    public Lang_EN(PackOutput gen, ExistingFileHelper helper) {
        super(gen, helper, FluidDrawersLegacyMod.MOD_ID, "en_us");
    }


    @Override
    protected void addTranslations() {
        add(FluidDrawersLegacyMod.MOD_ID, "Fluid Drawers Legacy");

        for (List<String> strings : List.of(List.of("", ""), List.of("_2", "1x2"), List.of("_4", "2x2"))) {
            addDrawer(strings.get(0),strings.get(1));
            addDrawer2(strings.get(0),strings.get(1));
        }


        addWailaHint("§o<..Hold shift to see more..>");
        addSlot("§7Slot %s: Contains %s of %s");

        addJadeConfig();

        add("itemGroup.fluiddrawers", "Fluid Drawers Legacy");
    }

    private void addJadeConfig() {
        add("config.jade.plugin_fluiddrawerslegacy.fluiddrawer", "Fluid Drawer");
        add("config.jade.plugin_fluiddrawerslegacy.controller", "Controller Fix");
        add("config.jade.plugin_fluiddrawerslegacy.controller_slave", "Controller Slave Fix");
        add("config.jade.plugin_fluiddrawerslegacy.trim", "Trim Fix");
    }

    @Override
    public void addDrawer(String countString, String hint) {
        super.addDrawer(countString, ("Fluid Drawer " + hint).strip());
        super.addFramedDrawer(countString, ("Framed Fluid Drawer " + hint).strip());
    }

    public void addDrawer2(String countString, String hint) {
        super.addDrawer(countString + "_half", ("Fluid Half Drawer " + hint).strip());
        super.addFramedDrawer(countString + "_half", ("Framed Fluid Half Drawer " + hint).strip());
    }
}
