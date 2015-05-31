/**
 * 
 */
package glStart;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.*;

import java.awt.Dimension;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import loader.Loader;
import loader.OBJLoader;
import math.vector.Vector3f;

import org.lwjgl.glfw.GLFWCharCallback;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWWindowSizeCallback;
import org.lwjgl.glfw.GLFWvidmode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLContext;

import render.Render;
import shader.StaticShader;
import terrain.Terrain;
import callbacks.CharHandler;
import callbacks.KeyHandler;
import callbacks.ResizeHandler;
import entity.Entity;
import entity.camera.MovableCamera;
import entity.light.Light;
import entity.model.Model;
import entity.model.TexturedModel;
import entity.texture.ModelTexture;

/**
 * OpenGL initialisation class based on the HelloWorld example found on the LWJGL website
 * 
 * @author Bert
 */
public class OpenGLStart {
	
	/**
	 * Window handle
	 */
	private DisplayHelper windowHelper;
	
	/**
	 * Errorhandler
	 */
	private GLFWErrorCallback errorHandler;
	
	/**
	 * Class that handles key interrupts
	 */
	private GLFWKeyCallback keyCallback;
	
	/**
	 * Class that handles character interrupts
	 */
	private GLFWCharCallback charCallback;
	
	/**
	 * Resize handler
	 */
	private GLFWWindowSizeCallback resizeCallback;
	
	/**
	 * The object that holds resources for rendering
	 */
	private RenderResources res;
	
	/**
	 * The object that handles loading resources
	 */
	private Loader loader;
	
	/**
	 * Constructor
	 */
	public OpenGLStart()
	{
		System.out.println("OpenGLStart called");
	}
	
	/**
	 * 
	 */
	private void init()
	{
		System.out.println("Initialising windows");
		
		// Set a callback handler
		errorHandler = errorCallbackPrint(System.err);
		glfwSetErrorCallback(errorHandler);
		
		// Initialize the GLFW core
		if ( glfwInit() != GL11.GL_TRUE )
		{
			throw new IllegalStateException("Could not initialize GLFW");
		}
		
		// Set window specs
		int windowWidth = 700;
		int windowHeight = 700;
		// The window title
		String windowTitle = "OpenGL project";
		
		glfwDefaultWindowHints();
		// Make the window invisible on creation
		glfwWindowHint(GLFW_VISIBLE, GL_FALSE);
		// Mark the window as resizable
		glfwWindowHint(GLFW_RESIZABLE, GL_TRUE);
		
		// Get the windowhandle
		long windowHandle = glfwCreateWindow(windowWidth, windowHeight, windowTitle,
				NULL, NULL);
		// Check the handle
		if ( windowHandle == NULL )
		{
			throw new RuntimeException("Could not create the GLFW window");
		}
		
		// Generate a displayhelper object for the created window
		windowHelper = new DisplayHelper(windowHandle);
		
		/* Get the real dimensions of the window */
		Dimension d = windowHelper.getWindowDimensions();
		windowWidth = (int) d.getWidth();
		windowHeight = (int) d.getHeight();
		
		// Fetch the graphical details of the primary monitor
		ByteBuffer videoMode = glfwGetVideoMode(glfwGetPrimaryMonitor());
		int centerWidth = (GLFWvidmode.width(videoMode) - windowWidth) / 2;
		int centerHeight = (GLFWvidmode.height(videoMode) - windowHeight) / 2;
		// Center the created window
		glfwSetWindowPos(windowHandle, centerWidth, centerHeight);
				
		// Create the OpenGL context
		glfwMakeContextCurrent(windowHandle);
		// Enable v-sync
		glfwSwapInterval(1); // Swap buffer on every framepush
		// Make the window visible
		glfwShowWindow(windowHandle);
	}
	
	/**
	 * 
	 */
	public void run()
	{
		System.out.println("Launching OpenGL");
		
		// Do initialisation
		init();
		
		// Get the current context from GLFW
		GLContext.createFromCurrent();
		
		// Create a new Loader
		this.loader = new Loader();
		
		/* SHADERS */
		// Create static shader
		// StaticShader stShader = new StaticShader(loader);
		// Load additional shaders
		loadShaders();
		
		// Generate render resources
		res = new RenderResources();
		
		/* CAMERAS */
		res.setActiveCamera(new MovableCamera(new Vector3f(0, 5, 5), 0, 0, 0));
		res.addCamera(new MovableCamera(new Vector3f(0, -10, -15), -90, 0, 0));
		
		/* LIGHTS */
		res.addLight(new Light(new Vector3f(3000, 2000, 2000), new Vector3f(1, 1, 1)));
		
		// Generate Keyhandlers
		initCallbackHandlers();
		
		// Run the loop
		loop();
		
		// Release all resources
		glfwDestroyWindow(windowHelper.getHandle());
		releaseInputHandlers();
		
		// Close up the application
		glfwTerminate();
		// Release the errorhandler
		errorHandler.release();
	}
	
