package render;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

import engine.Timer;
import filters.FragmentFilter;
import map.Map;
import shaders.FragmentShader;
import shaders.LightSource;
import shaders.RayShader;
import shaders.TextureShader;
import voxels.Voxel;
import voxels.VoxelSide;

public class Ray {
	
	private double x;
	private double y;
	private double z;
	
	private double xStep;
	private double yStep;
	private double zStep;
	
	private int xDir;
	private int yDir;
	private int zDir;
	
	private double mxz; //x-z slope
	private double mxy; //x-y slope
	private double bxz; //x-z offset
	private double bxy; //x-y offset
	
	private double mab;
	private double mac;
	private double bacc;
	private double cacc;
	private double ca;
	private double cb;
	private double cc;
	private double sa;
	private double sb;
	private double sc;
	private double ta;
	private double tb;
	private double tc;
	private int va;
	private int vb;
	private int vc;
	private int aDir;
	private int bDir;
	private int cDir;
	private int config;
	private boolean firstStep;
	
	private int maxSteps;
	private int steps;
	private boolean hit;
	
	private int color;
	
	private Voxel voxelHit;
	
	private LinkedList<FragmentFilter> filters;
	boolean hasFilter;
	
	LinkedList<LightSource> lights;
	
	public Ray (double xFrom, double yFrom, double zFrom, double xTo, double yTo, double zTo, int maxSteps) {
		setProperties (xFrom, yFrom, zFrom, xTo, yTo, zTo);
		this.maxSteps = maxSteps;
	}
	
	public void setProperties (double xFrom, double yFrom, double zFrom, double xTo, double yTo, double zTo) {
		
		//Set initial x and y coordinates
		x = xFrom;
		y = yFrom;
		z = zFrom;
		xStep = x;
		yStep = y;
		zStep = z;
		
		//Set direction values
		double xDiff = xFrom - xTo;
		double yDiff = yFrom - yTo;
		double zDiff = zFrom - zTo;
		if (xDiff < 0) {
			xDir = 1;
		} else if (xDiff > 0) {
			xDir = -1;
		} else {
			xDir = 0;
		}
		if (yDiff < 0) {
			yDir = 1;
		} else if (yDiff > 0) {
			yDir = -1;
		} else {
			yDir = 0;
		}
		if (zDiff < 0) {
			zDir = 1;
		} else if (zDiff > 0) {
			zDir = -1;
		} else {
			zDir = 0;
		}
		
		//Set slopes
		if (xDir == 0) {
			//Case for undefined slopes
		} else {
			//Slopes are defined, calculate them
			mxz = zDiff / xDiff;
			mxy = yDiff / xDiff;
			//Calculate the intercepts too
			bxz = z - mxz * x;
			bxy = y - mxy * x;
		}
	}
	
	public void setFastProperties (double xFrom, double yFrom, double zFrom, double xTo, double yTo, double zTo) {
		
		//Get diff values
		double xDiff = Math.abs (xFrom - xTo);
		double yDiff = Math.abs (yFrom - yTo);
		double zDiff = Math.abs (zFrom - zTo);
		
		//Find biggest coord
		if (xDiff >= yDiff && xDiff >= zDiff) {
			config = 1;
			ca = xFrom;
			ta = xTo;
			cb = yFrom;
			tb = yTo;
			cc = zFrom;
			tc = zTo;
		} else if (yDiff >= xDiff && yDiff >= zDiff) {
			config = 2;
			ca = yFrom;
			ta = yTo;
			cb = xFrom;
			tb = xTo;
			cc = zFrom;
			tc = zTo;
		} else {
			config = 3;
			ca = zFrom;
			ta = zTo;
			cb = xFrom;
			tb = xTo;
			cc = zFrom;
			tc = zTo;
		}
		
		//Store starting values
		sa = ca;
		sb = cb;
		sc = cc;
		
		//Find differences
		double aDiff = ca - ta;
		double bDiff = cb - tb;
		double cDiff = cc - tc;
		
		//Find directions
		if (aDiff < 0) {
			aDir = 1;
		} else {
			aDir = -1;
		}
		if (bDiff < 0) {
			bDir = 1;
		} else {
			bDir = -1;
		}
		if (cDiff < 0) {
			cDir = 1;
		} else {
			cDir = -1;
		}
		
		//Calculate slopes
		double invAM = 1 / (ca - ta);
		mab = (cb - tb) * invAM;
		mac = (cc - tc) * invAM;
		
		//Specify first step
		firstStep = true;
		
	}
	
