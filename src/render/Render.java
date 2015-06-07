/**
 * 
 */
package render;

import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.glClear;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Generated;

import org.lwjgl.opengl.GL11;

import camera.Camera;
import entity.Entity;
import entity.light.Light;
import entity.model.TexturedModel;
import glStart.DisplayHelper;
import glStart.RenderResources;
import loader.Loader;
import math.matrix.Matrix4f;
import math.vector.Vector3f;
import shader.StaticShader;
import shader.TerrainShader;
import terrain.Terrain;

/**
 * @author Bert
 */
public class Render {
	
	/**
	 * This variable declares that normal vectors will be stored inside the
	 * VAO at index 2
	 */
	public final static int NORMALS_ATTR_INDEX = 2;
	
	/**
	 * This variable declares that texture coordinate mappings will be stored inside the
	 * VAO at index 1
	 */
	public final static int TEXTURE_COORD_ATTR_INDEX = 1;
	
	/**
	 * This variable declares that the position attribute VBO has to be stored into the
	 * VAO at index 0
	 */
	public final static int POSITION_ATTR_INDEX = 0;
	
	/**
	 * The field of view angle used for the camera
	 */
	public final static float FOV = 70;
	
	/**
	 * The minimum distance for things to be visible
	 */
	public final static float NEAR_PLANE_DISTANCE = 0.1f;
	
	/**
	 * The distance as in how far away we can see
	 */
	public final static float FAR_PLANE_DISTANCE = 1000;
	
	/**
	 * The helper object for the current window
	 */
	private DisplayHelper displayHelper;
	
	/**
	 * The projection matrix
	 */
	private Matrix4f projectionMatrix;
	
	/**
	 * The object that holds all rendered objects
	 */
	private RenderResources resources;
	
	/* RENDERERS */
	
	/**
	 * The entity renderer instance
	 */
	private EntityRenderer entityRenderer;
	
	/**
	 * The terrain renderer instance
	 */
	private TerrainRenderer terrainRenderer;
	
	/* SHADERS */
	
	/**
	 * The static shader for this render instance
	 */
	private StaticShader entityShader;
	
	/**
	 * The shader for the terrain
	 */
	private TerrainShader terrainShader;
	
	/* ENTITY LISTS */
	
	/**
	 * List of entities grouped by model
	 */
	private Map<TexturedModel, List<Entity>> entityMap;
	
	/**
	 * List of terrain entities
	 */
	private List<Terrain> terrainList;
	
	/**
	 * Flag indicating that there has to be drawn in wireframe mode
	 */
	private static boolean wireframeEnabled;
	
	/**
	 * Constructor
	 */
	public Render( DisplayHelper displayHelper, Loader loader, RenderResources res )
	{
		resources = res;
		wireframeEnabled = false;
		
		// Assign values
		this.displayHelper = displayHelper;
		this.entityMap = new HashMap<>();
		this.terrainList = new ArrayList<>();
		
		// Generate the projetion matrix
		createProjectionMatrix();
		
		// Generate a new static shader
		this.entityShader = new StaticShader(loader);
		// Generate a new renderer
		this.entityRenderer = new EntityRenderer(displayHelper, this.entityShader,
				projectionMatrix);
		
		// Generate a new terrain shader
		this.terrainShader = new TerrainShader(loader);
		// Generate a new terrain renderer
		this.terrainRenderer = new TerrainRenderer(displayHelper, this.terrainShader,
				projectionMatrix);
		
		// Don't draw backwards facing primitives
		enableCulling();
	}
	
	/**
	 * Render the frame given the camera and sun object
	 * 
	 * @param light
	 * @param cam
	 */
	public void render()
	{
		// Prepare the renderer
		prepare();
		
		/* PROPERTIES */
		Camera cam = resources.getActiveCamera();
		Light sun = resources.getLightList().get(0);
		Vector3f skyColour = resources.getSkyColour();
		
		/* ENTITIES */
		// Start shader programs
		entityShader.start();
		// Load sky
		entityShader.loadSkyColour(skyColour);
		// Load lights into the shader
		entityShader.loadLight(sun);
		// Load the camera
		entityShader.loadviewMatrix(cam);
		// Render
		entityRenderer.render(entityMap, wireframeEnabled);
		// Stop the shader program
		entityShader.stop();
		
		/* TERRAIN */
		// Do the same as the entity render cycle
		terrainShader.start();
		terrainShader.loadSkyColour(skyColour);
		terrainShader.loadLight(sun);
		terrainShader.loadviewMatrix(cam);
		terrainRenderer.render(terrainList);
		terrainShader.stop();
		
		// Clear the entity collections
		this.entityMap.clear();
		this.terrainList.clear();
	}
	
