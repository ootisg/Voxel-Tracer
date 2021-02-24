package filters;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

public class Tint implements FragmentFilter {
	
	Color tint;
	BufferedImage mixImage = new BufferedImage (1, 1, BufferedImage.TYPE_INT_ARGB);
	Graphics g = mixImage.getGraphics ();
	
	public Tint (int color, int strength) {
		int red = (color & 0xFF0000) >> 16;
		int green = (color & 0x00FF00) >> 8;
		int blue = color & 0xFF;
		tint = new Color (red, green, blue, strength);
	}
	
	@Override
	public int filter (int color) {
		Color used = new Color (color);
		g.setColor (used);
		g.fillRect (0, 0, 1, 1);
		g.setColor (tint);
		g.fillRect (0, 0, 1, 1);
		return mixImage.getRGB (0, 0);
	}
	
}
