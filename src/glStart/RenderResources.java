/**
 * 
 */
package glStart;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import picking.PickingEngine;
import camera.Camera;
import math.vector.Vector3f;
import shader.ShaderProgram;
import entity.Entity;
import entity.light.Light;
import entity.model.TexturedModel;

/**
 * @author Bert
 */
public class RenderResources {
	
	// Entity buffers
	private Deque< Map<TexturedModel, List<Entity>> > entityBuffers;
	
	// Pick engine
	private PickingEngine pickEngine;
	
	/* SHADERS */	
	/**
	 * The static shader
	 */
	//private StaticShader stShader;
	
	/**
	 * The list of generated shaders
	 */
	private List<ShaderProgram> shaderList;
	
	
	/* CAMERAS */
	
	/**
	 * List of created camera's
	 */
	private List<Camera> cameraList;
	
	/**
	 * Current camera
	 */
	private int currentCamera;
	
	/* LIGHTS */
	/**
	 * List of created lights
	 */
	private List<Light> lightList;
	
	/**
	 * The colour of the sky
	 */
	private Vector3f skyColour;
	
	/**
	 * Constructor
	 */
	public RenderResources( )
	{		
		// Standard sky colour
		skyColour = new Vector3f(0, 0, 0);
		
		// Generate lists
		cameraList = new ArrayList<>();
		lightList = new ArrayList<>();
		
		// Generate entity buffers
		entityBuffers = new ArrayDeque<>();
		entityBuffers.add(new HashMap<TexturedModel, List<Entity>>());
		entityBuffers.add(new HashMap<TexturedModel, List<Entity>>());
	}
	
	/**
	 * Returns the current buffer used for entity storage
	 * @return
	 */
	public Map<TexturedModel, List<Entity>> getActiveEntityBuffer() {
		return entityBuffers.peek();
	}
	
	/**
	 * Swap the entityBuffer
	 */
	public void swapEntityBuffer() {
		// Remove the head of the queue and add it to the tail
		entityBuffers.addLast(entityBuffers.pollFirst());
	}
	
	/**
	 * Retrieves the used entityBuffer before the last swap
	 * @return
	 */
	public Map<TexturedModel, List<Entity>> getLastUsedEntityBuffer() {
		return entityBuffers.peekLast();
	}
	
	/**
	 * Add a camera to the list
	 * 
	 * @param cam
	 */
	public void addCamera( Camera cam )
	{
		cameraList.add(cam);
	}
	
	/**
	 * Add a shader program to the list
	 * 
	 * @param shader
	 */
	public void addShader( ShaderProgram shader )
	{
		shaderList.add(shader);
	}
	
	/**
	 * Add a light to the render
	 * @param light
	 */
	public void addLight( Light light )
	{
		this.lightList.add(light);
	}
	
	/**
	 * Set a camera as active. If the camera isn't found inside the list, it will be added
	 * 
	 * @param cam
	 */
	public void setActiveCamera( Camera cam )
	{
		// Find the camera in the list
		int index = cameraList.indexOf(cam);
		
		// Check if the camera was found
		if ( index == -1 )
		{
			// If the camera was not found, add it
			cameraList.add(cam);
			// Retry activating the current camera
			setActiveCamera(cam);
		}
		else
		{
			// Set the index of the found camera as active
			currentCamera = index;
			
			/* DEBUG */
			System.out.println("Registering camera: " + currentCamera);
		}
	}
	
	/**
	 * Mark the next camera as active camera
	 */
	public void nextCamera()
	{
		if ( currentCamera >= (cameraList.size() - 1) )
		{
			currentCamera = 0;
		}
		else
		{
			currentCamera++;
		}
		
		/* DEBUG */
		// System.out.println("Registering camera: " + currentCamera);
	}
	
	/**
	 * Mark the previous camera as active camera
	 */
	public void previousCamera()
	{
		if ( currentCamera <= 0 )
		{
			currentCamera = (cameraList.size() - 1);
		}
		else
		{
			currentCamera--;
		}
		
		/* DEBUG */
		// System.out.println("Registering camera: " + currentCamera);
	}
	
	/**
	 * Returns the currently selected camera
	 * 
	 * @return
	 */
	public Camera getActiveCamera()
	{
		return cameraList.get(currentCamera);
	}
	
	/**
	 * @return the stShader
	 */
	/*
	 * public StaticShader getStShader()
	 * {
	 * return this.stShader;
	 * }
	 */
	
	/**
	 * @return the shaderList
	 */
	public List<ShaderProgram> getShaderList()
	{
		return new ArrayList<>(shaderList);
	}
	
	/**
	 * @return the cameraList
	 */
	public List<Camera> getCameraList()
	{
		return new ArrayList<>(cameraList);
	}
	
	/**
	 * @return the lightList
	 */
	public List<Light> getLightList()
	{
		return this.lightList;
	}

	/**
	 * @return the skyColour
	 */
	public Vector3f getSkyColour()
	{
		return this.skyColour;
	}

	/**
	 * @param skyColour the skyColour to set
	 */
	public void setSkyColour( Vector3f skyColour )
	{
		this.skyColour = skyColour;
	}

	/**
	 * @return the pickEngine
	 */
	public PickingEngine getPickEngine()
	{
		return this.pickEngine;
	}

	/**
	 * @param pickEngine the pickEngine to set
	 */
	public void setPickEngine( PickingEngine pickEngine )
	{
		this.pickEngine = pickEngine;
	}
	
	
}
