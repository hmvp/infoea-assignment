/**
 * 
 */
package nl.infoea.th;

import travelingSalesmanProblem.TSP;
import AbstractClasses.ProblemDomain;
import BinPacking.BinPacking;
import FlowShop.FlowShop;
import PersonnelScheduling.PersonnelScheduling;
import SAT.SAT;
import VRP.VRP;

public enum Problem 
{
	SAT {
		@Override
		public ProblemDomain getProblemDomain(long instanceseed) {
			return new SAT(instanceseed);
		}
	}, BinPacking {
		@Override
		public ProblemDomain getProblemDomain(long instanceseed) {
			return new BinPacking(instanceseed);
		}
	}, PersonnelScheduling {
		@Override
		public ProblemDomain getProblemDomain(long instanceseed) {
			return new PersonnelScheduling(instanceseed);
		}
	}, FlowShop {
		@Override
		public ProblemDomain getProblemDomain(long instanceseed) {
			return new FlowShop(instanceseed);
		}
	}, VRP {
		@Override
		public ProblemDomain getProblemDomain(long instanceseed) {
			return new VRP(instanceseed);
		}
	}, TSP {
		@Override
		public ProblemDomain getProblemDomain(long instanceseed) {
			return new TSP(instanceseed);
		}
	};

	public static Problem asEnum(ProblemDomain problem) {
		if (problem instanceof SAT)
			return SAT;
		if (problem instanceof BinPacking)
			return BinPacking;
		if (problem instanceof PersonnelScheduling)
			return PersonnelScheduling;
		if (problem instanceof FlowShop)
			return FlowShop;
		if (problem instanceof VRP)
			return VRP;
		if (problem instanceof TSP)
			return TSP;
		else 
			throw new RuntimeException("Wrong Type");
	}
	
	/**
	 * this method creates the relevant ProblemDomain object from the index
	 * given as a parameter. for each instance, the ProblemDomain is initialised
	 * with an identical seed for each hyper-heuristic. this is so that each
	 * hyper-heuristic starts its search from the same initial solution.
	 */
	abstract public ProblemDomain getProblemDomain(long instanceseed);
}