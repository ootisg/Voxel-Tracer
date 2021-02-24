package voxels;

import shaders.FragmentShader;
import shaders.LazyShader;
import shaders.RayShader;

public class EmptyVoxel extends Voxel {
	
	public EmptyVoxel (int x, int y, int z) {
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
