package arizona.edu.foodflipper;

/**
 * Created by vsmayberry on 4/2/15.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DataHelper {
    private static final String DATABASE_NAME = "food_flipper.db";
    private static final int DATABASE_VERSION = 1;
    private Context context;
    private SQLiteDatabase db;
    static DataHelper sInstance;

    //users table
    static String createUsersTable = "create table users (" +
            "uid INTEGER PRIMARY KEY AUTOINCREMENT," +
            "email TEXT," +
            "password TEXT)";

    //food table
    static String createFoodTable = "create table food (" +
            "fid INTEGER PRIMARY KEY AUTOINCREMENT," +
            "uid INTEGER," +
            "name TEXT," +
            "calories INTEGER," +
            "carbs INTEGER," +
            "fat INTEGER," +
            "protein INTEGER," +
            "image BLOB,"+
            "FOREIGN KEY(uid) REFERENCES users(uid))";

    //scores table
    static String createScoresTable = "create table scores (" +
            "uid INTEGER," +
            "score INTEGER," +
            "datetime INTEGER," + //TODO: change after converting to MySQL
            "FOREIGN KEY(uid) REFERENCES users(uid))";


    public DataHelper(Context context) {
        this.context = context;
        openDatabase();
    }

    public static DataHelper getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new DataHelper(context);
        }
        sInstance.openDatabase();
        return sInstance;
    }

    private void openDatabase() {
        OpenHelper openHelper = new OpenHelper(this.context);
        db = openHelper.getWritableDatabase();
    }

    private static class OpenHelper extends SQLiteOpenHelper {
        OpenHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(createUsersTable);
            db.execSQL(createFoodTable);
            db.execSQL(createScoresTable);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
          //  db.execSQL("DROP TABLE IF EXISTS " + "users");
          //  onCreate(db);
        }
    }


    /*
     * Users
     */


    public Cursor getUsers() {

        return this.db.query("users", new String[]{"uid", "email"}, null, null, null, null, null);
    }

    public void insertUser(User user) {

        ContentValues cv = new ContentValues();
        cv.put("email", user.getEmail());
        cv.put("password", user.getPassword());
        db.insert("users", null, cv);
    }


    public List<User> getListOfUsers() {
        List<User> list = new ArrayList<User>();
        Cursor cursor = this.db.query("users", new String[]{"uid", "email"}, null, null, null, null, null);
        if (cursor.getCount() > 0) {
            if (cursor.moveToFirst()) {
                do {

                    int     uid      = cursor.getInt(0);
                    String  email     = cursor.getString(1);

                    User newUser = new User(uid, email);

                    list.add(newUser);
                } while (cursor.moveToNext());
            }
            if (cursor != null && !cursor.isClosed())
                cursor.close();
        }
        return list;
    }

    public void deleteUser(int id_key) {
        String id = "" + id_key;
        String prevName = "undefined";

        Cursor cursor_patientDB = db.query("users", null, "uid =?", new String[]{id}, null, null, null, null);
        if (cursor_patientDB != null) {
            cursor_patientDB.moveToFirst();
            prevName = cursor_patientDB.getString(1);
        }

        db.delete("users", "uid=?", new String[]{id});
    }

    public String getPassword(String userEmail) {
        String password = "undefined";
        Cursor cursor = db.query("users", null, "email =?", new String[]{userEmail}, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            password = cursor.getString(4);
        }
        return password;
    }

    public int getID(String userEmail) {
        int id_key = -99;
        Cursor cursor = db.query("users", null, "email =?", new String[]{userEmail}, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            id_key = cursor.getInt(0);
        }
        return id_key;
    }


    public void updateUserPassword(User user) {

        String uid = "" + user.getUID();
        ContentValues values = new ContentValues();
        values.put("password", user.getPassword());
        db.update("users", values, "uid=?", new String[]{uid});
    }

    //TODO: Generic update user

