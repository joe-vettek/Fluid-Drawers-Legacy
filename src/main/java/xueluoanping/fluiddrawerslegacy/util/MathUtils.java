package xueluoanping.fluiddrawerslegacy.util;


import net.minecraft.block.Block;
import net.minecraft.util.Direction;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.vector.Vector3d;


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

        public Point(Vector3d vec3) {
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

        //	旋转点
        public static Point rotatePoint(Point p, float angle) {
            angle = (float) Math.toRadians(angle);
            double x0 = p.x * Math.cos(angle) + p.z * Math.sin(angle);
            double z0 = -p.x * Math.sin(angle) + p.z * Math.cos(angle);
            return new Point(x0, p.y, z0);
        }

        //	适用于VoxelShape中得到的点
        public static double fromMCVoxelShapetoMathCenter(double value) {
            value = value * 16 - 8.0;
            return value;
        }

        //适用于Block.box，不需要/16
        public static Point fromMathCentertoMCBlock(Point p) {
            return new Point(p.x + 8.0D, p.y, p.z + 8.0D);
        }

        //适用于Block.box，不需要/16
        public static Point fromMCBlockBoxtoMathCenter(Point p) {
            return new Point(p.x - 8.0D, p.y, p.z - 8.0D);
        }

    }


    //请输入x正轴方向的碰撞箱点位，即EAST,需要为16D，MC的Block.box坐标系，代码很丑陋，但是如果需要数学解法很麻烦
    //数学解法，旋转后，再计算最小xz，最大xz，重排列
    private static VoxelShape getShapefromDirection(double x1, double y1, double z1, double x2, double y2, double z2, Direction direction) {
        //偏移到数学坐标系
        x1 = x1 - 8.0D;
        x2 = x2 - 8.0D;
        z1 = z1 - 8.0D;
        z2 = z2 - 8.0D;
        double x0 = Math.min(x1, x2);
        double xd = (x1 - x2) > 0 ? (x1 - x2) : (x2 - x1);
        double ymin = Math.min(y1, y2);
        double ymax = Math.max(y1, y2);
        double zmin = Math.abs(Math.min(z1, z2));
        double zmax = Math.abs(Math.max(z1, z2));
        switch (direction) {
            case SOUTH:
                return Block.box(-zmin + 8.0D, ymin, x0 + 8.0D, zmax + 8.0D, ymax, x0 + xd + 8.0D);
            case WEST:
                return Block.box(-x0 - xd + 8.0D, ymin, -zmin + 8.0D, -x0 + 8.0D, ymax, zmax + 8.0D);
            case NORTH:
                return Block.box(-zmax + 8.0D, ymin, -x0 - xd + 8.0D, zmin + 8.0D, ymax, -x0 + 8.0D);
            default:
                return Block.box(x0 + 8.0D, ymin, -zmin + 8.0D, x0 + xd + 8.0D, ymax, zmax + 8.0D);
        }
    }


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
        if (!useAngle)
            return getShapefromDirection(x1, y1, z1, x2, y2, z2, direction);
        else {

            if (direction == Direction.SOUTH)
                return getShapefromAngle(x1, y1, z1, x2, y2, z2, 270);

            if (direction == Direction.WEST)
                return getShapefromAngle(x1, y1, z1, x2, y2, z2, 180);

            if (direction == Direction.NORTH)
                return getShapefromAngle(x1, y1, z1, x2, y2, z2, 90);

//				North
            return Block.box(x1, y1, z1, x2, y2, z2);

        }
    }

    public static VoxelShape getShapefromAngle(VoxelShape v1, VoxelShape v2, int angle) {
        return getShapefromAngle(v1.min(Direction.Axis.X), v1.min(Direction.Axis.Y), v1.min(Direction.Axis.Z)
                , v2.min(Direction.Axis.X), v2.min(Direction.Axis.Y), v2.min(Direction.Axis.Z)
                , angle);
    }
}



