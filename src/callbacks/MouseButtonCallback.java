/**
 * 
 */
package callbacks;

import glStart.RenderResources;

import java.nio.DoubleBuffer;

import math.vector.Vector2f;
import math.vector.Vector3f;

import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWMouseButtonCallback;

import picking.PickingEngine;
import camera.Camera;

/**
 * @author Bert
 *
 */
public class MouseButtonCallback extends GLFWMouseButtonCallback {
	
	private RenderResources res;
	
	private Vector2f vectorBuffer;
	
	/**
	 * 
	 */
	public MouseButtonCallback(RenderResources res)
	{
		this.res = res;
		vectorBuffer = new Vector2f();
	}
	
	/* (non-Javadoc)
	 * @see org.lwjgl.glfw.GLFWMouseButtonCallback#invoke(long, int, int, int)
	 */
	@Override
	public void invoke( long window, int button, int action, int mods )
	{
		
		// Check which button is pressed
		switch(button) {
		case GLFW.GLFW_MOUSE_BUTTON_LEFT:
			if (action == GLFW.GLFW_PRESS) {
				// The left mouse button has been pressed, do picking
				
				// Get the mouse position on the screen
				getMousePosition(window, vectorBuffer);
				System.out.println(PickingEngine.readPixel((int)vectorBuffer.getX(), (int)vectorBuffer.getY()).getObjectID());
			}
			
			break;
		}
		
	}
	
	/**
	 * Gets the position of the mouse inside the window
	 * The origin is in the top left corner
	 * @param windowHandle
	 * @return
	 */
	private void getMousePosition(long windowHandle, Vector2f buffer) {
		// Create buffers
		DoubleBuffer xPosBuffer = BufferUtils.createDoubleBuffer(1);
		DoubleBuffer yPosBuffer = BufferUtils.createDoubleBuffer(1);
		// Get the mouse position
		GLFW.glfwGetCursorPos(windowHandle, xPosBuffer, yPosBuffer);
		
		float xPos = (float)xPosBuffer.get(0);
		float yPos = (float)yPosBuffer.get(0);
		
		buffer.x = xPos;
		buffer.y = yPos;
		
		return;
	}
	
}
