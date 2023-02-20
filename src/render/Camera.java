package render;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.WritableRaster;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Vector;

import engine.GameObject;
import engine.RenderLoop;
import engine.Timer;
import map.Map;
import physics.WaterEntity;
import shaders.LightSource;
import shaders.TargetedShader;
import voxels.EmptyVoxel;
import voxels.TexturedVoxel;
import voxels.Voxel;

public class Camera extends GameObject {
	
	private double x;
	private double y;
	private double z;
	
	private double hAng;
	private double vAng;
	
	private double fov;
	
	private double clipNear;
	private double clipFar;
	
	private int horizPixels;
	private int vertPixels;
	
	private int maxSteps = 50;
	
	Vector3[] rayPlane;
	int[] renderData;
	
	int usedId = 1;
	int numVoxels = 8;
	
	public static Map map;
	private Voxel targetedVoxel;
	
	private BufferedImage renderImage;
	
	private boolean hqmode;
	
	private LinkedList<LightSource> lights;
	
	public Camera (Map map, double x, double y, double z, double fov, double clipNear, double clipFar, int horizontalResolution, int verticalResolution) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.fov = fov;
		this.clipNear = clipNear;
		this.clipFar = clipFar;
		setResolution (horizontalResolution, verticalResolution);
		this.map = map;
		refreshRayPlane ();
		hAng = 0;
		vAng = 0;
		lights = new LinkedList<LightSource> ();
		lights.add (new LightSource (new Vector3 (25, 10, 50), 10));
	}
	
	public void setPosition (double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public void setFacing (double horizontalAngle, double verticalAngle) {
		hAng = horizontalAngle;
		vAng = verticalAngle;
	}
	
	public void setResolution (int width, int height) {
		horizPixels = width;
		vertPixels = height;
		renderImage = new BufferedImage (width, height, BufferedImage.TYPE_INT_RGB);
		renderData = new int[width * height * 3];
	}
	
	public void refreshRayPlane () {
		
		double w = 1;
		double d = .5;
		
		//Create the ray plane
		rayPlane = new Vector3[horizPixels * vertPixels];
		
		//Calculate corner points (TODO)
		double usedFov = (fov / 180) * Math.PI / 2;
		double angleLeft = hAng - usedFov;
		double angleRight = hAng + usedFov;
		double angleUp = vAng - usedFov;
		double angleDown = vAng + usedFov;
		double magnitude = clipNear / Math.cos (usedFov);
		Matrix4 rot = getRotation ();
		Vector3 topLeft = Matrix4.product (rot, new Vector3 (-1, -1, 1));
		Vector3 topRight = Matrix4.product (rot, new Vector3 (1, -1, 1));
		Vector3 bottomLeft = Matrix4.product (rot, new Vector3 (-1, 1, 1));
		Vector3 bottomRight = Matrix4.product (rot, new Vector3 (1, 1, 1));
		//Calculate remaining points
		double vXOffset = (bottomRight.x - topRight.x) / vertPixels;
		double vYOffset = (bottomRight.y - topRight.y) / vertPixels;
		double vZOffset = (bottomRight.z - topRight.z) / vertPixels;
		double hXOffset;
		double hYOffset;
		double hZOffset;
		//Theoretically generates a nice little plane
		for (int wy = 0; wy < vertPixels; wy ++) {
			Vector3 vEdgeLeft = new Vector3 (topLeft.x + vXOffset * wy, topLeft.y + vYOffset * wy, topLeft.z + vZOffset * wy);
			Vector3 vEdgeRight = new Vector3 (topRight.x + vXOffset * wy, topRight.y + vYOffset * wy, topRight.z + vZOffset * wy);
			hXOffset = (vEdgeRight.x - vEdgeLeft.x) / horizPixels;
			hYOffset = (vEdgeRight.y - vEdgeLeft.y) / horizPixels;
			hZOffset = (vEdgeRight.z - vEdgeLeft.z) / horizPixels;
			for (int wx = 0; wx < horizPixels; wx ++) {
				rayPlane [horizPixels * wy + wx] = new Vector3 (vEdgeLeft.x + hXOffset * wx + x, vEdgeLeft.y + hYOffset * wx + y, vEdgeLeft.z + hZOffset * wx + z);
				//uu += rayPlane[horizPixels * wy + wx].toString ();
			}
			//System.out.println (uu);
		}
	}
	
	public Matrix4 getRotation () {
		Matrix4 yaw = Matrix4.rotY (hAng);
		Matrix4 pitch = Matrix4.rotX (vAng);
		return Matrix4.product (yaw, pitch);
	}
	
	@Override
	public void frameEvent () {
		double turnSpeed = 3;
		double walkSpeed = .05;
		int reachDist = 10;
		
		//Do look at stuff n stuff
		Vector3 direction = Matrix4.product (getRotation (), new Vector3 (0, 0, 1));
		Ray ray = new Ray (x, y, z, direction.x + x, direction.y + y, direction.z + z, reachDist);
		Voxel target = null;
		while (!ray.hitVoxel ()) {
			target = ray.step (map);
		}
		/*if (target != null) {
			Voxel hitVoxel = map.getVoxel (target.getX (), target.getY (), target.getZ ());
			if (hitVoxel != targetedVoxel && targetedVoxel != null && targetedVoxel instanceof TexturedVoxel) {
				((TexturedVoxel)targetedVoxel).setFragmentShader (((TargetedShader)targetedVoxel.getFragmentShader ()).getFilteredShader ());
			}
			if (hitVoxel instanceof TexturedVoxel && hitVoxel != targetedVoxel) {
				((TexturedVoxel)hitVoxel).setFragmentShader (new TargetedShader (hitVoxel.getFragmentShader ()));
			}
			targetedVoxel = hitVoxel;
		}*/
		if (keyPressed ('C')) {
			ray = new Ray (x, y, z, direction.x + x, direction.y + y, direction.z + z, reachDist);
			Voxel toDestroy = null;
			Voxel temp = null;
			while (!ray.hitVoxel ()) {
				toDestroy = ray.step (map);
			}
			if (toDestroy != null) {
				map.putVoxel (new EmptyVoxel (toDestroy.getX (), toDestroy.getY (), toDestroy.getZ ()));
			}
		}
		if (keyPressed (' ')) {
			ray = new Ray (x, y, z, direction.x + x, direction.y + y, direction.z + z, reachDist);
			Voxel toPlace = null;
			Voxel temp = null;
			while (!ray.hitVoxel ()) {
				toPlace = temp;
				temp = ray.step (map);
			}
			if (toPlace != null) {
				int workingId = usedId + 1;
				if (keyDown (KeyEvent.VK_SHIFT)) {
					workingId = workingId | 0xFF00;
				}
				TexturedVoxel newVoxel = new TexturedVoxel (toPlace.getX (), toPlace.getY (), toPlace.getZ (), workingId);
				if ((workingId & 0xFF) == 5) {
					lights.add (new LightSource (new Vector3 (toPlace.getX () + .5, toPlace.getY () + .5, toPlace.getZ () + .5), 4));
				}
				map.putVoxel (newVoxel);
				if (usedId == 6) {
					new WaterEntity (newVoxel, map, 0).declare ();
				}
			}
		}
		if (keyDown ('A')) {
			hAng -= (Math.PI / 180) * turnSpeed;
		}
		if (keyDown ('D')) {
			hAng += (Math.PI / 180) * turnSpeed;
		}
		if (keyDown ('F')) {
			vAng += (Math.PI / 180) * turnSpeed;
		}
		if (keyDown ('G')) {
			vAng -= (Math.PI / 180) * turnSpeed;
		}
		if (keyDown ('W')) {
			x += direction.x * walkSpeed;
			y += direction.y * walkSpeed;
			z += direction.z * walkSpeed;
		}
		if (keyDown ('S')) {
			x -= direction.x * walkSpeed;
			y -= direction.y * walkSpeed;
			z -= direction.z * walkSpeed;
		}
		//Bound the yaw angle to +-80 degrees
		if (vAng > 80 * Math.PI / 180) {
			vAng = 80 * Math.PI / 180;
		}
		if (vAng < -80 * Math.PI / 180) {
			vAng = -80 * Math.PI / 180;
		}
		if (keyPressed ('Q')) {
			if (!hqmode) {
				setResolution (640, 480);
				hqmode = true;
			} else {
				setResolution (160, 120);
				hqmode = false;
			}
		}
		if (keyPressed ('R')) {
			usedId ++;
			usedId = usedId % numVoxels;
		}
		Iterator<LightSource> iter = lights.iterator ();
		while (iter.hasNext ()) {
			iter.next ();
			//iter.next ().frameEvent ();
		}
	}
	
	@Override
	public void draw () {
		refreshRayPlane ();
		WritableRaster raster = renderImage.getRaster ();
		boolean state = hqmode;
		Timer.reset ();
		for (int i = 0; i < rayPlane.length; i ++) {
			try {
				Ray ray = new Ray (x, y, z, rayPlane [i].x, rayPlane [i].y, rayPlane [i].z, maxSteps);
				ray.useLights (lights);
				renderData [i] = ray.collide (map);
			} catch (ArrayIndexOutOfBoundsException e) {
				return;
			}
		}
		//Timer.printTime ();
		raster.setDataElements (0, 0, raster.getWidth (), raster.getHeight (), renderData);
		Graphics g = RenderLoop.wind.getBufferGraphics ();
		g.drawImage (renderImage, 0, 0, RenderLoop.wind.getResolution ()[0], RenderLoop.wind.getResolution ()[1], 0, 0, raster.getWidth (), raster.getHeight (), null);
		//throw new NullPointerException ();
	}
	
	public Vector3 generatePoint (Vector3 start, double hAng, double vAng, double magnitude) {
		double xOff = Math.cos (hAng) * Math.cos (vAng) * magnitude;
		double yOff = Math.sin (vAng) * magnitude;
		double zOff = Math.sin (hAng) * Math.cos (vAng) * magnitude;
		return new Vector3 (start.x + xOff, start.y + yOff, start.z + zOff);
	}
}