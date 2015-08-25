package gladun.vladimir.nzdrivingtest;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;

/**
 * Data access methods for Question entities
 *
 * @author Vladimir Gladun vvgladoun@gmail.com
 */
final class CategoryDAOImpl {

    /**
     * get all categories for test type
     *
     * @param context  - app context
     * @param testType - car test, motorbike test, etc.
     * @return list of questions
     */
    static ArrayList<Category> getAllCategories(Context context, int testType) {
        // open connection to the database
        DBHandler dbHandler = new DBHandler(context);
        SQLiteDatabase db = dbHandler.getWritableDatabase();
        // create empty array list for categories
        ArrayList<Category> categories = new ArrayList<>();
        Cursor cursor = db.query(DBHandler.TABLE_CATEGORY,
                new String[]{DBHandler.COLUMN_ID, DBHandler.COLUMN_CATEGORY_NAME, DBHandler.COLUMN_TEST},
                DBHandler.COLUMN_TEST + " = ?", new String[]{"" + testType},
                null, null, DBHandler.COLUMN_ID);
        Log.d("SQL", "EXECUTED!");
        // loop through the results
        if (cursor.moveToFirst()) {
            do {
                Log.d("SQL", "LOOP!");
                int qId = cursor.getInt(0);
                String qText = cursor.getString(1);
                int qTestType = cursor.getInt(2);
                // create question
                Category category = new Category(qId, qText, qTestType);
                // add question to the list of questions
                categories.add(category);
            } while (cursor.moveToNext());
        }
        Log.d("SQL", "FINISHED!");
        //close connection
        cursor.close();
        db.close();
        dbHandler.close();
        return categories;
    }
}
