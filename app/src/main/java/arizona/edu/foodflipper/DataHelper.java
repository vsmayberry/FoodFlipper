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
import android.os.StrictMode;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class DataHelper {
    /*
    private static final String DATABASE_NAME = "food_flipper.db";
    private static final int DATABASE_VERSION = 1;
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
            "email TEXT," +
            "score INTEGER DEFAULT 0," +
            "datetime DATETIME DEFAULT CURRENT_TIMESTAMP," + //TODO: change after converting to MySQL
            "FOREIGN KEY(_id) REFERENCES users(_id))";
    int USE_REMOTE = 0;
    private SQLiteDatabase db;
    */

    private static final String FFAPI = "http://www.jsxshq.com/foodflipper.php";

    //Error codes
   public static final String NO_DATA = "NO DATA.";
   public static final String INVALID_ARGS ="INVALID ARGS.";

   public static final String INVALID_UID = "INVALID UID.";
   public static final String FAILED_LOAD_USER = "FAILED TO LOAD USER, PLEASE TRY AGAIN";
   public static final String FAILED_ADD_USER = "FAILED TO ADD USER, PLEASE TRY AGAIN.";
   public static final String FAILED_UPDATE_USER  = "FAILED TO UPDATE USER, PLEASE TRY AGAIN.";
   public static final String FAILED_TO_DELETE_USER = "FAILED TO DELETE USER, PLEASE TRY AGAIN";
   public static final String EMAIL_IN_SYSTEM = "THIS EMAIL IS ALREADY IN OUR SYSTEM.";
   public static final String EMAIL_NOT_IN_SYSTEM = "THIS EMAIL IS NOT IN OUR SYSTEM.";
   public static final String USER_DOES_NOT_EXIST = "USER DOES NOT EXIST.";
   public static final String INVALID_EMAIL = "INVAID EMAIL.";
   public static final String INCORRECT_PASSWORD = "INCORRECT PASSWORD.";
   public static final String INVALID_PASSWORD = "INVAID PASSWORD.";

   public static final String INVALID_FID = "INVAID FID.";
   public static final String FAILED_ADD_FOOD = "FAILED TO ADD FOOD, PLEASE TRY AGAIN";
   public static final String FAILED_LOAD_FOOD = "FAILED TO LOAD FOOD, PLEASE TRY AGAIN";
   public static final String FAILED_UPDATE_FOOD = "FAILED TO UPDATE FOOD, PLEASE TRY AGAIN.";
   public static final String FAILED_FLAG_FOOD = "FAILED TO FLAG FOOD, PLEASE TRY AGAIN";
   public static final String INVALID_FILE_NAME = "INVALID FILE NAME.";
   public static final String FAILED_TO_DELETE_FOOD = "FAILED TO DELETE FOOD, PLEASE TRY AGAIN";

   public static final String INVALID_SCORE = "INVAID SCORE.";
   public static final String FAILED_ADD_SCORE = "FAILED TO ADD SCORE, PLEASE TRY AGAIN.";
   public static final String FAILED_LOAD_SCORE =  "FAILED TO LOAD SCORES, PLEASE TRY AGAIN";
   public static final String INVALID_SIZE = "INVALID SIZE.";
   public static final String INVALID_RADIUS = "INVALID RADIUS.";

    //Success codes
   public static final String SUCCESSFULLY_ADDED   = "SUCCESSFULLY ADDED!";
   public static final String SUCCESSFULLY_UPDATED = "SUCCESSFULLY UPDATED!";
   public static final String SUCCESSFULLY_DELETED = "SUCCESSFULLY DELETED!";
   public static final String SUCCESSFULLY_FLAGGED = "SUCCESSFULLY FLAGGED!";

    private Context context;

    public DataHelper(Context context) {
        this.context = context;
    }




    /*
     * Users
     */




    public User insertUser(User user) {

        try {

            System.out.println("insertUser: " + user.getEmail() + ", " + user.getPassword());

            HttpClient httpClient = new DefaultHttpClient();
            HttpPost post = new HttpPost(FFAPI);
            ArrayList<NameValuePair> array = new ArrayList<NameValuePair>();
            array.add(new BasicNameValuePair("o", "insertUser"));
            array.add(new BasicNameValuePair("a[0]", user.getEmail()));
            array.add(new BasicNameValuePair("a[1]", user.getPassword()));
            post.setEntity(new UrlEncodedFormEntity(array));
            HttpResponse response = httpClient.execute(post);

            BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
            StringBuilder builder = new StringBuilder();
            for (String line = null; (line = reader.readLine()) != null; ) {
                builder.append(line);
            }
            System.out.println("insertUser builder: " + builder);

            String error = errorCheck(builder.toString());
            if(!error.equals("")){
                return new User(error);
            }
            System.out.println("insertUser create jsonArray");
            JSONArray jsonArray = new JSONArray(builder.toString());

            System.out.println("insertUser get uid from json");
            int uid = jsonArray.getInt(0);
            return new User(uid, user.getEmail(), user.getPassword(), "");

        } catch (IOException e) {

            System.out.println("insertUser IOException");
            return new User("IO EXCEPTION");
        } catch (JSONException e) {

            System.out.println("insertUserJSONException");
            return new User("JSONException");
        }
    }


    public User attemptLogin(String email, String password) {

        System.out.println("attemptLogin " + email + ", " + password);

        try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost post = new HttpPost(FFAPI);

            ArrayList<NameValuePair> array = new ArrayList<NameValuePair>();
            array.add(new BasicNameValuePair("o", "attemptLogin"));
            array.add(new BasicNameValuePair("a[0]", email));
            array.add(new BasicNameValuePair("a[1]", password));
            post.setEntity(new UrlEncodedFormEntity(array));

            HttpResponse response = httpClient.execute(post);

            BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
            StringBuilder builder = new StringBuilder();
            for (String line = null; (line = reader.readLine()) != null; ) {
                builder.append(line);
            }
            System.out.println("attemptLogin builder: " + builder);


            String error = errorCheck(builder.toString());
            if(!error.equals("")){
                return new User(error); //return error user
            }

            System.out.println("attemptLogin create jsonArray");
            JSONArray jsonArray = new JSONArray(builder.toString());

            System.out.println("attemptLogin get uid from json");
            int uid = jsonArray.getInt(0);
            System.out.println("attemptLogin get address from json");
            String address = "";
            if (jsonArray.length() >= 4) {
               address = jsonArray.getString(3);
            }
            return new User(uid, email, password, address);

        } catch (JSONException e) {

            System.out.println("attemptLogin JSONException");
            return(new User("JSONException"));
        } catch (ClientProtocolException e) {

            System.out.println("attemptLogin ClientProtocolException");
            return(new User("ClientProtocolException"));
        } catch (UnsupportedEncodingException e) {

            System.out.println("attemptLogin UnsupportedEncodingException");
            return(new User("UnsupportedEncodingException"));
        } catch (IOException e) {

            System.out.println("attemptLogin IOException");
            return(new User("IOException"));
        }
    }

    public User selectUser(User user) {

        try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost post = new HttpPost(FFAPI);
            ArrayList<NameValuePair> array = new ArrayList<NameValuePair>();
            array.add(new BasicNameValuePair("o", "selectUser"));
            array.add(new BasicNameValuePair("a[0]", "" + user.getUID()));
            post.setEntity(new UrlEncodedFormEntity(array));
            HttpResponse response = httpClient.execute(post);

            BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
            StringBuilder builder = new StringBuilder();
            for (String line = null; (line = reader.readLine()) != null; ) {
                builder.append(line);
            }
            System.out.println("selectUser builder: " + builder);

            String error = errorCheck(builder.toString());
            if(!error.equals("")){
                return new User(error);
            }

            JSONObject jsonObject = new JSONObject(builder.toString());
            return new User(jsonObject.getInt("uid"), jsonObject.getString("email"),
                            jsonObject.getString("password"), jsonObject.getString("address"));

        } catch (IOException e) {
            return new User("IOException");
        } catch (JSONException e) {
            return new User("JSONException");
        }
    }




    public boolean updateUser(User user) {

        try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost post = new HttpPost(FFAPI);
            ArrayList<NameValuePair> array = new ArrayList<NameValuePair>();
            array.add(new BasicNameValuePair("o", "updateUser"));
            array.add(new BasicNameValuePair("a[0]", "" + user.getUID()));
            array.add(new BasicNameValuePair("a[1]", user.getEmail()));
            array.add(new BasicNameValuePair("a[2]", user.getPassword()));
            array.add(new BasicNameValuePair("a[3]", user.getAddress()));
            post.setEntity(new UrlEncodedFormEntity(array));
            HttpResponse response = httpClient.execute(post);

            BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
            StringBuilder builder = new StringBuilder();
            for (String line = null; (line = reader.readLine()) != null; ) {
                builder.append(line);
            }
            System.out.println(builder);

            return true;
        } catch (IOException e) {
            return false;
        }
    }




    public boolean deleteUser(User user) {

        try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost post = new HttpPost(FFAPI);
            ArrayList<NameValuePair> array = new ArrayList<NameValuePair>();
            array.add(new BasicNameValuePair("o", "deleteUser"));
            array.add(new BasicNameValuePair("a[0]", "" + user.getUID()));
            post.setEntity(new UrlEncodedFormEntity(array));
            HttpResponse response = httpClient.execute(post);

            BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
            StringBuilder builder = new StringBuilder();
            for (String line = null; (line = reader.readLine()) != null; ) {
                builder.append(line);
            }
            System.out.println("deleteUser builder: " + builder);

               return true;

        } catch (IOException e) {
            return false;
        }
    }




    /*
     * Food
     */




    public boolean insertFood(Food food, Bitmap bitmap) {

        System.out.println("INSERT FOOD");

        try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost post = new HttpPost(FFAPI);
            MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
            ArrayList<NameValuePair> array = new ArrayList<NameValuePair>();

            entity.addPart("o", new StringBody("insertFood"));
            entity.addPart("a[0]", new StringBody("" + food.getUID()));
            entity.addPart("a[1]", new StringBody("" + food.getName()));
            entity.addPart("a[2]", new StringBody("" + food.getCalories()));
            entity.addPart("a[3]", new StringBody("" + food.getCarbs()));
            entity.addPart("a[4]", new StringBody("" + food.getFat()));
            entity.addPart("a[5]", new StringBody("" + food.getProtein()));

            if(bitmap != null) {

                System.out.println("BITMAP ISN'T NULL");

                //convert bitmap to file
                File bitmapFile = new File(context.getCacheDir(), food.getUID() + "-" + food.getName() + ".png");
                OutputStream os = new FileOutputStream(bitmapFile);
                bitmap.compress(Bitmap.CompressFormat.PNG, 0, os);
                os.flush();
                os.close();

                // add photo as file in http request
                entity.addPart("photo", new FileBody(bitmapFile));
            }else{
                System.out.println("NULL BITMAP");
            }

            System.out.println("POST ENTITY");
            post.setEntity(entity);

            System.out.println("RESPONSE");
            HttpResponse response = httpClient.execute(post);

            System.out.println("BUFFER");
            BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
            StringBuilder builder = new StringBuilder();
            for (String line = null; (line = reader.readLine()) != null; ) {
                builder.append(line);
            }

            System.out.println("insertFood builder: " + builder);
            return true;

        } catch (IOException e) {
            System.out.println("Insert Food IOExcepton");
            return false;
        }
    }




    public ArrayList<Food> selectFoodByUser(User user) {

        try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost post = new HttpPost(FFAPI);
            ArrayList<NameValuePair> array = new ArrayList<NameValuePair>();
            array.add(new BasicNameValuePair("o", "selectFoodByUser"));
            array.add(new BasicNameValuePair("a[0]", "" + user.getUID()));
            post.setEntity(new UrlEncodedFormEntity(array));
            HttpResponse response = httpClient.execute(post);

            BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
            StringBuilder builder = new StringBuilder();
            for (String line = null; (line = reader.readLine()) != null; ) {
                builder.append(line);
            }
            System.out.println("selectFoodByUser builder: " + builder);

            String error = errorCheck(builder.toString());
            if(!error.equals("")){
                return new ArrayList<Food>();
            }

            JSONObject jsonObject = new JSONObject(builder.toString());
            JSONObject jsonTemp;
            Food tempFood;
            ArrayList<Food> userList = new ArrayList<Food>();

            for(int i = 0; i < jsonObject.length(); i++){

                jsonTemp = jsonObject.getJSONObject("" + i);
                tempFood = new Food(jsonTemp.getInt("fid"), jsonTemp.getInt("uid"),
                                     jsonTemp.getString("name"), jsonTemp.getInt("calories"),
                                     jsonTemp.getInt("carbs"), jsonTemp.getInt("fat"),
                                     jsonTemp.getInt("protein"));

                userList.add(tempFood);
            }

            System.out.println("LIST SIZE WHEN RETURNED: " + userList.size());
            return userList;

        } catch (IOException e) {
            return new ArrayList<Food>();
        } catch (JSONException e) {
            return new ArrayList<Food>();
        }
    }




    public ArrayList<Food> selectFoodForGame(int size) {

        try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost post = new HttpPost(FFAPI);
            ArrayList<NameValuePair> array = new ArrayList<NameValuePair>();
            array.add(new BasicNameValuePair("o", "selectFoodByUser"));
            array.add(new BasicNameValuePair("a[0]", "" + size));
            post.setEntity(new UrlEncodedFormEntity(array));
            HttpResponse response = httpClient.execute(post);

            BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
            StringBuilder builder = new StringBuilder();
            for (String line = null; (line = reader.readLine()) != null; ) {
                builder.append(line);
            }
            System.out.println("selectFoodForGame builder: " + builder);

            String error = errorCheck(builder.toString());
            if(!error.equals("")){
                return new ArrayList<Food>();
            }

            JSONObject jsonObject = new JSONObject(builder.toString());
            JSONObject jsonTemp;
            Food tempFood;
            ArrayList<Food> gameList = new ArrayList<Food>();

            for(int i = 0; i < jsonObject.length(); i++){

                jsonTemp = jsonObject.getJSONObject("" + i);
                tempFood = new Food(jsonTemp.getInt("fid"), jsonTemp.getInt("uid"),
                        jsonTemp.getString("name"), jsonTemp.getInt("calories"),
                        jsonTemp.getInt("carbs"), jsonTemp.getInt("fat"),
                        jsonTemp.getInt("protein"));

                gameList.add(tempFood);
            }

            System.out.println("LIST SIZE WHEN RETURNED: " + gameList.size());
            return gameList;

        } catch (IOException e) {
            return new ArrayList<Food>();
        } catch (JSONException e) {
            return new ArrayList<Food>();
        }
    }




    public boolean updateFood(Food food) {

        try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost post = new HttpPost(FFAPI);
            ArrayList<NameValuePair> array = new ArrayList<NameValuePair>();
            array.add(new BasicNameValuePair("o", "updateFood"));
            array.add(new BasicNameValuePair("a[0]", "" + food.getFID()));
            array.add(new BasicNameValuePair("a[1]", food.getName()));
            array.add(new BasicNameValuePair("a[2]", "" + food.getCalories()));
            array.add(new BasicNameValuePair("a[3]", "" + food.getCarbs()));
            array.add(new BasicNameValuePair("a[4]", "" + food.getFat()));
            array.add(new BasicNameValuePair("a[5]", "" + food.getProtein()));
            post.setEntity(new UrlEncodedFormEntity(array));
            HttpResponse response = httpClient.execute(post);

            BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
            StringBuilder builder = new StringBuilder();
            for (String line = null; (line = reader.readLine()) != null; ) {
                builder.append(line);
            }
            System.out.println("updateFood builder: " + builder);

            return true;
        } catch (IOException e) {
            return false;
        }
    }




    public boolean flagFood(Food food) {

        try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost post = new HttpPost(FFAPI);
            ArrayList<NameValuePair> array = new ArrayList<NameValuePair>();
            array.add(new BasicNameValuePair("o", "flagFood"));
            array.add(new BasicNameValuePair("a[0]", "" + food.getFID()));
            post.setEntity(new UrlEncodedFormEntity(array));
            HttpResponse response = httpClient.execute(post);

            BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
            StringBuilder builder = new StringBuilder();
            for (String line = null; (line = reader.readLine()) != null; ) {
                builder.append(line);
            }
            System.out.println("flagFood builder: " + builder);

            return true;

        } catch (IOException e) {
            return false;
        }
    }




    public boolean deleteFood(Food food) {

        try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost post = new HttpPost(FFAPI);
            ArrayList<NameValuePair> array = new ArrayList<NameValuePair>();
            array.add(new BasicNameValuePair("o", "deleteFood"));
            array.add(new BasicNameValuePair("a[0]", "" + food.getFID()));
            post.setEntity(new UrlEncodedFormEntity(array));
            HttpResponse response = httpClient.execute(post);

            BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
            StringBuilder builder = new StringBuilder();
            for (String line = null; (line = reader.readLine()) != null; ) {
                builder.append(line);
            }
            System.out.println("deleteFood builder: " + builder);

            return true;

        } catch (IOException e) {
            return false;
        }
    }




    /*
     * Scores
     */




    public boolean insertScore(Score score) {

        try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost post = new HttpPost(FFAPI);
            ArrayList<NameValuePair> array = new ArrayList<NameValuePair>();
            array.add(new BasicNameValuePair("o", "insertScore"));
            array.add(new BasicNameValuePair("a[0]", "" + score.getUID()));
            array.add(new BasicNameValuePair("a[1]", "" + score.getScore()));
            array.add(new BasicNameValuePair("a[2]", "" + score.getLatitude()));
            array.add(new BasicNameValuePair("a[3]", "" + score.getLongitude()));
            post.setEntity(new UrlEncodedFormEntity(array));
            HttpResponse response = httpClient.execute(post);

            BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
            StringBuilder builder = new StringBuilder();
            for (String line = null; (line = reader.readLine()) != null; ) {
                builder.append(line);
            }
            System.out.println("insertScore builder: " + builder);

            return true;

        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }




    public ArrayList<Score> selectScores(int size) {

        try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost post = new HttpPost(FFAPI);
            ArrayList<NameValuePair> array = new ArrayList<NameValuePair>();
            array.add(new BasicNameValuePair("o", "selectScores"));
            array.add(new BasicNameValuePair("a[0]", "" + size));
            post.setEntity(new UrlEncodedFormEntity(array));
            HttpResponse response = httpClient.execute(post);

            BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
            StringBuilder builder = new StringBuilder();
            for (String line = null; (line = reader.readLine()) != null; ) {
                builder.append(line);
            }
            System.out.println("selectScores builder: " + builder);

            String error = errorCheck(builder.toString());
            if(!error.equals("")){
                return new ArrayList<Score>();
            }

            System.out.println("selectScores parse JSON...");

            JSONObject jsonObject = new JSONObject(builder.toString());
            JSONObject jsonTemp;
            Score tempScore;
            ArrayList<Score> scoreList = new ArrayList<Score>();

            for(int i = 0; i < jsonObject.length(); i++){

                jsonTemp = jsonObject.getJSONObject("" + i);
                tempScore = new Score(jsonTemp.getInt("uid"), jsonTemp.getInt("score"), jsonTemp.getString("timestamp"),
                                      jsonTemp.getDouble("latitude"), jsonTemp.getDouble("longitude"));

                scoreList.add(tempScore);
            }

            System.out.println("LIST SIZE WHEN RETURNED: " + scoreList.size());
            return scoreList;

        } catch (IOException e) {
            return new ArrayList<Score>();
        } catch (JSONException e) {
            return new ArrayList<Score>();
        }
    }




    public ArrayList<Score> selectScoresByDistance(int size, double latitude, double longitude, double radius) {

        try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost post = new HttpPost(FFAPI);
            ArrayList<NameValuePair> array = new ArrayList<NameValuePair>();
            array.add(new BasicNameValuePair("o", "selectScoresByDistance"));
            array.add(new BasicNameValuePair("a[0]", "" + size));
            array.add(new BasicNameValuePair("a[1]", "" + latitude));
            array.add(new BasicNameValuePair("a[2]", "" + longitude));
            array.add(new BasicNameValuePair("a[3]", "" + radius));
            post.setEntity(new UrlEncodedFormEntity(array));
            HttpResponse response = httpClient.execute(post);

            BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
            StringBuilder builder = new StringBuilder();
            for (String line = null; (line = reader.readLine()) != null; ) {
                builder.append(line);
            }
            System.out.println("selectScoresByDistance builder: " + builder);

            String error = errorCheck(builder.toString());
            if(!error.equals("")){
                return new ArrayList<Score>();
            }

            JSONObject jsonObject = new JSONObject(builder.toString());
            JSONObject jsonTemp;
            Score tempScore;
            ArrayList<Score> scoreList = new ArrayList<Score>();

            for(int i = 0; i < jsonObject.length(); i++){

                jsonTemp = jsonObject.getJSONObject("" + i);
                tempScore = new Score(jsonTemp.getInt("uid"), jsonTemp.getInt("score"), jsonTemp.getString("timestamp"),
                        jsonTemp.getDouble("latitude"), jsonTemp.getDouble("longitude"));

                scoreList.add(tempScore);
            }

            System.out.println("LIST SIZE WHEN RETURNED: " + scoreList.size());
            return scoreList;


        } catch (IOException e) {
            return new ArrayList<Score>();
        } catch (JSONException e) {
            return new ArrayList<Score>();
        }
    }




    public ArrayList<Score> selectScoresByUser(User user) {

        try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost post = new HttpPost(FFAPI);
            ArrayList<NameValuePair> array = new ArrayList<NameValuePair>();
            array.add(new BasicNameValuePair("o", "selectScoresByUser"));
            array.add(new BasicNameValuePair("a[0]", "" + user.getUID()));
            post.setEntity(new UrlEncodedFormEntity(array));
            HttpResponse response = httpClient.execute(post);

            BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
            StringBuilder builder = new StringBuilder();
            for (String line = null; (line = reader.readLine()) != null; ) {
                builder.append(line);
            }
            System.out.println("selectscoresByUser builder: " + builder);

            String error = errorCheck(builder.toString());
            if(!error.equals("")){
                return new ArrayList<Score>();
            }

            JSONObject jsonObject = new JSONObject(builder.toString());
            JSONObject jsonTemp;
            Score tempScore;
            ArrayList<Score> scoreList = new ArrayList<Score>();

            for(int i = 0; i < jsonObject.length(); i++){

                jsonTemp = jsonObject.getJSONObject("" + i);
                tempScore = new Score(user, jsonTemp.getInt("score"),
                                      jsonTemp.getString("timestamp"));

                scoreList.add(tempScore);
            }

            System.out.println("LIST SIZE WHEN RETURNED: " + scoreList.size());
            return scoreList;

        } catch (IOException e) {
            return new ArrayList<Score>();
        } catch (JSONException e) {
            return new ArrayList<Score>();
        }
    }


    public String errorCheck(String response){
         String errors[] = {NO_DATA, INVALID_ARGS, INVALID_UID, FAILED_LOAD_USER, FAILED_ADD_USER,
                 FAILED_UPDATE_USER, FAILED_TO_DELETE_USER, EMAIL_IN_SYSTEM, EMAIL_NOT_IN_SYSTEM,
                 USER_DOES_NOT_EXIST, INVALID_EMAIL, INCORRECT_PASSWORD, INVALID_PASSWORD,
                 INVALID_FID, FAILED_ADD_FOOD, FAILED_LOAD_FOOD, FAILED_UPDATE_FOOD,
                 FAILED_FLAG_FOOD, INVALID_FILE_NAME, FAILED_TO_DELETE_FOOD, INVALID_SCORE,
                 FAILED_ADD_SCORE, FAILED_LOAD_SCORE, INVALID_SIZE, INVALID_RADIUS,
                 SUCCESSFULLY_ADDED, SUCCESSFULLY_UPDATED, SUCCESSFULLY_DELETED,
                 SUCCESSFULLY_FLAGGED};
        for(int i = 0; i < errors.length; i++){
            if(response.equals(errors[i])){

                System.out.println("ERROR MESSAGE DETECTED: " + errors[i]);
                return errors[i];
            }
        }
        return "";
    }

}
