package loader;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import entity.model.Model;
import math.vector.Vector2f;
import math.vector.Vector3f;

public class OBJLoader {
	
	public static Model loadObjModel( String fileName, Loader loader )
	{
		/* Read the file from disk */
		InputStreamReader inReader = new InputStreamReader(loader.loadSource(fileName));
		
		// Create a buffered reader for the file content
		BufferedReader reader = new BufferedReader(inReader);
		
		/* Process the file content */
		String line;
		List<Vector3f> vertices = new ArrayList<Vector3f>();
		List<Vector2f> textures = new ArrayList<Vector2f>();
		List<Vector3f> normals = new ArrayList<Vector3f>();
		List<Integer> indices = new ArrayList<Integer>();
		float[] verticesArray = null;
		float[] normalsArray = null;
		float[] textureArray = null;
		int[] indicesArray = null;
		try
		{
			/*
			 * Read every line of the content and parse it according to the first
			 * character
			 */
			while (true)
			{
				line = reader.readLine();
				String[] currentLine = line.split(" ");
				if ( line.startsWith("v ") )
				{
					// Process the vertex
					Vector3f vertex = new Vector3f(Float.parseFloat(currentLine[1]),
							Float.parseFloat(currentLine[2]),
							Float.parseFloat(currentLine[3]));
					// Save it
					vertices.add(vertex);
				}
				else if ( line.startsWith("vt ") )
				{
					// Process the texture coord
					Vector2f texture = new Vector2f(Float.parseFloat(currentLine[1]),
							Float.parseFloat(currentLine[2]));
					// Save it
					textures.add(texture);
				}
				else if ( line.startsWith("vn ") )
				{
					// Process the normal
					Vector3f normal = new Vector3f(Float.parseFloat(currentLine[1]),
							Float.parseFloat(currentLine[2]),
							Float.parseFloat(currentLine[3]));
					// Save it
					normals.add(normal);
				}
				else if ( line.startsWith("f ") )
				{
					// Encountered the faces definition
					// Generate appropriate buffers for the resulting data
					textureArray = new float[vertices.size() * 2];
					normalsArray = new float[vertices.size() * 3];
					// Break from the loop
					break;
				}
			}
			
			// Link matching vertex, texture coord and normal together
			while (line != null)
			{
				// Check for junk between the faces definition
				if ( !line.startsWith("f ") )
				{
					line = reader.readLine();
					continue;
				}
				// Get each data piece
				String[] currentLine = line.split(" ");
				String[] vertex1 = currentLine[1].split("/");
				String[] vertex2 = currentLine[2].split("/");
				String[] vertex3 = currentLine[3].split("/");
				
				// Process all the matching data
				processVertex(vertex1, indices, textures, normals, textureArray,
						normalsArray);
				processVertex(vertex2, indices, textures, normals, textureArray,
						normalsArray);
				processVertex(vertex3, indices, textures, normals, textureArray,
						normalsArray);
				
				// Read the next line
				line = reader.readLine();
			}
			// Close the buffered reader
			reader.close();
			
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		// Generate the buffers to pass on
		verticesArray = new float[vertices.size() * 3];
		indicesArray = new int[indices.size()];
		
		int vertexPointer = 0;
		for (Vector3f vertex : vertices)
		{
			verticesArray[vertexPointer++] = vertex.x;
			verticesArray[vertexPointer++] = vertex.y;
			verticesArray[vertexPointer++] = vertex.z;
		}
		
		
		for (int i = 0; i < indices.size(); i++)
		{
			indicesArray[i] = indices.get(i);
		}
		
		return loader.loadToVAO(verticesArray, textureArray, normalsArray, indicesArray);
	}
	
	/**
	 * Put each piece of data in the correct place inside the buffers
	 * @param vertexData
	 * @param indices
	 * @param textures
	 * @param normals
	 * @param textureArray
	 * @param normalsArray
	 */
	private static void processVertex( String[] vertexData,
			List<Integer> indices,
			List<Vector2f> textures,
			List<Vector3f> normals,
			float[] textureArray,
			float[] normalsArray )
	{
		
		int currentVertexPointer = Integer.parseInt(vertexData[0]) - 1;
		indices.add(currentVertexPointer);
		Vector2f currentTex = textures.get(Integer.parseInt(vertexData[1]) - 1);
		textureArray[currentVertexPointer * 2] = currentTex.x;
		textureArray[currentVertexPointer * 2 + 1] = 1 - currentTex.y;
		Vector3f currentNorm = normals.get(Integer.parseInt(vertexData[2]) - 1);
		normalsArray[currentVertexPointer * 3] = currentNorm.x;
		normalsArray[currentVertexPointer * 3 + 1] = currentNorm.y;
		normalsArray[currentVertexPointer * 3 + 2] = currentNorm.z;
		
	}
	
}
