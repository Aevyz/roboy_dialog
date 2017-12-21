package roboy.dialog.personality.states;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.bytedeco.javacv.FrameFilter;
import roboy.linguistics.Linguistics;
import roboy.linguistics.Linguistics.SEMANTIC_ROLE;
import roboy.linguistics.sentenceanalysis.Interpretation;
import roboy.memory.Neo4jMemory;
import roboy.memory.Neo4jRelationships;
import roboy.memory.nodes.Interlocutor;
import roboy.memory.nodes.MemoryNodeModel;
import roboy.util.Lists;

/**
 * Roboy introduces himself and asks "Who are you?". Moves to success state if the answer
 * is at most 2 words.
 */
public class IntroductionState extends AbstractBooleanState{

	Interlocutor person = new Interlocutor();
    Neo4jMemory memory;
    public Neo4jRelationships predicate = Neo4jRelationships.FRIEND_OF;

	private static final List<String> introductions = Lists.stringList(
//			"I am Roboy. Who are you?",
//			"My name is Roboy. What is your name?",
			"Ehm, sorry, who am I currently talking to?",
			"Oh wow, who is this? My vision is in development",
			"Obviously you know I am Roboy, but who are you?",
			"Who is it there? Do I know you?"
			);
	
	public IntroductionState(Interlocutor person) {
		randomGenerator = new Random();
		setFailureTexts(Lists.stringList(
				"It's always nice to meet new people.",
				"well, I'm pleased to meet you. ",
				"nice to meet you.",
				"How refreshing to see a new face."));
		this.person = person;
	}
	
	@Override
	public List<Interpretation> act() {
		int index = randomGenerator.nextInt(introductions.size());
		return Lists.interpretationList(new Interpretation(introductions.get(index)));
	}

	/**
	 * Performs person detection by consulting memory.
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected boolean determineSuccess(Interpretation input) {
		String[] tokens = (String[]) input.getFeatures().get(Linguistics.TOKENS);
		String name = null;
		if(tokens.length==1){
			name = tokens[0];
		} else {
			Map<SEMANTIC_ROLE,Object> pas = (Map<SEMANTIC_ROLE,Object>) input.getFeature(Linguistics.PAS);
			if(pas==null || !pas.containsKey(SEMANTIC_ROLE.PREDICATE)) return false;
			String predicate = ((String)pas.get(SEMANTIC_ROLE.PREDICATE)).toLowerCase();
			String agent = (String)pas.get(SEMANTIC_ROLE.AGENT);
			String patient = (String)pas.get(SEMANTIC_ROLE.PATIENT);
			//if(agent==null) agent = "i";
			//TODO Handle cases where name could not be parsed.
			// Maybe something like "I did not quite get your name, could you repeat it."
			// When using a default value with persistent memory, Roboy will always recognize them.
			if(patient==null) agent = "laura";
			//if(!"am".equals(predicate) && !"is".equals(predicate)) return false;
			//if(!agent.toLowerCase().contains("i") && !agent.toLowerCase().contains("my")) return false;
			name = patient;
		}
		if(name!=null){
//			WorkingMemory.getInstance().save(new Triple("is","name",name));
//			List<Triple> agens = PersistentKnowledge.getInstance().retrieve(new Triple(null,name,null));
//			List<Triple> patiens = PersistentKnowledge.getInstance().retrieve(new Triple(null,null,name));
			//TODO Currently assuming no duplicate names in memory. Support for last name addition needed.
			person.addName(name);
			if(!person.FAMILIAR) {
				setFailureTexts(Lists.stringList(
						"It's always nice to meet new people,"+name,
						"well, I'm pleased to meet you, "+name,
						"How refreshing to see a new face."));
				return false;
			}
            String retrievedResult = "";
            ArrayList<Integer> ids = person.getRelationships(predicate);
            if (ids!=null) {
                memory = Neo4jMemory.getInstance();
                try {
                    for (int i = 0; i < ids.size() && i < 3; i++) {
                        MemoryNodeModel requestedObject = memory.getById(ids.get(i));
                        retrievedResult += requestedObject.getProperties().get("name").toString();
                        retrievedResult += " and ";
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
			if (!retrievedResult.equals("")) {
                retrievedResult = "By the way I know you are friends with " + retrievedResult.substring(0,retrievedResult.length()-5);
            }
			setSuccessTexts(Lists.stringList(
					"Oh hi, "+name+". Sorry, I didn't recognize you at first. But you know how the vision guys are. " + retrievedResult,
					"Hi "+name+" nice to see you again. " + retrievedResult
					));
			return true;
		}
		return false;
	}

}
