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

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class DataHelper {
    private static final String DATABASE_NAME = "food_flipper.db";
    private static final int DATABASE_VERSION = 1;
    private Context context;
    private SQLiteDatabase db;
    static DataHelper sInstance;

    //declare your tables

    //Patients Table
    static String createUsersTable = "create table users (" +
            "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "email TEXT," +
            "password TEXT)";
    static String createFoodTable = "create table food (" +
            "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "name TEXT," +
            "image BLOB," +
            "calories INTEGER)";

    //declare your columns


    //create your dbfunctions


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
            //Create your tables
            //Create users table
            db.execSQL(createUsersTable);
            db.execSQL(createFoodTable);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + "users");
            db.execSQL("DROP TABLE IF EXISTS " + "food");
            onCreate(db);
        }
    }

    public Cursor getUsers() {

        Cursor cursor = this.db.query("users", new String[]{"_id", "email", "password"}, null, null, null, null, null);
        return cursor;
    }

    public Cursor getFood() {

        Cursor cursor = this.db.query("food", new String[]{"_id", "name", "image", "calories"}, null, null, null, null, null);
        return cursor;
    }

    public void insertFood(Food food, Bitmap bitmap) {
        int size = bitmap.getRowBytes() * bitmap.getHeight();
        ByteBuffer b = ByteBuffer.allocate(size);
        bitmap.copyPixelsToBuffer(b);
        byte[] bytes = new byte[size];
        b.get(bytes, 0, bytes.length);
        ContentValues cv = new ContentValues();
        cv.put("user", food.name);
        cv.put("image", bytes);
        cv.put("calories", food.calories);
        db.insert("food", null, cv);

    }

    public void insertUser(User user) {

        ContentValues cv = new ContentValues();
        cv.put("email", user.getEmail());
        cv.put("password", user.getPassword());
        db.insert("users", null, cv);

    }


    public List<String> selectAll() {
        List<String> list = new ArrayList<String>();
        Cursor cursor = this.db.query("users", new String[]{"_id", "email", "password"}, null, null, null, null, null);
        if (cursor.getCount() > 0) {
            if (cursor.moveToFirst()) {
                do {
                    list.add(cursor.getString(1));
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

        Cursor cursor_patientDB = db.query("users", null, "_id =?", new String[]{id}, null, null, null, null);
        if (cursor_patientDB != null) {
            cursor_patientDB.moveToFirst();
            prevName = cursor_patientDB.getString(1);
        }

        db.delete("users", "_id=?", new String[]{id});

        /*Cursor cursor_apptsDB = db.query("appointments", null, "patientName =?", new String[]{prevName.toString()}, null, null, null, null);
        if (cursor_apptsDB != null){
            db.delete("appointments", "patientName=?", new String[]{prevName});
        }*/
    }

    public String getPassword(String userEmail) {
        String password = "undefined";
        Cursor cursor = db.query("users", null, "email =?", new String[]{userEmail.toString()}, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            password = cursor.getString(4);
        }
        return password;
    }

    public int getID(String userEmail) {
        int id_key = -99;
        Cursor cursor = db.query("users", null, "email =?", new String[]{userEmail.toString()}, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            id_key = cursor.getInt(0);
        }
        return id_key;
    }


    public void updateUserPassword(int id_key, String password) {
        String id = "" + id_key;
        ContentValues values = new ContentValues();
        values.put("password", password);
        db.update("users", values, "_id=?", new String[]{id});
    }
/*

    public List<String> selectAppointments() {
        List<String> list = new ArrayList<String>();
        Cursor cursor = this.db.query("appointments", new String[] {"_id","patientName","date",
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




*/


}
