package gladun.vladimir.nzdrivingtest;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.util.ArrayList;

/**
 * Answer entity
 *
 * @author Vladimir Gladun vvgladoun@gmail.com
 */
public class Answer implements Parcelable {

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

    protected Answer(Parcel in) {
        id = in.readInt();
        answerText = in.readString();
        isCorrect = (in.readByte() == 1);
    }

    public static final Creator<Answer> CREATOR = new Creator<Answer>() {
        @Override
        public Answer createFromParcel(Parcel in) {
            return new Answer(in);
        }

        @Override
        public Answer[] newArray(int size) {
            return new Answer[size];
        }
    };

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(answerText);
        dest.writeByte((byte)(isCorrect ? 1 : 0));
    }
}
