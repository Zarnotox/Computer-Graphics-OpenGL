/**
 * 
 */
package callbacks;

import glStart.RenderResources;

import org.lwjgl.glfw.GLFWWindowSizeCallback;
import org.lwjgl.opengl.GL11;

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
	public ResizeHandler(RenderResources res)
	{
		this.res = res;
	}
	
	/* (non-Javadoc)
	 * @see org.lwjgl.glfw.GLFWWindowSizeCallback#invoke(long, int, int)
	 */
	@Override
	public void invoke( long window, int width, int height )
	{
		// TODO loop camera's and increase viewangle
		
		
		// Update the OpenGL viewport
		GL11.glViewport(0, 0, width, height);		
	}
	
}
