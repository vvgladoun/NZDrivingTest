package gladun.vladimir.nzdrivingtest;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Answer entity
 *
 * @author Vladimir Gladun vvgladoun@gmail.com
 */
final class Answer implements Parcelable {

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

    public Answer(Parcel in) {
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
    String getAnswerText() {
        return answerText;
    }

    /**
     *
     * @return id in the db
     */
    int getId() {
        return id;
    }


    /**
     * Static method to change user's answers
     *
     * answers in user's array list(selected/unselected answers)
     * must be in the same order as answers in question's array list
     * @param questionAnswers - question answers' array list
     * @param userAnswers - boolean array of user answers (with same order)
     * @return true if user's answer is correct
     */
    static boolean checkAnswers(ArrayList<Answer> questionAnswers,
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
