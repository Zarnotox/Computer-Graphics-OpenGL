/**
 * 
 */
package loader;

import java.io.InputStream;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import model.Model;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import render.Renderer;

/**
 * @author Bert
 */
public class Loader {
	
	/**
	 * Contains all the ID's of the created VAO's
	 */
	private List<Integer> vaoIDS;
	
	/**
	 * Contains all the ID's of the created VBO's
	 */
	private List<Integer> vboIDS;
	
	/**
	 * Contains all the DI's of the loaded textures
	 */
	private List<Integer> textureIDS;
	
	/**
	 * Constructor
	 */
	public Loader()
	{
		// Init
		vaoIDS = new ArrayList<>();
		vboIDS = new ArrayList<>();
		textureIDS = new ArrayList<>();
	}
	
	/**
	 * Disposes all the acquired resources
	 */
	public void cleanUp()
	{
		// Cleanup all created VAO's
		for (int vao : vaoIDS)
		{
			GL30.glDeleteVertexArrays(vao);
		}
		
		// Cleanup all created VBO's
		for (int vbo : vboIDS)
		{
			GL30.glDeleteVertexArrays(vbo);
		}
		
		// Cleanup textures
		for (int texture : textureIDS)
		{
			GL11.glDeleteTextures(texture);
		}
	}
	
	// ------------------------------------------------------------------------
	
	/**
	 * Generates a model containing the VAO which is used to store the model properties
	 * 
	 * @param positions
	 * @return
	 */
	public Model loadToVAO( float[] positions, float[] textureCoords, int[] indices )
	{
		// Create new VAO
		int vaoID = createVAO();
		
		// Activate/Bind the new VAO
		GL30.glBindVertexArray(vaoID);
		
		// Store the positions inside the VAO, INDEX 0
		storeDataInVAO(Renderer.POSITION_ATTR_INDEX, 3, positions);
		// Store the texture coord mappings inside the VAO, INDEX 1
		storeDataInVAO(Renderer.TEXTURE_COORD_ATTR_INDEX, 2, textureCoords);
		
		// Store the indices into the VAO
		bindIndicesBuffer(indices);
		
		// Unbind the VAO
		unbindVAO();
		
		/* The vertex count is replaced with indices.length */
		// Count the amount of defined vertices
		// int vertexCount = positions.length / 3;
		
		// Generate a new Model
		return new Model(vaoID, indices.length);
	}
	
	/**
	 * Generate a new VAO and return his ID. The VAO needs to be activated (binded) to
	 * start working with it
	 * 
	 * @return
	 */
	private int createVAO()
	{
		// Create an empty VAO
		int vaoID = GL30.glGenVertexArrays();
		// Store the id into the list
		vaoIDS.add(vaoID);
		// Return the ID
		return vaoID;
	}
	
	/**
	 * Releases the active VAO
	 */
	private void unbindVAO()
	{
		// 0 releases the currently bind VAO
		GL30.glBindVertexArray(0);
	}
	
	/**
	 * Stores an object array into the propertieslist of the VAO at the given index
	 * 
	 * @param VAOIndex
	 * @param data
	 */
	private void storeDataInVAO( int VAOIndex, int coordSize, float[] data )
	{
		/*
		 * Create a VBO
		 * The VBO is a buffer that contains the given data
		 * Only VBO's can be stored into a VAO
		 */
		int vboID = GL15.glGenBuffers();
		// Save the VBO ID
		vboIDS.add(vboID);
		
		// Bind the VBO, as an ARRAY BUFFER
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboID);
		// Convert the given data into a FloatBuffer
		FloatBuffer dataBuffer = storeDataInFloatBuffer(data);
		
		/*
		 * Add the buffer tot the VBO
		 * The data is an Array
		 * STATIC_DRAW means the data won't change, just draw it
		 */
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, dataBuffer, GL15.GL_STATIC_DRAW);
		
		/*
		 * Store the VBO inside the VAO
		 * Store the VBO inside the VAO at VAOIndex
		 * Each pair consists of coordSize floats
		 * The data is a FLOAT
		 * The data is NOT NORMALIZED
		 * 0 defines the amount of floats between each vertex
		 * 0 defines the offset from where the data starts
		 */
		GL20.glVertexAttribPointer(VAOIndex, coordSize, GL11.GL_FLOAT, false, 0, 0);
		// Unbind the VBO
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
	}
	
	/**
	 * Put the given indices (Vertex iterate data) into the used VAO
	 * 
	 * @param indices
	 */
	private void bindIndicesBuffer( int[] indices )
	{
		// Create new VBO
		int vboID = GL15.glGenBuffers();
		// Save buffer id
		vboIDS.add(vboID);
		// Bind the created buffer as an element buffer
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vboID);
		// Get the intbuffer from the data
		IntBuffer buffer = storeDataInIntBuffer(indices);
		// Store the buffer data into the created VBO
		GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
	}
	
	// ------------------------------------------------------------------------
	
	/**
	 * Fetches the inputstream of a specific object
	 * 
	 * @param file The path to the object, separated with forward slashes
	 * @return
	 */
	private InputStream loadSource( String file )
	{
		InputStream input = Thread.currentThread()
				.getContextClassLoader()
				.getResourceAsStream(file);
		
		if ( input == null )
		{
			System.err.println("Couldn't load the texture");
		}
		
		return input;
	}
	
	/**
	 * Loads a specified texture into the memory
	 * 
	 * @param fileName
	 * @return
	 */
	public int loadTexture( String fileName )
	{
		int textureID = 0;
		
		// Load the texture
		TextureLoader textLoad = new TextureLoader(this.loadSource(fileName));
		// Get the texture id
		textureID = textLoad.loadTexture();
		// Save the id
		textureIDS.add(textureID);
		
		// Return the texture id
		return textureID;
	}
	
	// ------------------------------------------------------------------------
	
	/**
	 * Transfroms a regulary java floatarray to a full fledged floatbuffer
	 * 
	 * @param data
	 * @return
	 */
	private FloatBuffer storeDataInFloatBuffer( float[] data )
	{
		// Generate a new buffer
		FloatBuffer buffer = BufferUtils.createFloatBuffer(data.length);
		// Put the data
		buffer.put(data);
		// Flipping the buffer says we are done filling it
		buffer.flip();
		// Return the buffer
		return buffer;
	}
	
	/**
	 * Transforms a regularly java intarray to a full fledged floatbuffer
	 * 
	 * @param data
	 * @return
	 */
	private IntBuffer storeDataInIntBuffer( int[] data )
	{
		// Create new buffer
		IntBuffer buffer = BufferUtils.createIntBuffer(data.length);
		// Put the data
		buffer.put(data);
		// Flip the buffer to end it
		buffer.flip();
		// Return the buffer
		return buffer;
	}
	
}
