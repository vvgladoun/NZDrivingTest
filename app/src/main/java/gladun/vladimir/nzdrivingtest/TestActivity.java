package gladun.vladimir.nzdrivingtest;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Test simulation activity class
 *
 * @author Vladimir Gladun vvgladoun@gmail.com
 */
public final class TestActivity extends AppCompatActivity implements QuestionCallbacks{

    // exam duration in milliseconds
    private static final long TEST_DURATION = 1800000;
    private static final int NUMBER_OF_QUESTIONS = 35;
    private static final int DEFAULT_MAX_ERRORS = 3;

    // to pass arguments on first create
    public static final String EXTRA_MAX_ERRORS = "MAX_ERRORS";
    public static final String EXTRA_TEST_TYPE = "TEST_TYPE";
    // to save current state
    public static final String EXTRA_TIME_LEFT = "TIME_LEFT";
    public static final String EXTRA_CURRENT_QUESTION = "CURRENT_QUESTION";
    public static final String EXTRA_ERROR_COUNT = "ERROR_COUNT";
    public static final String EXTRA_QUESTIONS_LIST = "QUESTIONS_LIST";

    private int mMaxErrors;
    private TextView mQuestionCounter;
    private TextView mTimerText;
    private ProgressBar mProgressBar;
    private CountDownTimer mCountdownTimer;
    private int mTestType;
    private Context mContext;
    private DownloadQuestionsTask mDownloadTask;
    private FragmentManager mFragmentManager;

    // stats row attributes:
    // - time left for the test
    private long mTimeLeft;
    // - index of the current question
    private int mCurrentQuestion;
    // - count of errors
    private int mErrorCount;

