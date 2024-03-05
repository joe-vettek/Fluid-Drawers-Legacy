package xueluoanping.fluiddrawerslegacy.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;

public class MathUtils {

    public static class Point {
        public final double x;
        public final double y;
        public final double z;

        public Point(double x, double y, double z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        public Point(Vec3 vec3) {
            this.x = vec3.x();
            this.y = vec3.y();
            this.z = vec3.z();
        }

        public static Point[] fromVoxelShape(VoxelShape oldShape) {
            Point[] points = new Point[2];
            double x;
            double y;
            double z;
            x = fromMCVoxelShapetoMathCenter(oldShape.min(Direction.Axis.X));
            y = oldShape.min(Direction.Axis.Y) * 16D;
            z = fromMCVoxelShapetoMathCenter(oldShape.min(Direction.Axis.Z));
            points[0] = new Point(x, y, z);
            x = fromMCVoxelShapetoMathCenter(oldShape.max(Direction.Axis.X));
            y = oldShape.max(Direction.Axis.Y) * 16D;
            z = fromMCVoxelShapetoMathCenter(oldShape.max(Direction.Axis.Z));
            points[1] = new Point(x, y, z);
            return points;
        }

        //	Rotate point
        public static Point rotatePoint(Point p, float angle) {
            angle = (float) CMath.toRadians(angle);
            double x0 = p.x * CMath.cos(angle) + p.z * CMath.sin(angle);
            double z0 = -p.x * CMath.sin(angle) + p.z * CMath.cos(angle);
            return new Point(x0, p.y, z0);
        }

        //	适用于VoxelShape中得到的点
        public static double fromMCVoxelShapetoMathCenter(double value) {
            value = value * 16 - 8.0;
            return value;
        }

        // 适用于Block.box，不需要/16
        public static Point fromMathCentertoMCBlock(Point p) {
            return new Point(p.x + 8.0D, p.y, p.z + 8.0D);
        }

        // 适用于Block.box，不需要/16
        public static Point fromMCBlockBoxtoMathCenter(Point p) {
            return new Point(p.x - 8.0D, p.y, p.z - 8.0D);
        }

    }


    //	//请输入x正轴方向的碰撞箱点位，即EAST,需要为16D
    ////	这个输入要求为MC方块坐标系

    public static VoxelShape getShapefromAngle(double x1, double y1, double z1, double x2, double y2, double z2, float angle) {
        //		偏移到数学坐标系
        Point p1 = Point.fromMCBlockBoxtoMathCenter(new Point(x1, y1, z1));
        Point p2 = Point.fromMCBlockBoxtoMathCenter(new Point(x2, y2, z2));
        //		旋转
        p1 = Point.rotatePoint(p1, angle);
        p2 = Point.rotatePoint(p2, angle);
        //		还原
        p1 = Point.fromMathCentertoMCBlock(p1);
        p2 = Point.fromMathCentertoMCBlock(p2);
        //				重排列
        double xmin = Math.min(p1.x, p2.x);
        double xmax = Math.max(p1.x, p2.x);
        double ymin = Math.min(p1.y, p2.y);
        double ymax = Math.max(p1.y, p2.y);
        double zmin = Math.min(p1.z, p2.z);
        double zmax = Math.max(p1.z, p2.z);
        return Block.box(xmin, ymin, zmin, xmax, ymax, zmax);
    }

    public static VoxelShape getShapefromDirection(double x1, double y1, double z1, double x2, double y2, double z2, Direction direction, boolean useAngle) {
        // if (!useAngle)
        //     return getShapefromDirection(x1, y1, z1, x2, y2, z2, direction);
        // else {
        switch (direction) {
            case EAST -> {
                return getShapefromAngle(x1, y1, z1, x2, y2, z2, 270);
            }
            case SOUTH -> {
                return getShapefromAngle(x1, y1, z1, x2, y2, z2, 180);
            }
            case WEST -> {
                return getShapefromAngle(x1, y1, z1, x2, y2, z2, 90);
            }
            //				North
            default -> {
                return Block.box(x1, y1, z1, x2, y2, z2);
            }
        }
        // }
    }

    public static VoxelShape getShapefromAngle(VoxelShape v1, int angle) {
        return getShapefromAngle(v1.min(Direction.Axis.X) * 16, v1.min(Direction.Axis.Y) * 16, v1.min(Direction.Axis.Z) * 16
                , v1.max(Direction.Axis.X) * 16, v1.max(Direction.Axis.Y) * 16, v1.max(Direction.Axis.Z) * 16
                , angle);
    }

    public static int getRandomSpread(Random r) {
        int select = r.nextInt(3) - 1;
        return select;
    }

    public static BlockPos getRandomSpreadPos(BlockPos pos, Random r) {
        int xOffset = MathUtils.getRandomSpread(r);
        int yOffset = MathUtils.getRandomSpread(r);
        int zOffset = MathUtils.getRandomSpread(r);
        return pos.offset(xOffset, yOffset, zOffset);
    }

    private static class CMath {

        public static final Map<Integer, Integer> COS_ANGLE_MAP = Map.of(0, 1, 90, 0, 180, -1, 270, 0, -90, 1);

        public static double toRadians(float angle) {
            return Math.toRadians(angle);
        }

        public static double cos(float angle) {
            var temp = (int) angle;
            if (COS_ANGLE_MAP.containsKey(temp))
                return COS_ANGLE_MAP.get(temp);
            return Math.cos(angle);
        }

        public static double sin(float angle) {
            var temp = (int) angle - 90;
            if (COS_ANGLE_MAP.containsKey(temp))
                return COS_ANGLE_MAP.get(temp);
            return Math.sin(angle);
        }
    }
}


