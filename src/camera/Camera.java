/**
 * 
 */
package camera;

import entity.light.Light;
import math.Maths;
import math.matrix.Matrix4f;
import math.vector.Vector3f;
import math.vector.Vector4f;

/**
 * @author Bert
 */
public class Camera {
	
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
	 * The light attached to this camera
	 */
	private Light attachedLight;
	
	/**
	 * @param position
	 * @param pitch Rotation (deg) around the X-axis
	 * @param yaw Rotation (deg) around the Y-axis
	 * @param roll Rotation (deg) around the Z-axis
	 */
	public Camera( Vector3f position, float pitch, float yaw, float roll )
	{
		this.attachedLight = null;
		
		this.currentPosition = position;
		this.originalPosition = position;
		
		this.pitch = pitch;
		this.yaw = yaw;
		this.roll = 0;
		
		this.zoomLevel = STANDARD_ZOOM;
		this.verticalAngle = -pitch;
		this.horizontalAngle = -yaw;
		
		this.originalCameraRotations = new Vector3f(pitch, yaw, roll);
		this.viewMatrix = new Matrix4f();
		this.targetPoint = new Vector3f();
		
		/* DEBUG */
		System.out.println("Registering camera at position: "
				+ currentPosition.toString());
		
		Maths.createViewMatrix(this, viewMatrix);
		// Target Point needs the viewmatrix to find it
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
		// System.out.println("Inverted viewMatrix:");
		// System.out.println(invertedViewMatrix.toString());
		
		targetPoint = calculateTargetWorldPosition(invertedViewMatrix);
		
		/* DEBUG */
		//System.out.println("Target point: " + targetPoint.toString());
		// System.out.println();
	}
	
	/**
	 * Calculates the position of the targetPoint that the camera is looking at
	 * 
	 * @param invView Inverse viewMatrix
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
		Matrix4f inversePositioning = new Matrix4f();
		inversePositioning.setIdentity();
		
		// Rotation around the Y axis
		inversePositioning.rotate((float) Math.toRadians(horizontalAngle), new Vector3f(
				0, 1, 0));
		
		// Rotation around the X axis
		inversePositioning.rotate((float) Math.toRadians(verticalAngle), new Vector3f(1,
				0, 0));
		
		// Move the point to the targetpoint
		inversePositioning.translate(targetPoint);
		// Move the needed point by zoomlevel
		inversePositioning.translate(new Vector3f(0, 0, zoomLevel));
		
		// Transform the targetPoint with the generated matrix
		Vector4f cameraPosition = Matrix4f.transform(inversePositioning, new Vector4f(0,
				0, 0, 1), null);
		
		// System.out.println("Transformation matrix:\n" + InversePositioning.toString());
		
		// Set the position of the camera
		Vector3f newPosition = new Vector3f(cameraPosition.x, cameraPosition.y, cameraPosition.z);
		currentPosition = newPosition;
		
		/* Set attached light position */
		if(attachedLight != null) {
			attachedLight.setPosition(newPosition);
		}
		
		/*
		System.out.println("Position: " + currentPosition.toString());
		System.out.println("Target: " + targetPoint.toString());
		*/
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
		/*
		System.out.println("Zoom: " + zoomLevel);
		System.out.println("Pitch: " + pitch);
		System.out.println("Yaw: " + yaw);
		System.out.println("Roll: " + roll);
		*/
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
		
		this.horizontalAngle = -yaw;
		this.verticalAngle = -pitch;
		
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
		// calculateRelativePositionToTarget();
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
	protected final void updateTargetPosition( float dx, float dy, float dz )
	{
		targetPoint.x += dx;
		targetPoint.y += dy;
		targetPoint.z += dz;
		
		calculateRelativePositionToTarget();		
		
		/* DEBUG */
		//System.out.println("New position: " + currentPosition.toString());
		
		// Calculate rotations
		// calculateCameraRotations();
		
		// Create new viewmatrix
		//updateViewMatrix();
		
		// Apply the angles
		// pitch = verticalAngle;
		// yaw = horizontalAngle;
		
		// Reset the horizontal and vertical angles
		// horizontalAngle = 0;
		// verticalAngle = 0;
		
		// Calculate targetPoint
		// calculateTargetPoint();
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
		
		//System.out.println("Horizontal angle: " + horizontalAngle);
		
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
		
		// Clamp the vertical angle between 0 and 90
		verticalAngle = Math.max(-90, Math.min(verticalAngle, 90));
		
		//System.out.println("Vertical angle: " + verticalAngle);
		
		// Update the camera position
		calculateRelativePositionToTarget();
	}
	
