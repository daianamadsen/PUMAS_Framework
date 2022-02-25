package edu.isistan.pumas.framework.protocols.monotonicConcession.multilateralConcessionStrategy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import edu.isistan.christian.recommenders.sur.datatypes.SURItem;
import edu.isistan.pumas.framework.protocols.commons.exceptions.NoProposalsAvailableException;
import edu.isistan.pumas.framework.protocols.commons.exceptions.NonConcedableCurrentProposalException;
import edu.isistan.pumas.framework.protocols.commons.exceptions.NothingToProposeException;
import edu.isistan.pumas.framework.protocols.commons.proposal.AgProposal;
import edu.isistan.pumas.framework.protocols.commons.proposal.comparators.ProposalUtilityComparator;
import edu.isistan.pumas.framework.protocols.commons.userAgents.UserAg;

/**
 * This type of multilateral concession strategy uses the distance between the 
 * 'what we are giving to the other agents' and 'what they desire or want' in terms of utility.
 * 
 * The value of the "metric" that indicates what is the next proposal is computed using the utility of the current proposal
 * SD3_NS (NS=NoSatisfechos) : igual a S2 pero solo sumando cuando la utilidad de mi propuesta es menor que la de la propuesta actual del agente. O sea si 
 * mi propuesta le da utilidad 0.4 y la suya 0.3 => mi propuesta lo satisface => no influye en la distancia. Al tener que conceder debo buscar una propuesta 
 * que tenga una distancia (SD3_NS) < que la propuesta que tengo actualmente. 
 * (USAR < en vez de <= para evitar Si no tengo ninguna, no puedo condeder. Cuando conceda otro las distancias 
 * cambian y quizás luego pueda conceder de nuevo si fuera necesario. Esto puede que funcione tanto cuando no permito revisar las propuestas viejas (las que 
 * ya concedí, como cuando lo permito=> PROBAR).
 * 
 * 
 * ****************************************************************
 * Ex: 
 * Let suppose we have 3 agents AG1, AG2 and AG3, and the agent AG1 wants to know what is the item he should propose in the next round
 * in order to be closer to reach an agreement with the other agents (AG2 and AG3). So he has to evaluate his candidate proposals and pick one of them. 
 * In the current round we have the following matrix (where X11 is the proposal made by AG1, X21 the one made by AG2 and 
 * X31 the one made by AG3), and the values in the matrix represent the utility value of the proposal for the agent (M[AG1][X21] = 0.5 = utility value
 * of the proposal X21 for the agent AG1).
 * 		X11 X21 X31 
 * 	AG1	0.8 0.5 0.4
 *	AG2	0.4 0.3 0.5		=> (0.4-0.3)>=0 y (0.6-0.7)<0 => DD = |0.6-0.7| = 0.1 (dado que a AG2 ya lo satisfago, no me influye)
 *	AG3	0.6 0.2 0.7
 *  
 *  The Desires Distance (DD) metric is computed as follows:
 *  computeDD(agent, newProposal){
 *  	DD= 0
 *  	For every other agent{
 *  	-	compute the difference between the utility of  proposal gives them and the utility their proposals give them. Ex: for AG2 and proposal X11 => 0.4-0.3 = 0.1
 *  	-	if the difference computed in the previous step is lower than 0 (zero), compute it's module and add it to DD. Ex: |0.4-0.3| = |0.1| = 0.1 => DD=0+0.1 = 0.1
 *  	}
 *  }
 *  
 *  You compute the "DD value" for your current proposal and then you loop between your candidate proposals and computing their "DD value" till you find the first candidate
 *  that has a "DD value" lower or equal to your current proposal DD value. If you find one, that's the item you will propose in the next round. If you don't find any item 
 *  you have nothing to propose because all the candidate proposals you have will not improve the situation (none of them will allow you to achieve a greater level
 *  of satisfaction of the desires of the other agents).
 *  
 *  Ex. using the last matrix, suppose AG1 has the following candidates (proposal X12 in every matrix is different). 
 *  DD' is the DD value of the new proposal and DD is the value of AG1 current proposal, which was computed above (DD = |0.6-0.7| = 0.1)
 *  
 * 	Candidate_1: 
		X12 X21 X31 
	AG1	0.7 0.5 0.4
	AG2	0.3 0.3 0.5
	AG3	0.7 0.2 0.7	=> (0.3-0.3)>= 0 y (0.7-0.7)>=0 => DD' = 0 (none of the terms has influence in the value) =>  DD'< DD => X12 CAN be proposed in the next round

	Candidate_2: 
		X12 X21 X31 
	AG1	0.7 0.5 0.4
	AG2	0.2 0.3 0.5
	AG3	0.7 0.2 0.7	=> (0.2-0.3)<0 y (0.7-0.7)>=0 => DD' = |0.2-0.3| = 0.1 => DD'= DD => CAN/CAN'T be proposed in the next round depending if the strategy uses < or <=.

	Candidate_3:
		X12 X21 X31 
	AG1	0.7 0.5 0.4
	AG2	0.5 0.3 0.5
	AG3	0.4 0.2 0.7	=> (0.5-0.3)>= 0 y (0.4-0.7)<0 => DD' = |0.4-0.7|=0.3  => DD'>DD => X12 CAN'T be proposed in the next round

	Candidate_4:
		X12 X21 X31 
	AG1	0.7 0.5 0.4
	AG2	0.4 0.3 0.5
	AG3	0.8 0.2 0.7	=> (0.4-0.3)>=0 y (0.8-0.7)>=0 => DD' = 0 => DD'<DD => X12 CAN be proposed in the next round

	Candidate_5:
		X12 X21 X31 
	AG1	0.7 0.5 0.4
	AG2	0.5 0.3 0.5
	AG3	0.6 0.2 0.7	=> (0.5-0.3)>=0 y (0.6-0.7)<0 => DD' = |0.6-0.7| = 0.1 => DD'= DD => CAN/CAN'T be proposed in the next round depending if the strategy uses < or <=.

	Candidate_6:
		X12 X21 X31 
	AG1	0.7 0.5 0.4
	AG2	0.2 0.3 0.5
	AG3	0.6 0.2 0.7	=> (0.2-0.3)<0 y (0.6-0.7)<0 => DD' = |0.2-0.3|+|0.6-0.7| = 0.2 => DD'>DD => X12 CAN'T be proposed in the next round
 *   
 *  
 * @author Christian
 *
 */


