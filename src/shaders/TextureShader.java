package shaders;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.util.Iterator;
import java.util.LinkedList;

import filters.Tint;
import render.Vector3;
import render.Ray;
import voxels.Voxel;
import voxels.VoxelSide;

public class TextureShader implements FragmentShader {

	private BufferedImage texture;
	private Raster raster;
	private WritableRaster alphaRaster;
	
	private boolean hasTint;
	
	private boolean hasAlpha;
	
	private LinkedList<LightSource> lights;
	
	@Override
	public int shade (Ray ray, Voxel voxel, VoxelSide surface, double x, double y) {
		hasTint = false;
		//return voxel.getId ();
		/*int baseColor = voxel.getId ();
		double darkness = 1.0 - Math.sqrt (x * x + y * y) / Math.sqrt (2);
		int baseRed = (baseColor & 0xFF0000) >> 16;
		int baseGreen = (baseColor & 0xFF00) >> 8;
		int baseBlue = (baseColor & 0xFF);
		baseColor = (((int)(baseRed * darkness)) << 16) + (((int)(baseGreen * darkness)) << 8) + ((int)(baseBlue * darkness));
		return baseColor;*/
		/*switch (surface) {
			case SIDE_TOP:
				return 0xFF0000;
			case SIDE_BOTTOM:
				return 0x0000FF;
			case SIDE_NORTH:
				return 0x00FF00;
			case SIDE_SOUTH:
				return 0xFF00FF;
			case SIDE_EAST:
				return 0x00FFFF;
			case SIDE_WEST:
				return 0xFFFF00;
			default:
				return 0xFFFFFF;
		}*/
		//return (((int)(0xFF * x)) << 16) + (int)(0xFF * y);
		double lum = .2;
		if (raster != null) {
			int red = raster.getSample ((int)(x * raster.getWidth ()), (int)(y * raster.getHeight ()), 0);
			int green = raster.getSample ((int)(x * raster.getWidth ()), (int)(y * raster.getHeight ()), 1);
			int blue = raster.getSample ((int)(x * raster.getWidth ()), (int)(y * raster.getHeight ()), 2);
			if (hasAlpha) {
				int alpha = alphaRaster.getSample ((int)(x * alphaRaster.getWidth ()), (int)(y * alphaRaster.getHeight ()), 0);
				if (alpha != 255) {
					ray.continuePhysics ();
					if (alpha != 0 && !hasTint) {
						int c = (red << 16) | (green << 8) | blue;
						ray.addFilter (new Tint (c, alpha));
						hasTint = true;
					}
				}
			}
			if (lights != null) {
				Iterator<LightSource> iter = lights.iterator ();
				while (iter.hasNext ()) {
					LightSource src = iter.next ();
					double baseLum = src.getLuminance (ray.getStepPoint ());
					if (baseLum != 0) {
						lum += (1 - baseLum) / 10 * 8;
					}
					int c = (red << 16) | (green << 8) | blue;
				}
			}
			return darken ((red << 16) | (green << 8) | blue, lum);
		} else {
			return 0xFF00FF;
		}
	}
	
	public void setTexture (BufferedImage img) {
		texture = img;
		raster = img.getData ();
		alphaRaster = img.getAlphaRaster ();
		if (alphaRaster == null) {
			hasAlpha = false;
		} else {
			hasAlpha = true;
		}
	}
	
	protected Raster getRaster () {
		return raster;
	}
	
	protected Raster getAlphaRaster () {
		return alphaRaster;
	}
	
	protected boolean hasAlpha () {
		return hasAlpha;
	}
	
	protected boolean hasTint () {
		return hasTint;
	}
	
	protected void setTint (boolean tint) {
		hasTint = tint;
	}
	
	protected LinkedList<LightSource> getLights () {
		return lights;
	}
	
	public void useLights (LinkedList<LightSource> lights) {
		this.lights = lights;
	}
	
	private int darken (int color, double amt) {
		int red = (int)(((color & 0xFF0000) >> 16) * amt);
		int green = (int)(((color & 0x00FF00) >> 8) * amt);
		int blue = (int)((color & 0xFF) * amt);
		return (red << 16) | (green << 8) | blue;
	}
}