	public int collide (Map map) {
		steps = 0;
		while (!hit) {
			step (map);
		}
		return color;
	}
	
	public Voxel step (Map map) {
		
		//Escape for "doesn't hit anything" case
		if (steps >= maxSteps) {
			hit = true;
			return null;
		}
		//Compute possible steps
		double xTryX = jumpToNext (xStep, xDir);
		double xTryY = getYFromX (xTryX);
		double xTryZ = getZFromX (xTryX);
		double xDist = getSquaredDistance (xStep, yStep, zStep, xTryX, xTryY, xTryZ);
		
		double yTryY = jumpToNext (yStep, yDir);
		double yTryX = getXFromY (yTryY);
		double yTryZ = getZFromX (yTryX);
		double yDist = getSquaredDistance (xStep, yStep, zStep, yTryX, yTryY, yTryZ);
		
		double zTryZ = jumpToNext (zStep, zDir);
		double zTryX = getXFromZ (zTryZ);
		double zTryY = getYFromX (zTryX);
		double zDist = getSquaredDistance (xStep, yStep, zStep, zTryX, zTryY, zTryZ);
		
		//Find the shortest of the 3 steps and update the current step to those values
		int stepUsed = 0;
		if (xDist <= yDist && xDist <= zDist) {
			xStep = xTryX;
			yStep = xTryY;
			zStep = xTryZ;
			stepUsed = 0;
		} else if (yDist <= xDist && yDist <= zDist) {
			xStep = yTryX;
			yStep = yTryY;
			zStep = yTryZ;
			stepUsed = 1;
		} else if (zDist <= xDist && zDist <= yDist) {
			xStep = zTryX;
			yStep = zTryY;
			zStep = zTryZ;
			stepUsed = 2;
		}
		//Timer.start ();
		//Check for collision at the current step
		Voxel colliding = null;
		VoxelSide collisionSide = VoxelSide.SIDE_TOP;
		double collisionX = 0;
		double collisionY = 0;
		try {
			int voxelX = (int)xStep;
			int voxelY = (int)yStep;
			int voxelZ = (int)zStep;
			switch (stepUsed) {
				case 0:
					//Check in the x direction
					if (xDir == -1) {
						voxelX --;
						collisionSide = VoxelSide.SIDE_NORTH;
					} else if (xDir == 1) {
						collisionSide = VoxelSide.SIDE_SOUTH;
					}
					collisionX = zStep % 1.0;
					collisionY = yStep % 1.0;
					break;
				case 1:
					//Check in the y direction
					if (yDir == -1) {
						voxelY --;
						collisionSide = VoxelSide.SIDE_TOP;
					} else if (xDir == 1) {
						collisionSide = VoxelSide.SIDE_BOTTOM;
					}
					collisionX = xStep % 1.0;
					collisionY = zStep % 1.0;
					break;
				case 2:
					//Check in the z direction
					if (zDir == -1) {
						voxelZ --;
						collisionSide = VoxelSide.SIDE_EAST;
					} else if (zDir == 1) {
						collisionSide = VoxelSide.SIDE_WEST;
					}
					collisionX = xStep % 1.0;
					collisionY = yStep % 1.0;
					break;
			}
			colliding = map.getVoxel (voxelX, voxelY, voxelZ);
		} catch (Exception e) {
			hit = true;
			Timer.lap ();
			return null;
		}
		//Apply collision and shaders
		if (colliding.opaque ()) {
			//System.out.println (colliding.getX () + ", " + colliding.getY () + ", " + colliding.getZ ());
			hit = true;
		}
		RayShader rayShader = colliding.getRayShader ();
		FragmentShader fragShader = colliding.getFragmentShader ();
		if (rayShader != null) {
			rayShader.shade (this);
		}
		if (fragShader != null) {
			((TextureShader)fragShader).useLights (lights);
			color = fragShader.shade (this, colliding, collisionSide, collisionX, collisionY);
			if (hasFilter) {
				Iterator<FragmentFilter> iter = filters.iterator ();
				while (iter.hasNext ()) {
					color = iter.next ().filter (color);
				}
			}
		}
		steps ++;
		//Timer.lap ();
		return colliding;
	}
	
