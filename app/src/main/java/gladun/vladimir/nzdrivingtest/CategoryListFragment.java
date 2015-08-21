package gladun.vladimir.nzdrivingtest;


import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ListFragment;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * Fragment with a list of category
 *
 * @author Vladimir Gladun vvgladoun@gmail.com
 */
public class CategoryListFragment extends ListFragment{

    public static final String EXTRA_TEST_TYPE = "TEST_TYPE";
    private ArrayList<Category> mCategories;
    private int mTestType;
    private CategoryAdapter mCategoryAdapter;

    /**
     * Create new instance of the fragment with defined extra
     *
     * @param testType - type: car test, motorbike test etc
     * @return built fragment
     */
    public static CategoryListFragment newInstance(int testType) {
        Bundle args = new Bundle();
        args.putInt(EXTRA_TEST_TYPE, testType);
        CategoryListFragment fragment = new CategoryListFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // will retain instance after rotation
        setRetainInstance(true);

        // initialize variables
        mCategories = new ArrayList<>();
        mCategoryAdapter = null;

        // get test type from extras
        mTestType = getArguments().getInt(EXTRA_TEST_TYPE);

        // start async task to get categories and set adapter
        DownloadCategoriesTask downloadTask = new DownloadCategoriesTask();
        downloadTask.execute();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        // create new question activity with selected category
        Intent questionActivity = new Intent(v.getContext(), QuestionActivity.class);
        questionActivity.putExtra(QuestionActivity.EXTRA_TEST_TYPE, mTestType);
        int categoryId = ((Category)mCategoryAdapter.getItem(position)).getId();
        questionActivity.putExtra(QuestionActivity.EXTRA_CATEGORY, categoryId);
        v.getContext().startActivity(questionActivity);
    }

    /**
     * Async loader of categories
     * after execution sets adapter to the fragment
     */
    private class DownloadCategoriesTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... arg0) {
            mCategories = CategoryDAO.getAllCategories(getActivity(), mTestType);
            //add default element - all categories
            mCategories.add(0,new Category(0, "ALL CATEGORIES", mTestType));
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            if (mCategoryAdapter == null) {
                //if not defined add adapter with array list of categories
                mCategoryAdapter = new CategoryAdapter(mCategories);
                setListAdapter(mCategoryAdapter);
            } else {
                //update data
                mCategoryAdapter.notifyDataSetChanged();
            }
        }
    }

    /**
     * Array adapter to show list of categories
     */
    private class CategoryAdapter extends ArrayAdapter<Category> {

        public CategoryAdapter(ArrayList<Category> categories) {
            super(getActivity(), R.layout.category_list_item, categories);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            // If we get view, then fill it
            if (convertView == null) {
                convertView = getActivity().getLayoutInflater()
                        .inflate(R.layout.category_list_item,parent,false);
            }
            // Set view for category (Category object)
            Category c = getItem(position);
            TextView titleTextView =
                    (TextView)convertView.findViewById(R.id.category_list_item_title);
            titleTextView.setText(c.getName());
            return convertView;
        }

    }
}
