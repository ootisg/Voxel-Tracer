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
	
	Point3d[] rayPlane;
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
		lights.add (new LightSource (new Point3d (25, 10, 50), 10));
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
		rayPlane = new Point3d[horizPixels * vertPixels];
		
		//Calculate corner points (TODO)
		double usedFov = (fov / 180) * Math.PI / 2;
		double angleLeft = hAng - usedFov;
		double angleRight = hAng + usedFov;
		double angleUp = vAng - usedFov;
		double angleDown = vAng + usedFov;
		double magnitude = clipNear / Math.cos (usedFov);
		Point3d thisPos = new Point3d (x, y, z);
		Point3d topLeft = generatePoint (thisPos, angleLeft, angleUp, magnitude);
		Point3d topRight = generatePoint (thisPos, angleRight, angleUp, magnitude);
		Point3d bottomLeft = generatePoint (thisPos, angleLeft, angleDown, magnitude);
		Point3d bottomRight = generatePoint (thisPos, angleRight, angleDown, magnitude);
		//Calculate remaining points
		double vXOffset = (bottomRight.x - topRight.x) / vertPixels;
		double vYOffset = (bottomRight.y - topRight.y) / vertPixels;
		double vZOffset = (bottomRight.z - topRight.z) / vertPixels;
		double hXOffset;
		double hYOffset;
		double hZOffset;
		//Theoretically generates a nice little plane
		for (int wy = 0; wy < vertPixels; wy ++) {
			Point3d vEdgeLeft = new Point3d (topLeft.x + vXOffset * wy, topLeft.y + vYOffset * wy, topLeft.z + vZOffset * wy);
			Point3d vEdgeRight = new Point3d (topRight.x + vXOffset * wy, topRight.y + vYOffset * wy, topRight.z + vZOffset * wy);
			hXOffset = (vEdgeRight.x - vEdgeLeft.x) / horizPixels;
			hYOffset = (vEdgeRight.y - vEdgeLeft.y) / horizPixels;
			hZOffset = (vEdgeRight.z - vEdgeLeft.z) / horizPixels;
			for (int wx = 0; wx < horizPixels; wx ++) {
				rayPlane [horizPixels * wy + wx] = new Point3d (vEdgeLeft.x + hXOffset * wx, vEdgeLeft.y + hYOffset * wx, vEdgeLeft.z + hZOffset * wx);
				//uu += rayPlane[horizPixels * wy + wx].toString ();
			}
			//System.out.println (uu);
		}
	}
	
	@Override
	public void frameEvent () {
		double turnSpeed = 3;
		double walkSpeed = .05;
		int reachDist = 10;
		
		//Do look at stuff n stuff
		Point3d cursorPoint = generatePoint (new Point3d (x, y, z), hAng, vAng, 1);
		Ray ray = new Ray (x, y, z, cursorPoint.x, cursorPoint.y, cursorPoint.z, reachDist);
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
			x += Math.cos (hAng) * walkSpeed * Math.cos (vAng);
			y += Math.sin (vAng) * walkSpeed;
			z += Math.sin (hAng) * walkSpeed * Math.cos (vAng);
		}
		if (keyDown ('S')) {
			x -= Math.cos (hAng) * walkSpeed * Math.cos (vAng);
			y -= Math.sin (vAng) * walkSpeed;
			z -= Math.sin (hAng) * walkSpeed * Math.cos (vAng);
		}
		if (keyPressed ('C')) {
			cursorPoint = generatePoint (new Point3d (x, y, z), hAng, vAng, 1);
			ray = new Ray (x, y, z, cursorPoint.x, cursorPoint.y, cursorPoint.z, reachDist);
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
			cursorPoint = generatePoint (new Point3d (x, y, z), hAng, vAng, 1);
			ray = new Ray (x, y, z, cursorPoint.x, cursorPoint.y, cursorPoint.z, reachDist);
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
					lights.add (new LightSource (new Point3d (toPlace.getX () + .5, toPlace.getY () + .5, toPlace.getZ () + .5), 4));
				}
				map.putVoxel (newVoxel);
				if (usedId == 6) {
					new WaterEntity (newVoxel, map, 0).declare ();
				}
			}
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
	
	public Point3d generatePoint (Point3d start, double hAng, double vAng, double magnitude) {
		double xOff = Math.cos (hAng) * Math.cos (vAng) * magnitude;
		double yOff = Math.sin (vAng) * magnitude;
		double zOff = Math.sin (hAng) * Math.cos (vAng) * magnitude;
		return new Point3d (start.x + xOff, start.y + yOff, start.z + zOff);
	}
}