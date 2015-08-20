package gladun.vladimir.nzdrivingtest;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * Menu for motorbike driving test
 *
 * @author Vladimir Gladun vvgladoun@gmail.com
 */
public class MenuMotorbikeFragment extends Fragment implements View.OnClickListener{

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // get main menu fragment layout
        View fragment_view = inflater.inflate(R.layout.fragment_menu_motorbike, container, false);

        // get buttons from activity's layout
        Button btnMenuQuestions = (Button) fragment_view.findViewById(R.id.btn_menu_mb_questions);
        Button btnMenuTest = (Button) fragment_view.findViewById(R.id.btn_menu_mb_test);
        Button btnMenuStatistics = (Button) fragment_view.findViewById(R.id.btn_menu_mb_statistics);
        Button btnMenuReview = (Button) fragment_view.findViewById(R.id.btn_menu_mb_review);

        // set implemented method onClick as the onClickListener
        btnMenuQuestions.setOnClickListener(this);
        btnMenuTest.setOnClickListener(this);
        btnMenuStatistics.setOnClickListener(this);
        btnMenuReview.setOnClickListener(this);

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
                //TODO: uncomment after exam activity is ready
                //Intent carMenu = new Intent(v.getContext(), ExamActivity.class);
                //v.getContext().startActivity(carMenu);
                break;
            case R.id.btn_menu_mb_statistics:
                // show statistics by groups (car/bike):
                //  bar for finished tests
                //  bar for answered questions

                break;
            case R.id.btn_menu_mb_review:
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

