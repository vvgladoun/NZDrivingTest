package gladun.vladimir.nzdrivingtest;


import android.app.DialogFragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Question activity contains only one fragment - question fragment
 *
 * @author vvgladoun@gmail.com
 */
public class ReviewActivity extends AppCompatActivity implements QuestionCallbacks{

    // to pass arguments on first create
    public static final String EXTRA_TEST_TYPE = "TEST_TYPE";
    // to save current state
    public static final String EXTRA_CURRENT_QUESTION = "CURRENT_QUESTION";
    public static final String EXTRA_QUESTIONS_LIST = "QUESTIONS_LIST";
    public static final String EXTRA_TIME_SPENT = "TIME_SPENT";
    // views
    private ProgressBar mProgressBar;
    private TextView mQuestionCounter;
    private TextView mTimerText;
    // vars
    // changing on pause and resume to manage timer thread
    private boolean mStarted;
    private long mMilliseconds;
    private int mTestType;
    private Context mContext;
    private DownloadFailedTask mDownloadTask;
    private PracticeTimer mTimerThread;
    private FragmentManager mFragmentManager;
    //add Handler for timer and progress bar
    private android.os.Handler mTimerHandler;
    // count of errors
    private int mErrorCount;
    // number of the current question
    private int mCurrentQuestion;
    // Array of questions - create it on first create (if fragment == null)
    // and then store in savedInstanceState as parcelable array list
    private ArrayList<Question> mQuestions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_question);

        // show up navigation button in action bar
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        setTitle("Review");

        // hide status row from activity
        mQuestionCounter = (TextView)findViewById(R.id.question_counter);
        mTimerText = (TextView)findViewById(R.id.timer_text);
        mProgressBar = (ProgressBar)findViewById(R.id.progressBar);
        mProgressBar.setVisibility(View.GONE);
        mStarted = true;

        // get max number of errors from extras
        Bundle extras = this.getIntent().getExtras();
        if (extras != null) {
            mTestType = extras.getInt(EXTRA_TEST_TYPE);
        } else {
            mTestType = Question.CAR_TEST;
        }

        // if state was not saved, get default values
        if (savedInstanceState != null){
            //get from saved state
            mCurrentQuestion = savedInstanceState.getInt(EXTRA_CURRENT_QUESTION);
            mQuestions = savedInstanceState.getParcelableArrayList(EXTRA_QUESTIONS_LIST);
            mMilliseconds = savedInstanceState.getLong(EXTRA_TIME_SPENT);
            mQuestions = savedInstanceState.getParcelableArrayList(EXTRA_QUESTIONS_LIST);
        } else {
            setDefaultValues();
        }
        // initialize timer thread handler
        if (mTimerHandler == null)
            mTimerHandler = new android.os.Handler();
        if (mTimerThread == null)
            mTimerThread = new PracticeTimer();
        // initialize fragment if needed
        mContext = this;
        setFragment();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu for questions' review
        getMenuInflater().inflate(R.menu.menu_review, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
//            case android.R.id.home:
//                // if there are fragments in back stack - show navigation
//                if (getFragmentManager().getBackStackEntryCount() > 0) {
//                    getFragmentManager().popBackStack();
//                    return true;
//                }
            case R.id.remove_all:
                // clear table mistake
                QuestionDAOImpl.removeAllMistakes(mContext);
                // exit review
                exitReview();
            case R.id.remove_current:
                // remove current question from mistakes
                if (mQuestions != null) {
                    (new RemoveMistakenQuestion()).start();
                    // go to the next question
                    startNextQuestion(true);
                }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // number of the current question and category
        outState.putInt(EXTRA_CURRENT_QUESTION, mCurrentQuestion);
        // spent time
        outState.putLong(EXTRA_TIME_SPENT, mMilliseconds);
        // add array of questions (with possible answers)
        outState.putParcelableArrayList(EXTRA_QUESTIONS_LIST, mQuestions);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onStop() {
        super.onStop();
        // stop timer on activity stop
        mTimerHandler.removeCallbacks(mTimerThread);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // restart timer if needed
        if (!mStarted) {
            mTimerHandler.post(mTimerThread);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mStarted = false;
        mTimerHandler.removeCallbacks(mTimerThread);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //cancel async task to avoid crash
        if (mDownloadTask != null) {
            mDownloadTask.cancel(true);
        }
        if (mTimerHandler != null) {
            mTimerHandler.removeCallbacks(mTimerThread);
        }
    }

    /**
     * default initialization
     */
    private void setDefaultValues(){
        mMilliseconds = 0;
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
            mDownloadTask = new DownloadFailedTask();
            mDownloadTask.execute();
        } else {
            //re-start timer
            mTimerHandler.postDelayed(mTimerThread, 1000);
        }
    }

    /**
     * update timer text view
     */
    private void updateTimer(){
        mTimerText.setText(FormatHelper.getTimeString(mMilliseconds));
        // repeat process in a second
        mTimerHandler.postDelayed(mTimerThread, 1000);
    }

    /**
     * Update text for the question counter
     */
    private void setQuestionCounter() {
        //get total number of questions
        int total = (mQuestions == null) ? 0 : mQuestions.size();
        //set text
        String questionCounter = "Question: " + mCurrentQuestion + "/" +
                total + " (" + mErrorCount + ")";
        mQuestionCounter.setText(questionCounter);
    }

    /**
     * Show test results dialog
     */
    private void finishTest(){
        // stop timer
        mTimerHandler.removeCallbacks(mTimerThread);
        //show result dialog
        DialogFragment resultDialog = ResultDialogFragment
                .newInstance(true, mErrorCount, mQuestions.size(), 0);
        resultDialog.show(getFragmentManager(), "RESULT");
    }

    /**
     * Thread for timer
     */
    private class PracticeTimer implements Runnable {
        @Override
        public void run() {
            mStarted = true;
            // increase timer by 1 second
            mMilliseconds = mMilliseconds + 1000 ;
            // update timer text
            updateTimer();
        }
    }

    /**
     * finish activity and exit
     */
    private void exitReview(){
        this.finish();
    }

    /**
     * Async loading data (by category) from database
     *
     * parsed data will be saved into an array list
     */
    private class DownloadFailedTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... arg0) {
            // get questions for review from database
            mQuestions = QuestionDAOImpl.getFailedQuestions(mContext, mTestType);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if (mQuestions.size() == 0) {
                // check if question array is empty
                Toast.makeText(mContext, "No questions for review", Toast.LENGTH_LONG).show();
                exitReview();
            } else {
                // remove progress bar
                mProgressBar.setVisibility(View.GONE);
                // create first question's fragment
                QuestionFragment questionFragment = QuestionFragment.newInstance(true);
                mFragmentManager.beginTransaction()
                        .add(R.id.fragmentContainerQuestion, questionFragment)
                        .commit();
                // update question counter
                setQuestionCounter();
                // start timer
                mTimerHandler.postDelayed(mTimerThread, 1000);
            }
        }
    }

    @Override
    public Question getQuestion() {
        // return current question
        return mQuestions.get(mCurrentQuestion-1);
    }

    @Override
    public void startNextQuestion(boolean isCorrect) {
        // update status row if mistake was made
        if (!isCorrect) {
            mErrorCount++;
            setQuestionCounter();
        }

        // if question is not the last
        if (mCurrentQuestion != mQuestions.size()) {
            mCurrentQuestion++;
            setQuestionCounter();
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.fragmentContainerQuestion, QuestionFragment.newInstance(true));
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

    /**
     * Thread to remove question from review list
     * (question id will be deleted from mistake table)
     *
     * works with database in the background
     */
    private class RemoveMistakenQuestion extends Thread {

        @Override
        public void run() {
            QuestionDAOImpl.removeMistake(mContext, mQuestions.get(mCurrentQuestion - 1).getId());
        }
    }
}