package gladun.vladimir.nzdrivingtest;

import android.util.Log;

import java.util.ArrayList;

/**
 * Answer entity
 *
 * @author Vladimir Gladun vvgladoun@gmail.com
 */
public class Answer {

    private int id;
    private String answerText;
    private boolean isCorrect;

    /**
     * Answer object constructor
     *
     * @param id - id from db
     * @param answerText - text of the answer
     * @param isCorrect - true if this answer one of correct ones
     */
    public Answer(int id, String answerText, boolean isCorrect){
        this.id = id;
        this.answerText = answerText;
        this.isCorrect = isCorrect;
    }

    /**
     *
     * @return text of answer
     */
    public String getAnswerText() {
        return answerText;
    }

    /**
     *
     * @return id in the db
     */
    public int getId() {
        return id;
    }

    /**
     *
     * @return true if correct
     */
    public boolean isCorrect() {
        return isCorrect;
    }

    /**
     * Static method to change user's answers
     *
     * answers in user's array list(selected/unselected answers)
     * must be in the same order as answers in question's array list
     * @param questionAnswers
     * @param userAnswers
     * @return
     */
    public static boolean checkAnswers(ArrayList<Answer> questionAnswers,
                                       boolean[] userAnswers){

        for (int i = 0; i < questionAnswers.size(); i++) {
            if (questionAnswers.get(i).isCorrect != userAnswers[i]) {
                return false;
            }
        }
        return true;
    }
}
