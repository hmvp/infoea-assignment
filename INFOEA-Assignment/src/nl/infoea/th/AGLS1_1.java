/**
 * 
 */
package nl.infoea.th;

/**
 * @author hiram
 *
 */
public class AGLS1_1 extends AdaptiveGeneticLocalSearch {

	/**
	 * @param seed
	 */
	public AGLS1_1(long seed) {
		super(seed);
		
		alpha = 0.1;
		beta = 0.1;
	}
}