	/**
	 * Move along the camera's Z-axis
	 * 
	 * @param dz
	 */
	public final void moveForward( float dz )
	{
		/*
		 * // Fetch the x axis from the viewmatrix
		 * Vector3f xAxis = new Vector3f(viewMatrix.m00, viewMatrix.m01, viewMatrix.m02);
		 * // Normalize the axis
		 * xAxis.normalise();
		 * // Fetch the Y axis from the viewmatrix
		 * Vector3f yAxis = new Vector3f(viewMatrix.m10, viewMatrix.m11, viewMatrix.m12);
		 * // Normalize the axis
		 * yAxis.normalise();
		 * // Cross both vectors to fetch the vector really pointing forward
		 * Vector3f crossed = Vector3f.cross(xAxis, yAxis, null);
		 */
		
		// Get Z axis
		Vector3f zAxis = new Vector3f(viewMatrix.m02, viewMatrix.m12, viewMatrix.m22);
		// Normalize the axis
		zAxis.normalise();
		
		/*
		System.out.println("CAMERA Z AXIS: " + zAxis);
		System.out.println("Viewmatrix:");
		System.out.println(viewMatrix.toString());
		*/
		
		// Multiply the axis with the deficit, the Z axis points behind the cam so negate
		// the value
		zAxis.scale(-dz);
		// Add this vector to the current position
		updateTargetPosition(zAxis.x, zAxis.y, zAxis.z);
		
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
	 * Move along the camera's X-axis
	 * 
	 * @param dx
	 */
	public void moveRight( float dx )
	{
		/*
		 * // Get the Y axis from the viewmatrix
		 * Vector3f yAxis = new Vector3f(viewMatrix.m10, viewMatrix.m11, viewMatrix.m12);
		 * // Normalize the axis
		 * yAxis.normalise();
		 * // Get the Z axis fromt the viewmatrix
		 * Vector3f zAxis = new Vector3f(viewMatrix.m20, viewMatrix.m21, viewMatrix.m22);
		 * // Normalize the axis
		 * zAxis.normalise();
		 * // Cross both vectors to get the vector really pointing right
		 * Vector3f crossed = Vector3f.cross(zAxis, yAxis, null);
		 */
		Vector3f xAxis = new Vector3f(viewMatrix.m00, viewMatrix.m10, viewMatrix.m20);
		// Normalize the axis
		xAxis.normalise();
		
		//System.out.println("CAMERA X AXIS: " + xAxis);
		//System.out.println(viewMatrix.toString());
		
		// Multiply with deficit
		xAxis.scale(dx);
		
		// Add it to the current position
		updateTargetPosition(xAxis.x, xAxis.y, xAxis.z);
		
		// Reset horizonatal and vertical angles
		// horizontalAngle = 0;
		// verticalAngle = 0;
	}
	
	/**
	 * Move along the camera's Y-axis
	 * 
	 * @param dy
	 */
	public void moveUp( float dy ) {
		// Get the Y axis
		Vector3f yAxis = new Vector3f(viewMatrix.m01, viewMatrix.m11, viewMatrix.m12);
		// Normalize the axis
		yAxis.normalise();
		
		//System.out.println("CAMERA Y AXIS: " + yAxis);
		//System.out.println(viewMatrix.toString());
		
		// Multiply with deficit
		yAxis.scale(dy);
		
		// Add it to the current position
		updateTargetPosition(yAxis.x, yAxis.y, yAxis.z);
	}
	
	/**
	 * Attach a light to this camera, every camera update will also update the light.
	 * The currently attached light will be discarded.
	 * @param light
	 */
	public void attachLight(Light light) {
		this.attachedLight = light;
		
		light.setPosition(currentPosition);
	}
	
	/**
	 * Detaches the light of the camera
	 */
	public void detachLight() {
		this.attachedLight = null;
	}
	
}
