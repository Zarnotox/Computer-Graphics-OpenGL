/**
 * 
 */
package camera;

import math.Maths;
import math.matrix.Matrix;
import math.matrix.Matrix4f;
import math.vector.Vector3f;
import math.vector.Vector4f;

/**
 * @author Bert
 */
public abstract class Camera {
	
	/**
	 * The standard zoom level
	 */
	public final static float STANDARD_ZOOM = 5;
	
	/**
	 * The viewmatrix for this camera
	 */
	private Matrix4f viewMatrix;
	
	/**
	 * The position of the camera
	 */
	private Vector3f currentPosition;
	
	/**
	 * The hardcoded original position
	 */
	private Vector3f originalPosition;
	
	/**
	 * The point where we rotate around
	 */
	private Vector3f targetPoint;
	
	/**
	 * Rotation (deg) around the X-axis
	 */
	private float pitch;
	
	/**
	 * Rotation (deg) around the Y-axis
	 */
	private float yaw;
	
	/**
	 * Rotation (deg) around the Z-axis
	 */
	private float roll;
	
	/**
	 * Store the original pitch, yaw and roll
	 */
	private Vector3f originalCameraRotations;
	
	/**
	 * How far we are away from our targetpoint.
	 */
	private float zoomLevel;
	
	/**
	 * Angle (deg) elevated above the target point.
	 * Rotated around the Z-axis
	 */
	private float verticalAngle;
	
	/**
	 * Angle (deg) elevated to left of the targetpoint.
	 * Rotated around the Y-axis
	 */
	private float horizontalAngle;
	
	/**
	 * @param position
	 * @param pitch Rotation (deg) around the X-axis
	 * @param yaw Rotation (deg) around the Y-axis
	 * @param roll Rotation (deg) around the Z-axis
	 */
	public Camera( Vector3f position, float pitch, float yaw, float roll )
	{
		this.currentPosition = position;
		this.originalPosition = position;
		
		this.pitch = pitch;
		this.yaw = yaw;
		this.roll = roll;
		
		this.zoomLevel = STANDARD_ZOOM;
		this.verticalAngle = 0;
		this.horizontalAngle = 0;
		
		this.originalCameraRotations = new Vector3f(pitch, yaw, roll);
		this.viewMatrix = new Matrix4f();
		this.targetPoint = new Vector3f();
		
		/* DEBUG */
		System.out.println("Registering camera at position: "
				+ currentPosition.toString());
		
		Maths.createViewMatrix(this, viewMatrix);
		calculateTargetPoint();
	}
	
	/**
	 * Calculates a new targetPoint given the current camera settings
	 */
	private void calculateTargetPoint()
	{
		// Use the inverse of the viewmatrix to find the world worldposition
		Matrix4f invertedViewMatrix = new Matrix4f();
		Matrix4f.invert(viewMatrix, invertedViewMatrix);
		
		/* DEBUG */
		System.out.println("Inverted viewMatrix:");
		System.out.println(invertedViewMatrix.toString());
		
		targetPoint = calculateTargetWorldPosition(invertedViewMatrix);
		
		/* DEBUG */
		System.out.println("New target point: " + targetPoint.toString());
		System.out.println();
	}
	
	/**
	 * Calculates the position of the targetPoint that the camera is looking at
	 * 
	 * @param invView
	 * @return
	 */
	private Vector3f calculateTargetWorldPosition( Matrix4f invView )
	{
		// Generate relative eye coord
		Vector4f relCoord = new Vector4f(0, 0, -zoomLevel, 1);
		// Normalize because we used 1 as 4th coord
		// relCoord.normalise();
		// Convert them to world coord using the inverted viewmatrix
		Vector4f worldCoord = Matrix4f.transform(invView, relCoord, null);
		// Return the 3d vector
		return new Vector3f(worldCoord.x, worldCoord.y, worldCoord.z);
		
		/*
		 * Vector3f r = new Vector3f();
		 * float z = -zoomLevel;
		 * float w = 1;
		 * // Calculate real world position
		 * // The matrix is in Column major order, so '20' is the first row, third column
		 * r.x = invView.m00 + invView.m10 + invView.m20 * z + invView.m30 * w;
		 * r.y = invView.m01 + invView.m11 + invView.m21 * z + invView.m31 * w;
		 * r.z = invView.m02 + invView.m12 + invView.m22 * z + invView.m23 * w;
		 * return r;
		 */
	}
	
