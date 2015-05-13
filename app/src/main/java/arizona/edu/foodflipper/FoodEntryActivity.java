package arizona.edu.foodflipper;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class FoodEntryActivity extends ActionBarActivity {

    static final int REQUEST_TAKE_PHOTO = 1;
    public static final String PREFS_NAME = "MyPrefsFile";
    String mCurrentPhotoPath;
    private ImageView mImageView;
    File image;
    DataHelper dh;
    Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        dh = new DataHelper(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_entry);
        ImageButton button = (ImageButton) findViewById(R.id.imageButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(takePictureIntent, 1);
                }
            }
        });

        Button submit = (Button) findViewById(R.id.submitButton);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
                String userEmail = settings.getString("userEmail", null);
                EditText et = (EditText) findViewById(R.id.nameBox);
                String name = et.getText().toString();

                et = (EditText) findViewById(R.id.calorieBox);
                int calories = Integer.parseInt(et.getText().toString());

                et = (EditText) findViewById(R.id.carbBox);
                int carbs = Integer.parseInt(et.getText().toString());
                et = (EditText) findViewById(R.id.fatBox);
                int fat = Integer.parseInt(et.getText().toString());
                et = (EditText) findViewById(R.id.protBox);
                int protein = Integer.parseInt(et.getText().toString());

                //TODO: Update login to set a global User object that we can reference to get the UID
                Food food = new Food(1, name, calories, carbs, fat, protein);

                System.out.println("CALL: INSERT FOOD");

                dh.insertFood(food, bitmap);

                //picasso

                finish();
            }
        });
    }


    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {

            if (requestCode == 1 && resultCode == RESULT_OK) {
                Bundle extras = data.getExtras();
                bitmap = (Bitmap) extras.get("data");
                ImageButton ib = (ImageButton) findViewById(R.id.imageButton);
                ib.setImageBitmap(bitmap);

            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home_screen, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
