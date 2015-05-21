/**
 * 
 */
package render;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import entity.Entity;
import entity.model.TexturedModel;
import glCode.DisplayHelper;
import glCode.RenderResources;
import loader.Loader;
import shader.StaticShader;

/**
 * @author Bert
 */
public class Render {
	
	/**
	 * This variable declares that normal vectors will be stored inside the
	 * VAO at index 2
	 */
	public final static int NORMALS_ATTR_INDEX = 2;

	/**
	 * This variable declares that texture coordinate mappings will be stored inside the
	 * VAO at index 1
	 */
	public final static int TEXTURE_COORD_ATTR_INDEX = 1;

	/**
	 * This variable declares that the position attribute VBO has to be stored into the
	 * VAO at index 0
	 */
	public final static int POSITION_ATTR_INDEX = 0;
	
	/**
	 * The static shader for this render instance
	 */
	private StaticShader shader;
	
	/**
	 * The entity renderer instance
	 */
	private EntityRenderer renderer;
	
	/**
	 * List of entities grouped by model
	 */
	private Map<TexturedModel, List<Entity>> entityMap;
	
	/**
	 * Constructor
	 */
	public Render( DisplayHelper displayHelper, Loader loader )
	{
		// Generate a new static shader
		this.shader = new StaticShader(loader);
		this.renderer = new EntityRenderer(displayHelper, this.shader);
		this.entityMap = new HashMap<>();
	}
	
	/**
	 * Render the frame given the camera and sun object
	 * 
	 * @param light
	 * @param cam
	 */
	public void render( RenderResources res )
	{
		// Prepare the renderer
		renderer.prepare();
		
		// Start shader programs
		shader.start();
		// Load lights into the shader
		shader.loadLight(res.getLightList().get(0));
		// Load the camera
		shader.loadviewMatrix(res.getActiveCamera());
		// Render
		renderer.render(entityMap);
		
		// Stop the shader program
		shader.stop();
		
		// Clear the hashmap
		this.entityMap.clear();
	}
	
	/**
	 * Add an entity to the renderlist
	 * 
	 * @param entity
	 */
	public void processEntity( Entity entity )
	{
		// Get the model from the entity
		TexturedModel model = entity.getModel();
		// Get the list of the entities matched to this model
		List<Entity> entList = entityMap.get(model);
		// Check if there was a list
		if ( entList == null )
		{
			// Generate a new list
			List<Entity> newList = new ArrayList<>();
			// Add it to the map
			entityMap.put(model, newList);
			// Declare the list again
			entList = newList;
		}
		// Load the entity
		entList.add(entity);
	}
	
	/**
	 * Cleanup components
	 */
	public void cleanUp()
	{
		shader.cleanUp();
	}
	
}
