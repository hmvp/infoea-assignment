/**
 * 
 */
package nl.infoea.th;

import java.util.HashMap;
import java.util.Map;

import nl.infoea.th.ProblemRunner.Heuristic;
import nl.infoea.th.ProblemRunner.Problem;

/**
 * @author hiram
 *
 */
public class ScoreKeeper {
	
	Map<Problem, Map<Integer, Map<Heuristic, Double>>> scores = new HashMap<Problem, Map<Integer,Map<Heuristic,Double>>>();
	
	public synchronized void recordScore(Heuristic heuristic, Problem problem, int instance, double score)
	{
		if(!scores.containsKey(problem))
			scores.put(problem, new HashMap<Integer, Map<Heuristic, Double>>());
		
		if(!scores.get(problem).containsKey(problem))
			scores.get(problem).put(instance, new HashMap<Heuristic, Double>());
		
		scores.get(problem).get(instance).put(heuristic, score);
	}
	
	
	public void analyze()
	{
		for(Problem p : Problem.values())
		{
			for(int i = 0; i < 10; i++)
			{
				scores.get(p).get(i);
			}
		}
		
	}
}
