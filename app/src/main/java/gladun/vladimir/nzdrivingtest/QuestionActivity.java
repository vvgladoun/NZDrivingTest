package gladun.vladimir.nzdrivingtest;


import android.support.v7.app.AppCompatActivity;

/**
 * Question activity contains only one fragment - question fragment
 *
 * @author vvgladoun@gmail.com
 */
public class QuestionActivity extends AppCompatActivity implements QuestionCallbacks{

    @Override
    public Question getQuestion() {
        return null;
    }

    @Override
    public void startNextQuestion(boolean isCorrect) {

    }
    @Override
    public void RestartActivity() {

    }
}
