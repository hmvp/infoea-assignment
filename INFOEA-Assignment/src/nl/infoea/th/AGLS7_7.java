/**
 * 
 */
package nl.infoea.th;

/**
 * @author hiram
 *
 */
public class AGLS7_7 extends AdaptiveGeneticLocalSearch {

	/**
	 * @param seed
	 */
	public AGLS7_7(long seed) {
		super(seed);
	}

	
	protected static double alpha = 0.7;
	protected static double beta = 0.7;

}
