package com.sw.minavi.util;

public class LocationUtilities {

	private static final int[] CORRECT_AZIMUTH = new int[720];
	static {
		for (int i = 0; i < 360; i++) {
			CORRECT_AZIMUTH[i] = i;
		}
		int azimuth = 0;
		for (int i = 360; i < CORRECT_AZIMUTH.length; i++) {
			CORRECT_AZIMUTH[i] = azimuth;
			azimuth++;
		}
	}

	/**
	 * 2点間の距離を求める取得する
	 *
	 * @param latitude1
	 *            緯度1
	 * @param longitude1
	 *            経度1
	 * @param latitude2
	 *            緯度2
	 * @param longitude2
	 *            経度2
	 * @param precision
	 *            精度(小数点以下の桁数)
	 * @return 2点間の距離(km)
	 */
	public static float getDistance(double latitude1, double longitude1,
			double latitude2, double longitude2, int precision) {
		int r = 6371; // km
		double lat = Math.toRadians(latitude2 - latitude1);
		double lng = Math.toRadians(longitude2 - longitude1);
		double a = Math.sin(lat / 2) * Math.sin(lat / 2)
				+ Math.cos(Math.toRadians(latitude1))
				* Math.cos(Math.toRadians(latitude2)) * Math.sin(lng / 2)
				* Math.sin(lng / 2);
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
		double decimalNo = Math.pow(10, precision);
		double distance = r * c;
		distance = Math.round(decimalNo * distance / 1) / decimalNo;
		return (float) distance;
	}

	/**
	 * 2点間を結ぶ方角を取得する(北を0として0～360の数値で方角を表現する)
	 *
	 * @param latitude1
	 * @param longitude1
	 * @param latitude2
	 * @param longitude2
	 * @return
	 */
	public static int getDirection(double latitude1, double longitude1,
			double latitude2, double longitude2) {
		double lat1 = Math.toRadians(latitude1);
		double lat2 = Math.toRadians(latitude2);
		double lng1 = Math.toRadians(longitude1);
		double lng2 = Math.toRadians(longitude2);
		double y = Math.sin(lng2 - lng1) * Math.cos(lat2);
		double x = Math.cos(lat1) * Math.sin(lat2) - Math.sin(lat1)
				* Math.cos(lat2) * Math.cos(lng2 - lng1);
		double deg = Math.toDegrees(Math.atan2(y, x));
		double angle = (deg + 360) % 360;
		return (int) (Math.abs(angle) + (1 / 7200));
	}

	/**
	 * 2点間を結ぶ方角を取得する(北を0として0～360の数値で方角を表現する)
	 *
	 * @param latitude1
	 * @param longitude1
	 * @param latitude2
	 * @param longitude2
	 * @return
	 */
	public static double getDirectionRad(double latitude1, double longitude1,
			double latitude2, double longitude2) {
		return degreesToRads(getDirection(latitude1, longitude1, latitude2, longitude2));
	}

	/**
	 * ラジアンを度に変換する(方位角用)
	 *
	 * @param rad
	 *            ラジアン
	 * @return 度
	 */
	public static int radianToDegreeForAzimuth(float rad) {
		int angle = (int) Math.floor(Math.toDegrees(rad));
		if (angle >= 0)
			return angle;
		return angle + 360;
	}

	/**
	 * ラジアンを度に変換する
	 *
	 * @param rad
	 *            ラジアン
	 * @return 度
	 */
	public static int radianToDegree(float rad) {
		return (int) Math.floor(Math.toDegrees(rad));
	}

	/**
	 * 度からラジアンに変換する
	 * @param degrees 度
	 * @return ラジアン
	 */
	public static double degreesToRads(double degrees) {
		return degrees * (Math.PI / 180f);
	}

	public static double correctAzimuth(int azimuth) {
		return CORRECT_AZIMUTH[azimuth];
	}
}
