package nl.infoea.th;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import AbstractClasses.HyperHeuristic;
import AbstractClasses.ProblemDomain;

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
	//These fields enable us to switch algorithms and try new stuff
	public static final Problem[] PROBLEMS = new Problem[]
	{
//		Problem.SAT, 
//		Problem.BinPacking, 
//		Problem.PersonnelScheduling, 
		Problem.FlowShop,
		//Problem.VRP,
		//Problem.TSP,
	};//Problem.values()
	public static final Heuristic[] HEURISTICS = new Heuristic[]
	{
//		Heuristic.ILS, 
//		Heuristic.GLS, 
//		Heuristic.AGLS,
//		Heuristic.AGLS1_1,
//		Heuristic.AGLS1_5,
//		Heuristic.AGLS5_1,
//		Heuristic.AGLS5_5,
//		Heuristic.AGLS1_7,
//		Heuristic.AGLS7_1,
//		Heuristic.AGLS7_7,	
		Heuristic.ILSFlowShop0,
		Heuristic.ILSFlowShop1,
		Heuristic.ILSFlowShop2,
		Heuristic.ILSFlowShop3,
		Heuristic.ILSFlowShop4,
		//Heuristic.RHILS,
	};//Heuristic.values()
	public static final int RUNS = 3;//5
	public static final int INSTANCES = 3;//10
	public static final int THREADS = 2;
	public static final long SEED = 123457890;
	public static final long TIMELIMIT = 40000;//600000

	public static void main(String[] args) 
	{

		//New Scorekeeper
		final ScoreKeeper sc = new ScoreKeeper();
		
		// we first initialise the random number generator for this class
		// it is useful to generate all of the random numbers from one seed, so
		// that the experiments can be easily replicated
		final Random randomNumberGenerator = new Random(SEED);
		
		//Create a threadpool of n threads
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
								ProblemDomain problemDomain = problem.getProblemDomain(instanceSeed);
			
								// we create the hyper-heuristic object from the
								// hyperheuristic index. we provide the time limit,
								// which is set after the object is created in the
								// loadHyperHeuristic method
								HyperHeuristic hyperHeuristic =
										heuristic.getHyperHeuristic(TIMELIMIT,
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
			
			//start the analysis
			sc.analyze();
		} 
		catch (InterruptedException e) 
		{
			e.printStackTrace();
			sc.dump();
		}
	}
}
