package xueluoanping.fluiddrawerslegacy;

import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawerAttributes;
import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawerGroup;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;

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


    @CapabilityInject(IDrawerAttributes.class)
    public static Capability<IDrawerAttributes> DRAWER_ATTRIBUTES_CAPABILITY = null;

    @CapabilityInject(IDrawerGroup.class)
    public static Capability<IDrawerGroup> DRAWER_GROUP_CAPABILITY = null;
}
