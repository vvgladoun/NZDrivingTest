package gladun.vladimir.nzdrivingtest;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Show statistics for finished tests
 *
 * @author Vladimir Gladun vvgladoun@gmail.com
 */
public class StatisticsFragment extends Fragment implements View.OnClickListener{

    private View mFragmentView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        getActivity().setTitle("Statistics");

        // get statistics fragment layout
        View fragment_view = inflater.inflate(R.layout.fragment_statistics, container, false);

        // get buttons from activity's layout
        Button btnClearCar = (Button) fragment_view.findViewById(R.id.stat_car_clear_btn);
        Button btnClearMb = (Button) fragment_view.findViewById(R.id.stat_mb_clear_btn);

        // set icon color
        int color = getResources().getColor(R.color.material_yellow_600);
        ImageView ivCar = (ImageView)fragment_view.findViewById(R.id.statistics_car_icon);
        ImageView ivMotorbike = (ImageView)fragment_view.findViewById(R.id.statistics_mb_icon);
        ivCar.setColorFilter(color);
        ivMotorbike.setColorFilter(color);


        // set implemented method onClick as the onClickListener
        btnClearCar.setOnClickListener(this);
        btnClearMb.setOnClickListener(this);

        // set statistics text for all test types
        setStatisticsText(fragment_view, R.string.file_key_statistics_car, R.id.stat_car_total,
                R.id.stat_car_correct, R.id.stat_car_incorrect, R.id.stat_car_avg_scope, R.id.stat_car_avg_time);
        setStatisticsText(fragment_view, R.string.file_key_statistics_mb, R.id.stat_mb_total,
                R.id.stat_mb_correct, R.id.stat_mb_incorrect, R.id.stat_mb_avg_scope, R.id.stat_mb_avg_time);

        mFragmentView = fragment_view;

        return fragment_view;
    }

    @Override
    public void onClick(View v) {
        // action depends on the view's id
        switch (v.getId()) {
            case R.id.stat_car_clear_btn:
                // remove statistics for car test
                clearStatistics(Question.CAR_TEST);
                break;
            case R.id.stat_mb_clear_btn:
                // remove statistics for motorbike test
                clearStatistics(Question.MOTORBIKE_TEST);
                break;
        }
    }

    /**
     * Clear statistics for the test of selected type
     *
     * @param testType - type of test: car, motorbike etc.
     */
    private void clearStatistics(int testType){
        Context context = getActivity();
        int fileId;
        // get file key (depends on test type)
        switch (testType) {
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

//        // get previous statistics
//        int questions = sharedPref.getInt(getString(R.string.saved_questions_count), 0);
//        int mistakes = sharedPref.getInt(getString(R.string.saved_mistakes_count), 0);
//        int score = sharedPref.getInt(getString(R.string.saved_average_score), 0);
//        long avgMilliseconds = sharedPref.getLong(getString(R.string.saved_average_time), 0);

        // clear statistics
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.clear().apply();

        // update widgets
        switch (testType) {
            case Question.CAR_TEST:
                setStatisticsText(mFragmentView, R.string.file_key_statistics_car, R.id.stat_car_total,
                        R.id.stat_car_correct, R.id.stat_car_incorrect,
                        R.id.stat_car_avg_scope, R.id.stat_car_avg_time);
                break;
            case Question.MOTORBIKE_TEST:
                setStatisticsText(mFragmentView, R.string.file_key_statistics_mb, R.id.stat_mb_total,
                        R.id.stat_mb_correct, R.id.stat_mb_incorrect,
                        R.id.stat_mb_avg_scope, R.id.stat_mb_avg_time);
                break;
        }
    }

    /**
     * set statistics text for one test type
     * from internal storage file (preferences)
     *
     * @param fragment_view - fragment's layout
     * @param fileKeyId - id of file's key
     * @param idTotal - widget for total
     * @param idCorrect - widget for correct answers
     * @param idIncorrect - widget for incorrect answers
     * @param idScope - widget for avg scope
     * @param idTime - widget for avg time
     */
    private void setStatisticsText(View fragment_view, int fileKeyId, int idTotal,
                                   int idCorrect, int idIncorrect, int idScope, int idTime){
        Context context = getActivity();
        // find widgets
        TextView tvTotal = (TextView)fragment_view.findViewById(idTotal);
        TextView tvCorrect = (TextView)fragment_view.findViewById(idCorrect);
        TextView tvIncorrect = (TextView)fragment_view.findViewById(idIncorrect);
        TextView tvScope = (TextView)fragment_view.findViewById(idScope);
        TextView tvTime = (TextView)fragment_view.findViewById(idTime);

        // get shared preferences file by file key
        SharedPreferences sharedPref = context.getSharedPreferences(
                getString(fileKeyId), Context.MODE_PRIVATE);
        // set text
        int total = sharedPref.getInt(getString(R.string.saved_questions_count), 0);
        int mistaken = sharedPref.getInt(getString(R.string.saved_mistakes_count), 0);
        int avgScope = sharedPref.getInt(getString(R.string.saved_average_score), 0);
        long avgTime = sharedPref.getLong(getString(R.string.saved_average_time), 0);
        tvTotal.setText("Questions answered: " + total);
        tvCorrect.setText("Correct: " + (total - mistaken));
        tvIncorrect.setText("Incorrect: " + mistaken);
        tvScope.setText("Average scope: " + avgScope + "%");
        tvTime.setText("Average time: " + FormatHelper.getTimeString(avgTime));
    }
}
