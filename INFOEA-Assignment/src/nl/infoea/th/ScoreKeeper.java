/**
 * 
 */
package nl.infoea.th;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import nl.infoea.th.ProblemRunner.Heuristic;
import nl.infoea.th.ProblemRunner.Problem;
import AbstractClasses.HyperHeuristic;
import AbstractClasses.ProblemDomain;

/**
 * @author hiram
 *
 */
public class ScoreKeeper {
	
	Map<Problem, Map<Integer, Map<Heuristic, ArrayList<Double>>>> scores = new HashMap<Problem, Map<Integer,Map<Heuristic,ArrayList<Double>>>>();
	
	public synchronized void recordScore(int run, Heuristic heuristic, Problem problem, int instance, ProblemDomain problemDomain, HyperHeuristic hyperHeuristic)
	{
		if(!scores.containsKey(problem))
			scores.put(problem, new HashMap<Integer, Map<Heuristic, ArrayList<Double>>>());
		
		if(!scores.get(problem).containsKey(instance))
			scores.get(problem).put(instance, new HashMap<Heuristic, ArrayList<Double>>());

		if(!scores.get(problem).get(instance).containsKey(heuristic))
			scores.get(problem).get(instance).put(heuristic, new ArrayList<Double>());

		
		scores.get(problem).get(instance).get(heuristic).add(hyperHeuristic.getBestSolutionValue());
		
		//displayResults(problemDomain, hyperHeuristic);
		displayResultsR(run, heuristic, problem, instance, problemDomain, hyperHeuristic);
	}
	
	
	/**
	 * @param problemDomain
	 * @param hyperHeuristic
	 */
	private void displayResultsR(int run, Heuristic heuristic, Problem problem, int instance, ProblemDomain problemDomain,
			HyperHeuristic hyperHeuristic) 
	{
		double[] fitnesstrace = hyperHeuristic.getFitnessTrace();
		for (int r = 0; r < fitnesstrace.length; r++) {
			System.out.println(String.format("%s;%s;%s;%s;%s;%s",
					run, problem, heuristic, instance, r, fitnesstrace[r]));
		}
	}


	/**
	 * 
	 */
	private void displayResults(int run, ProblemDomain problemDomain, HyperHeuristic hyperHeuristic) {
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

		double[] fitnesstrace = hyperHeuristic.getFitnessTrace();
		System.out.print("\tFitness Trace: ");
		for (double f : fitnesstrace) {
			System.out.print(f + ", ");
		}
		System.out.println();

	}


	public void analyze()
	{
		FileOutputStream fos = null;
		ObjectOutputStream out = null;
		try
		{
			fos = new FileOutputStream("plots/data");
			out = new ObjectOutputStream(fos);
			out.writeObject(scores);
			out.close();
		}
		catch(IOException ex)
		{
			ex.printStackTrace();
		}
		
		SortedMap<Double, Heuristic> ranks;
		List<Double> scoreList;
		
		if(scores.size() != Problem.values().length)
			throw new RuntimeException("Missing values");
		
		Map<Heuristic, Integer> bordaCountTotal = new HashMap<Heuristic, Integer>();
		for(Heuristic h : Heuristic.values())
		{
			bordaCountTotal.put(h, 0);
		}
		
		for(Problem p : Problem.values())
		{
			if(scores.get(p).size() != 10)
				throw new RuntimeException("Missing values");
			
			System.out.print("\n\\begin{tabular}{ c | c | c | c }\n"+p);
			
			Map<Heuristic, Integer> bordaCount = new HashMap<Heuristic, Integer>();
			for(Heuristic h : Heuristic.values())
			{
				System.out.print("&" + h);
				bordaCount.put(h, 0);
			}
			
			System.out.println("\\\\\n\\hline");
			
			for(int i = 0; i < 10; i++)
			{
				if(scores.get(p).get(i).size() != Heuristic.values().length)
					throw new RuntimeException("Missing values");
				
				ranks = new TreeMap<Double, Heuristic>();
				
				for(Heuristic h : Heuristic.values())
				{
					scoreList = scores.get(p).get(i).get(h);
					
					if(scoreList.size() != ProblemRunner.RUNS)
						throw new RuntimeException("Missing values");
					
					Collections.sort(scoreList);
					Double score = scoreList.get((int) Math.floor(ProblemRunner.RUNS/2));
					ranks.put(score, h);
				}
				
				Heuristic[] outcome = ranks.values().toArray(new Heuristic[Heuristic.values().length]);
				System.out.print("Instance"+i);
				for(Heuristic h : Heuristic.values())
				{
					int rank = java.util.Arrays.asList(outcome).indexOf(h) + 1;
					System.out.print("&" + rank);
					bordaCount.put(h, bordaCount.get(h) + rank);
				}
				
				System.out.println("\\\\");
			}
			
			System.out.print("\\hline\nTotal");
			for(Heuristic h : Heuristic.values())
			{
				System.out.print("&" + bordaCount.get(h));
				bordaCountTotal.put(h, bordaCountTotal.get(h) + bordaCount.get(h));
			}
			System.out.println("\\\\\n\\end{tabular}");
		}
				
		System.out.print("\nDomain");
		
		for(Heuristic h : Heuristic.values())
		{
			System.out.print("&" + h);
		}
		
		System.out.println("\\\\");
		
		System.out.print("Totaal");
		for(Heuristic h : Heuristic.values())
		{
			System.out.print("&" + bordaCountTotal.get(h));
		}
		System.out.println("\\\\");
	}


	/**
	 * 
	 */
	public void init() {
		System.out.println("Run;Problem;Heuristic;Instance;Index;Fitness");
	}
}
