/**
 * 
 */
package picking;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

/**
 * @author Bert
 */
public class PickingEngine {
	
	/**
	 * ID of the framebuffer
	 */
	private static int fboID;
	
	/**
	 * ID of the picking texture
	 */
	private static int pickingTextureID;
	
	/**
	 * ID of the depthTexture
	 */
	private int depthTextureID;
	
	private static int WIDTH;
	
	private static int HEIGHT;
	
	/**
	 * Constructor
	 * 
	 * @param windowWidth
	 * @param windowHeight
	 */
	public PickingEngine( int windowWidth, int windowHeight )
	{
		WIDTH = windowWidth;
		HEIGHT = windowHeight;
		
		init(windowWidth, windowHeight);
	}
	
	/**
	 * Initialise the
	 * 
	 * @param windowWidth
	 * @param windowHeight
	 * @return
	 */
	private boolean init( int windowWidth, int windowHeight )
	{
		// Create the FBO
		fboID = GL30.glGenFramebuffers();
		// Bind the FBO
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, fboID);
		
		generatePrimitiveTexture(windowWidth, windowHeight);
		
		generateDepthTexture(windowWidth, windowHeight);
		
		// Legacy switch, disable reading to avoid problems with older GPU's
		GL11.glReadBuffer(GL11.GL_NONE);
		// Draw the pixel info to this buffer
		GL11.glDrawBuffer(GL30.GL_COLOR_ATTACHMENT0);
		
		// Unbind the texture
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
		
		// Verify the FBO status
		int bufferStatus = GL30.glCheckFramebufferStatus(GL30.GL_FRAMEBUFFER);
		if ( bufferStatus != GL30.GL_FRAMEBUFFER_COMPLETE )
		{
			System.err.println("Something went wrong with the picker buffers!");
			return false;
		}
		
		// Unbind the FBO
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
		
