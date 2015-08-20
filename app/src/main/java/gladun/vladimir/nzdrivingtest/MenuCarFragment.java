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

/**
 * Menu for car driving test
 *
 * @author Vladimir Gladun vvgladoun@gmail.com
 */
public class MenuCarFragment extends Fragment implements View.OnClickListener{

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // get main menu fragment layout
        View fragment_view = inflater.inflate(R.layout.fragment_menu_car, container, false);

        // get buttons from activity's layout
        Button btnMenuCarQuestions = (Button) fragment_view.findViewById(R.id.btn_menu_car_questions);
        Button btnMenuCarTest = (Button) fragment_view.findViewById(R.id.btn_menu_car_test);
        Button btnMenuCarStatistics = (Button) fragment_view.findViewById(R.id.btn_menu_car_statistics);
        Button btnMenuCarReview = (Button) fragment_view.findViewById(R.id.btn_menu_car_review);

        // set implemented method onClick as the onClickListener
        btnMenuCarQuestions.setOnClickListener(this);
        btnMenuCarTest.setOnClickListener(this);
        btnMenuCarStatistics.setOnClickListener(this);
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
            case R.id.btn_menu_car_statistics:
                // show statistics by groups (car/bike):
                //  bar for finished tests
                //  bar for answered questions

                break;
            case R.id.btn_menu_car_review:
                // start question activity with array list of questions
                // from error table
                //TODO: add intent question activity (with error option in extras)
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
