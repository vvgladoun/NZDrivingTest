package gladun.vladimir.nzdrivingtest;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Data access methods for Question entities
 *
 * @author Vladimir Gladun vvgladoun@gmail.com
 */
public final class QuestionDAOImpl {

    /**
     * Generate sql query string
     *
     * @param questionFilter - additional join and/or where clause
     * @param postFilter - clause after order statement
     * @return string of query
     */
    static String getQuestionQuery(String questionFilter, String postFilter) {
        return "SELECT tq." + DBHandler.COLUMN_ID +
                ", tq." + DBHandler.COLUMN_QUESTION +
                ", tq." + DBHandler.COLUMN_TEST +
                ", tq." + DBHandler.COLUMN_CATEGORY_FK +
                ", tq." + DBHandler.COLUMN_MULTIPLE +
                ", tq." + DBHandler.COLUMN_IMAGE +
                ", tq." + DBHandler.COLUMN_EXPLANATION +
                ", tq." + DBHandler.COLUMN_EXPLANATION_IMAGE +
                ", ta." + DBHandler.COLUMN_ID + " answer_id " +
                ", ta." + DBHandler.COLUMN_ANSWER +
                ", ta." + DBHandler.COLUMN_CORRECT +
                " FROM " + DBHandler.TABLE_QUESTION + " tq " +
                " LEFT JOIN " + DBHandler.TABLE_ANSWER +
                " ta ON tq." + DBHandler.COLUMN_ID +
                " = ta." + DBHandler.COLUMN_QUESTION_FK
                + questionFilter +
                " ORDER BY tq." + DBHandler.COLUMN_ID +
                ", RANDOM() " +
                //", ta." + DBHandler.COLUMN_ID +
                postFilter;
    }

    static ArrayList<Question> getQuestions(Context context, String whereClause, String postFilter) {
        // open connection to the database
        DBHandler dbHandler = new DBHandler(context);
        SQLiteDatabase db = dbHandler.getWritableDatabase();
        // create empty array list for questions
        ArrayList<Question> questions = new ArrayList<>();
        // create custom query to get data from questions and answers
        String selectQuestions = getQuestionQuery(whereClause, postFilter);
        // execute query
        Cursor cursor = db.rawQuery(selectQuestions, null);

        int current_id = -1;
        Question question = null;

        // loop through the results
        if (cursor.moveToFirst()) {
            do {
                int qId = cursor.getInt(0);
                if (qId != current_id) {
                    String qText = cursor.getString(1);
                    int qTestType = cursor.getInt(2);
                    int qCategory = cursor.getInt(3);
                    boolean qMultiple = (cursor.getInt(4) == 1);
                    String qImage = cursor.getString(5);
                    String qExplain = cursor.getString(6);
                    String qExplainImage = cursor.getString(7);

                    // create question
                    question = new Question(qId, qText, qCategory, new ArrayList<Answer>(),
                            qMultiple, qImage, qTestType, qExplain, qExplainImage);

                    // add question to the list of questions
                    questions.add(question);
                    current_id = qId;
                }
                int aId = cursor.getInt(8);
                String aText = cursor.getString(9);
                boolean aCorrect = (cursor.getInt(10) == 1);

                // create answer
                Answer answer = new Answer(aId, aText, aCorrect);
                // add answer to the question's array list
                assert question != null;
                question.addAnswer(answer);
            } while (cursor.moveToNext());
        }
        //close connection
        cursor.close();
        db.close();
        dbHandler.close();

        return questions;
    }

    /**
     * get all questions for test type
     *
     * @param context - app context
     * @param testType - car test, motorbike test, etc.
     * @return list of questions
     */
    static ArrayList<Question> getAllQuestions(Context context, int testType) {
        String whereClause = " WHERE tq." + DBHandler.COLUMN_TEST + " = " + testType;
        return getQuestions(context, whereClause, "");
    }

    /**
     * get questions by category
     *
     * @param context - app context
     * @param testType - car test, motorbike test, etc.
     * @return list of questions
     */
    static ArrayList<Question> getQuestionsByCategory(Context context, int testType, int categoryId){
        String whereClause = " WHERE tq." + DBHandler.COLUMN_TEST + " = " + testType +
                " AND tq." + DBHandler.COLUMN_CATEGORY_FK + " = " + categoryId;
        return getQuestions(context, whereClause, "");
    }

    /**
     * get all questions with wrong answer
     *
     * @param context - app context
     * @param testType - car test, motorbike test, etc.
     * @return list of questions
     */
    static ArrayList<Question> getFailedQuestions(Context context, int testType){
        String whereClause = " INNER JOIN " + DBHandler.TABLE_MISTAKES + " te ON te." +
                DBHandler.COLUMN_ID + " = tq." + DBHandler.COLUMN_ID +
                " WHERE tq." + DBHandler.COLUMN_TEST + " = " + testType;
        return getQuestions(context, whereClause, "");
    }


    /**
     * Add question to mistake table
     *
     * @param questionID - question's id in the database
     */
    static void addMistake(Context context, int questionID) {
        DBHandler dbHandler = new DBHandler(context);
        SQLiteDatabase db = dbHandler.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DBHandler.COLUMN_ID, questionID);

        //insert into db (if id is already there - ignore)
        db.insertWithOnConflict(DBHandler.TABLE_MISTAKES, null, values, SQLiteDatabase.CONFLICT_IGNORE);

        //close connection
        db.close();
        dbHandler.close();
    }



    /**
     * Remove question from mistake table
     *
     * @param questionID - question's id in the database
     */
    static void removeMistake(Context context, int questionID) {
        DBHandler dbHandler = new DBHandler(context);
        SQLiteDatabase db = dbHandler.getWritableDatabase();

        String whereClause = DBHandler.COLUMN_ID + "=?";
        String[] whereArgs = new String[] {String.valueOf(questionID)};

        //insert into db (if id is already there - ignore)
        db.delete(DBHandler.TABLE_MISTAKES, whereClause, whereArgs);

        //close connection
        db.close();
        dbHandler.close();
    }

    /**
     * Remove all questions from mistake table
     */
    static void removeAllMistakes(Context context) {
        DBHandler dbHandler = new DBHandler(context);
        SQLiteDatabase db = dbHandler.getWritableDatabase();
        // delete all
        db.execSQL("delete from " + DBHandler.TABLE_MISTAKES);
        // free allocated space
        db.execSQL("vacuum");
        //close connection
        db.close();
        dbHandler.close();
    }


    /**
     * Get exact quantity of random questions
     *
     * @param context - app context
     * @param testType - car test, motorbike test, etc.
     * @param questionCount - number of questions to return
     * @return list of questions
     */
    static ArrayList<Question> getShuffledQuestions(Context context, int testType, int questionCount){
        ArrayList<Question> questions = getAllQuestions(context, testType);
        //shuffle result
        Collections.shuffle(questions);
        for (int i=questionCount; i<questions.size(); i++) {
            questions.remove(i);
        }
        return questions;
    }
}
