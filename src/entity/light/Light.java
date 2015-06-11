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
	// The backup for the current color
	private Vector3f colorBackup;
	// Flag indicating this light is on
	private boolean on;

	/**
	 * @param position
	 * @param color
	 */
	public Light( Vector3f position, Vector3f color )
	{
		this.position = position;
		this.color = color;
		this.colorBackup = color;
		this.attenuation = new Vector3f(1,0,0);
		
		this.on = true;
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
		this.colorBackup = color;
		this.attenuation = attenuation;
		
		this.on = true;
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
		this.colorBackup = color;
	}
	
	/**
	 * Toggle this light on/off
	 */
	public void toggle() {
		// Toggle
		this.on = !this.on;
		
		if(this.on == true) {
			this.color = this.colorBackup;
		} else {
			this.color = new Vector3f(0,0,0);
		}
	}
	
		
	
	
}
