package com.sw.minavi.util;

public class GLUtils {

	public static float[] getRotatePos(float[] org, float[] midPoint, int degree) {
		return rotateY(org, midPoint, LocationUtilities.degreesToRads(degree));
	}

	private static float[] rotateY(float[] org, float[] midPoint, double radian) {
		float x = (float) ((org[0] - midPoint[0]) * Math.cos(radian)
				- (org[2] - midPoint[2]) * Math.sin(radian) + midPoint[0]);
		float y = org[1];
		float z = (float) ((org[0] - midPoint[0]) * Math.sin(radian)
				+ (org[2] - midPoint[2]) * Math.cos(radian) + midPoint[2]);
		return new float[] { x, y, z };
	}
}
