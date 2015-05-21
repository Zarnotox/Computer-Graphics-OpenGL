/**
 * 
 */
package shader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import loader.Loader;
import math.matrix.Matrix4f;
import math.vector.Vector3f;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

/**
 * @author Bert
 */
public abstract class ShaderProgram {
	
	/**
	 * The reusable buffer to for shipping Matrix4f objects to OpenGL
	 */
	private static FloatBuffer MAT4FBuffer = BufferUtils.createFloatBuffer(Matrix4f.SIZE);
	
	/**
	 * The ID of the compiled Shader
	 */
	private int programID;
	
	/**
	 * The id of the VertexShader
	 */
	private int vertexShaderID;
	
	/**
	 * The id of the FragmentShader
	 */
	private int fragmentShaderID;
	
	/**
	 * Constructor
	 * 
	 * @param vertexFile
	 * @param fragmentFile
	 */
	public ShaderProgram( String vertexFile, String fragmentFile, Loader loader )
	{
		try
		{
			// Compile the shaders
			vertexShaderID = loadShader(vertexFile, GL20.GL_VERTEX_SHADER, loader);
			fragmentShaderID = loadShader(fragmentFile, GL20.GL_FRAGMENT_SHADER, loader);
			
			// Create the shader program
			programID = GL20.glCreateProgram();
			// Attach both shaders to the program
			GL20.glAttachShader(programID, vertexShaderID);
			GL20.glAttachShader(programID, fragmentShaderID);
			
			// Bind the attributes to the shaders
			bindAttributes();
			
			// Use the shader
			GL20.glLinkProgram(programID);
			// Validate the shader program
			GL20.glValidateProgram(programID);
			
			// Load up all the uniform var locations
			getAllUniformVarLocations();
			
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
	}
	
	/**
	 * Method to fetch all the used uniform variables location
	 * These variables are used in the shader code
	 */
	protected abstract void getAllUniformVarLocations();
	
	/**
	 * Returns the id to the memory space where the given uniform variable resides
	 * 
	 * @param uniformName
	 * @return
	 */
	protected int getUniformVarLocation( String uniformName )
	{
		return GL20.glGetUniformLocation(programID, uniformName);
	}
	
	/**
	 * Link VAO attributes to the input vars of the shaders
	 */
	protected abstract void bindAttributes();
	
	/**
	 * Binds a VAO index (VBO object) to in variable in the shaders
	 * 
	 * @param attribute
	 * @param variableName
	 */
	protected void bindAttribute( int attribute, String variableName )
	{
		// Bind the index to a var inside this program
		GL20.glBindAttribLocation(programID, attribute, variableName);
	}
	
	/**
	 * Start the shaderprogram, every draw call will no run this shader
	 */
	public void start()
	{
		GL20.glUseProgram(programID);
	}
	
	/**
	 * Stop the shaderprogram
	 */
	public void stop()
	{
		GL20.glUseProgram(0);
	}
	
	/**
	 * Release all used resources
	 */
	public void cleanUp()
	{
		// Stop just in case
		stop();
		
		// Detach the shaders
		GL20.glDetachShader(programID, vertexShaderID);
		GL20.glDetachShader(programID, fragmentShaderID);
		
		// Delete the shaders
		GL20.glDeleteShader(vertexShaderID);
		GL20.glDeleteShader(fragmentShaderID);
		
		// Remove the program
		GL20.glDeleteProgram(programID);
	}
	
	/**
	 * Load a shader from the given filename and compiles it
	 * 
	 * @param file
	 * @param type
	 * @return
	 * @throws IOException
	 */
	private static int loadShader( String file, int type, Loader loader )
			throws IOException
	{
		// Read the file
		InputStreamReader inReader = new InputStreamReader(loader.loadSource(file));
		
		// Create a buffered reader for the file content
		BufferedReader reader = new BufferedReader(inReader);
		List<String> lines = new ArrayList<>();
		
		String line;
		while((line = reader.readLine()) != null) {
			lines.add(line);
		}
			
		// Concat everything
		String collectedLines = String.join("\n", lines);
		
		// Create a shader program
		int shaderID = GL20.glCreateShader(type);
		// Set the shader source
		GL20.glShaderSource(shaderID, collectedLines);
		// Compile the shader
		GL20.glCompileShader(shaderID);
		// Check the compile status
		if ( GL20.glGetShaderi(shaderID, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE )
		{
			// Dump shader debug info
			System.out.println(GL20.glGetShaderInfoLog(shaderID, 500));
			System.err.println("Failed to compile the shader");
			System.exit(-1);
		}
		
		// Return the shader id
		return shaderID;
	}
	
	/**
	 * Load a float into a uniform variable
	 * 
	 * @param location
	 * @param value
	 */
	protected void loadFloat( int location, float value )
	{
		GL20.glUniform1f(location, value);
	}
	
	/**
	 * Load a vector into a uniform variable
	 * 
	 * @param location
	 * @param vector
	 */
	protected void loadVector( int location, Vector3f vector )
	{
		GL20.glUniform3f(location, vector.getX(), vector.getY(), vector.getZ());
	}
	
	/**
	 * Load a boolean into a uniform variable
	 * 
	 * @param location
	 * @param value
	 */
	protected void loadBoolean( int location, boolean value )
	{
		float loadValue = 0;
		
		if ( value )
		{
			loadValue = 1;
		}
		
		GL20.glUniform1f(location, loadValue);
	}
	
	/**
	 * Load a 4x4 Matrix into a uniform value
	 * 
	 * @param location
	 * @param matrix
	 */
	protected void loadMatrix( int location, Matrix4f matrix )
	{
		// Store the matrix into the buffer
		matrix.store(MAT4FBuffer);
		// Flip the buffer
		MAT4FBuffer.flip();
		// Load the buffer into OpenGL
		/*
		 * The id of the memory location
		 * False means the buffer was filled in Column Major Order
		 * The buffer itself
		 */
		GL20.glUniformMatrix4fv(location, false, MAT4FBuffer);
	}
	
}
