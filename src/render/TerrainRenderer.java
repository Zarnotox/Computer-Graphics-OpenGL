/**
 * 
 */
package render;

import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import entity.model.Model;
import entity.texture.ModelTexture;
import glCode.DisplayHelper;
import math.Maths;
import math.matrix.Matrix4f;
import math.vector.Vector3f;
import shader.TerrainShader;
import terrain.Terrain;

/**
 * @author Bert
 */
public class TerrainRenderer {
	
	/**
	 * The helper for the current window
	 */
	private DisplayHelper displayHelper;
	
	/**
	 * The shader of the terrain
	 */
	private TerrainShader shader;
	
	/**
	 * Constructor
	 * 
	 * @param shader
	 * @param projectionMatrix
	 */
	public TerrainRenderer( DisplayHelper displayHelper,
			TerrainShader shader,
			Matrix4f projectionMatrix )
	{
		this.displayHelper = displayHelper;
		this.shader = shader;
		
		// Load the projectionMatrix into the shader
		shader.start();
		shader.loadProjectionMatrix(projectionMatrix);
		shader.stop();
	}
	
	public void render( List<Terrain> terrains )
	{
		// Loop each terrain from the list
		for (Terrain terrain : terrains)
		{
			// Prepare the model/texture
			prepareTerrainModel(terrain);
			// Load entity specific data
			prepareTerrainEntity(terrain);
			// Render the terrain
			GL11.glDrawElements(GL11.GL_TRIANGLES, terrain.getModel().getVertexCount(),
					GL11.GL_UNSIGNED_INT, 0);
			// Cleanup all data
			unbindModelData();
		}
		
	}
	
	/**
	 * Bind all model data
	 * 
	 * @param model
	 */
	private void prepareTerrainModel( Terrain terrain )
	{
		// Fetch the model from the terrain
		Model model = terrain.getModel();
		
		/* Bind all resources */
		// Bind the VAO attached to this model
		GL30.glBindVertexArray(model.getVoaID());
		// Enable the list with INDEX 0 from the VAO
		GL20.glEnableVertexAttribArray(Render.POSITION_ATTR_INDEX);
		// Enable texture coords
		GL20.glEnableVertexAttribArray(Render.TEXTURE_COORD_ATTR_INDEX);
		// Enable normals
		GL20.glEnableVertexAttribArray(Render.NORMALS_ATTR_INDEX);
		
		// Get the texture
		ModelTexture texture = terrain.getTexture();
		// Reflectivity
		shader.loadShineVariables(texture.getShineDamper(), texture.getReflectivity());
		// Activate the first texture bank, the 2DSampler (Shader) uses this one
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		// Set the active texture for the following draw
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture.getTextureID());
	}
	
	/**
	 * Unbind all model data
	 */
	private void unbindModelData()
	{
		/* Unbind all used resources */
		// Unbind the position VBO
		GL20.glDisableVertexAttribArray(Render.POSITION_ATTR_INDEX);
		// Unbind texture coords
		GL20.glDisableVertexAttribArray(Render.TEXTURE_COORD_ATTR_INDEX);
		// Unbind the normals
		GL20.glDisableVertexAttribArray(Render.NORMALS_ATTR_INDEX);
		
		// Unbind the chosen VAO
		GL30.glBindVertexArray(0);
	}
	
	/**
	 * Load entity specific data to the shader
	 * 
	 * @param entity
	 */
	private void prepareTerrainEntity( Terrain entity )
	{
		/* POSITION MANIPULATION */
		// Create transformation matrix for the object
		Matrix4f transformationMatrix = Maths.createTransformationMatrix(new Vector3f(
				entity.getX(), 0, entity.getZ()), 0, 0, 0, 1);
		// Load that matrix into the shader
		shader.loadTransformationMatrix(transformationMatrix);
	}
	
}