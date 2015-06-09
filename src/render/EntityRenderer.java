/**
 * 
 */
package render;

import java.util.List;
import java.util.Map;

import math.Maths;
import math.matrix.Matrix4f;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import picking.shader.PickingShader;
import shader.StaticShader;
import entity.Entity;
import entity.model.TexturedModel;
import entity.texture.ModelTexture;
import glStart.DisplayHelper;

/**
 * @author Bert
 */
public class EntityRenderer {
	
	/**
	 * Object that makes window properties more accessible
	 */
	private DisplayHelper displayHelper;
	
	/**
	 * Matrix that transforms our view of the scene
	 */
	private Matrix4f projectionMatrix;
	
	/**
	 * The static shader instance
	 */
	private StaticShader stShader;
	
	/**
	 * The picking shader program
	 */
	private PickingShader pickShader;
	
	/**
	 * 
	 */
	public EntityRenderer( DisplayHelper displayHelper,
			StaticShader stShader,
			PickingShader pickShader,
			Matrix4f projMatrix )
	{
		this.displayHelper = displayHelper;
		this.stShader = stShader;
		this.pickShader = pickShader;
		this.projectionMatrix = projMatrix;
		
		// Load the projectionMatrix straight into the shader
		stShader.start();
		stShader.loadProjectionMatrix(projectionMatrix);
		stShader.stop();
		
		pickShader.start();
		pickShader.loadProjectionMatrix(projectionMatrix);
		pickShader.stop();
	}
	
	/**
	 * Render every entity, grouped by model
	 * 
	 * @param entities
	 */
	public void render( Map<TexturedModel, List<Entity>> entities, boolean wireframe )
	{
		// Loop the map
		for (TexturedModel model : entities.keySet())
		{
			// Prepare the model
			prepareTexturedModel(model, wireframe);
			// Fetch all related entities
			List<Entity> ent = entities.get(model);
			// Loop all these entities
			for (Entity entity : ent)
			{
				// Prepare the entity
				prepareInstance(entity);
				// Render the entity
				/*
				 * Draw the model to the scene
				 * Draw Triangles
				 * Draw amount of vertices
				 * We are referring to the indices, so look for Unsigned Ints
				 * 0 offset
				 */
				GL11.glDrawElements(GL11.GL_TRIANGLES, model.getVertexCount(),
						GL11.GL_UNSIGNED_INT, 0);
			}
			
			// Unbind the model
			unbindTexturedModel();
		}
	}
	
	public void renderForPicking( Map<TexturedModel, List<Entity>> entities )
	{
		// The object counter
		float i = 1;
		
		// Preload object index 0
		this.pickShader.loadObjectIndex(0);
		
		// Loop the map
		for (TexturedModel model : entities.keySet())
		{
			// Prepare the model
			prepareTexturedModel(model, false);
			
			// Fetch all related entities
			List<Entity> ent = entities.get(model);
			// Loop all these entities
			for (Entity entity : ent)
			{
				// Load the object counter, and increase it afterwards
				this.pickShader.loadObjectIndex(i++);
				
				// Prepare the entity
				preparePickingInstance(entity);
				// Render the entity
				/*
				 * Draw the model to the scene
				 * Draw Triangles
				 * Draw amount of vertices
				 * We are referring to the indices, so look for Unsigned Ints
				 * 0 offset
				 */
				GL11.glDrawElements(GL11.GL_TRIANGLES, model.getVertexCount(),
						GL11.GL_UNSIGNED_INT, 0);
			}
			
			// Unbind the model
						unbindTexturedModel();
		}
	}
	
	/**
	 * Bind all model data
	 * 
	 * @param model
	 */
	private void prepareTexturedModel( TexturedModel model, boolean wireframe )
	{
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
		ModelTexture texture = model.getTexture();
		
		// Check for transparent texture
		if ( texture.isHasTransparency() )
		{
			// Disable culling on transparent textures
			Render.disableCulling();
		}
		
		// Wireframe mode
		stShader.loadWireframeVariable(wireframe);
		// Fake lighting
		stShader.loadFakeLightingVariable(texture.isUseFakeLighting());
		// Reflectivity
		stShader.loadShineVariables(texture.getShineDamper(), texture.getReflectivity());
		// Activate the first texture bank, the 2DSampler (Shader) uses this one
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		// Set the active texture for the following draw
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture.getTextureID());
	}
	
	/**
	 * Unbind all model data
	 */
	private void unbindTexturedModel()
	{
		// Reenable culling
		Render.enableCulling();
		
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
	 * Load entity specific data to the static shader
	 * 
	 * @param entity
	 */
	private void prepareInstance( Entity entity )
	{
		/* POSITION MANIPULATION */
		// Create transformation matrix for the object
		Matrix4f transformationMatrix = Maths.createTransformationMatrix(
				entity.getPosition(), entity.getRotationX(), entity.getRotationY(),
				entity.getRotationZ(), entity.getScale());
		// Load that matrix into the shader
		stShader.loadTransformationMatrix(transformationMatrix);
	}
	
	/**
	 * Load entity specific data to the picker shader
	 * 
	 * @param entity
	 */
	private void preparePickingInstance( Entity entity )
	{
		/* POSITION MANIPULATION */
		// Create transformation matrix for the object
		Matrix4f transformationMatrix = Maths.createTransformationMatrix(
				entity.getPosition(), entity.getRotationX(), entity.getRotationY(),
				entity.getRotationZ(), entity.getScale());
		// Load that matrix into the shader
		pickShader.loadTransformationMatrix(transformationMatrix);
	}
}
