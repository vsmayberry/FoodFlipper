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

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
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
import java.util.ArrayList;
import java.util.List;

import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;

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
    private Context context;




    /*
     * Constructor And related methods
     */




    public DataHelper(Context context) {
        this.context = context;
    }

    public boolean attemptLogin(String email, String password) {

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
            System.out.println(builder);


        } catch (Exception e) {
            e.printStackTrace();
        }


        return true;
    }



    /*
     * Query methods
     */




    /*
     * Users
     */




    public User insertUser(User user) {

        try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost post = new HttpPost(FFAPI);
            ArrayList<NameValuePair> array = new ArrayList<NameValuePair>();
            array.add(new BasicNameValuePair("o", "insertUser"));
            array.add(new BasicNameValuePair("a[0]", user.getEmail()));
            array.add(new BasicNameValuePair("a[1]", user.getPassword()));
            array.add(new BasicNameValuePair("a[2]", user.getAddress()));
            post.setEntity(new UrlEncodedFormEntity(array));
            HttpResponse response = httpClient.execute(post);

            BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
            StringBuilder builder = new StringBuilder();
            for (String line = null; (line = reader.readLine()) != null; ) {
                builder.append(line);
            }
            System.out.println(builder);

            return new User("INSERT SUCCESS");

        } catch (IOException e) {
            e.printStackTrace();
        }

        return new User("INSERT FAILED");
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
            System.out.println(builder);


        } catch (IOException e) {
            e.printStackTrace();
        }

        return new User("NO USER FOUND");
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
            System.out.println(builder);

               return true;

        } catch (IOException e) {
            return false;
        }
    }




    /*
     * Food
     */




    public boolean insertFood(Food food, Bitmap bitmap) {

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

            //convert bitmap to file
            File bitmapFile = new File(context.getCacheDir(), food.getUID()+ "-" + food.getName() +".png");
            OutputStream os = new FileOutputStream(bitmapFile);
            bitmap.compress(Bitmap.CompressFormat.PNG, 0, os);
            os.flush();
            os.close();

            // add photo as file in http request
            entity.addPart("photo", new FileBody(bitmapFile));
            post.setEntity(entity);
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
            System.out.println(builder);


        } catch (IOException e) {
            e.printStackTrace();
        }

        return new ArrayList<Food>();
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
            System.out.println(builder);


        } catch (IOException e) {
            e.printStackTrace();
        }

        return new ArrayList<Food>();
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
            System.out.println(builder);

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
            System.out.println(builder);

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
            System.out.println(builder);

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
            System.out.println(builder);

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
            System.out.println(builder);


        } catch (IOException e) {
            e.printStackTrace();
        }

        return new ArrayList<Score>();
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
            System.out.println(builder);


        } catch (IOException e) {
            e.printStackTrace();
        }

        return new ArrayList<Score>();
    }




    public ArrayList<Score> selectScoresByUser(User user) {

        try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost post = new HttpPost(FFAPI);
            ArrayList<NameValuePair> array = new ArrayList<NameValuePair>();
            array.add(new BasicNameValuePair("o", "selectScoresByDistance"));
            array.add(new BasicNameValuePair("a[0]", "" + user.getUID()));
            post.setEntity(new UrlEncodedFormEntity(array));
            HttpResponse response = httpClient.execute(post);

            BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
            StringBuilder builder = new StringBuilder();
            for (String line = null; (line = reader.readLine()) != null; ) {
                builder.append(line);
            }
            System.out.println(builder);


        } catch (IOException e) {
            e.printStackTrace();
        }

        return new ArrayList<Score>();
    }

}
