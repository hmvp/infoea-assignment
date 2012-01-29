/**
 * 
 */
package nl.infoea.th;

/**
 * @author hiram
 *
 */
public class AGLS5_5 extends AdaptiveGeneticLocalSearch {

	/**
	 * @param seed
	 */
	public AGLS5_5(long seed) {
		super(seed);
		
		alpha = 0.5;
		beta = 0.5;
	}
}
