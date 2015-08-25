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
 * Menu for car driving test
 *
 * @author Vladimir Gladun vvgladoun@gmail.com
 */
public final class MenuCarFragment extends Fragment implements View.OnClickListener{

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        getActivity().setTitle("Car test");

        // get main menu fragment layout
        View fragment_view = inflater.inflate(R.layout.fragment_menu_car, container, false);

        // get buttons from activity's layout
        Button btnMenuCarQuestions = (Button) fragment_view.findViewById(R.id.btn_menu_car_questions);
        Button btnMenuCarTest = (Button) fragment_view.findViewById(R.id.btn_menu_car_test);
        Button btnMenuCarReview = (Button) fragment_view.findViewById(R.id.btn_menu_car_review);

        // set icon color
        int color = getResources().getColor(R.color.icon_grey);
        ImageView ivQuestions = (ImageView)fragment_view.findViewById(R.id.iv_car_question);
        ImageView ivTest = (ImageView)fragment_view.findViewById(R.id.iv_car_test);
        ImageView ivReview = (ImageView)fragment_view.findViewById(R.id.iv_car_review);
        ivQuestions.setColorFilter(color);
        ivTest.setColorFilter(color);
        ivReview.setColorFilter(color);

        // set implemented method onClick as the onClickListener
        btnMenuCarQuestions.setOnClickListener(this);
        btnMenuCarTest.setOnClickListener(this);
        btnMenuCarReview.setOnClickListener(this);

        return fragment_view;
    }

    @Override
    public void onClick(View v) {
        // action depends on the view's id
        switch (v.getId()) {
            case R.id.btn_menu_car_questions:
                // open question categories
                startFragment(CategoryListFragment.newInstance(Question.CAR_TEST));
                break;
            case R.id.btn_menu_car_test:
                //start exam simulator
                Intent testActivity = new Intent(v.getContext(), TestActivity.class);
                testActivity.putExtra(TestActivity.EXTRA_MAX_ERRORS, 3);
                testActivity.putExtra(TestActivity.EXTRA_TEST_TYPE, Question.CAR_TEST);
                v.getContext().startActivity(testActivity);
                break;

            case R.id.btn_menu_car_review:
                // start question activity with array list of questions
                // from mistake table
                Intent reviewActivity = new Intent(v.getContext(), ReviewActivity.class);
                reviewActivity.putExtra(TestActivity.EXTRA_TEST_TYPE, Question.CAR_TEST);
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