public class MultilateralConcessionStrategyDesiresDistance<T extends SURItem> extends
MultilateralConcessionStrategySocialBased<T> {

	protected double getOtherAgentsUtility(AgProposal<T> p, List<UserAg<T>> otherAgents){ //this is the DD value of the proposal
		double dd=0.0;

		for (UserAg<T> otherAg: otherAgents){
			try {
				AgProposal<T> otherAgProp = otherAg.getCurrentProposal();
				double otherAgUtilityOverMyProposal = otherAg.getUtilityFor(p);
				double otherAgUtilityOverHisProposal = otherAg.getUtilityFor(otherAgProp);

				if (otherAgUtilityOverMyProposal-otherAgUtilityOverHisProposal <0){ //desires distance between what the otherAgent wants and what the proposal p gives him 
					dd+= Math.abs(otherAgUtilityOverMyProposal-otherAgUtilityOverHisProposal); //only add the distance when 'otherAgent' is not satisfied by p (p gives him a less utility than what he desired)
				}
			} catch (NothingToProposeException e) {
				//do nothing, if the other agent hasn't proposed anything => is not considered
			}
			
		}

		return dd;
	}

	@Override
	protected boolean isBetter(double utility1, double utility2) {

		return utility1 < utility2;
	}

	/**
	 * ****************************************************************
	 * 
	 * TODO PROBLEM: (TRADUCIR)
	 * A propone X11, B propone X21 (dd[x11]=0.21, dd[x21]=0.18).. A debe conceder y entonces propone X12 que tiene una dd[12]=0.19. 
	 * Dado que el valor de dd de una proposal cambia si los otros conceden. Si no hay agreement y debe conceder B. B propone x22 (dd[x22] = 0.17).
	 * No hay agreement y debe conceder A. Se da la casualidad que dado que B ya cambió su propuesta ahora la propuesta del pool de A que tiene menor distancia
	 * a los deseos de B (y los otros agentes si los hubiera) es x11 (que tiene ahora un dd[x11]= 0.19 vs el dd[x12]=0.22, los dd's cambiaron porque B concedió).
	 * Cuando a concede y toma X11, no hay agreement y debe conceder B puede ocurrir que por casualidad, la propuesta con menor dd sea x21 y si esto se repite infinitamente
	 * caigo en un bucle que es imposible de romper.... Este tipo de bucle puede pasar en muchos pasos, quizás se repiten 5 propuestas de forma cíclica porque por casualidad
	 * las distancias (dd) lo permiten y suele darse cuando concede un agente y luego otro y luego el primer agente. Si un agente concediera N veces seguidas sería menos probable.
	 * 
	 * El problema lo genera el hecho de que las distancias varían cuando los otros agentes conceden y pueden aumentar o disminuir. Faltó considerar esto: no consideramos
	 * que si un agente concede su utilidad debería disminuir pero puede que no ocurra porque justamente este criterio le permita considerar una proposal con utilidad mayor
	 * que la que ya tiene, todo porque esa proposal ahora tiene menor distancia respecto de los deseos de los otros agentes!!! Esto es lo que puede crear el bucle ya que las
	 * distancias no decrecerían siempre sino que podrían crecer o reducirse en cada concesión... Al crear esta estrategia no tuve en cuenta eso, y es lo que puede causar el bucle
	 * infinito. VER SI SE PUEDE ARREGLAR Y SI OCURRE EN NASH U OTRAS ESTRATEGIAS!
	 * 
	 * SOLUTION: filter the candidates. Only allow candidates with utility value lower than the proposal the agent is currently holding, this way you can't have an
	 * infinite loop as the distances of the other agents respect the agent current proposal will NEVER INCREASE, they will only be able to stay the same or decrease.
	 * 
	 */
	public AgProposal<T> getNextItemToPropose(UserAg<T> agent, List<AgProposal<T>> candidateProposals)
			throws NoProposalsAvailableException, NonConcedableCurrentProposalException {

		double currPropUtility;
		try {
			currPropUtility = agent.getCurrentProposal().getUtilityValue();
		} catch (NothingToProposeException e) {
			//Has proposed nothing => can't change it (can't propose anything new)
			throw new NonConcedableCurrentProposalException(e);
		}
		//We allow only candidates with utility value lower than the proposal the agent is currently helding to avoid infinite loops (read above)
		List<AgProposal<T>> filtered = new ArrayList<>();
		for (AgProposal<T> p : candidateProposals){
			if (agent.getUtilityFor(p) <= currPropUtility)
				filtered.add(p);
		}
		
		//Attempt to minimize the utility loss by ordering the proposals in descending order of its utility value
		Collections.sort(filtered, new ProposalUtilityComparator<T>(false));

		return super.getNextItemToPropose(agent, filtered);
	}

	@Override
	public String toString() {
		return "MultilateralConcessionStrategyDesiresDistance []";
	}
	
	
}
