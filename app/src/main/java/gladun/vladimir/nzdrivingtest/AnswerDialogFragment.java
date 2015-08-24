package gladun.vladimir.nzdrivingtest;

import android.app.Dialog;
import android.content.DialogInterface;
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
 * Dialog with answer result (correct / wrong)
 * and right answer
 *
 * @author vvgladoun@gmail.com
 */
public class AnswerDialogFragment extends DialogFragment{

    //labels for extras
    public static final String EXTRA_ANSWER_IMAGE = "ANSWER_IMAGE";
    public static final String EXTRA_ANSWER_TEXT = "ANSWER_TEXT";
    public static final String EXTRA_CORRECT = "CORRECT";


    // name of image resource for a right answer
    private String mAnswerImage;
    // string with the right answer
    private String mAnswerText;
    // flag - answer is correct or not
    private boolean mCorrect;

    //TODO: new instance method with answer text and is_correct flag


    /**
     * Fragment constructor
     * adds arguments to the extras bundle
     *
     * @param correct - flag if test passed
     * @param answerText - right answer's text
     * @param answerImage - name of right answer's image resource
     * @return new fragment object
     */
    public static AnswerDialogFragment newInstance(boolean correct, String answerText,
                                                   String answerImage) {
        Bundle args = new Bundle();
        args.putString(EXTRA_ANSWER_TEXT, answerText);
        args.putString(EXTRA_ANSWER_IMAGE, answerImage);
        args.putBoolean(EXTRA_CORRECT, correct);
        AnswerDialogFragment fragment = new AnswerDialogFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // get attributes from bundle
        super.onCreate(savedInstanceState);
        Bundle extras = getArguments();
        mCorrect = extras.getBoolean(EXTRA_CORRECT);
        mAnswerText = extras.getString(EXTRA_ANSWER_TEXT);
        mAnswerImage = extras.getString(EXTRA_ANSWER_IMAGE);

        // do not close dialog
        this.setCancelable(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedState){
        final View dialog = inflater.inflate(R.layout.fragment_answer, container, false);

        //set status
        ImageView statusIcon = (ImageView)dialog.findViewById(R.id.iv_answer_title);
        TextView statusText = (TextView)dialog.findViewById(R.id.answer_title);
        if (mCorrect) {
            //set passed
            statusIcon.setImageResource(R.drawable.ic_passed_large);
            int color = getResources().getColor(R.color.text_green);
            statusIcon.setColorFilter(color);
            statusText.setText(getResources().getText(R.string.answer_correct));
        } else {
            //set failed
            statusIcon.setImageResource(R.drawable.ic_failed_large);
            int color = getResources().getColor(R.color.text_red);
            statusIcon.setColorFilter(color);
            statusText.setText(getResources().getText(R.string.answer_wrong));
        }
        // set right answer text
        TextView tvAnswerText = (TextView)dialog.findViewById(R.id.answer_text);
        tvAnswerText.setText(mAnswerText);
        //if answer has image, add it
        if (!mAnswerImage.equals("")) {
            ImageView answerImage = (ImageView)dialog.findViewById(R.id.answer_image);
        }
        //find OK button and set listener
        Button btnExit = (Button)dialog.findViewById(R.id.answer_exit_btn);
        //set listeners
        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });

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
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if (getActivity() instanceof QuestionCallbacks) {
            QuestionCallbacks qa = (QuestionCallbacks) getActivity();
            qa.startNextQuestion(mCorrect);
        }
    }
}