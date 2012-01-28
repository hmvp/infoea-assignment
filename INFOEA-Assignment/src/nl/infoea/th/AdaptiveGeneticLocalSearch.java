package nl.infoea.th;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import AbstractClasses.HyperHeuristic;
import AbstractClasses.ProblemDomain;

public class AdaptiveGeneticLocalSearch extends HyperHeuristic {

	private SortedMap<Double, Integer> solutions;
	private int workingMemoryLocation;
	private int localSearchHeuristicToApply;

	private static double pMin = 0.1;
	private double pMax;
	protected static double alpha = 0.5;
	protected static double beta = 0.5;

	// P_i(t): Chance to pick heuristic i
	private Map<AdaptiveChoice, Double> Pt =
			new HashMap<AdaptiveChoice, Double>();
	// Q_i(t): Estimate reward of i
	private Map<AdaptiveChoice, Double> Qt =
			new HashMap<AdaptiveChoice, Double>();

	private Comparator<AdaptiveChoice> bvc =
			new ValueComparator<AdaptiveChoice, Double>(Qt);

	/**
	 * creates a new ExampleHyperHeuristic object with a random seed
	 */
	public AdaptiveGeneticLocalSearch(long seed) {
		super(seed);
		// store solutions sorted by fitness, to easily get the worst solution.
		solutions = new TreeMap<Double, Integer>();
	}

	public enum AdaptiveChoice {
		Crossover, Mutation
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
		int crossoverHeuristicToApply = Util.getCrossoverHeuristic(problem);
		int mutationHeuristicToApply = Util.getMutationHeuristic(problem);
		localSearchHeuristicToApply = Util.getLocalSearchHeuristic(problem);

		// k: Number of possible heuristics to choose from
		int k = AdaptiveChoice.values().length;
		pMax = 1 - (k - 1) * pMin;

		for (AdaptiveChoice choice : AdaptiveChoice.values()) {
			Pt.put(choice, 1d / k);
			Qt.put(choice, 1d);
		}

		// the main loop of any hyper-heuristic, which checks if the time limit
		// has been reached
		while (!hasTimeExpired()) {
			// Proportinally select Operator
			AdaptiveChoice selectedHeuristic = proportionalSelectOperator(Pt);

			int parent1Location = rng.nextInt(populationSize);
			// make sure parent2 is not the same
			int parent2Location =
					(parent1Location + rng.nextInt(populationSize - 1))
							% populationSize;

			// .5 chance to do crossover
			if (selectedHeuristic == AdaptiveChoice.Crossover) {
				problem.applyHeuristic(crossoverHeuristicToApply,
						parent1Location, parent2Location, workingMemoryLocation);
				process(problem, false, selectedHeuristic);
			} else { // else do one ILS iteration (mutation -> localsearch)

				problem.applyHeuristic(mutationHeuristicToApply,
						parent1Location, workingMemoryLocation);
				process(problem, true, selectedHeuristic);

				problem.applyHeuristic(mutationHeuristicToApply,
						parent2Location, workingMemoryLocation);
				process(problem, true, selectedHeuristic);
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
	private void process(ProblemDomain problem, boolean allowWorse,
			AdaptiveChoice chosenOperator) {
		double reward =
				problem.applyHeuristic(localSearchHeuristicToApply,
						workingMemoryLocation, workingMemoryLocation);

		double currentQa = Qt.get(chosenOperator);
		Qt.put(chosenOperator, currentQa + alpha * (reward - currentQa));

		// a*
		TreeMap<AdaptiveChoice, Double> Qtsorted =
				new TreeMap<AdaptiveChoice, Double>(bvc);
		Qtsorted.putAll(Qt);
		AdaptiveChoice bestOperator = Qtsorted.firstKey();

		// Pa*(t)
		double bestPa = Pt.get(bestOperator);
		Pt.put(bestOperator, bestPa + beta * (pMax - bestPa));

		for (AdaptiveChoice choice : AdaptiveChoice.values()) {
			if (choice != bestOperator) {
				double ptChoice = Pt.get(choice);
				Pt.put(choice, ptChoice + beta * (pMin - ptChoice));
			}
		}

		// replace worst solution if better
		// last in solutions is worst, since problem is minimum based
		double worstSolutionKey = solutions.lastKey();

		if (reward < worstSolutionKey || (allowWorse && rng.nextBoolean())) {
			int worstSolutionId = solutions.get(worstSolutionKey);
			problem.copySolution(workingMemoryLocation, worstSolutionId);
			solutions.remove(worstSolutionKey);
			solutions.put(reward, worstSolutionId);
		}
	}

	private AdaptiveChoice proportionalSelectOperator(
			Map<AdaptiveChoice, Double> P) {
		AdaptiveChoice selectedOperator = null;

		// double total = Util.sum(P.values()); // total should be 1
		double selectedChance = rng.nextDouble();
		double currentChance = 0d;
		for (AdaptiveChoice key : P.keySet()) {
			double chance = P.get(key);

			currentChance += chance;
			if (selectedChance < currentChance) {
				selectedOperator = key;
				break;
			}
		}
		return selectedOperator;
	}

	/**
	 * this method must be implemented, to provide a different name for each
	 * hyper-heuristic
	 * 
	 * @return a string representing the name of the hyper-heuristic
	 */
	public String toString() {
		return "Adaptive Genetic Local Search alpha"+alpha+" beta"+beta;
	}
}