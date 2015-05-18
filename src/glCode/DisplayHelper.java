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
 */
public class DisplayHelper {
	/**
	 * The buffer to store the raw width data
	 */
	private final static IntBuffer WIDTHBUFFER = BufferUtils.createIntBuffer(1);
	
	/**
	 * The buffer to store the raw height data
	 */
	private final static IntBuffer HEIGHTBUFFER = BufferUtils.createIntBuffer(1);
	
	/**
	 * The handle id for the created window
	 */
	private long windowHandle;
	
	/**
	 * @param window
	 */
	public DisplayHelper( long window )
	{
		this.windowHandle = window;
	}
	
	/**
	 * Returns the handle of the associated window
	 * 
	 * @return
	 */
	public long getHandle()
	{
		return this.windowHandle;
	}
	
	/**
	 * Fetch the dimensions of the associated window
	 * 
	 * @return
	 */
	public Dimension getWindowDimensions()
	{
		/* CHECK THE WINDOW DIMENSIONS */
		// Fetch the window dimensions
		glfwGetWindowSize(windowHandle, WIDTHBUFFER, HEIGHTBUFFER);
		// Assign the real window width and height
		int windowWidth = WIDTHBUFFER.get(0);
		int windowHeight = HEIGHTBUFFER.get(0);
		// Return the dimensions as a new object
		return new Dimension(windowWidth, windowHeight);
	}
	
}
