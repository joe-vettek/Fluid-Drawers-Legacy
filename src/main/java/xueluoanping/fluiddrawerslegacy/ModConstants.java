package xueluoanping.fluiddrawerslegacy;

import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawerAttributes;
import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawerGroup;
import com.jaquadro.minecraft.storagedrawers.capabilities.CapabilityDrawerAttributes;
import com.jaquadro.minecraft.storagedrawers.capabilities.CapabilityDrawerGroup;
import net.neoforged.neoforge.capabilities.BlockCapability;


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


    public static BlockCapability<IDrawerAttributes, Void> DRAWER_ATTRIBUTES_CAPABILITY = CapabilityDrawerAttributes.DRAWER_ATTRIBUTES_CAPABILITY;

    public static BlockCapability<IDrawerGroup, Void> DRAWER_GROUP_CAPABILITY = CapabilityDrawerGroup.DRAWER_GROUP_CAPABILITY;


}
