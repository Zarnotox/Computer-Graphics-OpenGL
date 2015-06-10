/**
 * 
 */
package entity.light;

import math.vector.Vector3f;

/**
 * @author Bert
 *
 */
public class Light {
	
	private Vector3f position;
	private Vector3f color;
	private Vector3f attenuation;

	/**
	 * @param position
	 * @param color
	 */
	public Light( Vector3f position, Vector3f color )
	{
		this.position = position;
		this.color = color;
		this.attenuation = new Vector3f(1,0,0);
	}
	
	/**
	 * @param position
	 * @param color
	 * @param attenuation
	 */
	public Light( Vector3f position, Vector3f color , Vector3f attenuation)
	{
		this.position = position;
		this.color = color;
		this.attenuation = attenuation;
	}
	
	/**
	 * @return the attenuation
	 */
	public Vector3f getAttenuation()
	{
		return this.attenuation;
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
		
		//System.out.println("Updated position");
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
