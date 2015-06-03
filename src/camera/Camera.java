/**
 * 
 */
package camera;

import math.vector.Vector3f;

/**
 * @author Bert
 */
public abstract class Camera {
	
	/**
	 * The position of the camera
	 */
	protected Vector3f position;
	
	/**
	 * The hardcoded original position
	 */
	private Vector3f originalPosition;
	
	/**
	 * Rotation around the X-axis
	 */
	private float pitch;
	
	/**
	 * Rotation around the Y-axis
	 */
	private float yaw;
	
	/**
	 * Rotation around the Z-axis
	 */
	private float roll;
	
	/**
	 * @param position
	 * @param pitch Rotation around the X-axis
	 * @param yaw Rotation around the Y-axis
	 * @param roll Rotation around the Z-axis
	 */
	public Camera( Vector3f position, float pitch, float yaw, float roll )
	{
		this.position = position;
		this.originalPosition = position;
		this.pitch = pitch;
		this.yaw = yaw;
		this.roll = roll;
	}
	
	/**
	 * @return the position
	 */
	public final Vector3f getPosition()
	{
		return this.position;
	}
	
	/**
	 * @return the pitch
	 */
	public final float getPitch()
	{
		return this.pitch;
	}
	
	/**
	 * @return the yaw
	 */
	public final float getYaw()
	{
		return this.yaw;
	}
	
	/**
	 * @return the roll
	 */
	public final float getRoll()
	{
		return this.roll;
	}
	
	/**
	 * Resets the original position of the camera
	 */
	public final void reset()
	{
		this.position = this.originalPosition;
	}
	
	/**
	 * Moves the camera in a certain position
	 * 
	 * @param dx
	 * @param dy
	 * @param dz
	 */
	public abstract void move( float dx, float dy, float dz );
	
	/**
	 * Move along the X-axis
	 * 
	 * @param dx
	 */
	public abstract void moveHorizontal( float dx );
	
	/**
	 * Move along the Y-axis
	 * 
	 * @param dy
	 */
	public abstract void moveVertical( float dy );
	
	/**
	 * Move along the Z-axis
	 * 
	 * @param dz
	 */
	public abstract void moveOut( float dz );
	
}
