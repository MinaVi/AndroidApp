package com.sw.minavi.util;

import javax.vecmath.Vector3f;

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

	/**
	 * 点Aと点Bを結ぶ線分が三角形vertexListに交差しているか判定する
	 * @param a 点A
	 * @param b 点B
	 * @param vertexList 三角形の各頂点
	 * @return 交差判定(true:交差している, false:交差していない)
	 */
	public static boolean intersect(Vector3f a, Vector3f b, Vector3f[] vertexList) {

		Vector3f ray = new Vector3f(b);
		ray.sub(a);
		ray.normalize();

		// 法線の計算
		Vector3f edge1 = new Vector3f(vertexList[1]);
		Vector3f edge2 = new Vector3f(vertexList[2]);
		edge1.sub(vertexList[0]);
		edge2.sub(vertexList[0]);
		edge1.normalize();
		edge2.normalize();

		// 点Pの計算
		Vector3f p = new Vector3f();
		p.cross(ray, edge2);
		p.normalize();

		float det = p.dot(edge1);
		if (det > 0.000001) {

			Vector3f t = new Vector3f();
			t.sub(a, vertexList[0]);
			//t.normalize();

			float u = p.dot(t);

			if ((u >= 0) && (u <= 1 * det)) {

				Vector3f q = new Vector3f();
				q.cross(t, edge1);
				q.normalize();

				float v = q.dot(ray);
				if ((v >= 0) && (u + v <= 1 * det)) {
					return true;
				}
			}
		}
		return false;
	}
}