	/**
	 * Calculates a new camera position relative to the targetPoint, using the horizontal
	 * and vertical angle
	 */
	private void calculateRelativePositionToTarget()
	{
		/* Generate rotation matrix */
		Matrix4f InversePositioning = new Matrix4f();
		// InversePositioning.setIdentity();
		
		// Rotation around the X axis
		InversePositioning.rotate((float) Math.toRadians(verticalAngle), new Vector3f(1,
				0, 0));
		// Rotation around the Y axis
		InversePositioning.rotate((float) Math.toRadians(horizontalAngle), new Vector3f(
				0, 1, 0));
		
		// Move the point to the targetpoint
		InversePositioning.translate(targetPoint);
		// Move the needed point by zoomlevel
		InversePositioning.translate(new Vector3f(0, 0, zoomLevel));
		
		// Transform the targetPoint with the generated matrix
		Vector4f cameraPosition = Matrix4f.transform(InversePositioning, new Vector4f(0,
				0, 0, 1), null);
		
		System.out.println("Transformation matrix:\n" + InversePositioning.toString());
		
		// Set the position of the camera
		currentPosition.x = cameraPosition.x;
		currentPosition.y = cameraPosition.y;
		currentPosition.z = cameraPosition.z;
		
		System.out.println("New position: " + currentPosition.toString());
		
		// Update the rotation
		calculateCameraRotations();
		
		// Update the viewMatrix
		updateViewMatrix();
		
		/*
		 * // Calculate distances between the camera and the targetPoint
		 * float horDistanceToTarget = (float) (zoomLevel * Math.cos(verticalAngle));
		 * float verDistanceToTarget = (float) (zoomLevel * Math.sin(verticalAngle));
		 * // Calculate the world position of the camera with these distances
		 * currentPosition.y = targetPoint.y + verDistanceToTarget;
		 * // Switcht to RHS X, Z 2D coord system
		 * // Because of the RHS, we minus the X and Z coord
		 * currentPosition.x = (float) (targetPoint.x - horDistanceToTarget
		 * Math.sin(horizontalAngle));
		 * currentPosition.z = (float) (targetPoint.z - horizontalAngle
		 * Math.cos(horizontalAngle));
		 * DEBUG
		 * System.out.println("New position: " + currentPosition.toString());
		 * // Update the camera rotations
		 * calculateCameraRotations();
		 * // Update the viewmatrix again
		 * updateViewMatrix();
		 */
	}
	
	/**
	 * Updates the camera rotations for the given camera location
	 */
	private void calculateCameraRotations()
	{
		// Update YAW in RHS X, Z 2D coord system
		yaw = -horizontalAngle;
		pitch = -verticalAngle;
		
	}
	
	/**
	 * Calculates the vector from the camera to the targetPoint
	 * 
	 * @param buffer The vector object to fill the data
	 */
	protected void getCamDirectionVector( Vector3f buffer )
	{
		Vector3f.sub(targetPoint, currentPosition, buffer);
	}
	
	/**
	 * @return the position
	 */
	public final Vector3f getPosition()
	{
		return this.currentPosition;
	}
	
	/**
	 * @return the pitch in degrees
	 */
	public final float getPitch()
	{
		return this.pitch;
	}
	
	/**
	 * @return the yaw in degrees
	 */
	public final float getYaw()
	{
		return this.yaw;
	}
	
	/**
	 * @return the roll in degrees
	 */
	public final float getRoll()
	{
		return this.roll;
	}
	
	/**
	 * @return The viewmatrix for this camera
	 */
	public final Matrix4f getViewMatrix()
	{
		return this.viewMatrix;
	}
	
	/**
	 * Resets the original position of the camera
	 */
	public final void reset()
	{
		// Restore original stats
		this.currentPosition = this.originalPosition;
		
		this.pitch = originalCameraRotations.x;
		this.yaw = originalCameraRotations.y;
		this.roll = originalCameraRotations.z;
		
		// Recalculate the targetPoint
		calculateTargetPoint();
	}
	
	/**
	 * Set a different distance to the targetpoint
	 * 
	 * @param dx
	 */
	public final void updateZoom( float dx )
	{
		zoomLevel += dx;
		
		if ( zoomLevel < 0 )
		{
			zoomLevel = 0;
		}
		
		// Changing this must update the position
		calculateRelativePositionToTarget();
	}
	
