package shaders;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import javax.imageio.ImageIO;

public class Textures {
	
	public static HashMap<String,BufferedImage> textures = new HashMap<String,BufferedImage> ();
	
	public static void loadTextures () {
		File folder = new File ("textures");
		File[] files = folder.listFiles ();
		for (int i = 0; i < files.length; i ++) {
			String name = files [i].getName ();
			try {
				BufferedImage tex = ImageIO.read (files [i]);
				textures.put (name, tex);
			} catch (IOException e) {
				
			}
		}
	}
	
	public static BufferedImage getTexture (String name) {
		BufferedImage tex = textures.get (name);
		if (tex != null) {
			return textures.get (name);
		} else {
			return textures.get ("missing.png");
		}
	}
}