	public Voxel stepFast (Map map) {
		if (steps >= maxSteps) {
			hit = true;
			return null;
		}
		Voxel working = null;
		if (firstStep) {
			va = (int)ca;
			vb = (int)cb;
			vc = (int)cc;
			double adist;
			double bdist;
			double cdist;
			while (true) {
				if (aDir == 1) {
					adist = 1 - (ca - (int)ca);
				} else {
					adist = ca - (int)ca;
				}
				if (bDir == 1) {
					bdist = 1 - (cb - (int)cb);
				} else {
					bdist = cb - (int)cb;
					if (bdist == 0) {
						bdist = 1;
					}
				}
				if (cDir == 2) {
					cdist = 1 - (cc - (int)cc);
				} else {
					cdist = cc - (int)cc;
					if (cdist == 0) {
						cdist = 1;
					}
				}
				bdist = Math.abs (bdist / mab);
				cdist = Math.abs (cdist / mac);
				if (adist <= bdist && adist <= cdist) {
					if (aDir == 1) {
						ca = Math.ceil (ca);
					} else {
						ca = Math.floor (ca);
					}
					cb = mab * (ca - sa) - sb;
					cc = mac * (ca - sa) - sc;
					bacc = cb - (int)cb;
					cacc = cc - (int)cc;
					working = getVoxelFromMap (map, va, vb, vc);
					if (working.opaque ()) {
						hit = true;
						applyShaders (working, 1);
					}
					firstStep = false;
					return working;
				} else if (bdist >= adist && bdist >= cdist) {
					if (bDir == 1) {
						cb = Math.ceil (cb);
					} else {
						cb = Math.floor (cb);
					}
					ca = (cb - sb) / mab + sa;
					cc = mac * (ca - sa) - sc;
					vb += bDir;
					working = getVoxelFromMap (map, va, vb, vc);
					if (working.opaque ()) {
						hit = true;
						applyShaders (working, 2);
						return working;
					}
				} else {
					if (cDir == 1) {
						cc = Math.ceil (cc);
					} else {
						cc = Math.floor (cc);
					}
					ca = (cc - sc) / mac + sa;
					cb = mab * (ca - sa) - sb;
					vc += cDir;
					working = getVoxelFromMap (map, va, vb, vc);
					if (working.opaque ()) {
						hit = true;
						applyShaders (working, 3);
						return working;
					}
				}
			}
		} else {
			if (bacc >= 1) {
				bacc -= 1;
			}
			if (cacc >= 1) {
				cacc -= 1;
			}
			bacc += mab;
			cacc += mac;
			if (bacc >= 1) {
				if (cacc >= 1) {
					//b and c overflowed
					double bdist = (1 - (bacc - 1)) / mab;
					double cdist = (1 - (cacc - 1)) / mac;
					if (bdist <= cdist) {
						vb += bDir;
						working = getVoxelFromMap (map, va, vb, vc);
						if (working.opaque ()) {
							hit = true;
							applyShaders (working, 2);
							return working;
						}
						vc += cDir;
						working = getVoxelFromMap (map, va, vb, vc);
						if (working.opaque ()) {
							hit = true;
							applyShaders (working, 3);
							return working;
						}
						va += aDir;
						working = getVoxelFromMap (map, va, vb, vc);
						if (working.opaque ()) {
							hit = true;
							applyShaders (working, 3);
							return working;
						}
					} else {
						vc += cDir;
						working = getVoxelFromMap (map, va, vb, vc);
						if (working.opaque ()) {
							hit = true;
							applyShaders (working, 3);
							return working;
						}
						vb += bDir;
						working = getVoxelFromMap (map, va, vb, vc);
						if (working.opaque ()) {
							hit = true;
							applyShaders (working, 2);
							return working;
						}
						va += aDir;
						working = getVoxelFromMap (map, va, vb, vc);
						if (working.opaque ()) {
							hit = true;
							applyShaders (working, 1);
							return working;
						}
					}
				} else {
					//b overflowed
					vb += bDir;
					working = getVoxelFromMap (map, va, vb, vc);
					if (working.opaque ()) {
						hit = true;
						applyShaders (working, 2);
						return working;
					}
					va += aDir;
					working = getVoxelFromMap (map, va, vb, vc);
					if (working.opaque ()) {
						hit = true;
						applyShaders (working, 1);
						return working;
					}
				}
			} else if (cacc >= 1) {
				//c overflowed
				vc += cDir;
				working = getVoxelFromMap (map, va, vb, vc);
				if (working.opaque ()) {
					hit = true;
					applyShaders (working, 3);
					return working;
				}
				va += aDir;
				working = getVoxelFromMap (map, va, vb, vc);
				if (working.opaque ()) {
					hit = true;
					applyShaders (working, 1);
					return working;
				}
			} else {
				//No overflow
				va += aDir;
				working = getVoxelFromMap (map, va, vb, vc);
				if (working.opaque ()) {
					hit = true;
					applyShaders (working, 1);
					return working;
				}
			}
			if (working == map.EDGE_VOXEL) {
				hit = true;
				return working;
			}
		}
		steps ++;
		return working;
	}
	
