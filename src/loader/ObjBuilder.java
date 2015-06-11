/**
 * 
 */
package loader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import loader.ObjBuildSystem.Face;
import loader.ObjBuildSystem.Material;
import loader.ObjBuildSystem.Vertex;
import loader.ObjBuildSystem.VertexGroup;
import loader.owens.BuilderInterface;
import math.vector.Vector2f;
import math.vector.Vector3f;

/**
 * @author Bert
 */
public class ObjBuilder implements BuilderInterface {
	
	/**
	 * Name of the model
	 */
	private String objName;
	
	/**
	 * The coords of the vertices
	 */
	private List<Vector3f> vertices;
	
	/**
	 * The texture coord mappings
	 */
	private List<Vector2f> textureCoords;
	
	/**
	 * The normals of each vertex
	 */
	private List<Vector3f> normal;
	
	/**
	 * The list with vertices, sorted by index
	 */
	private List<Vertex> sortedVertexList;
	
	private HashMap<Integer, ArrayList<Vertex>> smoothingGroups;
	private int currentSmoothingGroupNumber = NO_SMOOTHING_GROUP;
	private ArrayList<Vertex> currentSmoothingGroup = null;
	
	/**
	 * Collection of vertex groups stored by name
	 */
	private HashMap<String, ArrayList<VertexGroup>> groupLib;
	
	/**
	 * List of known groupNames
	 */
	private ArrayList<String> knownGroupNames;
	
	/**
	 * The list of vertexgroups that is currently active
	 */
	private ArrayList<ArrayList<VertexGroup>> currentGroupFaceLists;
	
	/**
	 * The material that's been used for the moment
	 */
	private Material currentMaterial;
	
	private Material currentMap;
	
	/**
	 * A collection of known materials stored by name
	 */
	private Map<String, Material> materialLib;
	
	/**
	 * 
	 */
	public ObjBuilder()
	{
		this.objName = null;
		
		this.vertices = new ArrayList<>();
		this.textureCoords = new ArrayList<>();
		this.normal = new ArrayList<>();
		this.sortedVertexList = new ArrayList<>();
		
		this.materialLib = new HashMap<>();
	}
	
	/*
	 * (non-Javadoc)
	 * @see loader.owens.BuilderInterface#setObjFilename(java.lang.String)
	 */
	@Override
	public void setObjFilename( String filename )
	{
		this.objName = filename;
	}
	
	/*
	 * (non-Javadoc)
	 * @see loader.owens.BuilderInterface#addVertexGeometric(float, float, float)
	 */
	@Override
	public void addVertexGeometric( float x, float y, float z )
	{
		this.vertices.add(new Vector3f(x, y, z));
		
	}
	
	/*
	 * (non-Javadoc)
	 * @see loader.owens.BuilderInterface#addVertexTexture(float, float)
	 */
	@Override
	public void addVertexTexture( float u, float v )
	{
		this.textureCoords.add(new Vector2f(u, v));
		
	}
	
	/*
	 * (non-Javadoc)
	 * @see loader.owens.BuilderInterface#addVertexNormal(float, float, float)
	 */
	@Override
	public void addVertexNormal( float x, float y, float z )
	{
		this.normal.add(new Vector3f(x, y, z));
	}
	
	/*
	 * (non-Javadoc)
	 * @see loader.owens.BuilderInterface#addPoints(int[])
	 */
	@Override
	public void addPoints( int[] values )
	{
		// Ignore
		
	}
	
	/*
	 * (non-Javadoc)
	 * @see loader.owens.BuilderInterface#addLine(int[])
	 */
	@Override
	public void addLine( int[] values )
	{
		// Ignore
		
	}
	
	/*
	 * (non-Javadoc)
	 * @see loader.owens.BuilderInterface#addFace(int[])
	 */
	@Override
	public void addFace( int[] vertexIndices )
	{
		Vector3f vertexCoord = null;
		Vector2f texCoord = null;
		Vector3f normVec = null;
		
		// Generate a new face object to store the following vertices
		VertexGroup face = new VertexGroup(currentMaterial, currentMap);
		
		// Generate new face from the indices
		// vertexIndices contains 3 pairs of vertex coords, texture coords and normal
		// vectors
		int i = 0;
		while (i < vertexIndices.length)
		{
			int compIndex;
			
			/* VERTEX COORD */
			// First element is the vertex coord
			compIndex = vertexIndices[i++];
			
			// Check for negative index, meaning a relative positioning
			if ( compIndex < 0 )
			{
				compIndex += vertices.size();
			}
			// Decrease the vertex index, because it's 1-indexed
			compIndex--;
			
			// Only add the vertex if it's a valid index
			if ( compIndex >= 0 && compIndex < vertices.size() )
			{
				vertexCoord = vertices.get(compIndex);
			}
			else
			{
				// Invalid vertex found
				System.err.println("Invalid vertex index found!");
			}
			
			/* TEXTURE COORD */
			compIndex = vertexIndices[i++];
			if ( compIndex == EMPTY_VERTEX_VALUE )
			{
				System.err.println("Missing texture coordinate index!");
			}
			else
			{
				// Check for negative index, meaning a relative positioning
				if ( compIndex < 0 )
				{
					compIndex += textureCoords.size();
				}
				// Decrease the vertex index, because it's 1-indexed
				compIndex--;
				
				// Only add the vertex if it's a valid index
				if ( compIndex >= 0 && compIndex < textureCoords.size() )
				{
					texCoord = textureCoords.get(compIndex);
				}
				else
				{
					// Invalid vertex found
					System.err.println("Invalid texture index found!");
				}
			}
			
			/* Normal Vector */
			compIndex = vertexIndices[i++];
			if ( compIndex == EMPTY_VERTEX_VALUE )
			{
				System.err.println("Missing normal vector index!");
			}
			else
			{
				// Check for negative index, meaning a relative positioning
				if ( compIndex < 0 )
				{
					compIndex += normal.size();
				}
				// Decrease the vertex index, because it's 1-indexed
				compIndex--;
				
				// Only add the vertex if it's a valid index
				if ( compIndex >= 0 && compIndex < normal.size() )
				{
					normVec = normal.get(compIndex);
				}
				else
				{
					// Invalid vertex found
					System.err.println("Invalid normal index found!");
				}
			}
			
			// Get the index of this vertexgroup
			int index = this.sortedVertexList.size();
			Vertex v = new Vertex(index, vertexCoord, texCoord, normVec);
			
			// Add to the list
			this.sortedVertexList.add(v);
			
			// Add the vertex to the active face
			face.addvertex(v);
			
		} // END OF WHILE
		if ( vertexCoord == null )
		{
			System.err.println("No vertex index found, returning!");
			return;
		}
		
		
		
	}
	
