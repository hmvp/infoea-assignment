/**
 * 
 */
package nl.infoea.th;

import AbstractClasses.ProblemDomain;

public enum Problem 
{
	SAT, BinPacking, PersonnelScheduling, FlowShop;

	public static Problem asEnum(ProblemDomain problem) {
		if (problem instanceof SAT.SAT)
			return SAT;
		if (problem instanceof BinPacking.BinPacking)
			return BinPacking;
		if (problem instanceof PersonnelScheduling.PersonnelScheduling)
			return PersonnelScheduling;
		if (problem instanceof FlowShop.FlowShop)
			return FlowShop;
		else 
			throw new RuntimeException("Wrong Type");
	}
}