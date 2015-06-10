/**
 * 
 */
package loader;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import javax.imageio.ImageIO;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

/**
 * @author Bert
 *         Source:
 *         http://stackoverflow.com/questions/5757884/transparent-png-isnt-transparent-in-
 *         lwjl
 *         http://stackoverflow.com/questions/10801016/lwjgl-textures-and-strings
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
		// Get the byte sequence type
		int byteSequence = image.getType();
		
		// Get the full pixelArray
		byte[] byteArray = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
		
		/* DEBUG */
		/*
		 * System.out.println("Image type: " + image.getType());
		 * System.out.println("Got " + byteArray.length + " bytes");
		 * // print four first bytes
		 * System.out.println(Byte.toUnsignedInt(byteArray[0]));
		 * System.out.println(Byte.toUnsignedInt(byteArray[1]));
		 * System.out.println(Byte.toUnsignedInt(byteArray[2]));
		 * System.out.println(Byte.toUnsignedInt(byteArray[3]));
		 */
		
		/* BYTEOFFSET per type */
		int redOffset = 0;
		int greenOffset = 0;
		int blueOffset = 0;
		int alphaOffset = 0;
		if ( byteSequence == BufferedImage.TYPE_4BYTE_ABGR )
		{
			// set offset
			alphaOffset = 0;
			blueOffset = 1;
			greenOffset = 2;
			redOffset = 3;
		}
		else if ( byteSequence == BufferedImage.TYPE_3BYTE_BGR )
		{
			blueOffset = 0;
			greenOffset = 1;
			redOffset = 2;
			// No alpha
		}
		else
		{
			// Don't know how to handle this image
			System.err.println("Unknown buffer byte order type! -> " + byteSequence);
			return 0;
		}
		
		// Generate the texture buffer for RGBA mode
		ByteBuffer buffer = BufferUtils.createByteBuffer(image.getHeight()
				* image.getHeight() * BYTES_PER_PIXEL_RGBA);
		
		// Switch on the amount of bytes per pixel in the bytearray
		if ( bytesPerPixel == BYTES_PER_PIXEL_RGBA )
		{
			// Copy all data into the bytebuffer
			for (int i = 0; i < byteArray.length; i = i + BYTES_PER_PIXEL_RGBA)
			{
				buffer.put(byteArray[i + redOffset]); // RED
				buffer.put(byteArray[i + greenOffset]); // GREEN
				buffer.put(byteArray[i + blueOffset]); // BLUE
				buffer.put(byteArray[i + alphaOffset]); // Alpha (1-255)
			}
			
		}
		else if ( bytesPerPixel == BYTES_PER_PIXEL_RGB )
		{			
			// Copy all data into the bytebuffer
			for (int i = 0; i < byteArray.length; i = i + BYTES_PER_PIXEL_RGB)
			{
				/* Load in RGBA sequence */
				buffer.put(byteArray[i + redOffset]); // RED
				buffer.put(byteArray[i + greenOffset]); // GREEN
				buffer.put(byteArray[i + blueOffset]); // BLUE
				
				// Set alpha to one
				buffer.put((byte) 255);
			}
		}
		else
		{
			// Print error
			System.err.println("Not the right amount of bytes per pixel!");
			// Empty the buffer
			buffer.clear();
			return 0;
		}
		
		// Flip the buffer, since we are done filling it
		buffer.flip();
		
		/* DEBUG */
		// System.out.println("Filled " + buffer.remaining() + " bytes");
		
		/* Generate texture of the RGBA pixel buffer */
		// Generate texture
		int textureID = GL11.glGenTextures();
		// Bind the texture as a 2d texture
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureID);
		
		// Add some GL params
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
		
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
		
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		
		// Send texel data to OpenGL
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, image.getWidth(),
				image.getHeight(), 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);
		
		// Return the textureID
		return textureID;
	}
	
	/**
	 * Creates an image(buffer) from the given inputstream
	 * 
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
