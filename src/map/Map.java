package map;

import voxels.EdgeVoxel;
import voxels.EmptyVoxel;
import voxels.TexturedVoxel;
import voxels.Voxel;

public class Map {
	
	private VoxelMatrix voxels;
	
	public static final EdgeVoxel EDGE_VOXEL = new EdgeVoxel (0, 0, 0);
	
	public Map (int width, int depth, int height) {
		voxels = new VoxelMatrix (width, depth, height);
	}
	
	public Voxel getVoxel (int x, int y, int z) {
		return voxels.getVoxel (x, y, z);
	}
	
	public void putVoxel (Voxel voxel) {
		voxels.putVoxel (voxel);
	}
	
	public int getWidth () {
		return voxels.width;
	}
	
	public int getDepth () {
		return voxels.depth;
	}
	
	public int getHeight () {
		return voxels.height;
	}
	
	private class VoxelMatrix {
		
		private int width;
		private int depth;
		private int height;
		private Voxel[][][] voxels;
		
		public VoxelMatrix (int width, int depth, int height) {
			this.width = width;
			this.depth = depth;
			this.height = height;
			voxels = new Voxel[height][width][depth];
			for (int wy = 0; wy < height; wy ++) {
				for (int wx = 0; wx < width; wx ++) {
					for (int wz = 0; wz < depth; wz ++) {
						voxels [wy][wx][wz] = new EmptyVoxel (wx, wy, wz);
					}
				}
			}
			/*voxels [10][24][50] = new TexturedVoxel (24, 10, 50, 255);
			voxels [10][16][50] = new TexturedVoxel (16, 10, 50, 255);
			voxels [10][20][54] = new TexturedVoxel (20, 10, 54, 255);
			voxels [10][20][46] = new TexturedVoxel (20, 10, 46, 255);
			voxels [8][23][48] = new TexturedVoxel (23, 8, 48, 255);*/
		}
		
		public Voxel getVoxel (int x, int y, int z) {
			return voxels [y][x][z];
		}
		
		public void putVoxel (Voxel voxel) {
			voxels [voxel.getY ()][voxel.getX ()][voxel.getZ ()] = voxel;
		}
	}
}