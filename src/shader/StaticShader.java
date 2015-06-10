/**
 * 
 */
package shader;

import java.util.List;

import camera.Camera;
import entity.light.Light;
import loader.Loader;
import math.Maths;
import math.matrix.Matrix4f;
import math.vector.Vector3f;
import render.Render;

/**
 * @author Bert
 */
public class StaticShader extends ShaderProgram {
	
	/**
	 * Maximum number of allowed lights
	 */
	private static final int MAX_LIGHTS = 2;
	
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
	 * The locations of the shader variables lightPosition[2]
	 */
	private int location_lightPosition[];
	
	/**
	 * The locations of the shader variables lightColour[2]
	 */
	private int location_lightColour[];
	
	/**
	 * The locations of the shader variables attenuation[2]
	 */
	private int location_attenuation[];
	
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
		location_shineDamper = super.getUniformVarLocation("shineDamper");
		location_reflectivity = super.getUniformVarLocation("reflectivity");
		location_useFakeLighting = super.getUniformVarLocation("useFakeLighting");
		location_wireframe = super.getUniformVarLocation("wireframe");
		location_skyColour = super.getUniformVarLocation("skyColour");
		
		// initialise arrays of lightPosition and lightColour
		location_lightPosition = new int[MAX_LIGHTS];
		location_lightColour = new int[MAX_LIGHTS];
		location_attenuation = new int[MAX_LIGHTS];
		for(int i=0; i<MAX_LIGHTS; i++){
			location_lightPosition[i] = super.getUniformVarLocation("lightPosition[" + i +"]");
			location_lightColour[i] = super.getUniformVarLocation("lightColour[" + i +"]");
			location_attenuation[i] = super.getUniformVarLocation("attenuation[" + i + "]");
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
	 * Loads multiple light objects into the shader
	 * 
	 * @param light
	 */
	public void loadLights( List<Light> lights )
	{
		for(int i=0; i<MAX_LIGHTS; i++){
			if(i<lights.size()){
				// load light position in
				super.loadVector(location_lightPosition[i], lights.get(i).getPosition());
				// load light colour in
				super.loadVector(location_lightColour[i], lights.get(i).getColor());
				// load light attenuation in
				super.loadVector(location_attenuation[i], lights.get(i).getAttenuation());
			}
			else{ // if there are less lights than available light posions
				// load 'empty' lights in
				super.loadVector(location_lightPosition[i], new Vector3f(0,0,0));
				super.loadVector(location_lightColour[i], new Vector3f(0,0,0));
				super.loadVector(location_attenuation[i], new Vector3f(1,0,0));
			}
		}
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
	 * @param useFakeLighting
	 */
	public void loadFakeLightingVariable(boolean useFakeLighting) {
		// Load the variable into the shade
		super.loadBoolean(location_useFakeLighting, useFakeLighting);
	}
	
	/**
	 * Tell the shader to render in wireframe mode
	 * @param wireframe
	 */
	public void loadWireframeVariable(boolean wireframe) {
		// Load the var
		super.loadBoolean(location_wireframe, wireframe);
	}
	
	/**
	 * Add a sky colour to the shader
	 * @param sky
	 */
	public void loadSkyColour(Vector3f sky) {
		// Load the var
		super.loadVector(location_skyColour, sky);
	}
	
}
