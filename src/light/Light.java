/**
 * 
 */
package light;

import math.vector.Vector3f;

/**
 * @author Bert
 *
 */
public class Light {
	
	private Vector3f position;
	
	private Vector3f color;

	/**
	 * @param position
	 * @param color
	 */
	public Light( Vector3f position, Vector3f color )
	{
		this.position = position;
		this.color = color;
	}

	/**
	 * @return the position
	 */
	public Vector3f getPosition()
	{
		return this.position;
	}

	/**
	 * @param position the position to set
	 */
	public void setPosition( Vector3f position )
	{
		this.position = position;
	}

	/**
	 * @return the color
	 */
	public Vector3f getColor()
	{
		return this.color;
	}

	/**
	 * @param color the color to set
	 */
	public void setColor( Vector3f color )
	{
		this.color = color;
	}
	
		
	
	
}
