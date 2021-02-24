package shaders;

import java.awt.Color;

import render.Ray;
import voxels.Voxel;
import voxels.VoxelSide;

public interface FragmentShader {
	
	/**
	 * Given a surface of the voxel and the coordinates on that surface (relative to the top-left), gives the color to render that pixel
	 * @param voxel The voxel to which this shader is being applied
	 * @param surface The surface of the voxel that is being rendered
	 * @param x The x coordinate of the pixel on the given surface
	 * @param y The y coordinate of the pixel on the given surface
	 * @return The color to render
	 */
	public int shade (Ray ray, Voxel voxel, VoxelSide surface, double x, double y);
	
}
