package shaders;

import render.Ray;
import voxels.Voxel;
import voxels.VoxelSide;

public class TargetedShader extends TextureShader {

	private FragmentShader filtered;
	public static final double SHADE_AMOUNT = .5;
	
	public TargetedShader (FragmentShader fragmentShader) {
		filtered = fragmentShader;
	}
	
	@Override
	public int shade (Ray ray, Voxel voxel, VoxelSide surface, double x, double y) {
		int color = filtered.shade (ray, voxel, surface, x, y);
		int red = (int)(((color & 0xFF0000) >> 16) * SHADE_AMOUNT);
		int green = (int)(((color & 0x00FF00) >> 8) * SHADE_AMOUNT);
		int blue = (int)(((color) & 0xFF) * SHADE_AMOUNT);
		int newColor = (red << 16) | (green << 8) | blue;
		return newColor;
	}
	
	public FragmentShader getFilteredShader () {
		return filtered;
	}
	
}
