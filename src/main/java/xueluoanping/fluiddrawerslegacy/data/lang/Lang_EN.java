package xueluoanping.fluiddrawerslegacy.data.lang;

import net.minecraft.data.PackOutput;

import net.neoforged.neoforge.common.data.ExistingFileHelper;
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
        addSlot("§7Slot %s: Contains %s of %s");

        addJadeConfig();

        add("itemGroup.fluiddrawers", "Fluid Drawers Legacy");

        add(FluidDrawersLegacyMod.MOD_ID+".configuration.Renderer", "Renderer");
        add(FluidDrawersLegacyMod.MOD_ID+".configuration.Display", "Display");
        add(FluidDrawersLegacyMod.MOD_ID+".configuration.debugMode", "Debug Mode");
        add(FluidDrawersLegacyMod.MOD_ID+".configuration.general", "General");
        add(FluidDrawersLegacyMod.MOD_ID+".configuration.compat", "Compat");

        add(FluidDrawersLegacyMod.MOD_ID+".configuration.distance", "Distance");
        add(FluidDrawersLegacyMod.MOD_ID+".configuration.showlimit", "Show Limit");
        // add(FluidDrawersLegacyMod.MOD_ID+".configuration.debugMode", "debugMode");
        add(FluidDrawersLegacyMod.MOD_ID+".configuration.volume", "Volume");
        add(FluidDrawersLegacyMod.MOD_ID+".configuration.retainFluid", "Retain Fluid");
        add(FluidDrawersLegacyMod.MOD_ID+".configuration.createPotionInteraction", "Create Potion Interaction");
        add(FluidDrawersLegacyMod.MOD_ID+".configuration.cauponaSoupBowlInteraction", "Caupona SoupBowl Interaction");

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
