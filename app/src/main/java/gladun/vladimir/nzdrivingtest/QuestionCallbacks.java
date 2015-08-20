package gladun.vladimir.nzdrivingtest;

import java.util.ArrayList;

/**
 * Test simulation activity class
 *
 * @author Vladimir Gladun vvgladoun@gmail.com
 */
public interface QuestionCallbacks {

    /**
     * Starts next question
     * If question was the last, show results
     *
     * For the next question fragment Question object
     * will be attached
     *
     * Also updates stat row of activity
     * (set current question and number of errors)
     *
     * @param isCorrect - flag, if current question was answered right
     */
    void startNextQuestion(boolean isCorrect);

    /**
     * Get current question for a fragment
     *
     * @return question object
     */
    public Question getQuestion();

    /**
     * Re-create current activity with default settings
     */
    void RestartActivity();


}
