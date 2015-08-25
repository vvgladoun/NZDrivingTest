package gladun.vladimir.nzdrivingtest;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;


/**
 * Main menu activity class
 *
 * @author Vladimir Gladun vvgladoun@gmail.com
 */
public final class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main); //frame for a fragment

        FragmentManager fm = getFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.fragmentContainer);

        // add up navigation between fragments
        fm.addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                placeUpButton();
            }
        });

        if (fragment == null) {
            fragment = new MenuMainFragment();
            fm.beginTransaction()
                    .add(R.id.fragmentContainer, fragment)
                    .commit();
        } else {
            //re-create navigation button for fragments after rotation
            placeUpButton();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    /**
     * Find action bar in a fragment and add up navigation
     * if fragment is not first oin stack
     */
     void placeUpButton(){
        int stackHeight = getFragmentManager().getBackStackEntryCount();
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            if (stackHeight > 0) {
                // show navigation button
                actionBar.setHomeButtonEnabled(true);
                actionBar.setDisplayHomeAsUpEnabled(true);
            } else {
                // hide navigation button
                actionBar.setDisplayHomeAsUpEnabled(false);
                actionBar.setHomeButtonEnabled(false);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // if there are fragments in back stack - show navigation
                if (getFragmentManager().getBackStackEntryCount() > 0) {
                    getFragmentManager().popBackStack();
                    return true;
                }
            case R.id.action_about:
                //show info about this app
                (new AboutDialogFragment()).show(getFragmentManager(), "ABOUT");
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * to navigate from contact details fragment back to list fragment
     */
    @Override
    public void onBackPressed(){
        FragmentManager fm = getFragmentManager();
        if (fm.getBackStackEntryCount() > 0) {
            fm.popBackStack();
        } else {
            super.onBackPressed();
        }
    }
}