	/**
	 * Initialise inputhandlers
	 */
	private void initCallbackHandlers()
	{
		// WindowHandle
		long windowHandle = windowHelper.getHandle();
		
		// The handler for window resizing
		resizeCallback = new ResizeHandler(res);
		
		// The handler for key events
		keyCallback = new KeyHandler(res);
		
		// The handler for char events
		charCallback = new CharHandler(res);
		
		// Create a resize callback
		glfwSetWindowSizeCallback(windowHandle, resizeCallback);
		
		// Add handlers to the window
		glfwSetKeyCallback(windowHandle, keyCallback);
		glfwSetCharCallback(windowHandle, charCallback);
	}
	
	/**
	 * Destroy inputhandlers
	 */
	private void releaseInputHandlers()
	{
		// Stop using the callback handlers
		keyCallback.release();
		
		charCallback.release();
	}
	
	/**
	 * Compile and load shaders
	 */
	private void loadShaders()
	{
		
	}
	
	/**
	 * 
	 */
	private void loop()
	{
		System.out.println("Running loop");
		
		// Create a new Renderer
		// EntityRenderer renderer = new EntityRenderer(windowHelper, res.getStShader());
		Render renderer = new Render(windowHelper, loader, res);
		
		// Generate an entitylist to render
		List<Entity> entityList = new ArrayList<>();
		List<Terrain> terrainList = new ArrayList<>();
		
		/* OBJECT MODELS */
		
		/* Dragon model */
		Model dragonModel = OBJLoader.loadObjModel("res/dragon.obj", loader);
		// Load the texture
		ModelTexture dragonTexture = new ModelTexture(
				loader.loadTexture("res/squareTexture_flatColour.png")); // trans_test.png
		// Link model and texture
		TexturedModel dragonTexturedModel = new TexturedModel(dragonModel, dragonTexture);
		
		// Reflectivity settings of the model
		dragonTexture.setShineDamper(10);
		dragonTexture.setReflectivity(1);
		
		// Generate an entity from the model and texture
		Entity dragonEntity1 = new Entity(dragonTexturedModel, new Vector3f(-10, 0, -15),
				0, 0, 0, 1);
		// Add the entity to the entity list
		entityList.add(dragonEntity1);
		
		// Generate an entity from the model and texture
		Entity dragonEntity2 = new Entity(dragonTexturedModel, new Vector3f(10, 0, -15),
				0, 0, 0, 1);
		// Add the entity to the entity list
		entityList.add(dragonEntity2);
		
		/* Rectangle model */		
		// Create the model
		Model boxModel = OBJLoader.loadObjModel("res/rectangle.obj", loader);
		// Load the texture
		ModelTexture boxTexture = new ModelTexture(
				loader.loadTexture("res/trans_test.png")); // trans_test.png
		boxTexture.setHasTransparency(true);
		boxTexture.setUseFakeLighting(true);
		// Link model and texture
		TexturedModel texturedModel = new TexturedModel(boxModel, boxTexture);
		// Generate an entity from the model and texture
		Entity boxEntity = new Entity(texturedModel, new Vector3f(0, 5, 0), 0, 0, 0, 1);
		
		entityList.add(boxEntity);
		
		/* TERRAINS */
		// Load grass terrain texture
		ModelTexture terrainTexture = new ModelTexture(
				loader.loadTexture("res/squareTexture_flatColour.png"));
		// Generate new terrain
		Terrain terrain = new Terrain(-1, -1, loader, terrainTexture);
		Terrain terrain2 = new Terrain(0, -1, loader, terrainTexture);
		Terrain terrain3 = new Terrain(-1, 0, loader, terrainTexture);
		Terrain terrain4 = new Terrain(0, 0, loader, terrainTexture);
		
		// Add the terrain to the list
		terrainList.add(terrain);
		terrainList.add(terrain2);
		terrainList.add(terrain3);
		terrainList.add(terrain4);
		
		// Set sky colour
		res.setSkyColour(new Vector3f(0.4f, 0.1f, 0.2f));
		
		// Fetch window handle
		long windowHandle = windowHelper.getHandle();
		// Loop till the user wants to close the window
		while (glfwWindowShouldClose(windowHandle) == GL_FALSE)
		{
			
			// Update entity
			dragonEntity1.increaseRotation(0, 1, 0);
			dragonEntity2.increaseRotation(0, -1, 0);
			boxEntity.increaseRotation(0, 1, 0);
			
			// Load all entities into the scene
			for (Entity e : entityList)
			{
				renderer.processEntity(e);
			}
			
			// Load all terrains into the scene
			for (Terrain t : terrainList)
			{
				renderer.processTerrain(t);
			}
			
			// Render the entity
			renderer.render();
			
			// Swap the buffer / show the rendered stuff
			glfwSwapBuffers(windowHandle);
			
			// Poll for events, interrupts and such get handled
			glfwPollEvents();
		}
		
		/* CLEANUP */
		renderer.cleanUp();
		
		// Cleanup buffers (VAO/VBO)
		loader.cleanUp();
		
	}
}
