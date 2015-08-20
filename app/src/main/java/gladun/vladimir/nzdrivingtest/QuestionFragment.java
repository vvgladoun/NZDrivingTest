package gladun.vladimir.nzdrivingtest;

import android.app.Fragment;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * @author Vladimir Gladun vvgladoun@gmail.com
 */
public class QuestionFragment extends Fragment {

    public static final String EXTRA_QUESTION_NUMBER = "QUESTION_NUMBER";
    public static final String EXTRA_SHOW_ANSWER = "SHOW_NUMBER";
    private Question mQuestion;
    private boolean mShowAnswer;
    // floating button to navigate to the next question
    private FloatingActionButton mNextButton;
    // array for the user's answers
    private boolean[] mAnswers;


    /**
     * Create new instance of the fragment with defined extra
     *
     * @param questionNumber - index of the current question
     * @param showAnswer - if true, show answer before next question
     * @return built fragment
     */
    public static QuestionFragment newInstance(int questionNumber, boolean showAnswer) {
        Bundle args = new Bundle();
        args.putInt(EXTRA_QUESTION_NUMBER, questionNumber);
        args.putBoolean(EXTRA_SHOW_ANSWER, showAnswer);
        QuestionFragment fragment = new QuestionFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // get question fragment layout
        View fragment_view = inflater.inflate(R.layout.fragment_question, container, false);

        if (mQuestion != null) {
            ((TextView)fragment_view.findViewById(R.id.question_text))
                    .setText(mQuestion.getQuestionText());
            // SHOW IMAGE IF NEEDED
            String questionImageName = mQuestion.getImageName();
            if (!questionImageName.equals("")) {
                int resID = getResources().getIdentifier(questionImageName, "drawable", getActivity().getPackageName());
                if (resID > 0) {
                    //add image if found
                    ImageView questionImage = (ImageView) fragment_view.findViewById(R.id.question_image);
                    questionImage.setImageResource(resID);
                }
            }
            // ADD POSSIBLE ANSWERS
            if (mQuestion.isMultipleChoice()) {
                addAnswerCheckBoxes(fragment_view);
            } else {
                addAnswerRadioButtons(fragment_view);
            }
        }

        // Material design floating button to go to the next question
        mNextButton = (FloatingActionButton) fragment_view.findViewById(R.id.next_question_button);
        mNextButton.setColorFilter(Color.WHITE);
        //mNextButton.setActivated(false);
        mNextButton.setVisibility(View.GONE);
        mNextButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (getActivity() instanceof QuestionCallbacks) {
                    QuestionCallbacks qa = (QuestionCallbacks) getActivity();
                    // check if answers were correct
                    boolean isCorrect = Answer.checkAnswers(mQuestion.getAnswers(), mAnswers);
                    if (!isCorrect) {
                        //save the mistake for review
                        (new SaveMistakenQuestion()).execute();
                    }
                    // if needed, show right answer
                    if (mShowAnswer) {
                        //TODO: show dialog window with result
                    }
                    // put fragment of the next question (or results)
                    qa.startNextQuestion(isCorrect);
                }
            }
        });
        return fragment_view;
    }

    /**
     * adds check box view for each possible answer
     * and retain their status if needed (if view were re-created)
     *
     * @param fragment_view - current fragment's view
     */
    private void addAnswerCheckBoxes(View fragment_view){
        // get answers
        ArrayList<Answer> answers = mQuestion.getAnswers();
        final int aCount = answers.size();
        if (mAnswers == null) {
            mAnswers = new boolean[aCount];
        }
        // get container for new views
        final LinearLayout answerContainer = (LinearLayout)fragment_view
                .findViewById(R.id.answers_check_boxes);

        CompoundButton.OnCheckedChangeListener answerListener = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                boolean somethingChecked = false;
                for (int i = 0; i < aCount; i++) {
                    // if checkbox is the changed one, change answers array
                    if (answerContainer.getChildAt(i) == buttonView) {
                        mAnswers[i] = ((CheckBox) answerContainer.getChildAt(i)).isChecked();
                    }
                    if (mAnswers[i])
                        somethingChecked = true;
                }
                // set availability for a floating button
                mNextButton.setActivated(somethingChecked);
                if (somethingChecked)
                    mNextButton.setVisibility(View.VISIBLE);
            }
        };

        //add checkboxes
        for (int i = 0; i < aCount; i++) {
            CheckBox answerView = new CheckBox(getActivity());
            TableRow.LayoutParams lpDetail = new TableRow
                    .LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
                    TableRow.LayoutParams.WRAP_CONTENT);
            answerView.setLayoutParams(lpDetail);
            answerView.setText(answers.get(i).getAnswerText());
            answerView.setOnCheckedChangeListener(answerListener);
            answerContainer.addView(answerView);
        }

        //retain checked
        for (int i = 0; i < aCount; i++) {
            ((CheckBox) answerContainer.getChildAt(i)).setChecked(mAnswers[i]);
        }
    }

    /**
     * adds radio button view for each possible answer
     * and retain their status if needed (if view were re-created)
     *
     * @param fragment_view - current fragment's view
     */
    private void addAnswerRadioButtons(View fragment_view){
        // get answers
        ArrayList<Answer> answers = mQuestion.getAnswers();
        final int aCount = answers.size();
        if (mAnswers == null) {
            mAnswers = new boolean[aCount];
        }
        //add radio buttons to rb group
        RadioGroup answerContainer = (RadioGroup)fragment_view
                .findViewById(R.id.answers_radio_group);

        RadioGroup.OnCheckedChangeListener answerListener = new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                for (int i = 0; i < aCount; i++) {
                    // change checked answer in the array
                    mAnswers[i] = (group.getChildAt(i).getId() == checkedId);

                    //mNextButton.setActivated(true);
                    mNextButton.setVisibility(View.VISIBLE);
                }
            }
        };

        //add radio buttons
        for (int i = 0; i < aCount; i++) {
            RadioButton answerView = new RadioButton(getActivity());
            TableRow.LayoutParams lpDetail = new TableRow
                    .LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
                    TableRow.LayoutParams.WRAP_CONTENT);
            answerView.setLayoutParams(lpDetail);
            answerView.setText(answers.get(i).getAnswerText());
            answerContainer.setOnCheckedChangeListener(answerListener);
            answerContainer.addView(answerView);
        }
        //retain checked
        for (int i = 0; i < aCount; i++) {
            if (mAnswers[i]) {
                ((RadioButton)answerContainer.getChildAt(i)).setChecked(true);
            }
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // will retain instance after rotation
        setRetainInstance(true);

        // get show answer flag
        mShowAnswer = getArguments().getBoolean(EXTRA_SHOW_ANSWER);

        //get question from activity
        if (mQuestion == null && (getActivity() instanceof QuestionCallbacks)) {
            QuestionCallbacks qa = (QuestionCallbacks)getActivity();
            mQuestion = qa.getQuestion();
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }


    /**
     * Async task to save the id
     * of question where mistake was made
     *
     * works with database in the background
     */
    private class SaveMistakenQuestion extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... arg0) {
            QuestionDAO.AddMistake(getActivity() ,mQuestion.getId());
            return null;
        }
    }
}
