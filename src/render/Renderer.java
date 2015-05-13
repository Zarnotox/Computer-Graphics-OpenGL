/**
 * 
 */
package render;

import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.glClear;

import org.lwjgl.opengl.GL11;
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
		GL11.glClearColor(0, 1, 0, 1);
	}
	
	/**
	 * Render the given model to the scene
	 * 
	 * @param model
	 */
	public void render( Model model )
	{
		/* Bind all resources */
		// Bind the VAO attached to this model
		GL30.glBindVertexArray(model.getVoaID());
		// Enable the list with INDEX 0 from the VAO
		GL20.glEnableVertexAttribArray(POSITION_ATTR_INDEX);
		
		// Tell OpenGL to draw the vertices defined inside the activated VBO's
		GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, model.getVertexCount());
		
		/* Unbind all used resources */
		// Unbind the position VBO
		GL20.glDisableVertexAttribArray(0);
		// Unbind the chosen VAO
		GL30.glBindVertexArray(0);
	}
	
}
