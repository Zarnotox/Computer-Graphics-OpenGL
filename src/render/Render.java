/**
 * 
 */
package render;

import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.glClear;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL32;
import org.lwjgl.opengl.GL33;

import picking.PickingEngine;
import picking.shader.PickingShader;
import camera.Camera;
import entity.Entity;
import entity.light.Light;
import entity.model.TexturedModel;
import entity.terrain.Terrain;
import glStart.DisplayHelper;
import glStart.RenderResources;
import loader.Loader;
import math.Maths;
import math.matrix.Matrix4f;
import math.vector.Vector3f;
import shader.FlatShader;
import shader.StaticShader;
import shader.TerrainShader;

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
	 * The flat shader
	 */
	private FlatShader flatShader;
	
	/**
	 * The shader used for picking
	 */
	private PickingShader pickingShader;
	
	/**
	 * The shader for the terrain
	 */
	private TerrainShader terrainShader;
	
	/* ENTITY LISTS */
	
	/**
	 * List of entities grouped by model
	 */
	// private Map<TexturedModel, List<Entity>> entityMap;
	
	/**
	 * List of terrain entities
	 */
	private List<Terrain> terrainList;
	
	/**
	 * Flag indicating that there has to be drawn in wireframe mode
	 */
	private static boolean wireframeEnabled;
	
	/**
	 * Flag indicating that flatShadeMode is activated.
	 * OR flatshade or smoothshade can be active at the same time
	 */
	private static boolean flatShadeModeEnabled;
	
	/**
	 * Constructor
	 */
	public Render( DisplayHelper displayHelper, Loader loader, RenderResources res )
	{
		resources = res;
		wireframeEnabled = false;
		flatShadeModeEnabled = false;
		
		// Assign values
		this.displayHelper = displayHelper;
		// this.entityMap = new HashMap<>();
		this.terrainList = new ArrayList<>();
		
		// Generate the projetion matrix
		createProjectionMatrix();
		
		// Generate a new static shaderprogram
		this.entityShader = new StaticShader(loader);
		// Generate a new shader for flat shading
		this.flatShader = new FlatShader(loader);
		// Generate a new picking shaderprogram
		this.pickingShader = new PickingShader(loader);
		// Generate a new terrain shader
		this.terrainShader = new TerrainShader(loader);
		
		initShaders();
		
		// Generate a new renderer
		this.entityRenderer = new EntityRenderer(displayHelper, this.entityShader,
				this.pickingShader, this.flatShader);
		
		// Generate a new terrain renderer
		this.terrainRenderer = new TerrainRenderer(displayHelper, this.terrainShader,
				this.flatShader);
		
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		// Don't draw backwards facing primitives
		enableCulling();
	}
	
	/**
	 * Setup some shader variables
	 */
	private void initShaders()
	{
		// Load the projectionMatrix straight into the shader
		
		this.entityShader.start();
		this.entityShader.loadProjectionMatrix(projectionMatrix);
		this.entityShader.stop();
		
		this.pickingShader.start();
		this.pickingShader.loadProjectionMatrix(projectionMatrix);
		this.pickingShader.stop();
		
		this.flatShader.start();
		this.flatShader.loadProjectionMatrix(projectionMatrix);
		this.flatShader.stop();
		
		this.terrainShader.start();
		this.terrainShader.loadProjectionMatrix(projectionMatrix);
		this.terrainShader.stop();
		
	}
	
	/**
	 * Render the frame given the camera and sun object
	 * 
	 * @param light
	 * @param cam
	 */
	public void render( Map<TexturedModel, List<Entity>> mapBuffer )
	{
		/* PROPERTIES */
		Camera cam = resources.getActiveCamera();
		List<Light> lights = resources.getLightList();
		Vector3f skyColour = resources.getSkyColour();
		
		/* Picking phase */
		pickingPhase(cam, skyColour, lights, mapBuffer);
		
		/* Render phase */
		renderPhase(cam, lights, skyColour, mapBuffer);
		
	}
	
	private void pickingPhase( Camera cam,
			Vector3f skyColour,
			List<Light> lights,
			Map<TexturedModel, List<Entity>> mapBuffer )
	{
		// Enable the picking texture
		PickingEngine.enableWriting();
		// Clear the color and depth buffers
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		
		pickingShader.start();
		// Load the camera
		pickingShader.loadviewMatrix(cam);
		// Render
		entityRenderer.renderForPicking(mapBuffer);
		// Stop the shader program
		pickingShader.stop();
		
		// Disable the picking texture
		PickingEngine.disableWriting();
		
	}
	
	private void renderPhase( Camera cam,
			List<Light> lights,
			Vector3f skyColour,
			Map<TexturedModel, List<Entity>> mapBuffer )
	{
		// Prepare the scene
		// Use the depth buffer to properly render triangles
		
		// Clear the color and depth buffers
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		
		// Fetch the sky colour
		Vector3f sky = resources.getSkyColour();
		// Set the base values of the color buffers
		GL11.glClearColor(sky.x, sky.y, sky.z, 1);
		// System.out.println("Sky colour:" + sky.toString());
		
		/* ENTITIES */
		if ( flatShadeModeEnabled != true )
		{
			// Render in normal mode
			renderPhaseNormalMode(cam, lights, skyColour, mapBuffer);
		}
		else
		{
			// Render in flat mode
			renderPhaseFlatMode(cam, lights, skyColour, mapBuffer);
		}
		
		// Clear the entity collections
		this.terrainList.clear();
	}
	
	private void renderPhaseNormalMode( Camera cam,
			List<Light> lights,
			Vector3f skyColour,
			Map<TexturedModel, List<Entity>> mapBuffer )
	{
		// Start shader programs
		entityShader.start();
		// Load sky
		entityShader.loadSkyColour(skyColour);
		// Load lights into the shader
		entityShader.loadLights(lights);
		// Load the camera
		entityShader.loadviewMatrix(cam);
		// Render
		entityRenderer.render(mapBuffer, false, wireframeEnabled);
		// Stop the shader program
		entityShader.stop();
		
		/* TERRAIN */
		
		// Do the same as the entity render cycle
		
		terrainShader.start();
		terrainShader.loadSkyColour(skyColour);
		terrainShader.loadLights(lights);
		terrainShader.loadviewMatrix(cam);
		terrainRenderer.render(terrainList, false);
		terrainShader.stop();
	}
	
	private void renderPhaseFlatMode( Camera cam,
			List<Light> lights,
			Vector3f skyColour,
			Map<TexturedModel, List<Entity>> mapBuffer )
	{
		flatShader.start();
		// Load sky
		// entityShader.loadSkyColour(skyColour);
		// Load lights into the shader
		flatShader.loadLights(lights);
		// Load the camera
		flatShader.loadviewMatrix(cam);
		// Render
		entityRenderer.render(mapBuffer, true, wireframeEnabled);
		// Stop the shader program
		
		/* TERRAIN */
		
		// Do the same as the entity render cycle
		// terrainShader.start();
		// terrainShader.loadSkyColour(skyColour);
		// terrainShader.loadLight(sun);
		// terrainShader.loadviewMatrix(cam);
		terrainRenderer.render(terrainList, true);
		// terrainShader.stop();
		flatShader.stop();
		
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
	 * Enable flat shading mode
	 */
	public static void enableFlatShading()
	{
		flatShadeModeEnabled = true;
		
		GL32.glProvokingVertex(GL32.GL_FIRST_VERTEX_CONVENTION);
		
	}
	
	/**
	 * Disable flat shading mode
	 */
	public static void enableSmoothShading()
	{
		flatShadeModeEnabled = false;
	}
	
	/**
	 * Prepares the OpenGL context
	 */
	public void prepare()
	{
		// Use the depth buffer to properly render triangles
		//GL11.glEnable(GL11.GL_DEPTH_TEST);
		
		// Clear the color and depth buffers
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		
		// Fetch the sky colour
		//Vector3f sky = resources.getSkyColour();
		// Set the base values of the color buffers
		// GL11.glClearColor(sky.x, sky.y, sky.z, 1);
		// System.out.println("Sky colour:" + sky.toString());
	}
	
	/**
	 * Process an entity into Buffer map
	 * 
	 * @param entity
	 * @param mapBuffer Maps entities to their model
	 */
	public void processEntity( Entity entity, Map<TexturedModel, List<Entity>> mapBuffer )
	{
		// Get the model from the entity
		TexturedModel model = entity.getModel();
		// Get the list of the entities matched to this model
		List<Entity> entList = mapBuffer.get(model);
		// Check if there was a list
		if ( entList == null )
		{
			// Generate a new list
			List<Entity> newList = new ArrayList<>();
			// Add it to the map
			mapBuffer.put(model, newList);
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
		Dimension dim = displayHelper.getWindowDimensions();
		projectionMatrix = Maths.createProjectionMatrix(dim, FOV, FAR_PLANE_DISTANCE, NEAR_PLANE_DISTANCE);
	}
	
	/**
	 * Cleanup components
	 */
	public void cleanUp()
	{
		entityShader.cleanUp();
		terrainShader.cleanUp();
		pickingShader.cleanUp();
	}
	
	/**
	 * Returns the projectionMatrix
	 * 
	 * @return
	 */
	public Matrix4f getProjectionMatrix()
	{
		return projectionMatrix;
	}
	
}
