package nl.infoea.th;

import java.util.Random;

import AbstractClasses.ProblemDomain;
import AbstractClasses.ProblemDomain.HeuristicType;

public class Util {
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
}