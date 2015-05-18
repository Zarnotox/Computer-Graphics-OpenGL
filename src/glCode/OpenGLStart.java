/**
 * 
 */
package glCode;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.*;

import java.awt.Dimension;
import java.nio.ByteBuffer;

import light.Light;
import loader.Loader;
import loader.OBJLoader;
import math.vector.Vector3f;
import model.Model;
import model.TexturedModel;

import org.lwjgl.glfw.GLFWCharCallback;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWvidmode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLContext;

import render.Renderer;
import shader.StaticShader;
import texture.ModelTexture;
import callbacks.CharHandler;
import callbacks.KeyHandler;
import camera.MovableCamera;
import entity.Entity;

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
		StaticShader stShader = new StaticShader(loader);
		// Load additional shaders
		loadShaders();
		
		// Generate render resources
		res = new RenderResources(stShader);
		
		/* CAMERAS */
		res.setActiveCamera(new MovableCamera(new Vector3f(0, 0, 0), 0, 0, 0));
		res.addCamera(new MovableCamera(new Vector3f(0, 0, 5), 0, 0, 0));
		
		/* LIGHTS */
		res.addLight(new Light(new Vector3f(0, -10, -5), new Vector3f(0.8f, 0.8f, 0.7f)));
		
		// Generate Keyhandlers
		initInputhandlers();
		
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
	private void initInputhandlers()
	{
		// The handler for key events
		keyCallback = new KeyHandler(res);
		
		// The handler for char events
		charCallback = new CharHandler(res);
		
		// Add handlers to the window
		glfwSetKeyCallback(windowHelper.getHandle(), keyCallback);
		glfwSetCharCallback(windowHelper.getHandle(), charCallback);
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
		int windowWidth = 400;
		int windowHeight = 400;
		// The window title
		String windowTitle = "Title";
		
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
	private void loop()
	{
		System.out.println("Running loop");
		
		// Create a new Renderer
		Renderer renderer = new Renderer(windowHelper, res.getStShader());
		
		// Create the model
		Model model = OBJLoader.loadObjModel("res/dragon.obj", loader);
		// Load the texture
		ModelTexture texture = new ModelTexture(
				loader.loadTexture("res/squareTexture_flatColour.png")); // trans_test.png
		// Link model and texture
		TexturedModel texturedModel = new TexturedModel(model, texture);
		// Generate an entity from the model and texture
		Entity entity = new Entity(texturedModel, new Vector3f(0, -5, -15), 0, 0, 0, 1);
		
		// Loop till the user wants to close the window
		while (glfwWindowShouldClose(windowHelper.getHandle()) == GL_FALSE)
		{
			// Prepare for rendering the scene
			renderer.prepare();
			
			// Update entity
			// entity.increasePosition(0, 0, -0.005f);
			entity.increaseRotation(0, 1, 0);
			
			// Render the entity
			renderer.render(entity, res);
			
			// Swap the buffer / show the rendered stuff
			glfwSwapBuffers(windowHelper.getHandle());
			
			// Poll for events, interrupts and such get handled
			glfwPollEvents();
		}
		
		/* CLEANUP */
		// Cleanup shader resources
		res.getStShader().cleanUp();
		
		// Cleanup buffers (VAO/VBO)
		loader.cleanUp();
		
	}
}
