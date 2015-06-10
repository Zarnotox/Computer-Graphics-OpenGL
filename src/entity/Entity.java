/**
 * 
 */
package entity;

import callbacks.EntityActionCallback;
import entity.model.TexturedModel;
import math.vector.Vector3f;

/**
 * @author Bert
 */
public class Entity {
	
	/**
	 * The model, including texture
	 */
	private TexturedModel model;
	
	/**
	 * The position of this entity
	 */
	private Vector3f position;
	
	/**
	 * The rotation over the X-Axis
	 */
	private float rotationX;
	
	/**
	 * The rotation over the Y-Axis
	 */
	private float rotationY;
	
	/**
	 * The rotation over the Z-Axis
	 */
	private float rotationZ;
	
	/**
	 * The scale of this entity
	 */
	private float scale;
	
	/**
	 * The index of the texture for this entity
	 */
	private int textureIndex;
	
	/**
	 * The callback that handles actions on this entity
	 */
	private EntityActionCallback callback;
	
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
		// Default to the first texture
		this.textureIndex = 0;
		
		this.model = model;
		this.position = position;
		this.rotationX = rotationX;
		this.rotationY = rotationY;
		this.rotationZ = rotationZ;
		this.scale = scale;
	}
	
	/**
	 * @param model
	 * @param textureIndex
	 * @param position
	 * @param rotationX
	 * @param rotationY
	 * @param rotationZ
	 * @param scale
	 */
	public Entity( TexturedModel model,
			int textureIndex,
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
		
		setTextureIndex(textureIndex);
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
	
	/**
	 * Sets the callback on this entity.
	 * The callback contains the actions when this entity is triggered.
	 * The callback will overwrite the already present callback
	 * 
	 * @param callback
	 */
	public void setCallback( EntityActionCallback callback )
	{
		this.callback = callback;
	}
	
	/**
	 * Triggers the operations defined in the EntityCallBack function set into this
	 * entity.
	 * Only executes the callback once, if set.
	 */
	public void doAction()
	{
		if ( this.callback != null )
		{
			this.callback.doAction(this);
		}
	}
	
	/**
	 * @return the textureIndex
	 */
	public int getTextureIndex()
	{
		return this.textureIndex;
	}
	
	/**
	 * @param textureIndex the textureIndex to set
	 */
	public void setTextureIndex( int textureIndex )
	{
		int maxTextures = model.getTexture().getNumberOfTextureRows();
		maxTextures = (int) Math.pow(maxTextures, maxTextures) - 1;
		
		// Check if the index is within 0 and the maximum amount
		if ( textureIndex < 0 )
		{
			textureIndex = 0;
		}
		else if ( textureIndex > maxTextures )
		{
			textureIndex = maxTextures;
		}
		
		this.textureIndex = textureIndex;
	}
	
	/**
	 * Toggle the next texture
	 */
	public void nextTexture()
	{
		int maxTextures = model.getTexture().getNumberOfTextureRows();
		maxTextures = (int) Math.pow(maxTextures, maxTextures) - 1;
		
		// Check if the index is within 0 and the maximum amount
		if ( textureIndex >= maxTextures )
		{
			textureIndex = 0;
		}
		else
		{
			textureIndex++;
		}
	}
	
	/**
	 * Gets the top left X coordinate from the targetTexture
	 * 
	 * @return
	 */
	public float getTextureXOffset()
	{
		// Get the number of texture rows
		int numTexRows = model.getTexture().getNumberOfTextureRows();
		// Calculate the target column
		int column = textureIndex % numTexRows;
		// Calculate the X coord offset
		return (float) column / (float) numTexRows;
	}
	
	/**
	 * Returns the top left Y coordinate from the targetTexture
	 * 
	 * @return
	 */
	public float getTextureYOffset()
	{
		// Get the number of texture rows
		int numTexRows = model.getTexture().getNumberOfTextureRows();
		// Calculate the target row
		int row = textureIndex / numTexRows;
		// Calculate the X coord offset
		return (float) row / (float) numTexRows;
	}
	
}
