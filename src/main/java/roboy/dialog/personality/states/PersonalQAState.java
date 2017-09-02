package roboy.dialog.personality.states;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import roboy.linguistics.Linguistics;
import roboy.linguistics.Linguistics.SEMANTIC_ROLE;
import roboy.linguistics.sentenceanalysis.Interpretation;
import roboy.memory.nodes.Interlocutor;
import roboy.util.Lists;

public class PersonalQAState extends AbstractBooleanState{

	private List<String> questions;
	private List<String[]> successTexts;
	private String predicate;
	private Interlocutor person;
	
	public PersonalQAState(List<String> questions, List<String> failureTexts,
						   List<String[]> successTexts, String predicate, Interlocutor person) {
		this.questions = questions;
		this.successTexts = successTexts;
		this.predicate = predicate;
		this.person = person;
		setFailureTexts(failureTexts);
	}

	/**
	 * Ask the question.
	 */
	@Override
	public List<Interpretation> act() {
		return Lists.interpretationList(new Interpretation(questions.get((int)Math.random()*questions.size())));
	}

	/**
	 * Retrieve the answer and add it to the memory, if needed.
	 *
	 * As locations, hobbies, workplaces etc are individual nodes in memory,
	 * those will be retrieved or created if necessary.
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected boolean determineSuccess(Interpretation input) {
		String[] tokens = (String[]) input.getFeatures().get(Linguistics.TOKENS);
		String answer = null;
		if(tokens.length==1){
			answer = tokens[0];
		}
		else if(input.getFeatures().containsKey(Linguistics.OBJ_ANSWER))
		{
			answer = (String) input.getFeature(Linguistics.OBJ_ANSWER);
		}
		else {
			Map<SEMANTIC_ROLE,Object> pas = (Map<SEMANTIC_ROLE,Object>) input.getFeature(Linguistics.PAS);
			if(pas==null) return false;
			String predicate = ((String)pas.get(SEMANTIC_ROLE.PREDICATE)).toLowerCase();
			String agent = (String)pas.get(SEMANTIC_ROLE.AGENT);
			String patient = (String)pas.get(SEMANTIC_ROLE.PATIENT);
			if(agent==null) return false;
			if(patient==null) return false;
			if(!"am".equals(predicate) && !agent.toLowerCase().contains("i") && !agent.toLowerCase().contains("my")) return false;
			answer = patient;
		}
		if(answer!=null){

			// TODO Remove old code after successfully switching to Neo4j memory
			//WorkingMemory memory = WorkingMemory.getInstance();
			// List<Triple> nameTriple = memory.retrieve(new Triple("is","name",null));
			//if(nameTriple.isEmpty()) return false;
			//String name = nameTriple.get(0).patiens;
			//WorkingMemory.getInstance().save(new Triple(predicate,name,answer));

			// Add the new information about the person to the memory.
			person.addInformation(predicate, answer);


			List<String> sTexts = new ArrayList<>();
			for(String[] s: successTexts){
				sTexts.add(s[0]+answer+s[1]);
			}
			setSuccessTexts(sTexts);
			return true;
		}
		return false;
	}

}
