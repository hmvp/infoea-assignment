package nl.infoea.th;

import java.util.PriorityQueue;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.TreeSet;

import nl.infoea.th.ProblemRunner.Heuristic;
import AbstractClasses.HyperHeuristic;
import AbstractClasses.ProblemDomain;
import AbstractClasses.ProblemDomain.HeuristicType;

public class GeneticLocalSearch extends HyperHeuristic {

	/**
	 * creates a new ExampleHyperHeuristic object with a random seed
	 */
	public GeneticLocalSearch(long seed) {
		super(seed);
	}

	/**
	 * This method defines the strategy of the hyper-heuristic
	 * 
	 * @param problem
	 *            the problem domain to be solved
	 */
	public void solve(ProblemDomain problem) {

		// it is often a good idea to record the number of low level heuristics,
		// as this changes depending on the problem domain
		// int numberOfHeuristics = problem.getNumberOfHeuristics();

		double currentObjectiveFunctionValues = Double.POSITIVE_INFINITY;

		// initialise the solution at index 0 in the solution memory array
		problem.initialiseSolution(0);
		problem.setMemorySize(2);

		// the main loop of any hyper-heuristic, which checks if the time limit
		// has been reached
		while (!hasTimeExpired()) {

			int mutationHeuristicToApply =
					Util.getRandomHeuristicOfType(rng, problem,
							HeuristicType.MUTATION);

			int localSearchHeuristicToApply =
					Util.getRandomHeuristicOfType(rng, problem,
							HeuristicType.LOCAL_SEARCH);

			problem.applyHeuristic(mutationHeuristicToApply, 0, 1);
			double newObjFunctionValue =
					problem.applyHeuristic(localSearchHeuristicToApply, 1, 1);

			double delta = currentObjectiveFunctionValues - newObjFunctionValue;

			// all of the problem domains are implemented as minimisation
			// problems. A lower fitness means a better solution.
			if (delta > 0 || rng.nextBoolean()) {
				// if there is an improvement then we 'accept' the solution by
				// copying the new solution into memory index 0
				problem.copySolution(1, 0);
				// we also set the current objective function value to the new
				// function value, as the new solution is now the current
				// solution
				currentObjectiveFunctionValues = newObjFunctionValue;
			}

			// one iteration has been completed, so we return to the start of
			// the main loop and check if the time has expired
		}
	}

	/**
	 * this method must be implemented, to provide a different name for each
	 * hyper-heuristic
	 * 
	 * @return a string representing the name of the hyper-heuristic
	 */
	public String toString() {
		return "Genetic Local Search";
	}
}
