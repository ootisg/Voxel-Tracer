# Voxel-Tracer

This is a voxel-based raytracer. It uses minecraft textures as placeholders, though it's hard to tell since it runs at 160x120. As that might imply, it's not particularly fast.
Rays do not bounce; instead, sharp shadows are cast by casting additional rays from each nearby light source. Multiple light sources are supported, but lights are somewhat buggy depending on their exact placement.
To get a higher resolution render of the current view, the Q key can be used to toggle the render quality. Real-time quality is 160x120; full render quality is 640x480.
Voxels use a modified shader model. These are as follows:  
	- The ray shader is invoked on the ray each time it enters a voxel. When a voxel is hit, its ray shader is called on the ray that hit it.
	- The fragment shader serves essentially the same purpose as it does for raster graphics. Each ray calls a fragment shader at the end of its lifetime (or, when it passes through a transparent object).
The following blocks (in-order) are supported:  
	- Stone Brick
	- Gravel (default selection)
	- Mossy Cobblestone
	- Glass
	- Glowstone (note: prone to crashing)
	- Gold Block
	- Water
	- Ice
The controls are as follows:  
	- Click and drag can be used to look around
	- W to move forward, S to move backwards
	- A to look left, and D to look right. F and G can be used to look up and down, respectively.
	- Spacebar to place a block
	- C to break a block
	- R to cycle through the block types. After Ice, this loops back to Stone Bricks.
	- Hold Shift when placing a block to place a slab instead.  
  
No libraries are required to build/run the project other than the default JRE/JDK library. This project uses the Swing framework for windowing and to capture user input.
  
This project was made for fun, for no other particular purpose. Feel free to use it for whatever you'd like.