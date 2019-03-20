package in.example.jsonparsing.DatabaseHelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String TABLE_NAME = "feedback_forms";
    public static final String COL_ID = "id";
    public static final String COL_QUESTION = "question";
    public static final String COL_OPTION_ONE = "option_one";
    public static final String COL_OPTION_TWO = "option_two";

    public DatabaseHelper(Context context) {
        super(context, TABLE_NAME, null, 1);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_NAME + " (" + COL_ID + " INTEGER PRIMARY KEY, " + COL_QUESTION + " TEXT, " + COL_OPTION_ONE + " TEXT, " + COL_OPTION_TWO + " TEXT)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public boolean addQuestion(Integer id, String question, String option1, String option2) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_ID, id);
        values.put(COL_QUESTION, question);
        values.put(COL_OPTION_ONE, option1);
        values.put(COL_OPTION_TWO, option2);

        long result = db.insert(TABLE_NAME, null, values);
        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }

    public Cursor getQuestion() {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME;
        Cursor cursor = (Cursor) db.rawQuery(query, null);
        return cursor;
    }

    public void updateQuestion(int i, String question) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "UPDATE " + TABLE_NAME + " SET " + COL_QUESTION + " = '" + question + "' WHERE id = " + String.valueOf(i);
        db.execSQL(query);
    }

    public void deleteQuestion(int i) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DELETE FROM " + TABLE_NAME + " WHERE id = " + String.valueOf(i);
        db.execSQL(query);
    }
}
