/**
 * 
 */
package callbacks;

import entity.Entity;

/**
 * @author Bert
 */
public interface EntityActionCallback {
	
	/**
	 * Do something when the user has clicked on this entity.
	 * The entity that receives this callback will be given as parameter to this function.
	 * 
	 * @param thisEntity
	 */
	public void doAction( Entity thisEntity );
	
}
