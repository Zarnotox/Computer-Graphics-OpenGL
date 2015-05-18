/**
 * 
 */
package shader;

import camera.Camera;
import light.Light;
import loader.Loader;
import math.Maths;
import math.matrix.Matrix4f;
import render.Renderer;

/**
 * @author Bert
 */
public class StaticShader extends ShaderProgram {
	
	/**
	 * Location of the vertex shader file
	 */
	private static final String VERTEX_SHADER_FILE = "shader/files/vertexShader.txt";
	
	/**
	 * Location of the fragment shader file
	 */
	private static final String FRAGMENT_SHADER_FILE = "shader/files/fragmentShader.txt";
	
	/**
	 * The location of the shader variable transformationMatrix
	 */
	private int location_transformationMatrix;
	
	/**
	 * The location of the shader variable projectionmatrix
	 */
	private int location_projectionMatrix;
	
	/**
	 * The location of the shader variable viewmatrix
	 */
	private int location_viewMatrix;
	
	/**
	 * The location of the shader variable lightPosition
	 */
	private int location_lightPosition;
	
	/**
	 * The location of the shader variable lightColour
	 */
	private int location_lightColour;
	
	/**
	 * The location of the shader variable shineDamper
	 */
	private int location_shineDamper;
	
	/**
	 * The location of the shader variable reflectivity
	 */
	private int location_reflectivity;
	
	/**
	 * Constructor
	 */
	public StaticShader( Loader loader )
	{
		// Let the super class handle the shader files
		super(VERTEX_SHADER_FILE, FRAGMENT_SHADER_FILE, loader);
	}
	
	/*
	 * (non-Javadoc)
	 * @see shader.ShaderProgram#bindAttributes()
	 */
	@Override
	protected void bindAttributes()
	{
		// Connect the position VBO (from the VAO at INDEX 0) to the variable position
		super.bindAttribute(Renderer.POSITION_ATTR_INDEX, "position");
		// Connect the texture coords VBO
		super.bindAttribute(Renderer.TEXTURE_COORD_ATTR_INDEX, "textureCoords");
		// Connect the normals VBO to the VAO
		super.bindAttribute(Renderer.NORMALS_ATTR_INDEX, "normal");
		
	}
	
	/*
	 * (non-Javadoc)
	 * @see shader.ShaderProgram#getAllUniformVarLocations()
	 */
	@Override
	protected void getAllUniformVarLocations()
	{
		location_transformationMatrix = super.getUniformVarLocation("transformationMatrix");
		location_projectionMatrix = super.getUniformVarLocation("projectionMatrix");
		location_viewMatrix = super.getUniformVarLocation("viewMatrix");
		location_lightColour = super.getUniformVarLocation("lightColour");
		location_lightPosition = super.getUniformVarLocation("lightPosition");
		location_shineDamper = super.getUniformVarLocation("shineDamper");
		location_reflectivity = super.getUniformVarLocation("reflectivity");
	}
	
	/**
	 * Load a transformation matrix into the shader
	 * 
	 * @param matrix
	 */
	public void loadTransformationMatrix( Matrix4f matrix )
	{
		super.loadMatrix(location_transformationMatrix, matrix);
	}
	
	/**
	 * Load a projection matrix into the shader
	 * 
	 * @param matrix
	 */
	public void loadProjectionMatrix( Matrix4f matrix )
	{
		super.loadMatrix(location_projectionMatrix, matrix);
	}
	
	/**
	 * Load a view matrix into the shader
	 * 
	 * @param matrix
	 */
	public void loadviewMatrix( Camera camera )
	{
		// Generate the viewmatrix based on the camera
		Matrix4f matrix = Maths.createViewMatrix(camera);
		// Store the viewmatrix
		super.loadMatrix(location_viewMatrix, matrix);
	}
	
	/**
	 * Loads a light object into the shader
	 * 
	 * @param light
	 */
	public void loadLight( Light light )
	{
		// Load the position of the light
		super.loadVector(location_lightPosition, light.getPosition());
		// Load the colour of the light
		super.loadVector(location_lightColour, light.getColor());
	}
	
	/**
	 * Pass shine variables onto the shader
	 * 
	 * @param damper
	 * @param reflectivity
	 */
	public void loadShineVariables( float damper, float reflectivity )
	{
		// Load the damper amount
		super.loadFloat(location_shineDamper, damper);
		// Load the reflectivity amount
		super.loadFloat(location_reflectivity, reflectivity);
	}
	
}
