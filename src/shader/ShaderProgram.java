/**
 * 
 */
package shader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

/**
 * @author Bert
 */
public abstract class ShaderProgram {
	
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
	public ShaderProgram( String vertexFile, String fragmentFile )
	{
		try
		{
			// Compile the shaders
			vertexShaderID = loadShader(vertexFile, GL20.GL_VERTEX_SHADER);
			fragmentShaderID = loadShader(fragmentFile, GL20.GL_FRAGMENT_SHADER);
			
			// Create the shader program
			programID = GL20.glCreateProgram();
			// Attach both shaders to the program
			GL20.glAttachShader(programID, vertexShaderID);
			GL20.glAttachShader(programID, fragmentShaderID);
			// Use the shader
			GL20.glLinkProgram(programID);
			// Validate the shader program
			GL20.glValidateProgram(programID);
			
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
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
	private static int loadShader( String file, int type )
			throws IOException
	{
		// Load the file content
		List<String> lines = Files.readAllLines(Paths.get(file));
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
	
}
