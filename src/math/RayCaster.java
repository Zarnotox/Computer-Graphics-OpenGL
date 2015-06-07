/**
 * 
 */
package math;

import java.awt.Dimension;

import glStart.DisplayHelper;
import camera.Camera;
import math.matrix.Matrix4f;
import math.vector.Vector2f;
import math.vector.Vector3f;
import math.vector.Vector4f;

/**
 * @author Bert
 */
public class RayCaster {
	
	/**
	 * The object containing window data
	 */
	private DisplayHelper windowHelper;
	
	/**
	 * The used projectionmatrix for rendering
	 */
	private Matrix4f projectionMatrix;
	
	/**
	 * Constructor
	 * 
	 * @param projMat
	 * @param windowHelper
	 */
	public RayCaster( Matrix4f projMat, DisplayHelper windowHelper )
	{
		this.projectionMatrix = projMat;
		this.windowHelper = windowHelper;
	}
	
	/**
	 * Calculate a ray through the world, based on the camera and mouse position in the
	 * viewport
	 * 
	 * @param camera
	 * @param mousePosition
	 * @return
	 */
	public Vector3f castRay( Camera camera, Vector2f mousePosition )
	{
		return calculateMouseRay(mousePosition, camera);
	}
	
	/**
	 * Convert a viewPort position to a 3D world ray
	 * 
	 * @param mousePosition
	 * @param cam
	 * @return
	 */
	private Vector3f calculateMouseRay( Vector2f mousePosition, Camera cam )
	{
		// Extract the mouse position
		float mouseX = mousePosition.getX();
		float mouseY = mousePosition.getY();
		
		// Get the viewport dimensions
		Dimension dim = windowHelper.getWindowDimensions();
		float viewWidth = (float) dim.getWidth();
		float viewHeight = (float) dim.getHeight();
		
		// Convert the viewport coord to OpenGL coord
		float x = (2f * mouseX) / viewWidth - 1;
		float y = (2f * mouseY) / viewHeight - 1;
		
		Vector4f clipVec = new Vector4f(x, y, -1, 1);
		// Undo the projection of the coords
		toEyeCoord(clipVec);
		// Update z and w
		clipVec.z = 1;
		clipVec.w = 0;
		
		// Reverse the view coords
		toWorldCoords(clipVec, cam.getViewMatrix());
		// Tweak the vector
		Vector3f worldVec = new Vector3f(clipVec.x, clipVec.y, clipVec.z);
		worldVec.normalise();
		
		return worldVec;
	}
	
	/**
	 * Convert the given coordinates to worldcoordinates by transforming them with the
	 * viewmatrix
	 * 
	 * @param eyeCoords
	 * @param viewMat
	 */
	private void toWorldCoords( Vector4f eyeCoords, Matrix4f viewMat )
	{
		Matrix4f invViewMatrix = Matrix4f.invert(viewMat, null);
		Matrix4f.transform(invViewMatrix, eyeCoords, eyeCoords);
	}
	
	/**
	 * Convert the given coordinates to a position relative to the camera
	 * 
	 * @param relativePos
	 */
	private void toEyeCoord( Vector4f relativePos )
	{
		// Invert the projection matrix
		Matrix4f invProjMatrix = Matrix4f.invert(projectionMatrix, null);
		// Transform the coords
		Matrix4f.transform(invProjMatrix, relativePos, relativePos);
	}
	
	public boolean calculateIntersetionWithSphere(Vector3f camPosition, Vector3f ray, Vector3f sphereOrigin, float sphereRadius) {
		// Calculate the vector between camera and origin
		Vector3f camOrigVec = Vector3f.sub(sphereOrigin, camPosition, null);
		// Check if the sphere is in front of the cam
		float distanceToProjectedSphereOrigin = Vector3f.dot(camOrigVec, ray);
		if(distanceToProjectedSphereOrigin < 0) {
			return false;
		}
		
		// Look for the distance between center of the sphere projected on the ray and the center of the sphere
		float d = Vector3f.dot(camOrigVec, camOrigVec);
		d -= distanceToProjectedSphereOrigin * distanceToProjectedSphereOrigin;
		if(d < 0) {
			return false;
		}
		
		float innerSpheredistanceToCollision = (float) Math.sqrt(sphereRadius*sphereRadius - d);
		
		float distanceToCollisionOne = distanceToProjectedSphereOrigin - innerSpheredistanceToCollision;
		float distanceToCollisionTwo = distanceToProjectedSphereOrigin - innerSpheredistanceToCollision;
		
		
		return false;		
	}
	
}
