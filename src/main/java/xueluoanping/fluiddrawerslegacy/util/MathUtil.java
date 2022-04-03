package xueluoanping.fluiddrawerslegacy.util;

import com.mojang.math.Vector3d;

public class MathUtil {
    public static float calDistance(Vector3d v1, Vector3d v2) {
        double dx=v1.x-v2.x;
        double dy=v1.y-v2.y;
        double dz=v1.z-v2.z;
        return (float) Math.sqrt(Math.pow(dx,2)+Math.pow(dy,2)+Math.pow(dx,2));
    }

    public static float calDistanceSelf(Vector3d v1) {
        double dx=v1.x;
        double dy=v1.y;
        double dz=v1.z;
        return (float) Math.sqrt(Math.pow(dx,2)+Math.pow(dy,2)+Math.pow(dx,2));
    }
}
