package nl.infoea.th;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

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
public class ProblemRunner 
{

	public static final int RUNS = 5;//5
	public static final int INSTANCES = 10;//10
	public static final Problem[] PROBLEMS = Arrays.copyOf(Problem.values(),4);//Problem.values()
	public static final Heuristic[] HEURISTICS = Arrays.copyOf(Heuristic.values(),3);//Heuristic.values()
	public static final int THREADS = 2;
	public static final long SEED = 123457890;
	public static final long TIMELIMIT = 600000;//600000



	/**
	 * This method creates the relevant HyperHeuristic object from the index
	 * given as a parameter. after the HyperHeuristic object is created, its
	 * time limit is set.
	 * 
	 * Deze methode MOET synchronized omdat er een wtf static in de HyperHeuristic class zit die nergens op slaat!!
	 */
	private synchronized static HyperHeuristic loadHyperHeuristic(Heuristic index,
			long timeLimit, long seed) 
	{
		HyperHeuristic h = null;
		switch (index) 
		{
			case ILS:
				h = new IteratedLocalSearch(seed);
				h.setTimeLimit(timeLimit);
				break;
			case RHILS:
				h = new RandomHeuristicIteratedLocalSearch(seed);
				h.setTimeLimit(timeLimit);
				break;
			case GLS:
				h = new GeneticLocalSearch(seed);
				h.setTimeLimit(timeLimit);
				break;
			case AGLS:
				h = new AdaptiveGeneticLocalSearch(seed);
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
			long instanceseed) 
	{
		ProblemDomain p = null;
		switch (index) 
		{
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

	public static void main(String[] args) 
	{

		//New Scorekeeper
		final ScoreKeeper sc = new ScoreKeeper();
		
		// we first initialise the random number generator for this class
		// it is useful to generate all of the random numbers from one seed, so
		// that the experiments can be easily replicated
		final Random randomNumberGenerator = new Random(SEED);
		
		//Create a threadpool of two threads
		ExecutorService es = Executors.newFixedThreadPool(THREADS);
				
		//loop through runs
		for (int i = 0; i < RUNS; i++)
		{	final int run = i;
			
			// loop through all four problem domains
			for (final Problem problem : PROBLEMS)
			{
	
				// to ensure that all hyperheuristics begin from the same initial
				// solution, we set a seed for each problem domain
				long problemDomainSeed = randomNumberGenerator.nextInt();
	
				// loop through the ten instances in the current problem domain
				for (int j = 0; j < INSTANCES; j++) 
				{	final int instance = j;
					
					// to ensure that all hyperheuristics begin from the same
					// initial solution, we set a seed for each instance
					final long instanceSeed = problemDomainSeed * (instance + 1);
	
					// loop through the hyper-heuristics that we will test in this
					// experiment
					for (final Heuristic heuristic : HEURISTICS)
					{
	
						//Create a new runnable
						es.execute(new Runnable()
						{

							@Override
							public void run()
							{
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
										loadHyperHeuristic(heuristic, TIMELIMIT,
												randomNumberGenerator.nextLong());
			
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
								
								// record score for analysis
								sc.recordScore(run, heuristic, problem, instance,
										problemDomain, hyperHeuristic);
							}
						});
					}
				}
			}
		}
		
		try 
		{
			//Wait untill everything is finished
			es.shutdown();
			es.awaitTermination(5, TimeUnit.DAYS);
			
			sc.analyze();
		} 
		catch (InterruptedException e) 
		{
			e.printStackTrace();
			sc.dump();
		}
	}
}
