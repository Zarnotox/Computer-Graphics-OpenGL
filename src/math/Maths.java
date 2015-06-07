/**
 * 
 */
package math;

import camera.Camera;
import math.matrix.Matrix4f;
import math.vector.Vector3f;

/**
 * @author Bert
 */
public class Maths {
	
	/**
	 * Create a matrix that combines all given transformations
	 * 
	 * @param translation
	 * @param rx
	 * @param ry
	 * @param rz
	 * @param scale
	 * @return
	 */
	public static Matrix4f createTransformationMatrix( Vector3f translation,
			float rx,
			float ry,
			float rz,
			float scale )
	{
		// Create the matrix
		Matrix4f matrix = new Matrix4f();
		// Set the matrix to identity
		matrix.setIdentity();
		
		// Translate the matrix
		matrix.translate(translation);
		// Rotate the matrix in each plane
		matrix.rotate((float) Math.toRadians(rx), new Vector3f(1, 0, 0));
		matrix.rotate((float) Math.toRadians(ry), new Vector3f(0, 1, 0));
		matrix.rotate((float) Math.toRadians(rz), new Vector3f(0, 0, 1));
		// Scale the matrix
		matrix.scale(new Vector3f(scale, scale, scale));
		
		// Return the transformation matrix
		return matrix;
	}
	
	/**
	 * Generate a viewMatrix that can be used to represent what the given camera sees
	 * 
	 * @param camera
	 * @return
	 */
	public static void createViewMatrix( Camera camera, Matrix4f targetMatrix )
	{
		// Create the matrix
		//Matrix4f viewMatrix = new Matrix4f();
		// Set the matrix to identity
		targetMatrix.setIdentity();
		
		/* Transform the matrix so it resembles the camera position */
		// Rotation around axes
		targetMatrix.rotate((float) Math.toRadians(camera.getPitch()),
				new Vector3f(1, 0, 0));
		targetMatrix.rotate((float) Math.toRadians(camera.getYaw()), new Vector3f(0, 1, 0));
		targetMatrix.rotate((float) Math.toRadians(camera.getRoll()), new Vector3f(0, 0, 1));
		
		// Inverse position
		Vector3f negativeCamerapos = camera.getPosition().negate(null);
		targetMatrix.translate(negativeCamerapos);
		
		/* DEBUG */
		//System.out.println("ViewMatrix:\n" + targetMatrix.toString());
		
		// Return the view matrix
		//return targetMatrix;
	}
}
