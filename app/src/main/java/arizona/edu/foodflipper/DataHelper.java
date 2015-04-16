package arizona.edu.foodflipper;

/**
 * Created by vsmayberry on 4/2/15.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;
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
            "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "email TEXT," +
            "password TEXT)";

    //food table
    static String createFoodTable = "create table food (" +
            "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "uid INTEGER," +
            "name TEXT," +
            "calories INTEGER DEFAULT 0," +
            "carbs INTEGER DEFAULT 0," +
            "fat INTEGER DEFAULT 0," +
            "protein INTEGER DEFAULT 0," +
            "image BLOB," +
            "FOREIGN KEY(uid) REFERENCES users(_id))";

    //scores table
    static String createScoresTable = "create table scores (" +
            "_id INTEGER," +
            "score INTEGER DEFAULT 0," +
            "datetime INTEGER DEFAULT 0," + //TODO: change after converting to MySQL
            "FOREIGN KEY(_id) REFERENCES users(_id))";


    /*
     * Constructor And related methods
     */


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
            db.execSQL("DROP TABLE IF EXISTS " + "users");
            db.execSQL("DROP TABLE IF EXISTS " + "food");
            db.execSQL("DROP TABLE IF EXISTS " + "scores");
            onCreate(db);
        }
    }


    /*
     * Query methods
     */

    /*
     * Users
     */

    public Cursor getUsers() {

        return db.query("users", new String[]{"_id", "email", "password"}, null, null, null, null, null);
    }

    public void insertUser(User user) {

        ContentValues cv = new ContentValues();
        cv.put("email", user.getEmail());
        cv.put("password", user.getPassword());
        db.insert("users", null, cv);

    }

    public List<User> getListOfUsers() {
        List<User> list = new ArrayList<User>();
        Cursor cursor = this.db.query("users", new String[]{"_id", "email"}, null, null, null, null, null);
        if (cursor.getCount() > 0) {
            if (cursor.moveToFirst()) {
                do {

                    int id = cursor.getInt(0);
                    String email = cursor.getString(1);
                    String password = cursor.getString(2);

                    User newUser = new User(id, email, password);

                    list.add(newUser);
                } while (cursor.moveToNext());
            }
            if (cursor != null && !cursor.isClosed())
                cursor.close();
        }
        return list;
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

    public String getPassword(String userEmail) {
        String password = "undefined";
        Cursor cursor = db.query("users", null, "email =?", new String[]{userEmail}, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            password = cursor.getString(2);
        }
        return password;
    }

    public void updateUserPassword(String userEmail, String password) {

        ContentValues values = new ContentValues();
        values.put("password", password);
        db.update("users", values, "email=?", new String[]{userEmail});
    }

    public void updateUserPassword(int id_key, String password) {
        String id = "" + id_key;
        ContentValues values = new ContentValues();
        values.put("password", password);
        db.update("users", values, "_id=?", new String[]{id});
    }

    public void deleteUser(int id_key) {
        String id = "" + id_key;
        String prevName = "undefined";

        Cursor cursor_patientDB = db.query("users", null, "_id =?", new String[]{id}, null, null, null, null);
        if (cursor_patientDB != null) {
            cursor_patientDB.moveToFirst();
            prevName = cursor_patientDB.getString(1);
        }

        db.delete("users", "_id=?", new String[]{id});
    }

    //TODO: Generic update user


    /*
     * Food
     */

    public Cursor getFood() {

        //Doesn't return image
        return this.db.query("food", new String[]{"_id", "uid", "name", "calories", "carbs", "fat", "protein", "image"}, null, null, null, null, null);
    }

    public List<Food> getListOfFood() {

        //TODO: add image

        List<Food> list = new ArrayList<Food>();
        Cursor cursor = this.db.query("food", new String[]{"_id", "uid", "name", "calories", "carbs", "fat", "protein", "image"}, null, null, null, null, null);
        if (cursor.getCount() > 0) {
            if (cursor.moveToFirst()) {
                do {

                    int id = cursor.getInt(0);
                    int uid = cursor.getInt(1);
                    String name = cursor.getString(2);
                    int calories = cursor.getInt(3);
                    int carbs = cursor.getInt(4);
                    int fat = cursor.getInt(5);
                    int protein = cursor.getInt(6);

                    Food newFoodItem = new Food(name, calories, carbs, fat, protein);
                    newFoodItem.setFID(id);
                    newFoodItem.setFID(uid);
                    newFoodItem.setImage(BitmapFactory.decodeByteArray(cursor.getBlob(7), 0, cursor.getBlob(7).length));
                    list.add(newFoodItem);
                } while (cursor.moveToNext());
            }
            if (cursor != null && !cursor.isClosed())
                cursor.close();
        }
        return list;
    }

    public List<Food> getListOfFoodByUser(int id) {

        //TODO: add image

        List<Food> list = new ArrayList<Food>();
        Cursor cursor = this.db.query("food", new String[]{"_id", "uid", "name", "calories", "carbs", "fat", "protein"}, "uid =?", new String[]{Integer.toString(id)}, null, null, null);
        if (cursor.getCount() > 0) {
            if (cursor.moveToFirst()) {
                do {

                    int uID = cursor.getInt(1);
                    String name = cursor.getString(2);
                    int calories = cursor.getInt(3);
                    int carbs = cursor.getInt(4);
                    int fat = cursor.getInt(5);
                    int protein = cursor.getInt(6);
                    //Bitmap image = (Bitmap) cursor.getBlob(7);

                    Food newFoodItem = new Food(name, calories, carbs, fat, protein);
                    newFoodItem.setFID(cursor.getInt(0));
                    newFoodItem.setFID(uID);
                    newFoodItem.setImage(BitmapFactory.decodeByteArray(cursor.getBlob(7), 0, cursor.getBlob(7).length));

                    list.add(newFoodItem);
                } while (cursor.moveToNext());
            }
            if (cursor != null && !cursor.isClosed())
                cursor.close();
        }
        return list;
    }

    public void insertFood(Food food, Bitmap bitmap) {

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
        byte[] bArray = bos.toByteArray();

        ContentValues cv = new ContentValues();
        cv.put("name", food.getName());
        cv.put("calories", food.getCalories());
        cv.put("carbs", food.getCarbs());
        cv.put("fat", food.getFat());
        cv.put("protein", food.getProtein());
        cv.put("image", bArray);
        db.insert("food", null, cv);
    }

        /*
     * Scores
     */

    public Cursor getScores() {

        //Doesn't return image
        return this.db.query("scores, users", new String[]{"_id", "score", "datetime", "email"}, null, null, null, null, "score DESC");
    }

    public List<Score> getListOfScores() {

        //TODO: update to MySQL date / timestamp

        List<Score> list = new ArrayList<Score>();
        Cursor cursor = this.db.query("score, users", new String[]{"_id", "score", "datetime", "email"}, null, null, null, null, "score DESC");
        if (cursor.getCount() > 0) {
            if (cursor.moveToFirst()) {
                do {

                    int id = cursor.getInt(0);
                    int score = cursor.getInt(1);
                    int datetime = cursor.getInt(2);
                    String email = cursor.getString(3);

                    Score newScore = new Score(id, score, datetime, email);

                    list.add(newScore);
                } while (cursor.moveToNext());
            }
            if (cursor != null && !cursor.isClosed())
                cursor.close();
        }
        return list;
    }

    public List<Score> getListOfScoresByUser(int id) {

        //TODO: update to MySQL date / timestamp

        List<Score> list = new ArrayList<Score>();
        Cursor cursor = this.db.query("score, users", new String[]{"_id", "score", "datetime", "email"}, "_id =?", new String[]{Integer.toString(id)}, null, null, "scores DESC");
        if (cursor.getCount() > 0) {
            if (cursor.moveToFirst()) {
                do {

                    int thisID = cursor.getInt(0);
                    int score = cursor.getInt(1);
                    int datetime = cursor.getInt(2);
                    String email = cursor.getString(3);

                    Score newScore = new Score(thisID, score, datetime, email);

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
        cv.put("_id", score.getID());
        cv.put("score", score.getScore());
        cv.put("datetime", score.getDatetime());
        cv.put("email", score.getEmail());
        db.insert("users", null, cv);
    }

    //TODO: Generic update score

}
