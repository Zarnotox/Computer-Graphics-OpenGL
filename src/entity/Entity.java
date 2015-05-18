/**
 * 
 */
package entity;

import math.vector.Vector3f;
import model.TexturedModel;

/**
 * @author Bert
 */
public class Entity {
	
	private TexturedModel model;
	
	private Vector3f position;
	
	private float rotationX;
	
	private float rotationY;
	
	private float rotationZ;
	
	private float scale;
	
	/**
	 * @param model
	 * @param position
	 * @param rotationX
	 * @param rotationY
	 * @param rotationZ
	 * @param scale
	 */
	public Entity( TexturedModel model,
			Vector3f position,
			float rotationX,
			float rotationY,
			float rotationZ,
			float scale )
	{
		this.model = model;
		this.position = position;
		this.rotationX = rotationX;
		this.rotationY = rotationY;
		this.rotationZ = rotationZ;
		this.scale = scale;
	}
	
	/**
	 * @param dx
	 * @param dy
	 * @param dz
	 */
	public void increasePosition( float dx, float dy, float dz )
	{
		this.position.x += dx;
		this.position.y += dy;
		this.position.z += dz;
	}
	
	/**
	 * 
	 * @param dx
	 * @param dy
	 * @param dz
	 */
	public void increaseRotation( float dx, float dy, float dz )
	{
		this.rotationX += dx;
		this.rotationY += dy;
		this.rotationZ += dz;
	}
	
	/**
	 * @return the model
	 */
	public TexturedModel getModel()
	{
		return this.model;
	}
	
	/**
	 * @param model the model to set
	 */
	public void setModel( TexturedModel model )
	{
		this.model = model;
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
	 * @return the rotationX
	 */
	public float getRotationX()
	{
		return this.rotationX;
	}
	
	/**
	 * @param rotationX the rotationX to set
	 */
	public void setRotationX( float rotationX )
	{
		this.rotationX = rotationX;
	}
	
	/**
	 * @return the rotationY
	 */
	public float getRotationY()
	{
		return this.rotationY;
	}
	
	/**
	 * @param rotationY the rotationY to set
	 */
	public void setRotationY( float rotationY )
	{
		this.rotationY = rotationY;
	}
	
	/**
	 * @return the rotationZ
	 */
	public float getRotationZ()
	{
		return this.rotationZ;
	}
	
	/**
	 * @param rotationZ the rotationZ to set
	 */
	public void setRotationZ( float rotationZ )
	{
		this.rotationZ = rotationZ;
	}
	
	/**
	 * @return the scale
	 */
	public float getScale()
	{
		return this.scale;
	}
	
	/**
	 * @param scale the scale to set
	 */
	public void setScale( float scale )
	{
		this.scale = scale;
	}
	
}