/*

    public List<String> selectAppointments() {
        List<String> list = new ArrayList<String>();
        Cursor cursor = this.db.query("appointments", new String[] {"uid","patientName","date",
                "time"}, null, null, null, null, null);
        if (cursor.getCount() > 0) {
            if (cursor.moveToFirst()) {
                do {
                    list.add(cursor.getString(2)+" : " +cursor.getString(1) + " @ " + cursor.getString(3));
                    // Date: name @ time
                } while (cursor.moveToNext());
            }
            if (cursor != null && !cursor.isClosed())
                cursor.close();
        }
        return list;
    }


    /*
     * Food
     */


    public Cursor getFood() {

        //Doesn't return image
        return this.db.query("food", new String[]{"fid", "uid", "name", "calories", "carbs", "fat", "protein"}, null, null, null, null, null);
    }

    public List<Food> getListOfFood() {

        //TODO: add image

        List<Food> list = new ArrayList<Food>();
        Cursor cursor = this.db.query("food", new String[]{"fid", "uid", "name", "calories", "carbs", "fat", "protein" }, null, null, null, null, null);
        if (cursor.getCount() > 0) {
            if (cursor.moveToFirst()) {
                do {

                    int     fid      = cursor.getInt(0);
                    int     uid      = cursor.getInt(1);
                    String  name     = cursor.getString(2);
                    int     calories = cursor.getInt(3);
                    int     carbs    = cursor.getInt(4);
                    int     fat      = cursor.getInt(5);
                    int     protein  = cursor.getInt(6);

                    Food newFoodItem = new Food(fid, uid, name, calories, carbs, fat, protein);

                    list.add(newFoodItem);
                } while (cursor.moveToNext());
            }
            if (cursor != null && !cursor.isClosed())
                cursor.close();
        }
        return list;
    }

    public List<Food> getListOfFoodByUser(int uid) {

        //TODO: add image

        List<Food> list = new ArrayList<Food>();
        Cursor cursor = this.db.query("food", new String[]{"fid", "uid", "name", "calories", "carbs", "fat", "protein" }, "uid =?", new String[]{Integer.toString(uid)}, null, null, null);
        if (cursor.getCount() > 0) {
            if (cursor.moveToFirst()) {
                do {

                    int     fid      = cursor.getInt(0);
                    int     thisUID  = cursor.getInt(1);
                    String  name     = cursor.getString(2);
                    int     calories = cursor.getInt(3);
                    int     carbs    = cursor.getInt(4);
                    int     fat      = cursor.getInt(5);
                    int     protein  = cursor.getInt(6);
                    //blob image = cursor.getBlob(7);

                    Food newFoodItem = new Food(fid, thisUID, name, calories, carbs, fat, protein);
                    //newFoodItem.setImage = image;

                    list.add(newFoodItem);
                } while (cursor.moveToNext());
            }
            if (cursor != null && !cursor.isClosed())
                cursor.close();
        }
        return list;
    }

    public void insertFood(Food food) {

        ContentValues cv = new ContentValues();
        cv.put("uid", food.getUID());
        cv.put("name", food.getName());
        cv.put("calories", food.getCalories());
        cv.put("carbs", food.getCarbs());
        cv.put("fat", food.getFat());
        cv.put("protein", food.getProtein());
        cv.put("image", 0); //TODO make sure this works to add an empty blob
        db.insert("users", null, cv);
    }

    //TODO: Generic update food

    /*
     * Scores
     */

    public Cursor getScores() {

        //Doesn't return image
        return this.db.query("scores, users", new String[]{"uid", "score", "datetime", "email"}, null, null, null, null, "score DESC");
    }

    public List<Score> getListOfScores() {

        //TODO: update to MySQL date / timestamp

        List<Score> list = new ArrayList<Score>();
        Cursor cursor = this.db.query("score, users", new String[]{"uid", "score", "datetime", "email"}, null, null, null, null, "score DESC");
        if (cursor.getCount() > 0) {
            if (cursor.moveToFirst()) {
                do {

                    int     uid      = cursor.getInt(0);
                    int     score    = cursor.getInt(1);
                    int     datetime = cursor.getInt(2);
                    String  email    = cursor.getString(3);

                    Score newScore = new Score(uid, score, datetime, email);

                    list.add(newScore);
                } while (cursor.moveToNext());
            }
            if (cursor != null && !cursor.isClosed())
                cursor.close();
        }
        return list;
    }

    public List<Score> getListOfScoresByUser(int uid) {

        //TODO: update to MySQL date / timestamp

        List<Score> list = new ArrayList<Score>();
        Cursor cursor = this.db.query("score, users", new String[]{"uid", "score", "datetime", "email"}, "uid =?", new String[]{Integer.toString(uid)}, null, null, "scores DESC");
        if (cursor.getCount() > 0) {
            if (cursor.moveToFirst()) {
                do {

                    int     thisUID  = cursor.getInt(0);
                    int     score    = cursor.getInt(1);
                    int     datetime = cursor.getInt(2);
                    String  email    = cursor.getString(3);

                    Score newScore = new Score(thisUID, score, datetime, email);

                    list.add(newScore);
                } while (cursor.moveToNext());
            }
            if (cursor != null && !cursor.isClosed())
                cursor.close();
        }
        return list;
    }

    public void insertScore(Score score) {

        ContentValues cv = new ContentValues();
        cv.put("uid", score.getUID());
        cv.put("score", score.getScore());
        cv.put("datetime", score.getDatetime());
        cv.put("email", score.getEmail());
        db.insert("users", null, cv);
    }

    //TODO: Generic update score
}
