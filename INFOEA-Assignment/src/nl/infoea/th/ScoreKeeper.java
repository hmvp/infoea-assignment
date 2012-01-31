/**
 * 
 */
package nl.infoea.th;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import AbstractClasses.HyperHeuristic;
import AbstractClasses.ProblemDomain;

/**
 * @author hiram
 * This class is receives all the scores and analyzes part of it.
 */
public class ScoreKeeper {
	
	private static final String REPORTDIR = "results";

	/**
	 * To store scores in
	 */
	Map<Problem, Map<Integer, Map<Heuristic, ArrayList<Double>>>> scores = new HashMap<Problem, Map<Integer,Map<Heuristic,ArrayList<Double>>>>();
	
	PrintStream csv;
	PrintStream latex;
	PrintStream progress;

	/**
	 * to make sure output files are unique
	 */
	private String postfix;
	
	/**
	 * This method is called every time after a hyperheuristic is run
	 * This records the score of the hyperheuristic
	 * @param run
	 * @param heuristic
	 * @param problem
	 * @param instance
	 * @param problemDomain
	 * @param hyperHeuristic
	 */
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
	 * Display results for R readable output (csv)
	 * Is called after every hyperheuristic.solve
	 * @param run
	 * @param heuristic
	 * @param problem
	 * @param instance
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
	 * Display results for human-readable progress output
	 * Is called after every hyperheuristic.solve
	 * @param run
	 * @param heuristic 
	 * @param problem
	 * @param instance
	 * @param problemDomain
	 * @param hyperHeuristic
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
		progress.println("Run: "+ run + " " + problem + instance+ ": " + heuristic);
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

	/**
	 * This method analyzes the contents of the score field
	 * This method is called after every heuristic is finished
	 * This method prints the borda count as latex source
	 */
	public void analyze()
	{
		progress.println("Run finished!");
		dump();
		
		HashMap<Heuristic, Double> ranks;
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
				
				ranks = new HashMap<Heuristic, Double>();
				
				
				//Calculate scores
				for(Heuristic h : ProblemRunner.HEURISTICS)
				{
					scoreList = scores.get(p).get(i).get(h);
					
					if(scoreList.size() != ProblemRunner.RUNS)
						throw new RuntimeException("Missing values");
					
					Collections.sort(scoreList);
					
					//get median
					double score = scoreList.get((int) Math.floor(ProblemRunner.RUNS/2));
					
					ranks.put(h, score);
				}
				
				//sort based on value
				TreeMap<Heuristic, Double> sortedRanks = new ValueSortedTreeMap<Heuristic, Double>(ranks);
				//Make an array of Heuristics with the first 
				ArrayList<Heuristic> rankList = new ArrayList<Heuristic>(sortedRanks.keySet());
				
				
				//print in right order
				latex.print("Instance"+i);
				for(Heuristic h : ProblemRunner.HEURISTICS)
				{
					int rank = rankList.indexOf(h) + 1;
					
					if (rank == 0)
					{
						rank = 10;
						progress.println("Oeps er mist iets: "+h+" in "+rankList);
					}
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
			
			//Write to file and to System.out
			progress = new PrintStream(new OutputStream() {
				
				private OutputStream file = new FileOutputStream(REPORTDIR + "/progress-"+postfix+".txt");
				
				@Override
				public void write(int b) throws IOException {
					System.out.write(b);
					file.write(b);
				}
			});
			
			copyFileToStream(REPORTDIR + "/report.tex", latex);
			
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
			latex.close();
			csv.close();
		}
				
		progress.println("Run: " + postfix + " " + new Time(System.currentTimeMillis()));
		progress.println("Parameters: ");
		progress.println("\tRuns: " + ProblemRunner.RUNS);
		progress.println("\tProblems: " + Arrays.toString(ProblemRunner.PROBLEMS));
		progress.println("\tInstances: " + ProblemRunner.INSTANCES);
		progress.println("\tHeuristics: " +  Arrays.toString(ProblemRunner.HEURISTICS));
		progress.println("\tThreads: " + ProblemRunner.THREADS);
		progress.println("\tSeed: " + ProblemRunner.SEED);
		progress.println("\tTimelimit: " + ProblemRunner.TIMELIMIT/1000 + "sec");
		progress.println("\tExpected runtime: " + 
					((	ProblemRunner.RUNS * 
						ProblemRunner.PROBLEMS.length * 
						ProblemRunner.INSTANCES *
						ProblemRunner.HEURISTICS.length * 
						(ProblemRunner.TIMELIMIT/1000)
					) / ProblemRunner.THREADS
					) / 60 + "min");
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
