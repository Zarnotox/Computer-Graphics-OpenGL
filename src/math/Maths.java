/**
 * 
 */
package math;

import math.matrix.Matrix4f;
import math.vector.Vector3f;

/**
 * @author Bert
 */
public class Maths {
	
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
}
