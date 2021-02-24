package voxels;

import java.awt.image.BufferedImage;

import shaders.FragmentShader;
import shaders.RayShader;

public abstract class Voxel {
	
	private int x;
	private int y;
	private int z;
	
	private int id;
	
	protected Voxel (int x, int y, int z, int id) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.id = id;
	}
	
	public int getX () {
		return x;
	}
	
	public int getY () {
		return y;
	}
	
	public int getZ () {
		return z;
	}
	
	public int getId () {
		return id;
	}
	
	public abstract boolean opaque ();
	public abstract RayShader getRayShader ();
	public abstract FragmentShader getFragmentShader ();
}
