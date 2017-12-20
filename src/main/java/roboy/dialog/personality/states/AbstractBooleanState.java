package roboy.dialog.personality.states;

import java.util.List;
import java.util.Random;

import roboy.linguistics.sentenceanalysis.Interpretation;
import roboy.talk.StatementBuilder;
import roboy.talk.Verbalizer;
import roboy.util.Lists;

/**
 * Abstract super class for states that fork between two possible subsequent states.
 * The determineSuccess method needs to be implemented by subclass to determine if
 * the success or failure state should be moved into next.
 */
public abstract class AbstractBooleanState implements State {

    protected State success;
    protected State failure;
    protected Random randomGenerator;
    
    private List<String> successTexts = Lists.stringList("");
    private List<String> failureTexts = Lists.stringList("");

    public State getSuccess() {
        return success;
    }

    /**
     * Sets the state Roboy moves into if the determineSuccess method
     * returns true.
     * 
     * @param success The following state
     */
    public void setSuccess(State success) {
        this.success = success;
    }

    public State getFailure() {
        return failure;
    }

    /**
     * Sets the state Roboy moves into if the determineSuccess method
     * returns false.
     * 
     * @param failure The following state
     */
    public void setFailure(State failure) {
        this.failure = failure;
    }

    public void setNextState(State state) {
        this.success = this.failure = state;
    }
    
    public void setSuccessTexts(List<String> texts){
    	successTexts = texts;
    }
    
    public void setFailureTexts(List<String> texts){
    	failureTexts = texts;
    }

    @Override
    public Reaction react(Interpretation input)
    {
        boolean successful = determineSuccess(input);
        String answer;
        State next;
        if(randomGenerator==null)
        {
            randomGenerator = new Random();
        }

        if (successful) {
            int index = randomGenerator.nextInt(successTexts.size());
            answer = successTexts.get(index);
            next = success;
        } else {
            int index = randomGenerator.nextInt(failureTexts.size());
            answer = failureTexts.get(index);
            next = failure;

        }
        if (this.getClass().getName().toLowerCase().contains("introduction")) {
            answer += StatementBuilder.random(Verbalizer.introductionSegue);
        }

        return new Reaction(next, Lists.interpretationList(
                new Interpretation(answer)));
    }

    /**
     * Needs to be implemented by subclasses. If the method returns true the
     * state machine moves to the success state, if it returns false it moves
     * to the failure state.
     * 
     * @param input The interpretation of all inputs
     * @return true or false depending on the examined condition of the method
     */
    abstract protected boolean determineSuccess(Interpretation input);

}
