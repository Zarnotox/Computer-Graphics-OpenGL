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
	 * The flag indicating that the attached texture has transparent area's
	 */
	private boolean hasTransparency;
	
	/**
	 * The flag indicating that the texture needs to be more evenly lit
	 */
	private boolean useFakeLighting;

	/**
	 * @param textureID
	 */
	public ModelTexture( int textureID )
	{
		super();
		this.textureID = textureID;
		
		this.shineDamper = 1;
		this.reflectivity = 0;
		
		this.hasTransparency = false;
		this.useFakeLighting = false;
		
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

	/**
	 * @return the hasTransparency
	 */
	public boolean isHasTransparency()
	{
		return this.hasTransparency;
	}

	/**
	 * @param hasTransparency the hasTransparency to set
	 */
	public void setHasTransparency( boolean hasTransparency )
	{
		this.hasTransparency = hasTransparency;
	}

	/**
	 * @return the useFakeLighting
	 */
	public boolean isUseFakeLighting()
	{
		return this.useFakeLighting;
	}

	/**
	 * @param useFakeLighting the useFakeLighting to set
	 */
	public void setUseFakeLighting( boolean useFakeLighting )
	{
		this.useFakeLighting = useFakeLighting;
	}
	
	
	
	
}
