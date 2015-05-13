/**
 * 
 */
package loader;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import render.Model;
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
	 * Constructor
	 */
	public Loader()
	{
		// Init
		vaoIDS = new ArrayList<>();
		vboIDS = new ArrayList<>();
	}
	
	/**
	 * Disposes all the acquired resources
	 */
	public void cleanUp() {
		// Cleanup all created VAO's
		for(int vao: vaoIDS) {
			GL30.glDeleteVertexArrays(vao);
		}
		
		// Cleanup all created VBO's
		for(int vbo: vboIDS) {
			GL30.glDeleteVertexArrays(vbo);
		}
	}
	
	/**
	 * Generates a model containing the VAO which is used to store the model properties
	 * 
	 * @param positions
	 * @return
	 */
	public Model loadToVAO( float[] positions )
	{
		// Create new VAO
		int vaoID = createVAO();
		
		// Activate/Bind the new VAO
		GL30.glBindVertexArray(vaoID);
		
		// Store the positions inside the VAO, INDEX 0
		storeDataInVAO(Renderer.POSITION_ATTR_INDEX, positions);
		
		// Unbind the VAO
		unbindVAO();
		
		// Count the amount of defined vertices
		int vertexCount = positions.length / 3;
		
		// Generate a new Model
		return new Model(vaoID, vertexCount);
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
	 * Stores an object array into the propertieslist of the VAO at the given index
	 * 
	 * @param VAOIndex
	 * @param data
	 */
	private void storeDataInVAO( int VAOIndex, float[] data )
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
		
		/* Add the buffer tot the VBO
		 * 
		 * The data is an Array
		 * STATIC_DRAW means the data won't change, just draw it
		 */
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, dataBuffer, GL15.GL_STATIC_DRAW);
		
		/* Store the VBO inside the VAO
		 * 
		 * Store the VBO inside the VAO at VAOIndex
		 * Each vertex consists of 3 floats
		 * The data is a FLOAT
		 * The data is NOT NORMALIZED
		 * 0 defines the amount of floats between each vertex
		 * 0 defines the offset from where the data starts
		 */
		GL20.glVertexAttribPointer(VAOIndex, 3, GL11.GL_FLOAT, false, 0, 0);
		// Unbind the VBO 
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
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
	 * Transfroms a regulary java floatarray to a full fledged floatbuffer
	 * @param data
	 * @return
	 */
	private FloatBuffer storeDataInFloatBuffer(float[] data) {
		// Generate a new buffer
		FloatBuffer buffer = BufferUtils.createFloatBuffer(data.length);
		// Put the data
		buffer.put(data);
		// Flipping the buffer says we are done filling it
		buffer.flip();
		// Return the buffer
		return buffer;
	}
	
}
