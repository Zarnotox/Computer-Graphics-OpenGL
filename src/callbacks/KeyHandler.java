/**
 * 
 */
package callbacks;

import glStart.RenderResources;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWKeyCallback;
import camera.Camera;
import render.Render;

/**
 * @author Bert
 */
public class KeyHandler extends GLFWKeyCallback {
	
	/**
	 * The amount of pixels the camera moves into a certain direction for each callback
	 */
	public final static float CAMERA_MOVEMENT_STEPSIZE = 0.5f;
	
	/**
	 * The resources for the rendering
	 */
	private RenderResources res;
	
	/**
	 * The flag for wireframe rendering
	 */
	private boolean wireframeModeEnabled;
	
	/**
	 * The flag for wireframe rendering
	 */
	private boolean flatModeEnabled;
	
	/**
	 * Constructor
	 * 
	 * @param res
	 */
	public KeyHandler( RenderResources res )
	{
		this.res = res;
		wireframeModeEnabled = false;
		flatModeEnabled = false;
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
			// System.out.println("Key: " + key);
			// System.out.println("Modifiers: " + Integer.toBinaryString(mods));
			
			// Get the active camera
			Camera activeCam = res.getActiveCamera();
			
			switch (key) {
			/* CAMERA MOVEMENT */
			case GLFW.GLFW_KEY_KP_8:
				// Move up
				activeCam.moveUp(CAMERA_MOVEMENT_STEPSIZE);
				break;
			case GLFW.GLFW_KEY_KP_2:
				// Move down
				activeCam.moveUp(-CAMERA_MOVEMENT_STEPSIZE);
				break;			
			case GLFW.GLFW_KEY_LEFT:
				// Rotate left around targetPoint
				activeCam.updateHorAngle(-CAMERA_MOVEMENT_STEPSIZE*10);
				break;
			case GLFW.GLFW_KEY_A:
				// Move camera to the left
				activeCam.moveRight(-CAMERA_MOVEMENT_STEPSIZE);
				break;
			case GLFW.GLFW_KEY_RIGHT:
				// Rotate right around targetPoint
				activeCam.updateHorAngle(CAMERA_MOVEMENT_STEPSIZE*10);
				break;
			case GLFW.GLFW_KEY_D:
				// Move camera to the right
				activeCam.moveRight(CAMERA_MOVEMENT_STEPSIZE);
				break;
			case GLFW.GLFW_KEY_DOWN:
				// Rotate down around targetPoint
				activeCam.updateVerAngle(CAMERA_MOVEMENT_STEPSIZE*10);
				break;
			case GLFW.GLFW_KEY_UP:
				// Rotate up around targetpoint
				activeCam.updateVerAngle(-CAMERA_MOVEMENT_STEPSIZE*10);
				break;
			case GLFW.GLFW_KEY_W:
				// Move forward
				activeCam.moveForward(CAMERA_MOVEMENT_STEPSIZE);
				break;
			case GLFW.GLFW_KEY_S:
				// Move backward
				activeCam.moveForward(-CAMERA_MOVEMENT_STEPSIZE);
				break;
			case GLFW.GLFW_KEY_F1:
				// Previous camera
				res.previousCamera();
				break;
			case GLFW.GLFW_KEY_F2:
				// Next camera
				res.nextCamera();
				break;
			case GLFW.GLFW_KEY_F3:
				// Switch flag
				wireframeModeEnabled = !wireframeModeEnabled;
				
				// Toggle wireframe mode
				if ( wireframeModeEnabled == true )
				{
					// Set the shader to use wireframe
					Render.enableWireFrame(true);
				}
				else
				{
					Render.disableWireFrame();
				}
				
				break;			
			case GLFW.GLFW_KEY_F4:
				// Switch flag
				wireframeModeEnabled = !wireframeModeEnabled;
				
				// Toggle wireframe mode
				if ( wireframeModeEnabled == true )
				{
					// Don't set the shaders to wireframe
					Render.enableWireFrame(false);
				}
				else
				{
					Render.disableWireFrame();
				}
				
				break;
			case GLFW.GLFW_KEY_F5:
				// Switch flag
				flatModeEnabled = !flatModeEnabled;
				
				// Toggle wireframe mode
				if ( flatModeEnabled == true )
				{
					Render.enableFlatShading();
				}
				else
				{
					Render.enableSmoothShading();
				}
				
				break;			
			}
		}
	}
	
}
