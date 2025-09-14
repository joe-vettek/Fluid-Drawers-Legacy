package xueluoanping.fluiddrawerslegacy;

import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawerAttributes;
import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawerGroup;
import com.jaquadro.minecraft.storagedrawers.capabilities.PlatformCapabilities;
import com.texelsaurus.minecraft.chameleon.capabilities.IForgeCapability;


public class ModConstants {

    /*
     *
     * 0 锁住且相同
     * 1 锁住且相同，此外包含销毁升级
     * 2 相同
     * 3 相同，但包含销毁升级
     * 4 空抽屉
     * 5 锁住，但是为空
     * 6 无效抽屉（这里改为满了）
     *
     */
    public static final int PRI_LOCKED = 0;
    public static final int PRI_LOCKED_VOID = 1;
    public static final int PRI_NORMAL = 2;
    public static final int PRI_VOID = 3;
    public static final int PRI_EMPTY = 4;
    public static final int PRI_LOCKED_EMPTY = 5;
    public static final int PRI_DISABLED = 6;


    public static IForgeCapability<IDrawerAttributes> DRAWER_ATTRIBUTES_CAPABILITY = (IForgeCapability<IDrawerAttributes>) PlatformCapabilities.DRAWER_ATTRIBUTES;

    public static IForgeCapability<IDrawerGroup> DRAWER_GROUP_CAPABILITY = (IForgeCapability<IDrawerGroup>) PlatformCapabilities.DRAWER_GROUP;

    // public final static ForgeCapability<IFluidHandler> FLUID_HANDLER_FORGE_CAPABILITY = new ForgeCapability<>(new ResourceLocation("forge","fluid"),  ForgeCapabilities.FLUID_HANDLER);

}
