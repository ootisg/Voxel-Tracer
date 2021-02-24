package shaders;

import render.Ray;
import voxels.Voxel;
import voxels.VoxelSide;

public class BlackShader implements FragmentShader {

	@Override
	public int shade (Ray ray, Voxel voxel, VoxelSide surface, double x, double y) {
		return 0x000000;
	}

}
