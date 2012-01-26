package nl.infoea.th;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import AbstractClasses.ProblemDomain;
import AbstractClasses.ProblemDomain.HeuristicType;

public class Util {

	private static Map<Problem, Integer> mutateHeuristic =
			new HashMap<Problem, Integer>();
	private static Map<Problem, Integer> crossoverHeuristic =
			new HashMap<Problem, Integer>();
	private static Map<Problem, Integer> localSearchHeuristic =
			new HashMap<Problem, Integer>();

	static {
		crossoverHeuristic.put(Problem.SAT, 9);// 9,10
		crossoverHeuristic.put(Problem.BinPacking, 7);// 7
		crossoverHeuristic.put(Problem.PersonnelScheduling, 10);// 8-10
		crossoverHeuristic.put(Problem.FlowShop, 12);// 11-14
		crossoverHeuristic.put(Problem.VRP, 5);// 5,6
		crossoverHeuristic.put(Problem.TSP, 9);// 9, 10, 11, 12
		
		mutateHeuristic.put(Problem.SAT, 1);// 0, 1, 2, 3, 4, 5
		mutateHeuristic.put(Problem.BinPacking, 2);// 0,3,5
		mutateHeuristic.put(Problem.PersonnelScheduling, 11);// 11
		mutateHeuristic.put(Problem.FlowShop, 2);// 0-4
		mutateHeuristic.put(Problem.VRP, 1);// 0,1,7
		mutateHeuristic.put(Problem.TSP, 2);// 0, 1, 2, 3, 4

		localSearchHeuristic.put(Problem.SAT, 7);// 7,8
		localSearchHeuristic.put(Problem.BinPacking, 6);// 4, 6
		localSearchHeuristic.put(Problem.PersonnelScheduling, 4);// 0-4
		localSearchHeuristic.put(Problem.FlowShop, 9);// 7-10
		localSearchHeuristic.put(Problem.VRP, 9);// 4,8,9
		localSearchHeuristic.put(Problem.TSP, 7);// 6, 7, 8
	}

	/**
	 * Gets a random heuristic of specified type. If non exists gets random
	 * heuristic.
	 * 
	 * @param problem
	 *            ProblemDomain the heuristics are run on
	 * @param type
	 *            Type of heuristic
	 * @return Heuristic of specified type or random if none found.
	 */
	public static int getRandomHeuristicOfType(Random rng,
			ProblemDomain problem, HeuristicType type) {
		int[] heuristics = problem.getHeuristicsOfType(type);
		if (heuristics != null) {
			int selected = rng.nextInt(heuristics.length);
			return heuristics[selected];
		} else {
			return rng.nextInt(problem.getNumberOfHeuristics());
		}
	}

	/**
	 * @param problem
	 * @return heuristic index
	 */
	public static int getMutationHeuristic(ProblemDomain problem) {
		return mutateHeuristic.get(Problem.asEnum(problem));
	}

	/**
	 * @param problem
	 * @return heuristic index
	 */
	public static int getCrossoverHeuristic(ProblemDomain problem) {
		return crossoverHeuristic.get(Problem.asEnum(problem));
	}

	/**
	 * @param problem
	 * @return heuristic index
	 */
	public static int getLocalSearchHeuristic(ProblemDomain problem) {
		return localSearchHeuristic.get(Problem.asEnum(problem));
	}

	public static double sum(Collection<? extends Number> nums) {
		double s = 0d;
		for (Number num : nums) {
			s += num.doubleValue();
		}
		return s;
	}
}