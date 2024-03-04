package xueluoanping.fluiddrawerslegacy.data.lang;


import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.ExistingFileHelper;
import xueluoanping.fluiddrawerslegacy.FluidDrawersLegacyMod;


public class Lang_ZH extends LangHelper {
	public Lang_ZH(PackOutput gen, ExistingFileHelper helper) {
		super(gen, helper, FluidDrawersLegacyMod.MOD_ID, "zh_cn");
	}


	@Override
	protected void addTranslations() {
		add(FluidDrawersLegacyMod.MOD_ID, "Dynamic Trees for Fruitful Fun");


	}


}
