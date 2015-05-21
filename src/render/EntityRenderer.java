/**
 * 
 */
package render;

import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.glClear;

import java.awt.Dimension;
import java.util.List;
import java.util.Map;

import math.Maths;
import math.matrix.Matrix4f;
import model.TexturedModel;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import shader.StaticShader;
import texture.ModelTexture;
import entity.Entity;
import glCode.DisplayHelper;

/**
 * @author Bert
 */
public class EntityRenderer {
	
	/**
	 * The field of view angle used for the camera
	 */
	public final static float FOV = 70;
	
	/**
	 * The minimum distance for things to be visible
	 */
	public final static float NEAR_PLANE_DISTANCE = 0.1f;
	
	/**
	 * The distance as in how far away we can see
	 */
	public final static float FAR_PLANE_DISTANCE = 1000;
	
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
	 * 
	 */
	public EntityRenderer( DisplayHelper displayHelper, StaticShader shader )
	{
		this.displayHelper = displayHelper;
		this.stShader = shader;
		
		// Generate the projectionMatrix
		createProjectionMatrix();
		
		// Load the projectionMatrix straight into the shader
		shader.start();
		shader.loadProjectionMatrix(projectionMatrix);
		shader.stop();
		
		/* OpenGL props */
		// Don't render backwards facing vertices
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glCullFace(GL11.GL_BACK);
		
	}
	
	/**
	 * Prepares the OpenGL context
	 */
	public void prepare()
	{
		// Use the depth buffer to properly render triangles
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		
		// Clear the color and depth buffers
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		
		// Set the base values of the color buffers
		GL11.glClearColor(0.2f, 0, 0, 1);
	}
	
	/**
	 * Render every entity, grouped by model
	 * 
	 * @param entities
	 */
	public void render( Map<TexturedModel, List<Entity>> entities )
	{
		// Loop the map
		for (TexturedModel model : entities.keySet())
		{
			// Prepare the model
			prepareTexturedModel(model);
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
	
	private void prepareTexturedModel( TexturedModel model )
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
		// Reflectivity
		stShader.loadShineVariables(texture.getShineDamper(), texture.getReflectivity());
		// Activate the first texture bank, the 2DSampler (Shader) uses this one
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		// Set the active texture for the following draw
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, model.getTexture().getTextureID());
	}
	
	private void unbindTexturedModel()
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
	 * Generate a projectionMatrix.
	 * The projectionMatrix makes the objects onscreen more realistic looking
	 */
	private void createProjectionMatrix()
	{
		// Fetch the window dimensions
		Dimension d = displayHelper.getWindowDimensions();
		
		/* DEBUG */
		System.out.println("Calculating projection matrix");
		System.out.println("WindowDimensions: " + d.toString());
		
		// Prepare matrix variables
		float aspectRatio = (float) d.getWidth() / (float) d.getHeight();
		float y_scale = (float) (1f / Math.tan(Math.toRadians(FOV / 2f)) * aspectRatio);
		float x_scale = y_scale / aspectRatio;
		float frustum_length = FAR_PLANE_DISTANCE - NEAR_PLANE_DISTANCE;
		
		// Generate the projection matrix
		projectionMatrix = new Matrix4f();
		projectionMatrix.m00 = x_scale;
		projectionMatrix.m11 = y_scale;
		projectionMatrix.m22 = -((FAR_PLANE_DISTANCE + NEAR_PLANE_DISTANCE) / frustum_length);
		projectionMatrix.m23 = -1;
		projectionMatrix.m32 = -((2 * NEAR_PLANE_DISTANCE * FAR_PLANE_DISTANCE) / frustum_length);
		projectionMatrix.m33 = 0;
		
		/* DEBUG */
		System.out.println(projectionMatrix.toString());
	}
}
