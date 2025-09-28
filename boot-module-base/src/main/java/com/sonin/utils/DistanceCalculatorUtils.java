package com.sonin.utils;

/**
 * @Author：sonin
 * @Date：2025/7/22 16:46
 */
public class DistanceCalculatorUtils {

    // 地球半径，单位：米
    private static final double EARTH_RADIUS = 6371000;

    /**
     * 将角度转换为弧度
     * @param degrees 角度值
     * @return 弧度值
     */
    private static double toRadians(double degrees) {
        return degrees * Math.PI / 180;
    }

    /**
     * 计算两个经纬度点之间的距离（单位：米）
     * @param lat1 第一个点的纬度
     * @param lon1 第一个点的经度
     * @param lat2 第二个点的纬度
     * @param lon2 第二个点的经度
     * @return 两点之间的距离（米）
     */
    public static double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        // 将经纬度转换为弧度
        double radLat1 = toRadians(lat1);
        double radLon1 = toRadians(lon1);
        double radLat2 = toRadians(lat2);
        double radLon2 = toRadians(lon2);

        // 计算纬度差和经度差
        double deltaLat = radLat2 - radLat1;
        double deltaLon = radLon2 - radLon1;

        // 应用Haversine公式
        double a = Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2)
                + Math.cos(radLat1) * Math.cos(radLat2)
                * Math.sin(deltaLon / 2) * Math.sin(deltaLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        // 计算距离（米）
        return EARTH_RADIUS * c;
    }

    public static void main(String[] args) {
        // 示例：计算北京到上海的距离
        double beijingLat = 39.9042;
        double beijingLon = 116.4074;

        double shanghaiLat = 31.2304;
        double shanghaiLon = 121.4737;

        double distance = calculateDistance(beijingLat, beijingLon, shanghaiLat, shanghaiLon);

        System.out.println("北京到上海的直线距离约为：" + Math.round(distance) + " 米");
        System.out.println("北京到上海的直线距离约为：" + Math.round(distance / 1000) + " 公里");
    }

}
