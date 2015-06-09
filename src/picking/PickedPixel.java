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
	private int objectID;
	
	/**
	 * The n-th vertex of the n-th object
	 */
	private int drawID;
	
	
	/**
	 * The n-th primitive of the n-th vertex
	 */
	private int primID;
	
	/**
	 * Constructor
	 * 
	 * @param pixelBuffer
	 */
	public PickedPixel( FloatBuffer pixelBuffer )
	{
		objectID = (int)pixelBuffer.get(0);		
		drawID = (int)pixelBuffer.get(1);
		primID = (int)pixelBuffer.get(2);
		
		//System.out.println(Float.toString(objectID) + ":" + Float.toString(drawID) + ":" + Float.toString(primID));
	}
	
	/**
	 * @return the objectID
	 */
	public int getObjectID()
	{
		return this.objectID;
	}
	
	/**
	 * @return the drawID
	 */
	public int getDrawID()
	{
		return this.drawID;
	}
	
	/**
	 * @return the primID
	 */
	public int getPrimID()
	{
		return this.primID;
	}
	
}
