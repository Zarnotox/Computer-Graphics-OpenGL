/**
 * 
 */
package loader.ObjBuildSystem;

import math.vector.Vector2f;
import math.vector.Vector3f;

/**
 * @author Bert
 *
 */
public class Vertex {
	
	private int index;
	
	private Vector3f vCoord;
	
	private Vector2f texCoord;
	
	private Vector3f normVec;

	/**
	 * @param index
	 * @param vCoord
	 * @param texCoord
	 * @param normVec
	 */
	public Vertex( int index, Vector3f vCoord, Vector2f texCoord, Vector3f normVec )
	{
		this.index = index;
		this.vCoord = vCoord;
		this.texCoord = texCoord;
		this.normVec = normVec;
	}

	/**
	 * @return the index
	 */
	public int getIndex()
	{
		return this.index;
	}

	/**
	 * @return the vCoord
	 */
	public Vector3f getvCoord()
	{
		return this.vCoord;
	}

	/**
	 * @return the texCoord
	 */
	public Vector2f getTexCoord()
	{
		return this.texCoord;
	}

	/**
	 * @return the normVec
	 */
	public Vector3f getNormVec()
	{
		return this.normVec;
	}	
}
