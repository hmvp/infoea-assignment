package nl.infoea.th.Heuristics;

import java.util.SortedMap;
import java.util.TreeMap;

import nl.infoea.th.Util;
import AbstractClasses.HyperHeuristic;
import AbstractClasses.ProblemDomain;

public class GeneticLocalSearch extends HyperHeuristic {

	private SortedMap<Double, Integer> solutions;
	private int workingMemoryLocation;
	private int localSearchHeuristicToApply = -1;
	private int crossoverHeuristicToApply = -1;

	/**
	 * creates a new GeneticLocalSearch object with a random seed
	 */
	public GeneticLocalSearch(long seed) {
		super(seed);
		// store solutions sorted by fitness, to easily get the worst solution.
		solutions = new TreeMap<Double, Integer>();
	}

	/**
	 * creates a new GeneticLocalSearch object with a random seed. Uses
	 * parameters to test crossover heuristics
	 */
	public GeneticLocalSearch(long seed, int heuristic) {
		this(seed);
		crossoverHeuristicToApply = heuristic;
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

		int populationSize = 10;
		problem.setMemorySize(populationSize + 1); // 1 extra for heuristics
													// working memory
		workingMemoryLocation = populationSize;
		for (int i = 0; i <= populationSize; i++) {
			problem.initialiseSolution(i);

			// Keep track of worst solution
			solutions.put(problem.getFunctionValue(i), i);
		}

		// get Heuristics to apply
		if (crossoverHeuristicToApply == -1)
			crossoverHeuristicToApply = Util.getCrossoverHeuristic(problem);

		int mutationHeuristicToApply = Util.getMutationHeuristic(problem);

		if (localSearchHeuristicToApply == -1)
			localSearchHeuristicToApply = Util.getLocalSearchHeuristic(problem);

		// the main loop of any hyper-heuristic, which checks if the time limit
		// has been reached
		while (!hasTimeExpired()) {
			int parent1Location = rng.nextInt(populationSize);
			// make sure parent2 is not the same
			int parent2Location =
					(parent1Location + rng.nextInt(populationSize - 1))
							% populationSize;

			// .5 chance to do crossover
			if (rng.nextBoolean()) {
				problem.applyHeuristic(crossoverHeuristicToApply,
						parent1Location, parent2Location, workingMemoryLocation);
				process(problem, false);
			} else { // else do one ILS iteration (mutation -> localsearch)

				problem.applyHeuristic(mutationHeuristicToApply,
						parent1Location, workingMemoryLocation);
				process(problem, true);

				problem.applyHeuristic(mutationHeuristicToApply,
						parent2Location, workingMemoryLocation);
				process(problem, true);
			}

			// one iteration has been completed, so we return to the start of
			// the main loop and check if the time has expired
		}
	}

	/**
	 * 
	 * @param problem
	 * @param outcome
	 */
	private void process(ProblemDomain problem, boolean allowWorse) {
		double outcome =
				problem.applyHeuristic(localSearchHeuristicToApply,
						workingMemoryLocation, workingMemoryLocation);

		// replace worst solution if better
		// last in solutions is worst, since problem is minimum based
		double worstSolutionKey = solutions.lastKey();

		if (outcome < worstSolutionKey || (allowWorse && rng.nextBoolean())) {
			int worstSolutionId = solutions.get(worstSolutionKey);
			problem.copySolution(workingMemoryLocation, worstSolutionId);
			solutions.remove(worstSolutionKey);
			solutions.put(outcome, worstSolutionId);
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