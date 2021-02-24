package voxels;

import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import shaders.FragmentShader;
import shaders.LazyShader;
import shaders.PartialShader;
import shaders.RayShader;
import shaders.TextureShader;
import shaders.Textures;

public class TexturedVoxel extends Voxel {

	private static RayShader rayShader = new LazyShader ();
	private FragmentShader fragShader;
	
	private boolean partial;
	
	public TexturedVoxel (int x, int y, int z, int id) {
		super (x, y, z, id);
		if ((id & 0xFF00) != 0) {
			fragShader = new PartialShader (.5);
		} else {
			fragShader = new TextureShader ();
		}
		setTexture ();
	}

	@Override
	public boolean opaque () {
		return true;
	}

	@Override
	public RayShader getRayShader () {
		return rayShader;
	}

	@Override
	public FragmentShader getFragmentShader () {
		return fragShader;
	}
	
	public void setTexture () {
		((TextureShader)fragShader).setTexture (Textures.getTexture (getTextureId () + ".png"));
	}
	
	public void setFragmentShader (FragmentShader shader) {
		fragShader = shader;
	}
	
	public int getTextureId () {
		return getId () & 0xFF;
	}
}
