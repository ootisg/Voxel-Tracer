package shaders;

import render.Camera;
import render.Vector3;
import render.Ray;

public class LightSource {
	
	private Vector3 location;
	private double strength;
	
	double offs = 0;
	double maxo = .1;
	double mino = -2;
	double speed = .0000004;
	int direction = -1;
	
	public LightSource (Vector3 location, double strength) {
		this.location = location;
		this.strength = strength;
	}
	
	public Vector3 getLocation () {
		return location;
	}
	
	public double getStrength () {
		return strength;
	}
	
	public double getLuminance (Vector3 point) {
		double tolerance = .005;
		double dist = getDist (location, point);
		Ray r = new Ray (location.x, location.y, location.z, point.x, point.y, point.z, (int)dist * 2);
		while (true) {
			r.step (Camera.map);
			Vector3 sp = r.getStepPoint ();
			if (Math.abs (sp.x - point.x) < tolerance && Math.abs (sp.y - point.y) < tolerance && Math.abs (sp.z - point.z) < tolerance) {
				break;
			}
			if (r.hitVoxel ()) {
				/*r.step (Camera.map);
				sp = r.getStepPoint ();
				if (!betweenPts (sp, location, point)) {
					break;
				}*/
				//System.out.println (sp.x + ", " + point.x + "; " + sp.y + ", " + point.y + "; " + sp.z + ", " + point.z);
				return 0.0;
			}
		}
		if (dist < strength) {
			return dist / strength;
		}
		return 0.0;
	}
	
	private double getDist (Vector3 p1, Vector3 p2) {
		double dx = p1.x - p2.x;
		double dy = p1.y - p2.y;
		double dz = p1.z - p2.z;
		return Math.sqrt (dx * dx + dy * dy + dz * dz);
	}
	
	private boolean betweenPts (Vector3 p1, Vector3 p2, Vector3 p3) {
		double lx = Math.min (p2.x, p3.x);
		double ly = Math.min (p2.y, p3.y);
		double lz = Math.min (p2.z, p3.z);
		double hx = Math.max (p2.x, p3.x);
		double hy = Math.max (p2.y, p3.y);
		double hz = Math.max (p2.z, p3.z);
		if (p1.x >= lx && p1.x <= hx && p1.y >= ly && p1.y <= hy && p1.z >= lz && p1.z <= hz) {
			return true;
		} else {
			return false;
		}
	}
	
	public void frameEvent () {
		System.out.println (offs);
		offs += speed * direction;
		if (offs > maxo || offs < mino) {
			if (direction == 1) {
				direction = -1;
			} else {
				direction = 1;
			}
			offs += speed * direction;
		}
		location = new Vector3 (location.x, location.y + offs, location.z);
	}
}
