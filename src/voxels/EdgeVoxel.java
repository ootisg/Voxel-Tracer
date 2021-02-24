package voxels;

import shaders.FragmentShader;
import shaders.RayShader;

public class EdgeVoxel extends Voxel {
	
	public EdgeVoxel (int x, int y, int z) {
		super (x, y, z, 0);
	}

	@Override
	public boolean opaque () {
		return false;
	}

	@Override
	public RayShader getRayShader () {
		return null;
	}

	@Override
	public FragmentShader getFragmentShader () {
		return null;
	}
}
