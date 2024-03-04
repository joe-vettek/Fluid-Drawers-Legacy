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
        add(FluidDrawersLegacyMod.MOD_ID, "Dynamic Trees for Fruitful Fun");

    }
}
