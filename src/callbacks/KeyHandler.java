/**
 * 
 */
package callbacks;

import glCode.RenderResources;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWKeyCallback;

import camera.Camera;

/**
 * @author Bert
 */
public class KeyHandler extends GLFWKeyCallback {
	
	/**
	 * The amount of pixels the camera moves into a certain direction for each callback
	 */
	public final static float CAMERA_MOVEMENT_STEPSIZE = 0.1f;
	
	/**
	 * The resources for the rendering
	 */
	private RenderResources res;
	
	/**
	 * Constructor
	 * 
	 * @param res
	 */
	public KeyHandler( RenderResources res )
	{
		this.res = res;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.lwjgl.glfw.GLFWKeyCallback#invoke(long, int, int, int, int)
	 */
	@Override
	public void invoke( long window, int key, int scancode, int action, int mods )
	{
		if ( action == GLFW.GLFW_REPEAT || action == GLFW.GLFW_PRESS )
		{
			/* DEBUG */
			// System.out.println("Window: " + window);
			System.out.println("Key: " + key);
			// System.out.println("Modifiers: " + Integer.toBinaryString(mods));
			
			// Get the active camera
			Camera activeCam = res.getActiveCamera();
			
			switch (key) {
			/* CAMERA MOVEMENT */
			case GLFW.GLFW_KEY_LEFT:
			case GLFW.GLFW_KEY_A:
				// Move camera to the left
				activeCam.moveHorizontal(-CAMERA_MOVEMENT_STEPSIZE);
				break;
			case GLFW.GLFW_KEY_RIGHT:
			case GLFW.GLFW_KEY_D:
				// Move camera to the right
				activeCam.moveHorizontal(CAMERA_MOVEMENT_STEPSIZE);
				break;
			case GLFW.GLFW_KEY_DOWN:
				// Move camera down
				activeCam.moveVertical(-CAMERA_MOVEMENT_STEPSIZE);
				break;
			case GLFW.GLFW_KEY_UP:
				// Move camera up
				activeCam.moveVertical(CAMERA_MOVEMENT_STEPSIZE);
				break;
			case GLFW.GLFW_KEY_W:
				// Move backward along the z axis
				activeCam.moveOut(-CAMERA_MOVEMENT_STEPSIZE);
				break;
			case GLFW.GLFW_KEY_S:
				// Move forward along the z axis
				activeCam.moveOut(CAMERA_MOVEMENT_STEPSIZE);
				break;
			}
		}
	}
	
}