    // Array of questions - create it on first create (if fragment == null)
    // and then store in savedInstanceState as parcelable array list
    private ArrayList<Question> mQuestions;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_question);

        //show up navigation button in action bar
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        setTitle("Test simulator");

        mQuestionCounter = (TextView)findViewById(R.id.question_counter);
        mTimerText = (TextView)findViewById(R.id.timer_text);
        mProgressBar = (ProgressBar)findViewById(R.id.progressBar);
        mProgressBar.setVisibility(View.GONE);

        //get max number of errors from extras
        Bundle extras = this.getIntent().getExtras();
        if (extras != null) {
            mMaxErrors = extras.getInt(EXTRA_MAX_ERRORS);
            mTestType = extras.getInt(EXTRA_TEST_TYPE);
        } else {
            mMaxErrors = DEFAULT_MAX_ERRORS;
            mTestType = Question.CAR_TEST;
        }

        // if state was not saved, get default values
        if (savedInstanceState == null){
            setDefaultValues();
        } else {
            //get from saved state
            mTimeLeft = savedInstanceState.getLong(EXTRA_TIME_LEFT);
            mCurrentQuestion = savedInstanceState.getInt(EXTRA_CURRENT_QUESTION);
            mErrorCount = savedInstanceState.getInt(EXTRA_ERROR_COUNT);
            mQuestions = savedInstanceState.getParcelableArrayList(EXTRA_QUESTIONS_LIST);
        }

        mContext = this;
        setFragment();

        // INITIALIZING QUESTION STATUS ROW
        //set question counter string
        setQuestionCounter();
        //start test timer
        initiateTimer();
    }

    @Override
    protected void onStop() {
        super.onStop();
        // stop timer on activity stop
        mCountdownTimer.cancel();
        // and remove the object
        mCountdownTimer = null;
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        // restart timer if needed
        if (mCountdownTimer == null) {
            initiateTimer();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //cancel all threads to avoid crash
        // or memory leaking
        if (mDownloadTask != null) {
            mDownloadTask.cancel(true);
        }
        if (mCountdownTimer != null) {
            mCountdownTimer.cancel();
        }
    }

    /**
     * default initialization
     */
    private void setDefaultValues(){
        mTimeLeft = TEST_DURATION;
        mCurrentQuestion = 1;
        mErrorCount = 0;
    }

    /**
     * Get activities' fragment manager
     * and create initial fragment if needed
     */
    private void setFragment(){
        mFragmentManager = getFragmentManager();
        QuestionFragment questionFragment = (QuestionFragment)mFragmentManager
                .findFragmentById(R.id.fragmentContainerQuestion);

        if (questionFragment == null) {
            // start progress bar until question load async task is finished
            mProgressBar.setVisibility(View.VISIBLE);
            // start async task to download
            mDownloadTask = new DownloadQuestionsTask();
            mDownloadTask.execute();
        }
    }

    /**
     * set countdown timer and start it
     */
    private void initiateTimer(){
        /**
         * Timer thread
         */
        mCountdownTimer = new CountDownTimer(mTimeLeft, 1000) {

            /**
             * Change timer text view on ticks
             *
             * @param millisUntilFinished - duration (in ms)
             */
            public void onTick(long millisUntilFinished) {
                mTimerText.setText(FormatHelper.getTimeString(millisUntilFinished));
                mTimeLeft = mTimeLeft - 1000;
            }

            /**
             * Stop exam on the end of timer
             */
            public void onFinish() {
                finishTest();
            }
        };
        mCountdownTimer.start();
    }

    /**
     * Update statistics stored in internal storage
     */
    private void updateStatistics(){
        Context context = this;
        int fileId;
        // get file key (depends on test type)
        switch (mTestType) {
            case Question.CAR_TEST:
                fileId = R.string.file_key_statistics_car;
                break;
            case Question.MOTORBIKE_TEST:
                fileId = R.string.file_key_statistics_mb;
                break;
            default:
                fileId = R.string.file_key_statistics_car;
        }
        // get shared preferences file by file key
        SharedPreferences sharedPref = context.getSharedPreferences(
                getString(fileId), Context.MODE_PRIVATE);

        // get previous statistics
        int questions = sharedPref.getInt(getString(R.string.saved_questions_count), 0);
        int mistakes = sharedPref.getInt(getString(R.string.saved_mistakes_count), 0);
        int score = sharedPref.getInt(getString(R.string.saved_average_score), 0);
        long avgMilliseconds = sharedPref.getLong(getString(R.string.saved_average_time), 0);

        // update statistics
        avgMilliseconds =
                ((avgMilliseconds * questions) + ((TEST_DURATION - mTimeLeft) * mCurrentQuestion)
                        / (questions + mCurrentQuestion));
        questions = questions + mCurrentQuestion;
        mistakes = mistakes + mErrorCount;
        if (questions > 0)
            score = (questions - mistakes) / questions;

        // save updated statistics
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(getString(R.string.saved_questions_count), questions);
        editor.putInt(getString(R.string.saved_mistakes_count), mistakes);
        editor.putInt(getString(R.string.saved_average_score), score);
        editor.putLong(getString(R.string.saved_average_time), avgMilliseconds);
        editor.apply();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //save all variables to a bundle
        outState.putLong(EXTRA_TIME_LEFT, mTimeLeft);
        outState.putInt(EXTRA_CURRENT_QUESTION, mCurrentQuestion);
        outState.putInt(EXTRA_ERROR_COUNT, mErrorCount);
        //add array of questions (with possible answers)
        outState.putParcelableArrayList(EXTRA_QUESTIONS_LIST, mQuestions);
        super.onSaveInstanceState(outState);
    }

    /**
     * Show dialog "Do you really want to quit test mode?"
     * on Back button pressed
     */
    @Override
    public void onBackPressed() {
        //show quit dialog
        QuitDialogFragment quitDialog = new QuitDialogFragment();
        quitDialog.show(getFragmentManager(), "QUIT");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Update text for the question counter
     *
     * @param curQuestion - index of current question
     * @param total - total count of questions
     * @param numOfErrors - number of errors
     */
    private void setQuestionCounter(int curQuestion, int total, int numOfErrors) {

        String questionCounter = "Question: " + curQuestion + "/" +
                total + " (" + numOfErrors + ")";

        // color depends on number of errors
        int color = R.color.text_green;
        if (numOfErrors > mMaxErrors) {
            //if more then maximum errors allowed for a test - red
            color = R.color.text_red;
        } else if (numOfErrors > 0) {
            // if more then zero - orange
            color = R.color.text_orange;
        }
        int endIndex = questionCounter.length();
        //create spannable text to change the color for a part of text view
        Spannable spannable = new SpannableString(questionCounter);
        spannable.setSpan(new ForegroundColorSpan(getResources().getColor(color)),
                endIndex-4, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        //set text
        mQuestionCounter.setText(spannable, TextView.BufferType.SPANNABLE);
    }

    /**
     * Update text for the question counter
     * from the local variables
     */
    private void setQuestionCounter(){
        setQuestionCounter(mCurrentQuestion, NUMBER_OF_QUESTIONS, mErrorCount);
    }

    /**
     * Show test results dialog
     */
    private void finishTest(){
        // stop timer
        mCountdownTimer.cancel();
        updateStatistics();
        // get errors/ unanswered questions
        int mistaken = mErrorCount + NUMBER_OF_QUESTIONS - mCurrentQuestion;
        //show result dialog
        DialogFragment resultDialog = ResultDialogFragment
                .newInstance((mistaken <= mMaxErrors), mistaken, NUMBER_OF_QUESTIONS, (TEST_DURATION - mTimeLeft));
        resultDialog.show(getFragmentManager(), "RESULT");
    }

    /**
     * Start next question from the fragment
     */
    @Override
    public void startNextQuestion(boolean isCorrect) {
        // update status row if mistake was made
        if (!isCorrect) {
            mErrorCount++;
            setQuestionCounter();
        }

        // if question is not the last
        if (mCurrentQuestion != NUMBER_OF_QUESTIONS) {
            mCurrentQuestion++;
            setQuestionCounter();
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.fragmentContainerQuestion, QuestionFragment.newInstance(false));
            transaction.commit();
        } else {
            // if test was finished
            finishTest();
        }
    }

    @Override
    public void RestartActivity() {
        mFragmentManager = getFragmentManager();
        QuestionFragment questionFragment = (QuestionFragment)mFragmentManager
                .findFragmentById(R.id.fragmentContainerQuestion);
        // remove fragment
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        transaction.remove(questionFragment).commit();
        // initialize default values
        setDefaultValues();
        // re-create activity
        this.recreate();
    }

    @Override
    public Question getQuestion() {
        // return current question
        return mQuestions.get(mCurrentQuestion-1);
    }

    /**
     * Async loading data from database
     *
     * parsed data will be saved into an array list
     */
    private class DownloadQuestionsTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... arg0) {
            mQuestions = QuestionDAOImpl.getShuffledQuestions(mContext, mTestType, NUMBER_OF_QUESTIONS);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // remove progress bar
            mProgressBar.setVisibility(View.GONE);

            // create first question's fragment
            QuestionFragment questionFragment = QuestionFragment.newInstance(false);
            mFragmentManager.beginTransaction()
                    .add(R.id.fragmentContainerQuestion, questionFragment)
                    .commit();
        }
    }
}

