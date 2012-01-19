package nl.infoea.th;

import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import AbstractClasses.HyperHeuristic;
import AbstractClasses.ProblemDomain;

public class GeneticLocalSearch extends HyperHeuristic {

	private SortedMap<Double, Integer> solutions;
	private int workingMemoryLocation;
	
	private Map<Problem, Integer> crossoverHeuristic = new HashMap<Problem, Integer>();
	private Map<Problem, Integer> localSearchHeuristic = new HashMap<Problem, Integer>();
	
	{
		crossoverHeuristic.put(Problem.SAT, 9);
		crossoverHeuristic.put(Problem.BinPacking, 8);//8
		crossoverHeuristic.put(Problem.PersonnelScheduling, 11);//10-12
		crossoverHeuristic.put(Problem.FlowShop, 13);//12-14
		
		localSearchHeuristic.put(Problem.SAT, 7);
		localSearchHeuristic.put(Problem.BinPacking, 7); //6,7
		localSearchHeuristic.put(Problem.PersonnelScheduling, 7);//5-9
		localSearchHeuristic.put(Problem.FlowShop, 10);//8-11
	}

	/**
	 * creates a new ExampleHyperHeuristic object with a random seed
	 */
	public GeneticLocalSearch(long seed) {
		super(seed);
		// store solutions sorted by fitness, to easily get the worst solution.
		solutions = new TreeMap<Double, Integer>();
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

		// the main loop of any hyper-heuristic, which checks if the time limit
		// has been reached
		while (!hasTimeExpired()) {

			int crossoverHeuristicToApply = crossoverHeuristic.get(Problem.asEnum(problem));

			int localSearchHeuristicToApply = localSearchHeuristic.get(Problem.asEnum(problem));

			int parent1Location = rng.nextInt(populationSize);
			int parent2Location = rng.nextInt(populationSize);

			// .5 chance to do crossover
			if (rng.nextBoolean()) {
				problem.applyHeuristic(crossoverHeuristicToApply,
								parent1Location, parent2Location,
								workingMemoryLocation);
				double lsOutcome =
						problem.applyHeuristic(localSearchHeuristicToApply,
								workingMemoryLocation, workingMemoryLocation);
				process(problem, lsOutcome);
			} else { // else do LS
				double outcome1 =
						problem.applyHeuristic(localSearchHeuristicToApply,
								parent1Location, workingMemoryLocation);
				process(problem, outcome1);
				double outcome2 =
						problem.applyHeuristic(localSearchHeuristicToApply,
								parent2Location, workingMemoryLocation);
				process(problem, outcome2);
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
	private void process(ProblemDomain problem, double outcome) {
		// replace worst solution if better
		// last in solutions is worst, since problem is minimum based
		double worstSolutionKey = solutions.lastKey();

		if (outcome < worstSolutionKey) {
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