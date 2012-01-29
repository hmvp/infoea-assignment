/**
 * 
 */
package nl.infoea.th;

/**
 * @author hiram
 *
 */
public class AGLS5_1 extends AdaptiveGeneticLocalSearch {

	/**
	 * @param seed
	 */
	public AGLS5_1(long seed) {
		super(seed);
		
		alpha = 0.5;
		beta = 0.1;
	}
}
