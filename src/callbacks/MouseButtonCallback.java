/**
 * 
 */
package callbacks;

import entity.Entity;
import entity.model.TexturedModel;
import glStart.RenderResources;

import java.nio.DoubleBuffer;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import math.vector.Vector2f;
import math.vector.Vector3f;

import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWMouseButtonCallback;

import picking.PickedPixel;
import picking.PickingEngine;
import camera.Camera;

/**
 * @author Bert
 */
public class MouseButtonCallback extends GLFWMouseButtonCallback {
	
	private RenderResources res;
	
	private Vector2f vectorBuffer;
	
	/**
	 * 
	 */
	public MouseButtonCallback( RenderResources res )
	{
		this.res = res;
		vectorBuffer = new Vector2f();
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.lwjgl.glfw.GLFWMouseButtonCallback#invoke(long, int, int, int)
	 */
	@Override
	public void invoke( long window, int button, int action, int mods )
	{
		
		// Check which button is pressed
		switch (button) {
		case GLFW.GLFW_MOUSE_BUTTON_LEFT:
			if ( action == GLFW.GLFW_PRESS )
			{
				// The left mouse button has been pressed, do picking
				
				// Get the mouse position on the screen
				getMousePosition(window, vectorBuffer);
				PickedPixel pick = PickingEngine.readPixel((int) vectorBuffer.getX(),
						(int) vectorBuffer.getY());
				
				///System.out.println("Clicked on entity with ID: " + pick.getObjectID());
				
				// Fetch the entity with the gotten ID
				Entity e = getEntityByID(pick);
				
				// Do action on clicked entity
				if ( e != null )
				{
					e.doAction();
				}
				
			}
			
			break;
		}
		
	}
	
	/**
	 * Gets the position of the mouse inside the window
	 * The origin is in the top left corner
	 * 
	 * @param windowHandle
	 * @return
	 */
	private void getMousePosition( long windowHandle, Vector2f buffer )
	{
		// Create buffers
		DoubleBuffer xPosBuffer = BufferUtils.createDoubleBuffer(1);
		DoubleBuffer yPosBuffer = BufferUtils.createDoubleBuffer(1);
		// Get the mouse position
		GLFW.glfwGetCursorPos(windowHandle, xPosBuffer, yPosBuffer);
		
		float xPos = (float) xPosBuffer.get(0);
		float yPos = (float) yPosBuffer.get(0);
		
		buffer.x = xPos;
		buffer.y = yPos;
		
		return;
	}
	
	/**
	 * Reads the previously used entitymap to find the entity with the given id
	 * 
	 * @param pick
	 * @return
	 */
	private Entity getEntityByID( PickedPixel pick )
	{
		Entity eTarget = null;
		
		// Get the object id
		int objID = pick.getObjectID();
		// If we got 0 back from the shader, nothing was clicked
		if ( objID == 0 )
		{
			return null;
		}
		// Decrease the id counter, because the lists are 0-indexed
		objID--;
		
		// Get the previously used entity map
		Map<TexturedModel, List<Entity>> entityMap = res.getLastUsedEntityBuffer();
		// Fetch all entities
		Collection<List<Entity>> valCollection = entityMap.values();
		
		// Iterate the valuesCollection
		Iterator<List<Entity>> it = valCollection.iterator();
		// Loop all lists
		// Break when the objID gets below 0
		while (it.hasNext() && objID >= 0)
		{
			List<Entity> l = it.next();
			// Get the size of this list
			int size = l.size();
			
			/* DEBUG */
			//System.out.println("Exploring list with size: " + size);
			
			// Check if the ID is bigger than the size of this list
			if ( objID < size )
			{
				// The object is inside this list
				eTarget = l.get(objID);
				// Break because we got our target
				break;
			}
			else
			{
				// The obj is in the next list
				// Decrease the id with the size of this list
				objID -= size;
			}
			
		}
		
		return eTarget;
	}
	
}
