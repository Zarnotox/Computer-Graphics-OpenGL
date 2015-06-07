/**
 * 
 */
package camera;

import math.vector.Vector3f;

/**
 * @author Bert
 *
 */
public class MovableCamera extends Camera {
	
	/**
	 * The vector object used for calculations
	 */
	private Vector3f vecBuffer;
	
	/**
	 * @param position
	 * @param pitch
	 * @param yaw
	 * @param roll
	 */
	public MovableCamera( Vector3f position, float pitch, float yaw, float roll )
	{
		super(position, pitch, yaw, roll);
		
		vecBuffer = new Vector3f();
	}

	/* (non-Javadoc)
	 * @see camera.Camera#move(float, float, float)
	 */
	@Override
	public void moveRelative( float dx, float dy, float dz )
	{
		super.updateTargetPosition(dx, dy, dz);
	}

	/* (non-Javadoc)
	 * @see camera.Camera#moveHorizontal(float)
	 */

	/*public void moveForward( float dx )
	{
		// Get the vector between the target and the camera
		super.getCamDirectionVector(vecBuffer);
		
		 DEBUG 
		System.out.println("Direction vector: " + vecBuffer.toString());
		
		// Normalize the vector and scale it with the delta value
		vecBuffer.normalise().scale(dx);
		
		// Add the move vector to the current position
		super.updatePosition(vecBuffer.x, vecBuffer.y, vecBuffer.z);
	}*/

	/* (non-Javadoc)
	 * @see camera.Camera#moveVertical(float)
	 */
	
	/*public void moveLeft( float dy )
	{
		// Fetch the 
	}
*/
	/* (non-Javadoc)
	 * @see camera.Camera#rotateHorizontalClockwise(float)
	 */
	
	/*public void rotateHorizontalClockwise( float dx )
	{
		super.updateHorAngle(dx);
		
	}*/

	/* (non-Javadoc)
	 * @see camera.Camera#rotateVerticalClockwise(float)
	 */
	
	/*public void rotateVerticalClockwise( float dx )
	{
		super.updateVerAngle(dx);
		
	}*/
	
	
	
}
