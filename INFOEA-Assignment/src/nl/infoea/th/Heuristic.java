/**
 * 
 */
package nl.infoea.th;

import nl.infoea.th.Heuristics.AdaptiveGeneticLocalSearch;
import nl.infoea.th.Heuristics.GeneticLocalSearch;
import nl.infoea.th.Heuristics.IteratedLocalSearch;
import nl.infoea.th.Heuristics.RandomHeuristicIteratedLocalSearch;
import AbstractClasses.HyperHeuristic;

/**
 * Abstract over the underlying hyperheuristics
 * This enum makes it easier to try multiple heuristics or heuristics with 
 * different parameters
 *
 */
public enum Heuristic 
{
	ILS {
		@Override
		protected HyperHeuristic createHyperHeuristic(long seed) {
			return new IteratedLocalSearch(seed);
		}
	}, 
	GLS {
		@Override
		protected HyperHeuristic createHyperHeuristic(long seed) {
			return new GeneticLocalSearch(seed);
		}
	}, 
	AGLS {
		@Override
		protected HyperHeuristic createHyperHeuristic(long seed) {
			return new AdaptiveGeneticLocalSearch(seed);
		}
	}, 
	RHILS {
		@Override
		protected HyperHeuristic createHyperHeuristic(long seed) {
			return new RandomHeuristicIteratedLocalSearch(seed);
		}
	}, 
	AGLS1_1 {
		@Override
		protected HyperHeuristic createHyperHeuristic(long seed) {
			return new AdaptiveGeneticLocalSearch(seed, 0.1, 0.1);
		}
	},
	AGLS1_5 {
		@Override
		protected HyperHeuristic createHyperHeuristic(long seed) {
			return new AdaptiveGeneticLocalSearch(seed, 0.1, 0.5);
		}
	},
	AGLS5_1 {
		@Override
		protected HyperHeuristic createHyperHeuristic(long seed) {
			return new AdaptiveGeneticLocalSearch(seed, 0.5, 0.1);
		}
	},
	AGLS5_5 {
		@Override
		protected HyperHeuristic createHyperHeuristic(long seed) {
			return new AdaptiveGeneticLocalSearch(seed, 0.5, 0.5);
		}
	},
	AGLS1_7 {
		@Override
		protected HyperHeuristic createHyperHeuristic(long seed) {
			return new AdaptiveGeneticLocalSearch(seed, 0.1, 0.7);
		}
	},
	AGLS7_1 {
		@Override
		protected HyperHeuristic createHyperHeuristic(long seed) {
			return new AdaptiveGeneticLocalSearch(seed, 0.7, 0.1);
		}
	},
	AGLS7_7 {
		@Override
		protected HyperHeuristic createHyperHeuristic(long seed) {
			return new AdaptiveGeneticLocalSearch(seed, 0.7, 0.7);
		}
	},
	ILSFlowShop0 {
		@Override
		protected HyperHeuristic createHyperHeuristic(long seed) {
			return new IteratedLocalSearch(seed, 0);
		}
	},
	ILSFlowShop1 {
		@Override
		protected HyperHeuristic createHyperHeuristic(long seed) {
			return new IteratedLocalSearch(seed, 1);
		}
	},
	ILSFlowShop2 {
		@Override
		protected HyperHeuristic createHyperHeuristic(long seed) {
			return new IteratedLocalSearch(seed, 2);
		}
	},
	ILSFlowShop3 {
		@Override
		protected HyperHeuristic createHyperHeuristic(long seed) {
			return new IteratedLocalSearch(seed, 3);
		}
	},
	ILSFlowShop4 {
		@Override
		protected HyperHeuristic createHyperHeuristic(long seed) {
			return new IteratedLocalSearch(seed, 4);
		}
	};
	
	/**
	 * This method creates the relevant HyperHeuristic object from the index
	 * given as a parameter. after the HyperHeuristic object is created, its
	 * time limit is set.
	 * 
	 * Deze methode MOET synchronized omdat er een wtf static in de HyperHeuristic class zit die nergens op slaat!!
	 */
	public HyperHeuristic getHyperHeuristic(long timeLimit, long seed)
	{
		//just synchronize on some object since the method lock would be on different objects
		synchronized (AGLS) {
			HyperHeuristic h = createHyperHeuristic(seed);
			h.setTimeLimit(timeLimit);
			return h;
		}
	}

	/**
	 * Instantiate the appropriate class unfortunately this has to be a overridden method
	 * @param seed
	 * @return
	 */
	abstract protected HyperHeuristic createHyperHeuristic(long seed);
}