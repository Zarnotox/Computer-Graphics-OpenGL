/**
 * 
 */
package callbacks;

import glStart.RenderResources;

import org.lwjgl.glfw.GLFWCharCallback;

/**
 * @author Bert
 */
public class CharHandler extends GLFWCharCallback {
	
	/**
	 * The resources for rendering
	 */
	private RenderResources res;
	
	/**
	 * @param res
	 */
	public CharHandler( RenderResources res )
	{
		this.res = res;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.lwjgl.glfw.GLFWCharCallback#invoke(long, int)
	 */
	@Override
	public void invoke( long window, int codepoint )
	{
		// Get the first character from the codepoint
		char character = Character.toChars(codepoint)[0];
		
		/* DEBUG */
		// System.out.println("Char: " + character);
		
		// Switch on Unicode keyvalues
		switch (character) {
		/* CAMERA SWITCHING */
		case 'n':
			/* DEBUG */
			// System.out.println("Next cam");
			// Next camera
			res.nextCamera();
			break;
		
		case 'p':
			/* DEBUG */
			// System.out.println("Prev cam");
			// Previous camera
			res.previousCamera();
			break;
		}
		
	}
	
}
