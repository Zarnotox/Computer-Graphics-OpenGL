/**
 * 
 */
package loader.ObjBuildSystem;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Bert
 *
 */
public class VertexGroup {
	
	/**
	 * List of all vertices that are part of this face
	 */
	private List<Vertex> vertexList;
	
	/**
	 * The used material for this face
	 */
	private Material material;
	
	/**
	 * 
	 */
	private Material map;
	
	/**
	 * 
	 */
	public VertexGroup(Material material, Material map)
	{
		this.material = material;
		this.map = map;
		
		this.vertexList = new ArrayList<>();
	}
	
	/**
	 * Add a vertex to this face
	 * @param vertex
	 */
	public void addvertex(Vertex vertex) {
		this.vertexList.add(vertex);
	}
	
	
	
}
