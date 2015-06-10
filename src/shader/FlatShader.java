/**
 * 
 */
package shader;

import render.Render;
import camera.Camera;
import entity.light.Light;
import loader.Loader;
import math.matrix.Matrix4f;
import math.vector.Vector3f;

/**
 * @author Bert
 *
 */
public class FlatShader extends ShaderProgram {
	
	/**
	 * Location of the vertex shader file
	 */
	private static final String VERTEX_SHADER_FILE = "shader/files/flatVertexShader.txt";
	
	/**
	 * Location of the fragment shader file
	 */
	private static final String FRAGMENT_SHADER_FILE = "shader/files/flatFragmentShader.txt";
	
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
	 * Constructor
	 */
	public FlatShader( Loader loader )
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
		super.bindAttribute(Render.POSITION_ATTR_INDEX, "position");
		// Connect the texture coords VBO
		super.bindAttribute(Render.TEXTURE_COORD_ATTR_INDEX, "textureCoords");
		// Connect the normals VBO to the VAO
		super.bindAttribute(Render.NORMALS_ATTR_INDEX, "normal");
		
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
		//Matrix4f matrix = Maths.createViewMatrix(camera);
		// Store the viewmatrix
		super.loadMatrix(location_viewMatrix, camera.getViewMatrix());
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
}
