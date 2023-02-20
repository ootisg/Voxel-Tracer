package render;

public class Matrix4 {

	double[][] data;
	
	public Matrix4 () {
		data = new double[][] {
			{1, 0, 0, 0},
			{0, 1, 0, 0},
			{0, 0, 1, 0},
			{0, 0, 0, 1}
		};
	}
	
	public Matrix4 (double a1, double b1, double c1, double d1, double a2, double b2, double c2, double d2, double a3, double b3, double c3, double d3, double a4, double b4, double c4, double d4) {
		this ();
		data[0][0] = a1;
		data[0][1] = b1;
		data[0][2] = c1;
		data[0][3] = d1;
		data[1][0] = a2;
		data[1][1] = b2;
		data[1][2] = c2;
		data[1][3] = d2;
		data[2][0] = a3;
		data[2][1] = b3;
		data[2][2] = c3;
		data[2][3] = d3;
		data[3][0] = a4;
		data[3][1] = b4;
		data[3][2] = c4;
		data[3][3] = d4;
	}
	
	public static Matrix4 product (Matrix4 a, Matrix4 b) {
		Matrix4 prod = new Matrix4 ();
		prod.data[0][0] = a.data[0][0] * b.data[0][0] + a.data[0][1] * b.data[1][0] + a.data[0][2] * b.data[2][0] + a.data[0][3] * b.data[3][0];
		prod.data[0][1] = a.data[0][0] * b.data[0][1] + a.data[0][1] * b.data[1][1] + a.data[0][2] * b.data[2][1] + a.data[0][3] * b.data[3][1];
		prod.data[0][2] = a.data[0][0] * b.data[0][2] + a.data[0][1] * b.data[1][2] + a.data[0][2] * b.data[2][2] + a.data[0][3] * b.data[3][2];
		prod.data[0][3] = a.data[0][0] * b.data[0][3] + a.data[0][1] * b.data[1][3] + a.data[0][2] * b.data[2][3] + a.data[0][3] * b.data[3][3];
		prod.data[1][0] = a.data[1][0] * b.data[0][0] + a.data[1][1] * b.data[1][0] + a.data[1][2] * b.data[2][0] + a.data[1][3] * b.data[3][0];
		prod.data[1][1] = a.data[1][0] * b.data[0][1] + a.data[1][1] * b.data[1][1] + a.data[1][2] * b.data[2][1] + a.data[1][3] * b.data[3][1];
		prod.data[1][2] = a.data[1][0] * b.data[0][2] + a.data[1][1] * b.data[1][2] + a.data[1][2] * b.data[2][2] + a.data[1][3] * b.data[3][2];
		prod.data[1][3] = a.data[1][0] * b.data[0][3] + a.data[1][1] * b.data[1][3] + a.data[1][2] * b.data[2][3] + a.data[1][3] * b.data[3][3];
		prod.data[2][0] = a.data[2][0] * b.data[0][0] + a.data[2][1] * b.data[1][0] + a.data[2][2] * b.data[2][0] + a.data[2][3] * b.data[3][0];
		prod.data[2][1] = a.data[2][0] * b.data[0][1] + a.data[2][1] * b.data[1][1] + a.data[2][2] * b.data[2][1] + a.data[2][3] * b.data[3][1];
		prod.data[2][2] = a.data[2][0] * b.data[0][2] + a.data[2][1] * b.data[1][2] + a.data[2][2] * b.data[2][2] + a.data[2][3] * b.data[3][2];
		prod.data[2][3] = a.data[2][0] * b.data[0][3] + a.data[2][1] * b.data[1][3] + a.data[2][2] * b.data[2][3] + a.data[2][3] * b.data[3][3];
		prod.data[3][0] = a.data[3][0] * b.data[0][0] + a.data[3][1] * b.data[1][0] + a.data[3][2] * b.data[2][0] + a.data[3][3] * b.data[3][0];
		prod.data[3][1] = a.data[3][0] * b.data[0][1] + a.data[3][1] * b.data[1][1] + a.data[3][2] * b.data[2][1] + a.data[3][3] * b.data[3][1];
		prod.data[3][2] = a.data[3][0] * b.data[0][2] + a.data[3][1] * b.data[1][2] + a.data[3][2] * b.data[2][2] + a.data[3][3] * b.data[3][2];
		prod.data[3][3] = a.data[3][0] * b.data[0][3] + a.data[3][1] * b.data[1][3] + a.data[3][2] * b.data[2][3] + a.data[3][3] * b.data[3][3];
		return prod;
	}
	
	public static Vector3 product (Matrix4 mat, Vector3 v) {
		Vector3 res = new Vector3 (0, 0, 0);
		res.x = mat.data[0][0] * v.x + mat.data[0][1] * v.y + mat.data[0][2] * v.z + mat.data[0][3];
		res.y = mat.data[1][0] * v.x + mat.data[1][1] * v.y + mat.data[1][2] * v.z + mat.data[1][3];
		res.z = mat.data[2][0] * v.x + mat.data[2][1] * v.y + mat.data[2][2] * v.z + mat.data[2][3];
		return res;
	}
	
	public void multiply (Matrix4 x) {
		Matrix4 prod = product (this, x);
		data[0][0] = prod.data[0][0];
		data[0][1] = prod.data[0][1];
		data[0][2] = prod.data[0][2];
		data[0][3] = prod.data[0][3];
		data[1][0] = prod.data[1][0];
		data[1][1] = prod.data[1][1];
		data[1][2] = prod.data[1][2];
		data[1][3] = prod.data[1][3];
		data[2][0] = prod.data[2][0];
		data[2][1] = prod.data[2][1];
		data[2][2] = prod.data[2][2];
		data[2][3] = prod.data[2][3];
		data[3][0] = prod.data[3][0];
		data[3][1] = prod.data[3][1];
		data[3][2] = prod.data[3][2];
		data[3][3] = prod.data[3][3];
	}
	
	public static Matrix4 rotX (double theta) {
		return new Matrix4 (
			1, 0, 0, 0,
			0, Math.cos (theta), -Math.sin (theta), 0,
			0, Math.sin (theta), Math.cos (theta), 0,
			0, 0, 0, 1
			);
	}
	
	public static Matrix4 rotY (double theta) {
		return new Matrix4 (
			Math.cos (theta), 0, Math.sin (theta), 0,
			0, 1, 0, 0,
			-Math.sin (theta), 0, Math.cos (theta), 0,
			0, 0, 0, 1
		);
	}
	
	public static Matrix4 rotZ (double theta) {
		return new Matrix4 (
			Math.cos (theta), -Math.sin (theta), 0, 0,
			Math.sin (theta), Math.cos (theta), 0, 0,
			0, 0, 1, 0,
			0, 0, 0, 1
		);
	}
	
}
