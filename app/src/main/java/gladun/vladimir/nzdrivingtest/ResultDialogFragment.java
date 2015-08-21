package gladun.vladimir.nzdrivingtest;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Dialog with results of the test
 *
 * @author vvgladoun@gmail.com
 */
public class ResultDialogFragment extends DialogFragment implements View.OnClickListener {

    //labels for extras
    public static final String EXTRA_MISTAKES = "MISTAKES";
    public static final String EXTRA_QUESTIONS = "QUESTIONS";
    public static final String EXTRA_TIME_SPENT = "TIME_SPENT";
    public static final String EXTRA_PASSED = "PASSED";


    // mistakes count
    private int mMistakes;
    // questions count
    private int mQuestions;
    // time spent for the test (in ms)
    private long mTimeSpent;
    // flag - passed or not
    private boolean mPassed;

    /**
     * Fragment constructor
     * adds arguments to the extras bundle
     *
     * @param passed - flag if test passed
     * @param mistakes - number of mistakes
     * @param questions - num of questions
     * @param timeSpent - time spent for the test
     * @return new fragment object
     */
    public static ResultDialogFragment newInstance(boolean passed, int mistakes,
                                                   int questions, long timeSpent) {

        Bundle args = new Bundle();
        args.putInt(EXTRA_MISTAKES, mistakes);
        args.putInt(EXTRA_QUESTIONS, questions);
        args.putLong(EXTRA_TIME_SPENT, timeSpent);
        args.putBoolean(EXTRA_PASSED, passed);
        ResultDialogFragment fragment = new ResultDialogFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // get attributes from bundle
        super.onCreate(savedInstanceState);
        Bundle extras = getArguments();
        mMistakes = extras.getInt(EXTRA_MISTAKES);
        mQuestions = extras.getInt(EXTRA_QUESTIONS);
        mTimeSpent = extras.getLong(EXTRA_TIME_SPENT);
        mPassed = extras.getBoolean(EXTRA_PASSED);

        // do not close dialog
        this.setCancelable(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedState){
        View dialog = inflater.inflate(R.layout.fragment_result, container, false);

        //set status
        ImageView statusIcon = (ImageView)dialog.findViewById(R.id.test_result_icon);
        TextView statusText = (TextView)dialog.findViewById(R.id.test_result_status);
        if (mTimeSpent == 0) {
            // practice mode (not for test simulation)
            statusIcon.setImageResource(R.drawable.ic_statistics);
            int color = getResources().getColor(R.color.material_yellow_600);
            statusIcon.setColorFilter(color);
            statusText.setText(getResources().getText(R.string.result_status_statistics));
            dialog.findViewById(R.id.test_result_time).setVisibility(View.GONE);
        } else {
            if (mPassed) {
                //set passed
                statusIcon.setImageResource(R.drawable.ic_passed_large);
                int color = getResources().getColor(R.color.text_green);
                statusIcon.setColorFilter(color);
                statusText.setText(getResources().getText(R.string.result_status_passed));
            } else {
                //set failed
                statusIcon.setImageResource(R.drawable.ic_failed_large);
                int color = getResources().getColor(R.color.text_red);
                statusIcon.setColorFilter(color);
                statusText.setText(getResources().getText(R.string.result_status_failed));
            }
            TextView tvTime = (TextView)dialog.findViewById(R.id.test_result_time);
            tvTime.setText("Time: " + FormatHelper.getTimeString(mTimeSpent));
        }
        // set details
        TextView tvScore = (TextView)dialog.findViewById(R.id.test_result_score);
        tvScore.setText("Your score: " + ((100*(mQuestions-mMistakes))/mQuestions)+ "%");
        TextView tvCorrect = (TextView)dialog.findViewById(R.id.test_result_correct);
        tvCorrect.setText("Correct answers: " + (mQuestions - mMistakes));
        TextView tvWrong = (TextView)dialog.findViewById(R.id.test_result_wrong);
        tvWrong.setText("Wrong answers: " + mMistakes);

        //find buttons
        Button btnExit = (Button)dialog.findViewById(R.id.test_result_exit_btn);
        Button btnTryAgain = (Button)dialog.findViewById(R.id.test_result_again_btn);
        //set listeners
        btnExit.setOnClickListener(this);
        btnTryAgain.setOnClickListener(this);

        return dialog;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        //remove the default title
        TextView title = (TextView)dialog.findViewById(android.R.id.title);
        title.setVisibility(View.GONE);

        return dialog;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.test_result_exit_btn:
                getActivity().finish();
                break;
            case R.id.test_result_again_btn:
                if (getActivity() instanceof QuestionCallbacks) {
                    QuestionCallbacks qa = (QuestionCallbacks) getActivity();
                    qa.RestartActivity();
                }
                dismiss();
                break;
        }
    }
}
