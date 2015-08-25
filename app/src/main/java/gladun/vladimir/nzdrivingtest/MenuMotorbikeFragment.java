package gladun.vladimir.nzdrivingtest;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

/**
 * Menu for motorbike driving test
 *
 * @author Vladimir Gladun vvgladoun@gmail.com
 */
public final class MenuMotorbikeFragment extends Fragment implements View.OnClickListener{

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        getActivity().setTitle("Motorbike test");

        // get motorbike menu fragment layout
        View fragment_view = inflater.inflate(R.layout.fragment_menu_motorbike, container, false);

        // get buttons from activity's layout
        Button btnMenuQuestions = (Button) fragment_view.findViewById(R.id.btn_menu_mb_questions);
        Button btnMenuTest = (Button) fragment_view.findViewById(R.id.btn_menu_mb_test);
        Button btnMenuReview = (Button) fragment_view.findViewById(R.id.btn_menu_mb_review);

        // set implemented method onClick as the onClickListener
        btnMenuQuestions.setOnClickListener(this);
        btnMenuTest.setOnClickListener(this);
        btnMenuReview.setOnClickListener(this);

        // set icon color
        int color = getResources().getColor(R.color.icon_grey);
        ImageView ivQuestions = (ImageView)fragment_view.findViewById(R.id.iv_mb_question);
        ImageView ivTest = (ImageView)fragment_view.findViewById(R.id.iv_mb_test);
        ImageView ivReview = (ImageView)fragment_view.findViewById(R.id.iv_mb_review);
        ivQuestions.setColorFilter(color);
        ivTest.setColorFilter(color);
        ivReview.setColorFilter(color);

        return fragment_view;
    }

    @Override
    public void onClick(View v) {
        // action depends on the view's id
        switch (v.getId()) {
            case R.id.btn_menu_mb_questions:
                // open question categories
                startFragment(CategoryListFragment.newInstance(Question.MOTORBIKE_TEST));
                break;
            case R.id.btn_menu_mb_test:
                //start exam simulator
                Intent testActivity = new Intent(v.getContext(), TestActivity.class);
                testActivity.putExtra(TestActivity.EXTRA_MAX_ERRORS, 3);
                testActivity.putExtra(TestActivity.EXTRA_TEST_TYPE, Question.MOTORBIKE_TEST);
                v.getContext().startActivity(testActivity);
                break;
            case R.id.btn_menu_mb_review:
                // start question activity with array list of questions
                // from error table
                Intent reviewActivity = new Intent(v.getContext(), ReviewActivity.class);
                reviewActivity.putExtra(TestActivity.EXTRA_TEST_TYPE, Question.MOTORBIKE_TEST);
                v.getContext().startActivity(reviewActivity);
                break;
        }
    }

    /**
     * Replace current fragment with the next one
     *
     * @param fragment - next fragment
     */
    private void startFragment(Fragment fragment){
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.fragmentContainer, fragment);
        transaction.addToBackStack(null); //add to stack for proper back navigation
        transaction.commit();
    }
}