	/**
	 * Don't draw backwards facing primitives
	 */
	public static void enableCulling()
	{
		// Don't render backwards facing vertices
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glCullFace(GL11.GL_BACK);
	}
	
	/**
	 * Draw backwards facing primitives
	 * This function can be called to draw models with transparent textures, this improves
	 * visibility
	 */
	public static void disableCulling()
	{
		GL11.glDisable(GL11.GL_CULL_FACE);
	}
	
	/**
	 * Enables wireframe mode
	 */
	public static void enableWireFrame( boolean shaderWireframe )
	{
		// Draw outline only
		GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
		// Disable textures
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		
		if ( shaderWireframe == true )
		{
			wireframeEnabled = true;
		}
	}
	
	/**
	 * Disables wireframe mode
	 */
	public static void disableWireFrame()
	{
		// Draw full triangles
		GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
		// Enable textures
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		
		wireframeEnabled = false;
	}
	
	/**
	 * Prepares the OpenGL context
	 */
	public void prepare()
	{
		// Use the depth buffer to properly render triangles
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		
		// Clear the color and depth buffers
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		
		// Fetch the sky colour
		Vector3f sky = resources.getSkyColour();
		// Set the base values of the color buffers
		GL11.glClearColor(sky.x, sky.y, sky.z, 1);
	}
	
	/**
	 * Add an entity to the renderlist
	 * 
	 * @param entity
	 */
	public void processEntity( Entity entity )
	{
		// Get the model from the entity
		TexturedModel model = entity.getModel();
		// Get the list of the entities matched to this model
		List<Entity> entList = entityMap.get(model);
		// Check if there was a list
		if ( entList == null )
		{
			// Generate a new list
			List<Entity> newList = new ArrayList<>();
			// Add it to the map
			entityMap.put(model, newList);
			// Declare the list again
			entList = newList;
		}
		// Load the entity
		entList.add(entity);
	}
	
	/**
	 * Add a terrain object to the renderlist
	 * 
	 * @param terrain
	 */
	public void processTerrain( Terrain terrain )
	{
		this.terrainList.add(terrain);
	}
	
	/**
	 * Generate a projectionMatrix.
	 * The projectionMatrix makes the objects onscreen more realistic looking
	 */
	private void createProjectionMatrix()
	{
		// Fetch the window dimensions
		Dimension d = displayHelper.getWindowDimensions();
		
		/* DEBUG */
		//System.out.println("Calculating projection matrix");
		//System.out.println("WindowDimensions: " + d.toString());
		
		// Prepare matrix variables
		float aspectRatio = (float) d.getWidth() / (float) d.getHeight();
		float y_scale = (float) (1f / Math.tan(Math.toRadians(FOV / 2f)) * aspectRatio);
		float x_scale = y_scale / aspectRatio;
		float frustum_length = FAR_PLANE_DISTANCE - NEAR_PLANE_DISTANCE;
		
		// Generate the projection matrix
		projectionMatrix = new Matrix4f();
		projectionMatrix.m00 = x_scale;
		projectionMatrix.m11 = y_scale;
		projectionMatrix.m22 = -((FAR_PLANE_DISTANCE + NEAR_PLANE_DISTANCE) / frustum_length);
		projectionMatrix.m23 = -1;
		projectionMatrix.m32 = -((2 * NEAR_PLANE_DISTANCE * FAR_PLANE_DISTANCE) / frustum_length);
		projectionMatrix.m33 = 0;
		
		/* DEBUG */
		System.out.println(projectionMatrix.toString());
	}
	
	/**
	 * Cleanup components
	 */
	public void cleanUp()
	{
		entityShader.cleanUp();
		terrainShader.cleanUp();
	}
	
	/**
	 * Returns the projectionMatrix
	 * @return
	 */
	public Matrix4f getProjectionMatrix() {
		return projectionMatrix;
	}
	
}
