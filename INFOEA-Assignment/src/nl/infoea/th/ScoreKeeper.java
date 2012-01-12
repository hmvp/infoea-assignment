/**
 * 
 */
package nl.infoea.th;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import nl.infoea.th.ProblemRunner.Heuristic;
import nl.infoea.th.ProblemRunner.Problem;

/**
 * @author hiram
 *
 */
public class ScoreKeeper {
	
	Map<Problem, Map<Integer, Map<Heuristic, ArrayList<Double>>>> scores = new HashMap<Problem, Map<Integer,Map<Heuristic,ArrayList<Double>>>>();
	
	public synchronized void recordScore(Heuristic heuristic, Problem problem, int instance, double score)
	{
		if(!scores.containsKey(problem))
			scores.put(problem, new HashMap<Integer, Map<Heuristic, ArrayList<Double>>>());
		
		if(!scores.get(problem).containsKey(instance))
			scores.get(problem).put(instance, new HashMap<Heuristic, ArrayList<Double>>());

		if(!scores.get(problem).get(instance).containsKey(heuristic))
			scores.get(problem).get(instance).put(heuristic, new ArrayList<Double>());

		
		scores.get(problem).get(instance).get(heuristic).add(score);
	}
	
	
	public void analyze()
	{
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


	public static void main(String[] args) {
		ScoreKeeper sc = new ScoreKeeper();
		
		for(Problem p : Problem.values())
		{
			for(int i = 0; i < 10; i++)
			{

				for(Heuristic h : Heuristic.values())
				{
					for(int j = 0; j < 5; j++)
					{
						sc.recordScore(h, p, i, Math.random()*100);
					}
				}				
			}
		}
		
		sc.analyze();
	}
}
