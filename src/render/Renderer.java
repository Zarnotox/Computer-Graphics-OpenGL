/**
 * 
 */
package render;

import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.glClear;

import java.awt.Dimension;

import math.Maths;
import math.matrix.Matrix4f;
import model.TexturedModel;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import shader.StaticShader;
import entity.Entity;
import glCode.DisplayHelper;
import glCode.RenderResources;

/**
 * @author Bert
 */
public class Renderer {
	
	/**
	 * This variable declares that the position attribute VBO has to be stored into the
	 * VAO at index 0
	 */
	public final static int POSITION_ATTR_INDEX = 0;
	
	/**
	 * This variable declares that texture coordinate mappings will be stored inside the
	 * VAO at index 1
	 */
	public final static int TEXTURE_COORD_ATTR_INDEX = 1;
	
	/**
	 * This variable declares that normal vectors will be stored inside the
	 * VAO at index 2
	 */
	public final static int NORMALS_ATTR_INDEX = 2;
	
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
	 * 
	 */
	public Renderer( DisplayHelper displayHelper, StaticShader shader )
	{
		this.displayHelper = displayHelper;
		
		// Generate the projectionMatrix
		createProjectionMatrix();
		
		// Load the projectionMatrix straight into the shader
		shader.start();
		shader.loadProjectionMatrix(projectionMatrix);
		shader.stop();
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
	 * Render the given model to the scene
	 * 
	 * @param model
	 */
	public void render( Entity entity, RenderResources res )
	{
		// Fetch the model from the entity
		TexturedModel model = entity.getModel();
		
		// Fetch the static shader
		StaticShader staticShader = res.getStShader();
		
		/* Run shader */
		staticShader.start();
		
		/* Bind all resources */
		// Bind the VAO attached to this model
		GL30.glBindVertexArray(model.getVoaID());
		// Enable the list with INDEX 0 from the VAO
		GL20.glEnableVertexAttribArray(POSITION_ATTR_INDEX);
		// Enable texture coords
		GL20.glEnableVertexAttribArray(TEXTURE_COORD_ATTR_INDEX);
		// Enable normals
		GL20.glEnableVertexAttribArray(NORMALS_ATTR_INDEX);
		
		/* LIGHT MANIPULATION */
		staticShader.loadLight(res.getLightList().get(0));
		
		/* CAMERA MANIPULATION */
		staticShader.loadviewMatrix(res.getActiveCamera());
		
		/* POSITION MANIPULATION */
		// Create transformation matrix for the object
		Matrix4f transformationMatrix = Maths.createTransformationMatrix(
				entity.getPosition(), entity.getRotationX(), entity.getRotationY(),
				entity.getRotationZ(), entity.getScale());
		// Load that matrix into the shader
		res.getStShader().loadTransformationMatrix(transformationMatrix);
		
		// Activate the first texture bank, the 2DSampler (Shader) uses this one
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		// Set the active texture for the following draw
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, model.getTexture().getTextureID());
		
		/*
		 * Draw the model to the scene
		 * Draw Triangles
		 * Draw amount of vertices
		 * We are referring to the indices, so look for Unsigned Ints
		 * 0 offset
		 */
		GL11.glDrawElements(GL11.GL_TRIANGLES, model.getVertexCount(),
				GL11.GL_UNSIGNED_INT, 0);
		
		/* Unbind all used resources */
		// Unbind the position VBO
		GL20.glDisableVertexAttribArray(POSITION_ATTR_INDEX);
		// Unbind texture coords
		GL20.glDisableVertexAttribArray(TEXTURE_COORD_ATTR_INDEX);
		// Unbind the normals
		GL20.glDisableVertexAttribArray(NORMALS_ATTR_INDEX);
		
		// Unbind the chosen VAO
		GL30.glBindVertexArray(0);
		
		/* Stop the shader program */
		staticShader.stop();
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
