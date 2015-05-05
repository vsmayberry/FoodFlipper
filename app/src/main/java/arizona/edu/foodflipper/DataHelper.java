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
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class DataHelper {
    private static final String DATABASE_NAME = "food_flipper.db";
    private static final int DATABASE_VERSION = 1;
    static DataHelper sInstance;
    //users table
    static String createUsersTable = "create table users (" +
            "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "user TEXT," +
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
            "user TEXT," +
            "score INTEGER DEFAULT 0," +
            "datetime DATETIME DEFAULT CURRENT_TIMESTAMP," + //TODO: change after converting to MySQL
            "FOREIGN KEY(_id) REFERENCES users(_id))";
    private Context context;
    private SQLiteDatabase db;


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

    public static JSONObject getJSONfromURL(String url) {
        InputStream is = null;
        String result = "";
        JSONObject jArray = null;

        // Download JSON data from URL
        try {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(url);
            HttpResponse response = httpclient.execute(httppost);
            HttpEntity entity = response.getEntity();
            is = entity.getContent();

        } catch (Exception e) {
            Log.e("log_tag", "Error in http connection " + e.toString());
        }

        // Convert response to string
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    is, "iso-8859-1"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            is.close();
            result = sb.toString();
        } catch (Exception e) {
            Log.e("log_tag", "Error converting result " + e.toString());
        }

        try {
            jArray = new JSONObject(result);
        } catch (Exception e) {
            Log.e("log_tag", "Error parsing data " + e.toString());
        }

        return jArray;
    }

    private void openDatabase() {
        OpenHelper openHelper = new OpenHelper(this.context);
        db = openHelper.getWritableDatabase();
    }


    /*
     * Query methods
     */

    /*
     * Users
     */

    public Cursor getUsers() {

        return db.query("users", new String[]{"_id", "user", "password"}, null, null, null, null, null);
    }

    public void insertUser(User user) {

        ContentValues cv = new ContentValues();
        cv.put("user", user.getUser());
        cv.put("password", user.getPassword());
        db.insert("users", null, cv);

    }

    public List<User> getListOfUsers() {
        List<User> list = new ArrayList<User>();
        Cursor cursor = this.db.query("users", new String[]{"_id", "user"}, null, null, null, null, null);
        if (cursor.getCount() > 0) {
            if (cursor.moveToFirst()) {
                do {

                    int id = cursor.getInt(0);
                    String user = cursor.getString(1);
                    String password = cursor.getString(2);

                    User newUser = new User(user, password);

                    list.add(newUser);
                } while (cursor.moveToNext());
            }
            if (cursor != null && !cursor.isClosed())
                cursor.close();
        }
        return list;
    }

    public int getID(String user) {
        if (user == null)
            return -1;
        int id_key = -99;
        Cursor cursor = db.query("users", null, "user =?", new String[]{user}, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            id_key = cursor.getInt(0);
        }
        return id_key;
    }

    public String getPassword(String user) {
        String password = "undefined";
        Cursor cursor = db.query("users", null, "user =?", new String[]{user}, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            password = cursor.getString(2);
        }
        return password;
    }

    public void updateUserPassword(String user, String password) {

        ContentValues values = new ContentValues();
        values.put("password", password);
        db.update("users", values, "user=?", new String[]{user});
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

                    Food newFoodItem = new Food(id, uid, name, calories, carbs, fat, protein);
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

                    Food newFoodItem = new Food(cursor.getInt(0), uID, name, calories, carbs, fat, protein);
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
        return this.db.query("scores, users", new String[]{"_id", "score", "datetime", "user"}, null, null, null, null, "score DESC");
    }

    public List<String> getListOfScores() {

        //TODO: update to MySQL date / timestamp

        List<String> list = new ArrayList<String>();
        Cursor cursor = this.db.query("scores", new String[]{"_id", "score", "datetime", "user"}, null, null, null, null, "score DESC");
        if (cursor.getCount() > 0) {
            if (cursor.moveToFirst()) {
                do {

                    int id = cursor.getInt(0);
                    int score = cursor.getInt(1);
                    int datetime = cursor.getInt(2);
                    String user = cursor.getString(3);

                    Score newScore = new Score(score, user);


                    list.add(newScore.toString());
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
        Cursor cursor = this.db.query("scores, users", new String[]{"_id", "score", "datetime", "user"}, "_id =?", new String[]{Integer.toString(id)}, null, null, "score DESC");
        if (cursor.getCount() > 0) {
            if (cursor.moveToFirst()) {
                do {

                    int thisID = cursor.getInt(0);
                    int score = cursor.getInt(1);
                    int datetime = cursor.getInt(2);
                    String user = cursor.getString(3);

                    Score newScore = new Score(score, user);
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
        cv.put("score", score.getScore());
        cv.put("user", score.getUser());
        db.insert("scores", null, cv);
    }

    //TODO: Generic update score

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


}
