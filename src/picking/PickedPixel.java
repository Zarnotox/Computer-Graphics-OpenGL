/**
 * 
 */
package picking;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

/**
 * @author Bert
 */
public class PickedPixel {
	
	/**
	 * The n-th entity drawn on the scene
	 */
	private float objectID;
	
	/**
	 * The n-th vertex of the n-th object
	 */
	private float drawID;
	
	
	/**
	 * The n-th primitive of the n-th vertex
	 */
	private float primID;
	
	/**
	 * Constructor
	 * 
	 * @param pixelBuffer
	 */
	public PickedPixel( FloatBuffer pixelBuffer )
	{
		objectID = pixelBuffer.get(0);
		drawID = pixelBuffer.get(1);
		primID = pixelBuffer.get(2);
		
		System.out.println(Float.toString(objectID) + ":" + Float.toString(drawID) + ":" + Float.toString(primID));
	}
	
	/**
	 * @return the objectID
	 */
	public float getObjectID()
	{
		return this.objectID;
	}
	
	/**
	 * @return the drawID
	 */
	public float getDrawID()
	{
		return this.drawID;
	}
	
	/**
	 * @return the primID
	 */
	public float getPrimID()
	{
		return this.primID;
	}
	
}
