/**
 * 
 */
package shader;

import camera.Camera;
import entity.light.Light;
import loader.Loader;
import math.Maths;
import math.matrix.Matrix4f;
import math.vector.Vector2f;
import math.vector.Vector3f;
import render.Render;

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
	 * The location of the shader variable useFakeLighting
	 */
	private int location_useFakeLighting;
	
	/**
	 * The location of the shader variable wireframe
	 */
	private int location_wireframe;
	
	/**
	 * The location of the shader variable skycolour
	 */
	private int location_skyColour;
	
	/**
	 * The location of the shader variable wireframe
	 */
	private int location_numberOfTextureRows;
	
	/**
	 * The location of the shader variable skycolour
	 */
	private int location_texOffset;
	
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
		location_shineDamper = super.getUniformVarLocation("shineDamper");
		location_reflectivity = super.getUniformVarLocation("reflectivity");
		location_useFakeLighting = super.getUniformVarLocation("useFakeLighting");
		location_wireframe = super.getUniformVarLocation("wireframe");
		location_skyColour = super.getUniformVarLocation("skyColour");
		location_numberOfTextureRows = super.getUniformVarLocation("numberOfTextureRows");
		location_texOffset = super.getUniformVarLocation("texOffset");
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
		// Matrix4f matrix = Maths.createViewMatrix(camera);
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
		super.load3DVector(location_lightPosition, light.getPosition());
		// Load the colour of the light
		super.load3DVector(location_lightColour, light.getColor());
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
	
	/**
	 * Tell the shader to apply a fake light
	 * 
	 * @param useFakeLighting
	 */
	public void loadFakeLightingVariable( boolean useFakeLighting )
	{
		// Load the variable into the shade
		super.loadBoolean(location_useFakeLighting, useFakeLighting);
	}
	
	/**
	 * Tell the shader to render in wireframe mode
	 * 
	 * @param wireframe
	 */
	public void loadWireframeVariable( boolean wireframe )
	{
		// Load the var
		super.loadBoolean(location_wireframe, wireframe);
	}
	
	/**
	 * Add a sky colour to the shader
	 * 
	 * @param sky
	 */
	public void loadSkyColour( Vector3f sky )
	{
		// Load the var
		super.load3DVector(location_skyColour, sky);
	}
	
	/**
	 * Load the number of texture rows present in the texture
	 * 
	 * @param rows
	 */
	public void loadNumberOfTextureRows( int rows )
	{
		//System.out.println("Number of texture rows loaded");
		super.loadFloat(location_numberOfTextureRows, rows);
	}
	
	/**
	 * Load the target texture coordinates offset
	 * 
	 * @param x
	 * @param y
	 */
	public void loadTextureOffset( float x, float y )
	{
		super.load2DVector(location_texOffset, new Vector2f(x, y));
		
		//System.out.println("Loading texture offset: " + x + "x" + y);
	}
	
}
