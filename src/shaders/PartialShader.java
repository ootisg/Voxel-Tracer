package shaders;

import filters.Tint;
import render.Vector3;
import render.Ray;
import voxels.Voxel;
import voxels.VoxelSide;

public class PartialShader extends TextureShader {
	
	double renderHeight;
	
	public PartialShader (double height) {
		renderHeight = height;
	}
	
	@Override
	public int shade (Ray ray, Voxel voxel, VoxelSide surface, double x, double y) {
		if (surface.equals (VoxelSide.SIDE_NORTH) || surface.equals (VoxelSide.SIDE_EAST) || surface.equals (VoxelSide.SIDE_WEST) || surface.equals (VoxelSide.SIDE_SOUTH) || surface.equals (VoxelSide.SIDE_TOP)) {
			if (y < (1 - renderHeight) || surface.equals (VoxelSide.SIDE_TOP)) {
				double planeY = voxel.getY () + (1 - renderHeight);
				double newX = ray.getXFromY (planeY);
				double newZ = ray.getZFromX (newX);
				newX -= voxel.getX ();
				newZ -= voxel.getZ ();
				if (newX >= 0 && newX <= 1 && newZ >= 0 && newZ <= 1) {
					ray.setStepPoint (new Vector3 (newX, planeY, newZ));
					return super.shade (ray, voxel, VoxelSide.SIDE_TOP, newX, newZ);
				} else {
					ray.continuePhysics ();
					return 0;
				}
			}
		}
		return super.shade (ray, voxel, surface, x, y);
	}
}
