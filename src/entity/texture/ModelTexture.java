/**
 * 
 */
package entity.texture;

/**
 * @author Bert
 *
 */
public class ModelTexture {
	
	/**
	 * The ID of the loaded texture
	 */
	private int textureID;
	
	/**
	 * The amount of shine loss when the camera moves away from the reflected light
	 */
	private float shineDamper;
	
	/**
	 * The reflectivity of this model
	 */
	private float reflectivity;

	/**
	 * @param textureID
	 */
	public ModelTexture( int textureID )
	{
		super();
		this.textureID = textureID;
		
		this.shineDamper = 1;
		this.reflectivity = 0;
	}

	/**
	 * @return the textureID
	 */
	public int getTextureID()
	{
		return this.textureID;
	}

	/**
	 * @return the shineDamper
	 */
	public float getShineDamper()
	{
		return this.shineDamper;
	}

	/**
	 * @param shineDamper the shineDamper to set
	 */
	public void setShineDamper( float shineDamper )
	{
		this.shineDamper = shineDamper;
	}

	/**
	 * @return the reflectivity
	 */
	public float getReflectivity()
	{
		return this.reflectivity;
	}

	/**
	 * @param reflectivity the reflectivity to set
	 */
	public void setReflectivity( float reflectivity )
	{
		this.reflectivity = reflectivity;
	}
	
	
	
	
}