	private void applyShaders (Voxel voxel, int face) {
		if (face == 1) {
			ca = va + aDir;
			cb = mab * (ca - sa) + sb;
			cc = mac * (ca - sa) + sc;
		} else if (face == 2) {
			cb = vb + bDir;
			ca = (cb - sb) / mab + sa;
			cc = mac * (ca - sa) + sc;
			//System.out.println (ca + ", " + cb + ", " + cc + ", " + va + ", " + vb + ", " + vc);
		} else {
			cc = vc + cDir;
			ca = (cc - sc) / mac + sa;
			cb = mab * (ca - sa) + sb;
		}
		if (config == 1) {
			xStep = ca;
			yStep = cb;
			zStep = cc;
		} else if (config == 2) {
			xStep = cb;
			yStep = ca;
			zStep = cc;
		} else if (config == 3) {
			xStep = cb;
			yStep = cc;
			zStep = ca;
		}
		color = voxel.getFragmentShader ().shade (this, voxel, VoxelSide.SIDE_TOP, xStep - (int)xStep, zStep - (int)zStep);
	}
	
	private Voxel getVoxelFromMap (Map map, int a, int b, int c) {
		int ux;
		int uy;
		int uz;
		if (config == 1) {
			ux = a;
			uy = b;
			uz = c;
		} else if (config == 2) {
			ux = b;
			uy = a;
			uz = c;
		} else {
			ux = b;
			uy = c;
			uz = a;
		}
		if (ux < 0 || ux >= map.getWidth () || uy < 0 || uy >= map.getHeight () || uz < 0 || uz >= map.getDepth ()) {
			return Map.EDGE_VOXEL;
		} else {
			return map.getVoxel (ux, uy, uz);
		}
	}
	
	public void continuePhysics () {
		hit = false;
	}
	
	public boolean hitVoxel () {
		return hit;
	}
	
	public double getXFromY (double y) {
		if (xStep == 0) {
			return x;
		} else {
			return (y - bxy) / mxy; 
		}
	}
	
	public double getXFromZ (double z) {
		if (xStep == 0) {
			return x;
		} else {
			return (z - bxz) / mxz;
		}
	}
	
	public double getYFromX (double x) {
		if (yStep == 0) {
			return y;
		} else {
			return mxy * x + bxy;
		}
	}
	
	public double getZFromX (double x) {
		if (zStep == 0) {
			return z;
		} else {
			return mxz * x + bxz;
		}
	}
	
	public double getSquaredDistance (double x1, double y1, double z1, double x2, double y2, double z2) {
		return (x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2) + (z1 - z2) * (z1 - z2);
	}
	
	private double jumpToNext (double value, int direction) {
		if (value % 1.0 == 0) {
			value += direction;
		} else {
			if (direction < 0) {
				value = Math.floor (value);
			} else if (direction > 0) {
				value = Math.ceil (value);
			}
		}
		return value;
	}
	
	public void addFilter (FragmentFilter filter) {
		if (!hasFilter) {
			filters = new LinkedList<FragmentFilter> ();
		}
		filters.add (filter);
		hasFilter = true;
	}
	
	public void setStepPoint (Vector3 point) {
		xStep = point.x;
		yStep = point.y;
		zStep = point.z;
	}
	
	public Vector3 getStepPoint () {
		return new Vector3 (xStep, yStep, zStep);
	}
	
	public void useLights (LinkedList<LightSource> lights) {
		this.lights = lights;
	}
	
}
