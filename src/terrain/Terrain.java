/**
 * 
 */
package terrain;

import loader.Loader;
import entity.model.Model;
import entity.texture.ModelTexture;

/**
 * @author Bert
 */
public class Terrain {
	
	/**
	 * Size of the terrain
	 */
	private final static float SIZE = 800;
	
	/**
	 * The amount of vertices
	 */
	private final static int VERTEX_COUNT = 128;
	
	/**
	 * The dimension over the X-axis
	 */
	private float x;
	
	/**
	 * The dimension over the Y-axis
	 */
	private float z;
	
	/**
	 * The model of this terrain object
	 */
	private Model model;
	
	/**
	 * The texture of this terrain object
	 */
	private ModelTexture texture;
	
	/**
	 * Constructor
	 */
	public Terrain( int gridX, int gridZ, Loader loader, ModelTexture texture )
	{
		// Save the texture of this terrain
		this.texture = texture;
		
		// Calculate the amount of pixels in X-axis direction
		this.x = gridX * SIZE;
		// Calculate the amount of pixels in the Z-axis direction
		this.z = gridX * SIZE;
		
		// Generate a terrain model
		this.model = generateTerrain(loader);
	}
	
	/**
	 * Generate a terrain model
	 * @param loader
	 * @return
	 */
	private Model generateTerrain( Loader loader )
	{
		// Prepare buffers
		int count = VERTEX_COUNT * VERTEX_COUNT;
		float[] vertices = new float[count * 3];
		float[] normals = new float[count * 3];
		float[] textureCoords = new float[count * 2];
		int[] indices = new int[6 * (VERTEX_COUNT - 1) * (VERTEX_COUNT * 1)];
		
		// Start generating vertices
		int vertexPointer = 0;
		for (int i = 0; i < VERTEX_COUNT; i++)
		{
			for (int j = 0; j < VERTEX_COUNT; j++)
			{
				// Add vertices
				vertices[vertexPointer * 3] = (float) j / ((float) VERTEX_COUNT - 1)
						* SIZE;
				vertices[vertexPointer * 3 + 1] = 0;
				vertices[vertexPointer * 3 + 2] = (float) i / ((float) VERTEX_COUNT - 1)
						* SIZE;
				
				// Set the normal
				normals[vertexPointer * 3] = 0;
				normals[vertexPointer * 3 + 1] = 1;
				normals[vertexPointer * 3 + 2] = 0;
				
				// Add the texture mapping
				textureCoords[vertexPointer * 2] = (float) j / ((float) VERTEX_COUNT - 1);
				textureCoords[vertexPointer * 2 + 1] = (float) i
						/ ((float) VERTEX_COUNT - 1);
				
				// Next vertex pointer
				vertexPointer++;
			}
		}
		
		// Start generating indices buffer
		int pointer = 0;
		for (int gz = 0; gz < VERTEX_COUNT - 1; gz++)
		{
			for (int gx = 0; gx < VERTEX_COUNT - 1; gx++)
			{
				int topLeft = (gz * VERTEX_COUNT) + gx;
				int topRight = topLeft + 1;
				int bottomLeft = ((gz + 1) * VERTEX_COUNT) + gx;
				int bottomRight = bottomLeft + 1;
				indices[pointer++] = topLeft;
				indices[pointer++] = bottomLeft;
				indices[pointer++] = topRight;
				indices[pointer++] = topRight;
				indices[pointer++] = bottomLeft;
				indices[pointer++] = bottomRight;
			}
		}
		
		// Return the reference to the generated model
		return loader.loadToVAO(vertices, textureCoords, normals, indices);
	}
	
	/**
	 * 
	 * @return
	 */
	public float getX()
	{
		return this.x;
	}
	
	/**
	 * 
	 * @return
	 */
	public float getZ()
	{
		return this.z;
	}
	
	/**
	 * 
	 * @return
	 */
	public Model getModel()
	{
		return this.model;
	}
	
	/**
	 * 
	 * @return
	 */
	public ModelTexture getTexture()
	{
		return this.texture;
	}
	
}
