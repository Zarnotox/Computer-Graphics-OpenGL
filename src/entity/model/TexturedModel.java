/**
 * 
 */
package entity.model;

import entity.texture.ModelTexture;

/**
 * @author Bert
 */
public class TexturedModel extends Model {
	
	/**
	 * The abstract representation of the assigned texture of this model
	 */
	private ModelTexture texture;
	
	/**
	 * Constructor
	 * 
	 * @param voaID
	 * @param vertexCount
	 * @param texture
	 */
	public TexturedModel( int voaID, int vertexCount, ModelTexture texture )
	{
		// Let super handle the model itself
		super(voaID, vertexCount);
		// We keep track of the linked texture
		this.texture = texture;
	}
	
	/**
	 * Constructor for an already existant model
	 * 
	 * @param model
	 * @param texture
	 */
	public TexturedModel( Model model, ModelTexture texture )
	{
		// Let super handle the model itself
		super(model.getVoaID(), model.getVertexCount());
		// We keep track of the linked texture
		this.texture = texture;
	}
	
	/**
	 * @return the texture
	 */
	public ModelTexture getTexture()
	{
		return this.texture;
	}
	
	
}
