package com.sonin.utils;

/**
 * @Author：sonin
 * @Date：2025/7/14 14:32
 */
public class CoordinateConverter {

    // 判断坐标是否在中国范围内
    private static boolean outOfChina(double lat, double lon) {
        if (lon < 72.004 || lon > 137.8347)
            return true;
        if (lat < 0.8293 || lat > 55.8271)
            return true;
        return false;
    }

    // 转换纬度的辅助函数
    private static double transformLat(double x, double y) {
        double ret = -100.0 + 2.0 * x + 3.0 * y + 0.2 * y * y + 0.1 * x * y + 0.2 * Math.sqrt(Math.abs(x));
        ret += (20.0 * Math.sin(6.0 * x * Math.PI) + 20.0 * Math.sin(2.0 * x * Math.PI)) * 2.0 / 3.0;
        ret += (20.0 * Math.sin(y * Math.PI) + 40.0 * Math.sin(y / 3.0 * Math.PI)) * 2.0 / 3.0;
        ret += (160.0 * Math.sin(y / 12.0 * Math.PI) + 320 * Math.sin(y * Math.PI / 30.0)) * 2.0 / 3.0;
        return ret;
    }

    // 转换经度的辅助函数
    private static double transformLon(double x, double y) {
        double ret = 300.0 + x + 2.0 * y + 0.1 * x * x + 0.1 * x * y + 0.1 * Math.sqrt(Math.abs(x));
        ret += (20.0 * Math.sin(6.0 * x * Math.PI) + 20.0 * Math.sin(2.0 * x * Math.PI)) * 2.0 / 3.0;
        ret += (20.0 * Math.sin(x * Math.PI) + 40.0 * Math.sin(x / 3.0 * Math.PI)) * 2.0 / 3.0;
        ret += (150.0 * Math.sin(x / 12.0 * Math.PI) + 300.0 * Math.sin(x / 30.0 * Math.PI)) * 2.0 / 3.0;
        return ret;
    }

    // WGS84 转 GCJ02
    public static Coordinate wgs84ToGcj02(double wgLat, double wgLon) {
        Coordinate coord = new Coordinate(wgLat, wgLon);
        if (outOfChina(wgLat, wgLon)) {
            return coord;
        }
        double dLat = transformLat(wgLon - 105.0, wgLat - 35.0);
        double dLon = transformLon(wgLon - 105.0, wgLat - 35.0);
        double radLat = wgLat / 180.0 * Math.PI;
        double magic = Math.sin(radLat);
        magic = 1 - 0.00669342162296594323 * magic * magic;
        double sqrtMagic = Math.sqrt(magic);
        dLat = (dLat * 180.0) / ((6378245.0 * (1 - 0.00669342162296594323)) / (magic * sqrtMagic) * Math.PI);
        dLon = (dLon * 180.0) / (6378245.0 / sqrtMagic * Math.cos(radLat) * Math.PI);
        double mgLat = wgLat + dLat;
        double mgLon = wgLon + dLon;
        return new Coordinate(mgLat, mgLon);
    }

    // 坐标类
    public static class Coordinate {
        private double latitude;
        private double longitude;

        public Coordinate(double latitude, double longitude) {
            this.latitude = latitude;
            this.longitude = longitude;
        }

        public double getLatitude() {
            return latitude;
        }

        public double getLongitude() {
            return longitude;
        }

        @Override
        public String toString() {
            return "Coordinate{" +
                    "latitude=" + latitude +
                    ", longitude=" + longitude +
                    '}';
        }
    }

    // 测试示例
    public static void main(String[] args) {
        // 示例：北京天安门广场的 WGS84 坐标
        double wgsLat = 39.908719;
        double wgsLon = 116.397428;

        Coordinate gcjCoord = wgs84ToGcj02(wgsLat, wgsLon);
        System.out.println("WGS84 坐标: " + wgsLat + ", " + wgsLon);
        System.out.println("GCJ-02 坐标: " + gcjCoord.getLatitude() + ", " + gcjCoord.getLongitude());
    }

}