	/*
	 * (non-Javadoc)
	 * @see loader.owens.BuilderInterface#addObjectName(java.lang.String)
	 */
	@Override
	public void addObjectName( String name )
	{
		// TODO Auto-generated method stub
		
	}
	
	/*
	 * (non-Javadoc)
	 * @see loader.owens.BuilderInterface#addMapLib(java.lang.String[])
	 */
	@Override
	public void addMapLib( String[] names )
	{
		// Ignore
		
	}
	
	/*
	 * (non-Javadoc)
	 * @see loader.owens.BuilderInterface#setCurrentGroupNames(java.lang.String[])
	 */
	@Override
	public void setCurrentGroupNames( String[] names )
	{
		// TODO Auto-generated method stub
		
	}
	
	/*
	 * (non-Javadoc)
	 * @see loader.owens.BuilderInterface#setCurrentSmoothingGroup(int)
	 */
	@Override
	public void setCurrentSmoothingGroup( int groupNumber )
	{
		// TODO Auto-generated method stub
		
	}
	
	/*
	 * (non-Javadoc)
	 * @see loader.owens.BuilderInterface#setCurrentUseMap(java.lang.String)
	 */
	@Override
	public void setCurrentUseMap( String name )
	{
		// TODO Auto-generated method stub
		
	}
	
	/*
	 * (non-Javadoc)
	 * @see loader.owens.BuilderInterface#setCurrentUseMaterial(java.lang.String)
	 */
	@Override
	public void setCurrentUseMaterial( String name )
	{
		this.currentMaterial = this.materialLib.get(name);
	}
	
	/*
	 * (non-Javadoc)
	 * @see loader.owens.BuilderInterface#newMtl(java.lang.String)
	 */
	@Override
	public void newMtl( String name )
	{
		// Ignore
		
	}
	
	/*
	 * (non-Javadoc)
	 * @see loader.owens.BuilderInterface#setXYZ(int, float, float, float)
	 */
	@Override
	public void setXYZ( int type, float x, float y, float z )
	{
		// Ignore
		
	}
	
	/*
	 * (non-Javadoc)
	 * @see loader.owens.BuilderInterface#setRGB(int, float, float, float)
	 */
	@Override
	public void setRGB( int type, float r, float g, float b )
	{
		// Ignore
		
	}
	
	/*
	 * (non-Javadoc)
	 * @see loader.owens.BuilderInterface#setIllum(int)
	 */
	@Override
	public void setIllum( int illumModel )
	{
		// Ignore
		
	}
	
	/*
	 * (non-Javadoc)
	 * @see loader.owens.BuilderInterface#setD(boolean, float)
	 */
	@Override
	public void setD( boolean halo, float factor )
	{
		// Ignore
		
	}
	
	/*
	 * (non-Javadoc)
	 * @see loader.owens.BuilderInterface#setNs(float)
	 */
	@Override
	public void setNs( float exponent )
	{
		// Ignore
		
	}
	
	/*
	 * (non-Javadoc)
	 * @see loader.owens.BuilderInterface#setSharpness(float)
	 */
	@Override
	public void setSharpness( float value )
	{
		// Ignore
		
	}
	
	/*
	 * (non-Javadoc)
	 * @see loader.owens.BuilderInterface#setNi(float)
	 */
	@Override
	public void setNi( float opticalDensity )
	{
		// Ignore
		
	}
	
	/*
	 * (non-Javadoc)
	 * @see loader.owens.BuilderInterface#setMapDecalDispBump(int, java.lang.String)
	 */
	@Override
	public void setMapDecalDispBump( int type, String filename )
	{
		// Ignore
		
	}
	
	/*
	 * (non-Javadoc)
	 * @see loader.owens.BuilderInterface#setRefl(int, java.lang.String)
	 */
	@Override
	public void setRefl( int type, String filename )
	{
		// Ignore
		
	}
	
	/*
	 * (non-Javadoc)
	 * @see loader.owens.BuilderInterface#doneParsingMaterial()
	 */
	@Override
	public void doneParsingMaterial()
	{
		// Ignore
		
	}
	
	/*
	 * (non-Javadoc)
	 * @see loader.owens.BuilderInterface#doneParsingObj(java.lang.String)
	 */
	@Override
	public void doneParsingObj( String filename )
	{
		System.out.println("Parsed object with name: " + filename);
	}
	
}
