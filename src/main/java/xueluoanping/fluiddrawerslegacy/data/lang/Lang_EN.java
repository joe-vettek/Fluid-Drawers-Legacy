package xueluoanping.fluiddrawerslegacy.data.lang;

import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.ExistingFileHelper;

import xueluoanping.fluiddrawerslegacy.FluidDrawersLegacyMod;

public class Lang_EN extends LangHelper {
    public Lang_EN(PackOutput gen, ExistingFileHelper helper) {
        super(gen, helper, FluidDrawersLegacyMod.MOD_ID, "en_us");
    }


    @Override
    protected void addTranslations() {
        add(FluidDrawersLegacyMod.MOD_ID, "Fluid Drawers Legacy");
        
        addDrawer("", "");
        addDrawer("_2", "1x2");
        addDrawer("_4", "2x2");
        addDrawer2("", "");
        addDrawer2("_2", "1x2");
        addDrawer2("_4", "2x2");

        addWailaHint( "§o<..Hold shift to see more..>");
        addSlot("Slot %s: Contains %s of %s");

        addJadeConfig();
    }
    
    private void addJadeConfig() {
        add("config.jade.plugin_fluiddrawerslegacy.fluiddrawer", "Fluid Drawer");
        add("config.jade.plugin_fluiddrawerslegacy.controller", "Controller Fix");
        add("config.jade.plugin_fluiddrawerslegacy.controller_slave", "Controller Slave Fix");
        add("config.jade.plugin_fluiddrawerslegacy.trim", "Trim Fix");
    }

    @Override
    public void addDrawer(String countString, String hint) {
        super.addDrawer(countString, ("Fluid Drawer "+hint).strip());
    }

    public void addDrawer2(String countString, String hint) {
        super.addDrawer(countString + "_half", ("Fluid Half Drawer "+hint).strip());
    }
}