	/**
	 * Generates a new viewmatrix into the existing object
	 */
	protected final void updateViewMatrix()
	{
		Maths.createViewMatrix(this, viewMatrix);
	}
	
	/**
	 * Update the position of this camera
	 * 
	 * @param dx
	 * @param dy
	 * @param dz
	 */
	protected final void updatePosition( float dx, float dy, float dz )
	{
		currentPosition.x += dx;
		currentPosition.y += dy;
		currentPosition.z += dz;
		
		/* DEBUG */
		System.out.println("New position: " + currentPosition.toString());
		
		// Create new viewmatrix
		updateViewMatrix();
		
		// Apply the angles
		//pitch = verticalAngle;
		//yaw = horizontalAngle;
		
		// Reset the horizontal and vertical angles
		//horizontalAngle = 0;
		//verticalAngle = 0;
		
		// Calculate targetPoint
		calculateTargetPoint();
		
		// Calculate rotations
		calculateCameraRotations();
	}
	
	/**
	 * Increase/decrease the horizontal angle around the targetPoint
	 * 
	 * @param dx angledeficit in degrees
	 */
	public final void updateHorAngle( float dx )
	{
		// Update the yaw
		horizontalAngle += dx;
		// Update the camera position
		calculateRelativePositionToTarget();
	}
	
	/**
	 * Increase/decrease the vertical angle around the targetPoint
	 * 
	 * @param dx angledeficit in degrees
	 */
	public final void updateVerAngle( float dx )
	{
		// Update the pitch
		verticalAngle += dx;
		// Update the camera position
		calculateRelativePositionToTarget();
	}
	
	/**
	 * Moves the camera in a certain position
	 * 
	 * @param dx
	 * @param dy
	 * @param dz
	 */
	public abstract void moveRelative( float dx, float dy, float dz );
	
	/**
	 * Move along the X-axis
	 * 
	 * @param dx
	 */
	public final void moveForward( float dx )
	{
		// Fetch the x axis from the viewmatrix
		Vector3f xAxis = new Vector3f(viewMatrix.m00, viewMatrix.m01, viewMatrix.m02);
		// Normalize the axis
		xAxis.normalise();
		
		// Fetch the Y axis from the viewmatrix
		Vector3f yAxis = new Vector3f(viewMatrix.m10, viewMatrix.m11, viewMatrix.m12);
		// Normalize the axis
		yAxis.normalise();
		
		// Cross both vectors to fetch the vector really pointing forward
		Vector3f crossed = Vector3f.cross(xAxis, yAxis, null);
		
		// Multiply the axis with the deficit
		crossed.scale(-dx);
		// Add this vector to the current position
		updatePosition(crossed.x, crossed.y, crossed.z);
		
		// Reset horizonatal and vertical angles
		// horizontalAngle = 0;
		// verticalAngle = 0;
		
		/*
		 * // Get the vector between the target and the camera
		 * Vector3f vecBuffer = new Vector3f();
		 * getCamDirectionVector(vecBuffer);
		 * System.out.println("Direction vector: " + vecBuffer.toString());
		 * // Normalize the vector and scale it with the delta value
		 * vecBuffer.normalise().scale(dx);
		 * // Add the move vector to the current position
		 * updatePosition(vecBuffer.x, vecBuffer.y, vecBuffer.z);
		 */
		
	}
	
	/**
	 * Move along the Y-axis
	 * 
	 * @param dy
	 */
	public void moveRight( float dy )
	{
		// Get the Y axis from the viewmatrix
		Vector3f yAxis = new Vector3f(viewMatrix.m10, viewMatrix.m11, viewMatrix.m12);
		// Normalize the axis
		yAxis.normalise();
		
		// Get the Z axis fromt the viewmatrix
		Vector3f zAxis = new Vector3f(viewMatrix.m20, viewMatrix.m21, viewMatrix.m22);
		// Normalize the axis
		zAxis.normalise();
		
		// Cross both vectors to get the vector really pointing right
		Vector3f crossed = Vector3f.cross(zAxis, yAxis, null);
		
		// Multiply with deficit
		crossed.scale(-dy);
		
		// Add it to the current position
		updatePosition(crossed.x, crossed.y, crossed.z);
		
		// Reset horizonatal and vertical angles
		// horizontalAngle = 0;
		// verticalAngle = 0;
	}
	
	/**
	 * Move along the Z-axis
	 * 
	 * @param dz
	 */
	// public abstract void moveOut( float dz );
	
}
