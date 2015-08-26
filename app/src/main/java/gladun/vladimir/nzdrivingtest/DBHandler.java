package gladun.vladimir.nzdrivingtest;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * SQLite helper class for database
 *
 * @author Vladimir Gladun vvgladoun@gmail.com
 */
public final class DBHandler extends SQLiteOpenHelper {

    private static final String TAG = DBHandler.class.getName();
    // DB version (used on upgrade)
    private static final int DATABASE_VERSION = 2;

    // Database Name
    private static final String DATABASE_NAME = "nzdrivingtest.db";
    private String DB_PATH;
    private SQLiteDatabase mDataBase = null;
    private final Context mContext;

    // Table names
    public static final String TABLE_CATEGORY = "category";
    public static final String TABLE_QUESTION = "question";
    public static final String TABLE_ANSWER = "answer";
    public static final String TABLE_MISTAKES = "mistake";

    // columns names
    public static final String COLUMN_ID = "_id";
    //  category
    public static final String COLUMN_CATEGORY_NAME = "category_name";
    //  question id is used as id in errors table
    //  question
    public static final String COLUMN_QUESTION = "question_text";
    public static final String COLUMN_MULTIPLE = "multiple_choice"; //1 - multiple choice question, else - 0
    public static final String COLUMN_CATEGORY_FK = "category_id";
    public static final String COLUMN_IMAGE = "image_name"; //to get image from drawable
    public static final String COLUMN_TEST = "test_id"; //1-car test, 2-bike test
    public static final String COLUMN_EXPLANATION = "explanation";
    public static final String COLUMN_EXPLANATION_IMAGE = "explanation_image";

    //  answers
    public static final String COLUMN_ANSWER = "answer";
    public static final String COLUMN_QUESTION_FK = "question_id";
    public static final String COLUMN_CORRECT = "is_correct";


    @SuppressLint("SdCardPath")
    public DBHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

        this.mContext = context;
        DB_PATH= "/data/data/" + context.getPackageName() + "/databases/";
        //DB_PATH = context.getFilesDir().getAbsolutePath() + "/databases/";
        openDataBase();
    }

    /**
     * Run copy database from assets if not exists
     */
    public void createDataBase(){
        boolean dbExist = checkDataBase();
        if(dbExist){
            Log.v(TAG, "database does exist");
        }else{
            Log.v(TAG, "database does not exist");
            this.getReadableDatabase();
            try {
                copyDataBase();
            } catch (IOException e) {
                Log.e(TAG, "Copy database failed!");
            }
        }
    }

    /**
     * Copy database from assets to the app data
     * @throws IOException
     */
    private void copyDataBase() throws IOException{
        // get source and target streams
        InputStream myInput = mContext.getAssets().open(DATABASE_NAME);

        String outFileName = DB_PATH + DATABASE_NAME;
        OutputStream myOutput = new FileOutputStream(outFileName);

        // copy file
        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer))>0){
            myOutput.write(buffer, 0, length);
        }
        // close streams
        myOutput.flush();
        myOutput.close();
        myInput.close();
    }

    /**
     * Check if source db is created
     *
     * @return true if exists
     */
    private boolean checkDataBase(){
        File dbFile = new File(DB_PATH + DATABASE_NAME);
        return dbFile.exists();
    }

    /**
     * Allows to write and read to/from the database
     *
     * @return database opened for writing and reading
     */
    public boolean openDataBase() {
        String mPath = DB_PATH + DATABASE_NAME;
        if (!checkDataBase()) {
            createDataBase();
        }
        if (mDataBase == null) {
            mDataBase = SQLiteDatabase.openDatabase(mPath, null, SQLiteDatabase.OPEN_READWRITE);
        }

        return mDataBase.isOpen();
    }

    /**
     * Close db connection
     */
    @Override
    public synchronized void close() {
        if(mDataBase != null)
            mDataBase.close();
        super.close();
    }



    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // close and delete database
        try {
            if (mDataBase != null)
                mDataBase.close();
            mContext.deleteDatabase(DATABASE_NAME);
            // copy new version of the database
            openDataBase();
            // log info
            Log.i(TAG, "Database was updated from ver." + oldVersion + " to ver." + newVersion);
        } catch (SQLiteException e) {
            Log.e(TAG, "Database update failed");
        }

    }
}
