/**
 * 
 */
package nl.infoea.th;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
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
	
	private static final String REPORTDIR = "plots";

	Map<Problem, Map<Integer, Map<Heuristic, ArrayList<Double>>>> scores = new HashMap<Problem, Map<Integer,Map<Heuristic,ArrayList<Double>>>>();
	
	PrintStream csv;
	PrintStream latex;
	PrintStream progress;

	private String postfix;
	
	public synchronized void recordScore(int run, Heuristic heuristic, Problem problem, int instance, ProblemDomain problemDomain, HyperHeuristic hyperHeuristic)
	{
		if(!scores.containsKey(problem))
			scores.put(problem, new HashMap<Integer, Map<Heuristic, ArrayList<Double>>>());
		
		if(!scores.get(problem).containsKey(instance))
			scores.get(problem).put(instance, new HashMap<Heuristic, ArrayList<Double>>());

		if(!scores.get(problem).get(instance).containsKey(heuristic))
			scores.get(problem).get(instance).put(heuristic, new ArrayList<Double>());

		
		scores.get(problem).get(instance).get(heuristic).add(hyperHeuristic.getBestSolutionValue());
		
		displayResults(run, heuristic, problem, instance, problemDomain, hyperHeuristic);
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
		for (int r = 0; r < fitnesstrace.length; r++) 
		{
			csv.println(String.format("%s;%s;%s;%s;%s;%s",
					run, problem, heuristic, instance, r, fitnesstrace[r]));
		}
	}


	/**
	 * 
	 */
	private void displayResults(int run, Heuristic heuristic, Problem problem, int instance, ProblemDomain problemDomain,
			HyperHeuristic hyperHeuristic)
	{
		
		// for this example, we use the record within each problem
		// domain of the number of times each low level heuristic
		// was called.
		// we sum the results to obtain the total number of times
		// that a low level heuristic was called
		int[] i = problemDomain.getHeuristicCallRecord();
		int counter = 0;
		for (int y : i) 
		{
			counter += y;
		}

		// we print the results of this hyper-heuristic on this
		// problem instance
		// print the name of this hyper-heuristic
		progress.println("Run: "+ run + " " + problem + instance+ ": " + hyperHeuristic.toString());
		// print the best solution found within the time limit
		progress.print("\tBest: "
				+ hyperHeuristic.getBestSolutionValue());
		// print the elapsed time in seconds
		progress.print("\tTime Elapsed: "
				+ (hyperHeuristic.getElapsedTime() / 1000.0));
		// print the number of calls to any low level heuristic
		progress.println("\t Heuristic Calls: " + counter);

		double[] fitnesstrace = hyperHeuristic.getFitnessTrace();
		progress.print("\tFitness Trace: ");
		
		for (double f : fitnesstrace) 
		{
			progress.print(f + ", ");
		}
		progress.println();

	}


	public void analyze()
	{
		progress.println("Run finished!");
		dump();
		
		SortedMap<Double, Heuristic> ranks;
		List<Double> scoreList;
		
		if(scores.size() != ProblemRunner.PROBLEMS.length)
			throw new RuntimeException("Missing values");
		
		Map<Heuristic, Integer> bordaCountTotal = new HashMap<Heuristic, Integer>();
		for(Heuristic h : ProblemRunner.HEURISTICS)
		{
			bordaCountTotal.put(h, 0);
		}
		
		for(Problem p : ProblemRunner.PROBLEMS)
		{
			if(scores.get(p).size() != ProblemRunner.INSTANCES)
				throw new RuntimeException("Missing values");
			
			latex.print("\n\\begin{tabular}{ c | c | c | c }\n"+p);
			
			Map<Heuristic, Integer> bordaCount = new HashMap<Heuristic, Integer>();
			for(Heuristic h : ProblemRunner.HEURISTICS)
			{
				latex.print("&" + h);
				bordaCount.put(h, 0);
			}
			
			latex.println("\\\\\n\\hline");
			
			for(int i = 0; i < ProblemRunner.INSTANCES; i++)
			{
				if(scores.get(p).get(i).size() != ProblemRunner.HEURISTICS.length)
					throw new RuntimeException("Missing values");
				
				ranks = new TreeMap<Double, Heuristic>();
				
				for(Heuristic h : ProblemRunner.HEURISTICS)
				{
					scoreList = scores.get(p).get(i).get(h);
					
					if(scoreList.size() != ProblemRunner.RUNS)
						throw new RuntimeException("Missing values");
					
					Collections.sort(scoreList);
					Double score = scoreList.get((int) Math.floor(ProblemRunner.RUNS/2));
					ranks.put(score, h);
				}
				
				Heuristic[] outcome = ranks.values().toArray(new Heuristic[Heuristic.values().length]);
				
				latex.print("Instance"+i);
				for(Heuristic h : ProblemRunner.HEURISTICS)
				{
					int rank = java.util.Arrays.asList(outcome).indexOf(h) + 1;
					latex.print("&" + rank);
					bordaCount.put(h, bordaCount.get(h) + rank);
				}
				latex.println("\\\\");
			}
			
			latex.print("\\hline\nTotal");
			for(Heuristic h : ProblemRunner.HEURISTICS)
			{
				latex.print("&" + bordaCount.get(h));
				bordaCountTotal.put(h, bordaCountTotal.get(h) + bordaCount.get(h));
			}
			latex.println("\\\\\n\\end{tabular}");
		}
				
		latex.print("\nDomain");
		for(Heuristic h : ProblemRunner.HEURISTICS)
		{
			latex.print("&" + h);
		}
		latex.println("\\\\");
		
		latex.print("Totaal");
		for(Heuristic h : ProblemRunner.HEURISTICS)
		{
			latex.print("&" + bordaCountTotal.get(h));
		}
		latex.println("\\\\");
		
		latex.println("\\end{document}");
	}


	/**
	 * 
	 */
	public ScoreKeeper() 
	{
		String postfixbase = new SimpleDateFormat("yyyy-MM-dd").format(new Date(System.currentTimeMillis()));
		postfix = postfixbase;
		
		//Check file does not exists and append an int if it exists
		for(int i = 0; new File(REPORTDIR + "/bordacount-"+postfix+".tex").exists();)
		{
			postfix = postfixbase + "-" + i++;
		}
		
		try 
		{
			latex = new PrintStream(REPORTDIR + "/bordacount-"+postfix+".tex");
			csv = new PrintStream(REPORTDIR + "/runtrace-"+postfix+".csv");
			progress = System.out;
			
			copyFileToStream(REPORTDIR + "/report.tex", latex);
			
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
			latex.close();
			csv.close();
		}
				
		progress.println("Run: " + postfix);
		csv.println("Run;Problem;Heuristic;Instance;Index;Fitness");
		
	}

	/**
	 * Copy Contents of file to a printstream
	 * @param filename
	 * @param outstream
	 * @throws IOException
	 */
	private void copyFileToStream(String filename, PrintStream outstream) throws IOException {
		FileInputStream instream = new FileInputStream(filename);
		try 
		{
			FileChannel fc = instream.getChannel();
			MappedByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
			/* Instead of using default, pass in a decoder. */
			outstream.print(Charset.defaultCharset().decode(bb));
		} 
		finally 
		{
			instream.close();
		}
	}

	/**
	 * dump current score to file for safekeeping
	 */
	public void dump() {
		FileOutputStream fos = null;
		ObjectOutputStream out = null;
		try
		{
			fos = new FileOutputStream(REPORTDIR + "/data-" + postfix);
			out = new ObjectOutputStream(fos);
			out.writeObject(scores);
			out.close();
		}
		catch(IOException ex)
		{
			ex.printStackTrace();
		}
	}
}
