/**
 * 
 */
package model;

/**
 * @author Bert
 */
public class Model {
	
	/**
	 * Identifier for the used Vertex Array Object. The VAO stores properties of the used
	 * model.
	 */
	private int voaID;
	
	/**
	 * Tells us how much vertices this model contains
	 */
	private int vertexCount;

	/**
	 * @param voaID
	 * @param vertexCount
	 */
	public Model( int voaID, int vertexCount )
	{
		this.voaID = voaID;
		this.vertexCount = vertexCount;
	}

	/**
	 * @return the voaID
	 */
	public int getVoaID()
	{
		return this.voaID;
	}

	/**
	 * @return the vertexCount
	 */
	public int getVertexCount()
	{
		return this.vertexCount;
	}
	
	
	
	
}
