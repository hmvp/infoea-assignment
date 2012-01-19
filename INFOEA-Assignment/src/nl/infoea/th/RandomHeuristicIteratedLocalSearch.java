package nl.infoea.th;

import AbstractClasses.HyperHeuristic;
import AbstractClasses.ProblemDomain;

/**
 * Based on ExampleHyperHeuristic1.java from the CHeSC project
 * 
 * This class presents an example hyper-heuristic demonstrates basic
 * functionality. it first determines how many low level heuristics exist for
 * the problem domain to be solved. it applies a random low level
 * hyper-heuristic to the current solution. if there is an improvement in
 * solution quality the new solution is accepted. if the new solution is worse
 * than the current solution, it is accepted with a 50% probability.
 * <p>
 * the easiest way to create your own strategy is to modify the solve method of
 * this class.
 * <p>
 * we suggest that the reader goes through the example code of this class, and
 * then reads the notes below, which provide further clarification.
 * <p>
 * It is important to note that every call to the hasTimeExpired() method
 * records the best fitness function value found so far by the hyper-heuristic.
 * This is a mechanism which ensures that only solutions found within the time
 * limit are used for scoring, and it is intended to ensure that there is NO
 * BENEFIT to exceeding the time limit. Every time a low level heuristic is
 * applied with ProblemDomain.applyHeuristic(), the ProblemDomain object updates
 * the "best fitness found so far". However, this is ONLY recorded for scoring
 * purposes when the hasTimeExpired() method is called. For this reason it is
 * beneficial to put as many hasTimeExpired() checks into your hyper-heuristic
 * code as possible. Ideally, for many hyper-heuristics, this will be after
 * every application of a low level heuristic, rather than at the end of each
 * iteration (every iteration may contain multiple low level heuristic
 * applications).
 * 
 * In the extreme case, if there are no calls to the hasTimeExpired() method,
 * then the hyper-heuristic will iterate for as long as the user wants. It could
 * obtain a much better solution than the other competitors by using much more
 * time. However, this solution will never be recorded for scoring purposes
 * because the hasTimeExpired() method was never called. While we have taken
 * these reasonable steps to negate any advantage from exceeding the time limit,
 * we admit that we may not have thought of everything! Therefore we issue this
 * warning: attempting to obtain advantage by exceeding the time limit is
 * definitely not in the spirit of the competiton, and will result in
 * disqualification.
 * <p>
 * we only initialise the solution at index 0 in the memory, we do not need to
 * initialise the solution at index 1 because a solution is subequently created
 * and placed there when the randomly selected low level heuristic is applied.
 * the solution at index 0 needs to be initialised because the chosen heuristic
 * must be applied to it. if it is not initialised then there would be nothing
 * to modify.
 * <p>
 * using two indices of memory is the standard method to handle a single point
 * search. it is not the only way however. in this example we treat 0 as the
 * current solution, and a new candidate solution is stored temporarily at index
 * 1. This is just the method by which we choose to handle the solutions, but it
 * is up to the user to manage the solution memory. there is technically no
 * reason why we cannot set the memory size to ten, and only use indices 9 and
 * 10.
 */

public class RandomHeuristicIteratedLocalSearch extends HyperHeuristic 
{

	/**
	 * creates a new ExampleHyperHeuristic object with a random seed
	 */
	public RandomHeuristicIteratedLocalSearch(long seed) 
	{
		super(seed);
	}

	/**
	 * This method defines the strategy of the hyper-heuristic
	 * 
	 * @param problem
	 *            the problem domain to be solved
	 */
	public void solve(ProblemDomain problem) 
	{

		// it is often a good idea to record the number of low level heuristics,
		// as this changes depending on the problem domain
		//int numberOfHeuristics = problem.getNumberOfHeuristics();

		problem.setMemorySize(2);

		// initialise the solution at index 0 in the solution memory array
		problem.initialiseSolution(0);

		double currentObjectiveFunctionValues = Double.POSITIVE_INFINITY;

		// the main loop of any hyper-heuristic, which checks if the time limit
		// has been reached
		while (!hasTimeExpired()) 
		{

			int mutationHeuristicToApply = 1;
					//Util.getRandomHeuristicOfType(rng, problem, HeuristicType.MUTATION);

			int localSearchHeuristicToApply = 1;
					//Util.getRandomHeuristicOfType(rng, problem,
					//		HeuristicType.LOCAL_SEARCH);

			problem.applyHeuristic(mutationHeuristicToApply, 0, 1);
			double newObjFunctionValue =
					problem.applyHeuristic(localSearchHeuristicToApply, 1, 1);

			double delta = currentObjectiveFunctionValues - newObjFunctionValue;

			// all of the problem domains are implemented as minimisation
			// problems. A lower fitness means a better solution.
			if (delta > 0 || rng.nextBoolean()) 
			{
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
	public String toString() 
	{
		return "Iterated Local Search";
	}
}
