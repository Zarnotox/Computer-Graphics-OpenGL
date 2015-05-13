/**
 * 
 */
package render;

import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.glClear;
import model.Model;
import model.TexturedModel;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

/**
 * @author Bert
 */
public class Renderer {
	
	/**
	 * This variable declares that the position attribute VBO has to be stored into the
	 * VAO at index 0
	 */
	public final static int POSITION_ATTR_INDEX = 0;
	
	/**
	 * This variable declares that texture coordinate mappings will be stored inside the
	 * VAO at index 1
	 */
	public final static int TEXTURE_COORD_ATTR_INDEX = 1;
	
	/**
	 * 
	 */
	public Renderer()
	{
	}
	
	/**
	 * Prepares the OpenGL context
	 */
	public void prepare()
	{
		// Clear the color and depth buffers
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		
		// Clear the scene
		GL11.glClearColor(1, 0, 0, 1);
	}
	
	/**
	 * Render the given model to the scene
	 * 
	 * @param model
	 */
	public void render( TexturedModel model )
	{
		/* Bind all resources */
		// Bind the VAO attached to this model
		GL30.glBindVertexArray(model.getVoaID());
		// Enable the list with INDEX 0 from the VAO
		GL20.glEnableVertexAttribArray(POSITION_ATTR_INDEX);
		// Enable texture coords
		GL20.glEnableVertexAttribArray(TEXTURE_COORD_ATTR_INDEX);
		
		// Activate the first texture bank, the 2DSampler (Shader) uses this one
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		// Set the active texture for the following draw
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, model.getTexture().getTextureID());
		
		/* The drawArray function is replaced with the drawElements function */
		// Tell OpenGL to draw the vertices defined inside the activated VBO's
		// GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, model.getVertexCount());
		
		/*
		 * Draw the model to the scene
		 * Draw Triangles
		 * Draw amount of vertices
		 * We are referring to the indices, so look for Unsigned Ints
		 * 0 offset
		 */
		GL11.glDrawElements(GL11.GL_TRIANGLES, model.getVertexCount(),
				GL11.GL_UNSIGNED_INT, 0);
		
		/* Unbind all used resources */
		// Unbind the position VBO
		GL20.glDisableVertexAttribArray(POSITION_ATTR_INDEX);
		// Unbind texture coords
		GL20.glDisableVertexAttribArray(TEXTURE_COORD_ATTR_INDEX);
		
		// Unbind the chosen VAO
		GL30.glBindVertexArray(0);
	}
	
}
