package nl.infoea.th;

import java.util.Random;

import AbstractClasses.HyperHeuristic;
import AbstractClasses.ProblemDomain;
import BinPacking.BinPacking;
import FlowShop.FlowShop;
import PersonnelScheduling.PersonnelScheduling;
import SAT.SAT;

/**
 * Based on ExampleRun2.java from the CHeSC project
 * 
 * This class shows an example of how to test a number of hyper heuristics on a
 * number of problem domains. this class is similar to that which will be used
 * to test the hyper-heuristics in the final competition. it is intended to be
 * read after studying ExampleRun1, because it is more complex, and so it is
 * easier to understand if the commands in ExampleRun1 are understood first.
 * <p>
 * we suggest that the reader goes through the example code of this class, and
 * then refers to the notes below, which provide further clarification.
 * <p>
 * For each run, a new object is created for the ProblemDomain, and a new object
 * is created for the HyperHeuristic. This is important because it ensures that
 * the object (particularly the solution memory in the ProblemDomain object) is
 * completely initialised before the next test. It is not sufficient to simply
 * load a new problem instance into the same object as this is not guaranteed to
 * completely initialise the object for a new run.
 * <p>
 * The elapsed time printed at the end of each hyper-heuristic run may be
 * slightly longer than the time limit for the hyper-heuristic, as the last low
 * level heuristic to be called overruns the time limit. However, the
 * hyper_heuristic_object.getBestSolutionValue() method disregards any solution
 * found after the hyper-heuristic exceeds its time limit. So the best solution
 * that is printed at the end has been found within the time limit.
 */
public class ProblemRunner {

	private static final long SEED = 123457890;
	private static final long TIMELIMIT = 600;

	public enum Problem {
		SAT, BinPacking, PersonnelScheduling, FlowShop;
	}

	public enum Heuristic {
		IteratedLocalSearch, GeneticLocalSearch, AdaptiveGeneticLocalSearch;
	}

	/**
	 * This method creates the relevant HyperHeuristic object from the index
	 * given as a parameter. after the HyperHeuristic object is created, its
	 * time limit is set.
	 */
	private static HyperHeuristic loadHyperHeuristic(Heuristic index,
			long timeLimit, Random rng) {
		HyperHeuristic h = null;
		switch (index) {
		case IteratedLocalSearch:
			h = new IteratedLocalSearch(rng.nextLong());
			h.setTimeLimit(timeLimit);
			break;
		case GeneticLocalSearch:
			h = new GeneticLocalSearch(rng.nextLong());
			h.setTimeLimit(timeLimit);
			break;
		case AdaptiveGeneticLocalSearch:
			h = new AdaptiveGeneticLocalSearch(rng.nextLong());
			h.setTimeLimit(timeLimit);
			break;
		}
		return h;
	}

	/**
	 * this method creates the relevant ProblemDomain object from the index
	 * given as a parameter. for each instance, the ProblemDomain is initialised
	 * with an identical seed for each hyper-heuristic. this is so that each
	 * hyper-heuristic starts its search from the same initial solution.
	 */
	private static ProblemDomain loadProblemDomain(Problem index,
			long instanceseed) {
		ProblemDomain p = null;
		switch (index) {
		case SAT:
			p = new SAT(instanceseed);
			break;
		case BinPacking:
			p = new BinPacking(instanceseed);
			break;
		case PersonnelScheduling:
			p = new PersonnelScheduling(instanceseed);
			break;
		case FlowShop:
			p = new FlowShop(instanceseed);
			break;
		}
		return p;
	}

	public static void main(String[] args) {

		ScoreKeeper sc = new ScoreKeeper();
		// we first initialise the random number generator for this class
		// it is useful to generate all of the random numbers from one seed, so
		// that the experiments can be easily replicated
		Random randomNumberGenerator = new Random(SEED);

		// we set the number of hyper-heuristics involved in this example
		// experiment,
		// and a time limit of 10 minutes for each hyper-heuristic run
		long timeLimit = TIMELIMIT;

		// loop through all four problem domains
		for (Problem problem : Problem.values()) {

			// to ensure that all hyperheuristics begin from the same initial
			// solution, we set a seed for each problem domain
			long problemDomainSeed = randomNumberGenerator.nextInt();

			// loop through the ten instances in the current problem domain
			for (int instance = 0; instance < 10; instance++) {

				// we retrieve the exact index of the instance we wish to use

				System.out.println("Problem Domain " + problem);
				System.out.println("\tInstance " + instance);

				// to ensure that all hyperheuristics begin from the same
				// initial solution, we set a seed for each instance
				long instanceSeed = problemDomainSeed * (instance + 1);

				// loop through the hyper-heuristics that we will test in this
				// experiment
				for (Heuristic heuristic : Heuristic.values()) {

					// we create the problem domain object. we give the problem
					// domain index and the unique
					// seed for this instance, so that the problem domain object
					// is initialised in the same way,
					// and each hyper-heuristic will begin from the same initial
					// solution.
					ProblemDomain problemDomain =
							loadProblemDomain(problem, instanceSeed);

					// we create the hyper-heuristic object from the
					// hyperheuristic index. we provide the time limit,
					// which is set after the object is created in the
					// loadHyperHeuristic method
					HyperHeuristic hyperHeuristic =
							loadHyperHeuristic(heuristic, timeLimit,
									randomNumberGenerator);

					// the required instance is loaded in the ProblemDomain
					// object
					problemDomain.loadInstance(instance);

					// critically, the ProblemDomain object is provided to the
					// HyperHeuristic object, so that it knows which problem to
					// solve
					hyperHeuristic.loadProblemDomain(problemDomain);

					// now that all objects have been initialised, the current
					// hyper-heuristic is run on the current instance to produce
					// a solution
					hyperHeuristic.run();

					// for this example, we use the record within each problem
					// domain of the number of times each low level heuristic
					// was called.
					// we sum the results to obtain the total number of times
					// that a low level heuristic was called
					int[] i = problemDomain.getHeuristicCallRecord();
					int counter = 0;
					for (int y : i) {
						counter += y;
					}

					// we print the results of this hyper-heuristic on this
					// problem instance
					// print the name of this hyper-heuristic
					System.out.print("\tHYPER HEURISTIC "
							+ hyperHeuristic.toString());
					// print the best solution found within the time limit
					System.out.print("\tBest: "
							+ hyperHeuristic.getBestSolutionValue());
					// print the elapsed time in seconds
					System.out.print("\tTime Elapsed: "
							+ (hyperHeuristic.getElapsedTime() / 1000.0));
					// print the number of calls to any low level heuristic
					System.out.println("\t Heuristic Calls: " + counter);

					// record score for analysis
					sc.recordScore(heuristic, problem, instance,
							hyperHeuristic.getBestSolutionValue());

					double[] fitnesstrace = hyperHeuristic.getFitnessTrace();
					System.out.print("\tFitness Trace: ");
					for (double f : fitnesstrace) {
						System.out.print(f + " ");
					}
					System.out.println();
				}
			}
		}

		sc.analyze();
	}
}
