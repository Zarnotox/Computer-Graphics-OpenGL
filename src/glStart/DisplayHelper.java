/**
 * 
 */
package glStart;

import static org.lwjgl.glfw.GLFW.glfwGetWindowSize;

import java.awt.Dimension;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLUtil;
import org.lwjgl.Sys;
import org.lwjgl.glfw.GLFW;

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
	 * The last time 
	 */
	private static long lastTimeMillis;
	
	/**
	 * The delta between the previous calculated time and the time before that calculation.
	 * The time is in milliseconds
	 */
	private static int lastDeltaTime;
	
	/**
	 * @param window
	 */
	public DisplayHelper( long window )
	{
		this.windowHandle = window;
		
		// Calculate the time
		calculateCurrentTime();
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
	
	/**
	 * Calculate the time difference between the last call and the current call
	 */
	public static void calculateCurrentTime() {
		// Fetch current time in milliseconds
		long current = (long) (GLFW.glfwGetTime() * 1000);
		// Calculate time differences
		lastDeltaTime = (int) (current - lastTimeMillis);
		// Set the previous time
		lastTimeMillis = current;
	}
	
	/**
	 * 
	 * @return
	 */
	public static double getFrameTimeInSeconds() {
		return lastDeltaTime/1000d;
	}
	
	
	
}
