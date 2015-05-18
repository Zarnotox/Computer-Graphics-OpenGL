/**
 * 
 */
package glCode;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.*;

import java.nio.ByteBuffer;

import loader.Loader;
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
import entity.Entity;

/**
 * OpenGL initialisation class based on the HelloWorld example found on the LWJGL website
 * 
 * @author Bert
 */
public class OpenGLStart {
	
	// Window handle
	private long window;
	
	// Errorhandler
	private GLFWErrorCallback errorHandler;
	
	// Class that handles key interrupts
	private GLFWKeyCallback keyCallback;
	
	// Class that handles character interrupts
	private GLFWCharCallback charCallback;
	
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
		
		// Generate Keyhandlers
		generateInputhandler();
		
		// Do initialisation
		init();
		
		// Run the loop
		loop();
		
		// Release all resources
		glfwDestroyWindow(window);
		releaseInputHandlers();
		
		// Close up the application
		glfwTerminate();
		// Release the errorhandler
		errorHandler.release();
	}
	
	/**
	 * 
	 */
	private void generateInputhandler()
	{
		// The handler for key events
		keyCallback = new KeyHandler();
		
		// The handler for char events
		charCallback = new CharHandler();
	}
	
	/**
	 * 
	 */
	private void releaseInputHandlers()
	{
		// Stop using the callback handlers
		keyCallback.release();
		
		charCallback.release();
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
		window = glfwCreateWindow(windowWidth, windowHeight, windowTitle, NULL, NULL);
		// Check the handle
		if ( window == NULL )
		{
			throw new RuntimeException("Could not create the GLFW window");
		}
		
		/* SETUP KEY LISTENERS */
		glfwSetKeyCallback(window, keyCallback);
		glfwSetCharCallback(window, charCallback);
		
		// Fetch the graphical details of the primary monitor
		ByteBuffer videoMode = glfwGetVideoMode(glfwGetPrimaryMonitor());
		int centerWidth = (GLFWvidmode.width(videoMode) - windowWidth) / 2;
		int centerHeight = (GLFWvidmode.height(videoMode) - windowHeight) / 2;
		// Center the created window
		glfwSetWindowPos(window, centerWidth, centerHeight);
		
		// Create the OpenGL context
		glfwMakeContextCurrent(window);
		// Enable v-sync
		glfwSwapInterval(1); // Swap buffer on every framepush
		// Make the window visible
		glfwShowWindow(window);
	}
	
	/**
	 * 
	 */
	private void loop()
	{
		System.out.println("Running loop");
		
		// Get the current context from GLFW
		GLContext.createFromCurrent();
		
		// Set the base values of the color buffers
		glClearColor(0, 0, 0, 0);
		
		// Create a new Loader
		Loader loader = new Loader();
		// Create a new Renderer
		Renderer renderer = new Renderer();
		
		/* SHADERS */
		// Create static shader
		StaticShader stShader = new StaticShader();
		
		// A model
		float[] vertices = {
		/* First vertex */
		-0.5f, 0.5f, 0f,
		/* Second vertex */
		-0.5f, -0.5f, 0f,
		/* Third vertex */
		0.5f, -0.5f, 0f,
		/* Fourth vertex */
		0.5f, 0.5f, 0 };
		
		// The index telling in what order to draw
		int[] indices = {
		/* First */
		0, 1, 3,
		/* Second */
		3, 1, 2 };
		
		// Texture coord mappings
		float[] textureCoords = {
				// SW
				0, 0,
				// NW
				0, 1,
				// NE
				1, 1,
				// SE
				1, 0 };
		
		// Create the model
		Model model = loader.loadToVAO(vertices, textureCoords, indices);
		ModelTexture texture = new ModelTexture(
				loader.loadTexture("res/trans_test.png")); // trans_test.png
		TexturedModel texturedModel = new TexturedModel(model, texture);
		
		// Generate an entity from the model and texture
		Entity entity =  new Entity(texturedModel, new Vector3f(0, 0, 0), 0, 0, 0, 1);
		
		// Loop till the user wants to close the window
		while (glfwWindowShouldClose(window) == GL_FALSE)
		{
			// Prepare for rendering the scene
			renderer.prepare();
			// Run shader
			stShader.start();
			
			// Update entity
			//entity.increasePosition(0.05f, 0, 0);
			entity.increaseRotation(0, 1, 0);
			// Render the model
			renderer.render(entity, stShader);
			
			// Stop the shader program
			stShader.stop();
			
			// Swap the buffer / show the rendered stuff
			glfwSwapBuffers(window);
			
			// Poll for events, interrupts and such get handled
			glfwPollEvents();
		}
		
		/* CLEANUP */
		// Cleanup shader resources
		stShader.cleanUp();
		
		// Cleanup buffers (VAO/VBO)
		loader.cleanUp();
		
	}
}
