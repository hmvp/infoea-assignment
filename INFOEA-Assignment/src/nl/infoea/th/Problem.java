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
	SAT, BinPacking, PersonnelScheduling, FlowShop, VRP, TSP;

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
}