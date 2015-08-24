package gladun.vladimir.nzdrivingtest;

import android.app.Application;
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
 * Main menu
 *
 * @author Vladimir Gladun vvgladoun@gmail.com
 */
public class MenuMainFragment extends Fragment implements View.OnClickListener {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        getActivity().setTitle(R.string.app_name);

        // get main menu fragment layout
        View fragment_view = inflater.inflate(R.layout.fragment_menu_main, container, false);
        // get buttons from fragment's layout
        Button btnMainCar = (Button) fragment_view.findViewById(R.id.btn_main_car);
        Button btnMainMotorbike = (Button) fragment_view.findViewById(R.id.btn_main_motorbike);
        Button btnStatistics = (Button) fragment_view.findViewById(R.id.btn_menu_statistics);
        Button btnContactMe = (Button) fragment_view.findViewById(R.id.btn_menu_contact_me); //TOSUBMIT
        // set implemented method onClick as the onClickListener
        btnMainCar.setOnClickListener(this);
        btnMainMotorbike.setOnClickListener(this);
        btnStatistics.setOnClickListener(this);
        btnContactMe.setOnClickListener(this); //TOSUBMIT

        // set icon color
        int color = getResources().getColor(R.color.icon_grey);
        ImageView ivCar = (ImageView)fragment_view.findViewById(R.id.iv_main_car);
        ImageView ivMb = (ImageView)fragment_view.findViewById(R.id.iv_main_mb);
        ImageView ivStat = (ImageView)fragment_view.findViewById(R.id.iv_main_statistics);
        ivCar.setColorFilter(color);
        ivMb.setColorFilter(color);
        ivStat.setColorFilter(color);

        //TOSUBMIT
        ImageView ivContact = (ImageView)fragment_view.findViewById(R.id.iv_main_contact_me);
        ivContact.setColorFilter(color);


        return fragment_view;
    }


    @Override
    public void onClick(View v) {
        // action depends on the view's id
        switch (v.getId()) {
            case R.id.btn_main_car:
                // start car test menu fragment
                startFragment(new MenuCarFragment());
                break;
            case R.id.btn_main_motorbike:
                // open menu for a motorbike test
                startFragment(new MenuMotorbikeFragment());
                break;
            case R.id.btn_menu_statistics:
                // show statistics by groups (car/bike):
                startFragment(new StatisticsFragment());
                break;
            //TOSUBMIT
            case R.id.btn_menu_contact_me:
                // show statistics by groups (car/bike):
                startFragment(new SendMessagesFragment());
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
