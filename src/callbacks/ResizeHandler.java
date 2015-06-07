/**
 * 
 */
package callbacks;

import java.awt.Dimension;

import glStart.DisplayHelper;
import glStart.RenderResources;

import org.lwjgl.glfw.GLFWWindowSizeCallback;
import org.lwjgl.opengl.GL11;

import camera.Camera;

/**
 * @author Bert
 *
 */
public class ResizeHandler extends GLFWWindowSizeCallback {
	
	/**
	 * Object that holds all the render resources
	 */
	private RenderResources res;
	
	/**
	 * The last recorded width
	 */
	private int previousWidth;
	
	/**
	 * The last recorded height
	 */
	private int previousHeight;
	
	/**
	 * 
	 */
	public ResizeHandler(RenderResources res, DisplayHelper windowHelper)
	{
		this.res = res;
		
		Dimension dim = windowHelper.getWindowDimensions();
		previousHeight = (int) dim.getHeight();
		previousWidth = (int) dim.getWidth();
		
	}
	
	/* (non-Javadoc)
	 * @see org.lwjgl.glfw.GLFWWindowSizeCallback#invoke(long, int, int)
	 */
	@Override
	public void invoke( long window, int width, int height )
	{
		// Check how much we have to move forward/backward
		int dWidth = previousWidth - width;
		int dHeight = previousHeight - height;
		
		// Assign previous values
		previousWidth = width;
		previousHeight = height;
		
		// TODO loop camera's and increase viewangle
		for(Camera cam: this.res.getCameraList()) {
			cam.moveForward((dWidth+dHeight)/100f);
		}
		
		// Update the OpenGL viewport
		GL11.glViewport(0, 0, width, height);		
	}
	
}
