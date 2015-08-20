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
 * Main menu
 *
 * @author Vladimir Gladun vvgladoun@gmail.com
 */
public class MenuMainFragment extends Fragment implements View.OnClickListener {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // get main menu fragment layout
        View fragment_view = inflater.inflate(R.layout.fragment_menu_main, container, false);
        // get buttons from fragment's layout
        Button btnMainCar = (Button) fragment_view.findViewById(R.id.btn_main_car);
        Button btnMainMotorbike = (Button) fragment_view.findViewById(R.id.btn_main_motorbike);
        // set implemented method onClick as the onClickListener
        btnMainCar.setOnClickListener(this);
        btnMainMotorbike.setOnClickListener(this);

        // set icon color
        int color = getResources().getColor(R.color.icon_grey);
        ImageView ivCar = (ImageView)fragment_view.findViewById(R.id.iv_main_car);
        ImageView ivMb = (ImageView)fragment_view.findViewById(R.id.iv_main_mb);
        ivCar.setColorFilter(color);
        ivMb.setColorFilter(color);

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
