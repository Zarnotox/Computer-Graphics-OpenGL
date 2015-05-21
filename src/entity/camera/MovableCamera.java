/**
 * 
 */
package entity.camera;

import math.vector.Vector3f;

/**
 * @author Bert
 *
 */
public class MovableCamera extends Camera {
	
	/**
	 * @param position
	 * @param pitch
	 * @param yaw
	 * @param roll
	 */
	public MovableCamera( Vector3f position, float pitch, float yaw, float roll )
	{
		super(position, pitch, yaw, roll);
	}

	/* (non-Javadoc)
	 * @see camera.Camera#move(float, float, float)
	 */
	@Override
	public void move( float dx, float dy, float dz )
	{
		super.position.x += dx;
		super.position.y += dy;
		super.position.z += dz;		
	}

	/* (non-Javadoc)
	 * @see camera.Camera#moveHorizontal(float)
	 */
	@Override
	public void moveHorizontal( float dx )
	{
		super.position.x += dx;		
	}

	/* (non-Javadoc)
	 * @see camera.Camera#moveVertical(float)
	 */
	@Override
	public void moveVertical( float dy )
	{
		super.position.y += dy;		
	}

	/* (non-Javadoc)
	 * @see camera.Camera#moveOut(float)
	 */
	@Override
	public void moveOut( float dz )
	{
		super.position.z += dz;		
	}
	
	
	
}