		// Return false if error occured
		return (GL11.glGetError() == GL11.GL_NO_ERROR);
	}
	
	/**
	 * @param windowWidth
	 * @param windowHeight
	 */
	private void generatePrimitiveTexture( int windowWidth, int windowHeight )
	{
		// Create the texture object for primitives
		pickingTextureID = GL11.glGenTextures();
		
		FloatBuffer primBuffer = BufferUtils.createFloatBuffer(windowWidth * windowHeight
				* 3);
		
		// Bind the primitives texture
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, pickingTextureID);
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL30.GL_RGB32F, windowWidth,
				windowHeight, 0, GL11.GL_RGB, GL11.GL_FLOAT, primBuffer);
		
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER,
				GL11.GL_NEAREST);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER,
				GL11.GL_NEAREST);
		
		// Make this buffer the output of the fragment shaders
		GL30.glFramebufferTexture2D(GL30.GL_DRAW_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0,
				GL11.GL_TEXTURE_2D, pickingTextureID, 0);
	}
	
	/**
	 * @param windowWidth
	 * @param windowHeight
	 */
	private void generateDepthTexture( int windowWidth, int windowHeight )
	{
		// Create the texture object for depth buffer
		depthTextureID = GL11.glGenTextures();
		
		FloatBuffer depthBuffer = BufferUtils.createFloatBuffer(windowWidth
				* windowHeight * 3);
		
		// Bind the depth texture
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, depthTextureID);
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_DEPTH_COMPONENT, windowWidth,
				windowHeight, 0, GL11.GL_DEPTH_COMPONENT, GL11.GL_FLOAT, depthBuffer);
		
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER,
				GL11.GL_LINEAR);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER,
				GL11.GL_LINEAR);
		
		GL30.glFramebufferTexture2D(GL30.GL_DRAW_FRAMEBUFFER, GL30.GL_DEPTH_ATTACHMENT,
				GL11.GL_TEXTURE_2D, depthTextureID, 0);
	}
	
	/**
	 * Delete and recreate the textures for the active framebuffer
	 * 
	 * @param windowWidth
	 * @param windowHeight
	 */
	public void regenerateTextures( int windowWidth, int windowHeight )
	{
		WIDTH = windowWidth;
		HEIGHT = windowHeight;
		
		// delete all existing textures
		GL11.glDeleteTextures(pickingTextureID);
		GL11.glDeleteTextures(depthTextureID);
		
		// Bind the frame buffer again
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, fboID);
		
		generatePrimitiveTexture(windowWidth, windowHeight);
		
		generateDepthTexture(windowWidth, windowHeight);
		
		// Legacy switch, disable reading to avoid problems with older GPU's
		GL11.glReadBuffer(GL11.GL_NONE);
		// Draw the pixel info to this buffer
		GL11.glDrawBuffer(GL30.GL_COLOR_ATTACHMENT0);
		
		// Unbind the texture
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
		
		// Verify the FBO status
		int bufferStatus = GL30.glCheckFramebufferStatus(GL30.GL_FRAMEBUFFER);
		if ( bufferStatus != GL30.GL_FRAMEBUFFER_COMPLETE )
		{
			System.err.println("Something went wrong with the picker buffers!");
			return;
		}
		
		// Unbind the FBO
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
	}
	
	/**
	 * Release all resources
	 * 
	 * @return
	 */
	public boolean cleanup()
	{
		// delete all existing textures
		GL11.glDeleteTextures(pickingTextureID);
		GL11.glDeleteTextures(depthTextureID);
		
		GL30.glDeleteFramebuffers(fboID);
		
		return true;
	}
	
	/**
	 * Bind the FBO to the drawing framebuffer
	 */
	public static void enableWriting()
	{
		GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, fboID);
	}
	
	/**
	 * Unbind the FBO
	 */
	public static void disableWriting()
	{
		GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, 0);
	}
	
	/**
	 * Pick a pixel from the viewport and return a wrapper object
	 * 
	 * @param viewPortX
	 * @param viewPortY
	 * @return
	 */
	public static PickedPixel readPixel( int viewPortX, int viewPortY )
	{
		// Verify the FBO status
		int bufferStatus = GL30.glCheckFramebufferStatus(GL30.GL_FRAMEBUFFER);
		if ( bufferStatus != GL30.GL_FRAMEBUFFER_COMPLETE )
		{
			System.err.println("Something went wrong with the picker buffers!");
			return null;
		}
		
		// Bind the FBO
		GL30.glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, fboID);
		// Read from the color buffer
		GL11.glReadBuffer(GL30.GL_COLOR_ATTACHMENT0);
		
		// Invert Y because origin is bottom left
		viewPortY = HEIGHT - viewPortY;
		
		// System.out.println("Picking pixel: " + viewPortX + ":" + viewPortY);
		
		/*
		 * Generate floatbuffer, We get back the following items
		 * ObjectID Index of the object = n-th entity
		 * DrawID Index of the draw call within the object = n-th vertex of the object
		 * PrimID Primitive index inside the draw call
		 */
		FloatBuffer pixelBuffer = BufferUtils.createFloatBuffer(3);
		
		// Read one pixel, at vwX - vwY
		GL11.glReadPixels(viewPortX, viewPortY, 1, 1, GL11.GL_RGB, GL11.GL_FLOAT,
				pixelBuffer);
		
		/* DEBUG */
		// debugTexture();
		
		// Unbind the buffer
		GL11.glReadBuffer(GL11.GL_NONE);
		// Unbind the FBO
		GL30.glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, 0);
		
		return new PickedPixel(pixelBuffer);
		// return null;
	}
	
	/**
	 * Return the texture used for colorpicking
	 * 
	 * @return
	 */
	public static int getPickingTextureID()
	{
		return pickingTextureID;
	}
	
	/**
	 * Dump whole colour texture
	 */
	private static void debugTexture()
	{
		/*
		 * Generate floatbuffer, We get back the following items
		 * ObjectID Index of the object = n-th entity
		 * DrawID Index of the draw call within the object = n-th vertex of the object
		 * PrimID Primitive index inside the draw call
		 */
		FloatBuffer pixelBuffer = BufferUtils.createFloatBuffer(WIDTH * HEIGHT * 3);
		
		// Read one pixel, at vwX - vwY
		GL11.glReadPixels(0, 0, WIDTH, HEIGHT, GL11.GL_RGB, GL11.GL_FLOAT, pixelBuffer);
		// Loop row
		for (int i = 0; i < WIDTH; i++)
		{
			// Loop column
			for (int j = 0; j < HEIGHT; j++)
			{
				System.out.println(i + ":" + j + " ->"
						+ pixelBuffer.get((j + i * WIDTH) * 3));
				System.out.println(i + ":" + j + " ->"
						+ pixelBuffer.get((j + i * WIDTH) * 3 + 1));
				System.out.println(i + ":" + j + " ->"
						+ pixelBuffer.get((j + i * WIDTH) * 3 + 2));
			}
		}
	}
	
}
