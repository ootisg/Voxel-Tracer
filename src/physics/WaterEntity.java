package physics;

import engine.GameObject;
import map.Map;
import shaders.PartialShader;
import voxels.EmptyVoxel;
import voxels.TexturedVoxel;
import voxels.Voxel;

public class WaterEntity extends GameObject {
	
	private Voxel voxel;
	private Map map;
	private int time;
	private int flow;
	
	public static final int FLOW_TIME = 20;
	public static final int FLOW_DIST = 7;
	
	public WaterEntity (Voxel voxel, Map map, int flow) {
		this.voxel = voxel;
		this.map = map;
		time = FLOW_TIME;
		this.flow = flow;
	}
	
	public boolean spread (int x, int y, int z, boolean spreadDown) {
		try {
			Voxel toSpread = map.getVoxel (x, y, z);
			Voxel under = map.getVoxel (x, y + 1, z);
			if (toSpread instanceof EmptyVoxel && ((spreadDown) || (!(under instanceof EmptyVoxel || under.getId () == 7)))) {
				TexturedVoxel newWater = new TexturedVoxel (x, y, z, 7);
				double h = (1.0 / (FLOW_DIST + 1)) * (FLOW_DIST - flow);
				newWater.setFragmentShader (new PartialShader (h));
				newWater.setTexture ();
				map.putVoxel (newWater);
				if (spreadDown) {
					new WaterEntity (newWater, map, 0).declare ();
				} else {
					new WaterEntity (newWater, map, flow + 1).declare ();
				}
				return true;
			}
		} catch (ArrayIndexOutOfBoundsException e) {
			return false;
		}
		return false;
	}
	
	@Override
	public void frameEvent () {
		if (flow < FLOW_DIST) {
			if (time <= 0) {
				int sx = voxel.getX ();
				int sy = voxel.getY ();
				int sz = voxel.getZ ();
				spread (sx + 1, sy, sz, false);
				spread (sx - 1, sy, sz, false);
				spread (sx, sy, sz + 1, false);
				spread (sx, sy, sz - 1, false);
				spread (sx, sy + 1, sz, true);
			}
			time --;
		}
	}
}
