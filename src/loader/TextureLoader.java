/**
 * 
 */
package loader;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import javax.imageio.ImageIO;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

/**
 * @author Bert
 * 
 * Source:
 * http://stackoverflow.com/questions/5757884/transparent-png-isnt-transparent-in-lwjl
 * http://stackoverflow.com/questions/10801016/lwjgl-textures-and-strings
 */
public class TextureLoader {
	
	/**
	 * The amount of bytes needed to represent one pixel in RGBA mode
	 */
	private static int BYTES_PER_PIXEL_RGBA = 4;
	
	/**
	 * The amount of bytes needed to represent one pixel in RGB mode
	 */
	private static int BYTES_PER_PIXEL_RGB = 3;
	
	/**
	 * The location of the imageFile
	 */
	private BufferedImage image;
	
	/**
	 * Constructor
	 * 
	 * @param imageData
	 */
	public TextureLoader( InputStream imageData )
	{
		this.image = createBufferedImage(imageData);
	}
	
	/**
	 * Load the texture data into the memory and return its id
	 * 
	 * @return
	 */
	public int loadTexture()
	{
		if ( image == null )
		{
			return 0;
		}
		
		// Get the amount of used bytes per pixel
		int bytesPerPixel = image.getRaster().getNumDataElements();
		
		// Get the full pixelArray
		byte[] byteArray = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
		
		// Generate a new ByteBuffer for RGBA mode
		/* THE BYTES ARE IN ABGR SEQUENCE! */
		ByteBuffer buffer = null;
		
		/* DEBUG */
		System.out.println("Got " + byteArray.length + " bytes");
		// print four first bytes
		System.out.println(Byte.toUnsignedInt(byteArray[0]));
		System.out.println(Byte.toUnsignedInt(byteArray[1]));
		System.out.println(Byte.toUnsignedInt(byteArray[2]));
		System.out.println(Byte.toUnsignedInt(byteArray[3]));
		
		// Switch on the amount of bytes per pixel in the bytearray
		if ( bytesPerPixel == BYTES_PER_PIXEL_RGBA )
		{
			System.out.println("4Bytes");
			
			buffer = BufferUtils.createByteBuffer(image.getHeight()
					* image.getHeight() * BYTES_PER_PIXEL_RGBA);
			
			// Copy all data into the bytebuffer
			for (int i = 0; i < byteArray.length; i = i + BYTES_PER_PIXEL_RGBA)
			{
				buffer.put(byteArray[i + 3]); // RED
				buffer.put(byteArray[i + 2]); // GREEN
				buffer.put(byteArray[i + 1]); // BLUE
				buffer.put(byteArray[i]); // Alpha (normalized)				
			}
			
			// NEW
			/*buffer = ByteBuffer.allocateDirect(byteArray.length);
			buffer.order(ByteOrder.nativeOrder());
			buffer.put(byteArray, 0, byteArray.length);*/	
			
		}
		else if ( bytesPerPixel == BYTES_PER_PIXEL_RGB )
		{
			/* DEBUG */
			System.out.println(BYTES_PER_PIXEL_RGB + " bytes per pixel detected");
			
			buffer = BufferUtils.createByteBuffer(image.getHeight()
					* image.getHeight() * BYTES_PER_PIXEL_RGBA);
			
			// Copy all data into the bytebuffer
			for (int i = 0; i < byteArray.length; i = i + BYTES_PER_PIXEL_RGB)
			{
				/* Load in RGBA sequence */
				buffer.put(byteArray[i + 2]); // RED
				buffer.put(byteArray[i + 1]); // GREEN
				buffer.put(byteArray[i]); // BLUE
				
				// Set alpha to one
				buffer.put((byte)(1));
			}
		}
		else
		{
			// Print error
			System.err.println("Unrecognized format!");
		}
		
		// Flip the buffer, since we are done filling it
		buffer.flip();
		
		/* DEBUG */
		//System.out.println("Filled " + buffer.remaining() + " bytes");
		
		/* Generate texture of the RGBA pixel buffer */
		// Generate texture
		int textureID = GL11.glGenTextures();
		// Bind the texture as a 2d texture
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureID);
		
		// Add some GL params
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S,
				GL12.GL_CLAMP_TO_EDGE);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T,
				GL12.GL_CLAMP_TO_EDGE);
		
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER,
				GL11.GL_NEAREST);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER,
				GL11.GL_NEAREST);
		
		GL11.glEnable(GL11.GL_BLEND);
		//GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_SRC_ALPHA);
		
		// Send texel data to OpenGL
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, image.getWidth(),
				image.getHeight(), 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);
		
		// Return the textureID	
		return textureID;
	}
	
	/**
	 * Creates an image(buffer) from the given inputstream
	 * @param imageData
	 * @return
	 */
	private BufferedImage createBufferedImage( InputStream imageData )
	{
		BufferedImage img = null;
		
		try
		{
			// Create bufferedimage
			img = ImageIO.read(imageData);
			
			// Close stream
			imageData.close();
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return img;
	}
	
}
