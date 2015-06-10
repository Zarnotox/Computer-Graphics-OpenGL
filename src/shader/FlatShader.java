/**
 * 
 */
package shader;

import java.util.List;

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
	 * Maximum number of allowed lights
	 */
	private static final int MAX_LIGHTS = 2;
	
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
	 * The locations of the shader variable lightPosition[2]
	 */
	private int location_lightPosition[];
	
	/**
	 * The locations of the shader variable lightColour[2]
	 */
	private int location_lightColour[];
	
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
		
		// initialise arrays of lightPosition and lightColour
				location_lightPosition = new int[MAX_LIGHTS];
				location_lightColour = new int[MAX_LIGHTS];
				for(int i=0; i<MAX_LIGHTS; i++){
					location_lightPosition[i] = super.getUniformVarLocation("lightPosition[" + i +"]");
					location_lightColour[i] = super.getUniformVarLocation("lightColour[" + i +"]");
				}
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
	public void loadLights( List<Light> lights )
	{
		for(int i=0; i<MAX_LIGHTS; i++){
			if(i<lights.size()){
				// load light position in
				super.load3DVector(location_lightPosition[i], lights.get(i).getPosition());
				// load light colour in
				super.load3DVector(location_lightColour[i], lights.get(i).getColor());
			}
			else{ // if there are less lights than available light posions
				// load 'empty' lights in
				super.load3DVector(location_lightPosition[i], new Vector3f(0,0,0));
				super.load3DVector(location_lightColour[i], new Vector3f(0,0,0));
			}
		}

	}	
}
