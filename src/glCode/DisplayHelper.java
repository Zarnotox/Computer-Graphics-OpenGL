/**
 * 
 */
package glCode;

import static org.lwjgl.glfw.GLFW.glfwGetWindowSize;

import java.awt.Dimension;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;

/**
 * @author Bert
 *
 */
public class DisplayHelper {
	
	/**
	 * The handle id for the created window
	 */
	private long windowHandle;

	/**
	 * 
	 * @param window
	 */
	public DisplayHelper( long window )
	{
		this.windowHandle = window;
	}
	
	/**
	 * Returns the handle of the associated window
	 * @return
	 */
	public long getHandle() {
		return this.windowHandle;
	}
	
	/**
	 * Fetch the dimensions of the associated window
	 * @return
	 */
	public Dimension getWindowDimensions() {
		/* CHECK THE WINDOW DIMENSIONS */
		IntBuffer wBuffer = BufferUtils.createIntBuffer(1);
		IntBuffer hBuffer = BufferUtils.createIntBuffer(1);
		// Fetch the window dimensions
		glfwGetWindowSize(windowHandle, wBuffer, hBuffer);
		// Assign the real window width and height
		int windowWidth = wBuffer.get(0);
		int windowHeight = hBuffer.get(0);
		
		return new Dimension(windowWidth, windowHeight);
	}
	
}
