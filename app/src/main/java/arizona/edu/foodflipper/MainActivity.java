package arizona.edu.foodflipper;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;


public class MainActivity extends ActionBarActivity {

    public static final String PREFS_NAME = "MyPrefsFile";
    static final int LOGIN_USER = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        boolean isLoggedIn = settings.getBoolean("isLoggedIn", false);
        if (!isLoggedIn) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivityForResult(intent, LOGIN_USER);
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        boolean isLoggedIn = settings.getBoolean("isLoggedIn", false);
        if (requestCode == LOGIN_USER) {
            if (!isLoggedIn) {
                Intent intent = new Intent(this, LoginActivity.class);
                startActivityForResult(intent, LOGIN_USER);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.action_logout:
                logoutUser();
                return true;
            case R.id.action_settings:
                userSettings();
                return true;
        }


        return super.onOptionsItemSelected(item);
    }

    public void userSettings() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    public void logoutUser() {
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean("isLoggedIn", false);
        editor.putString("userName", "");
        editor.commit();
        this.recreate();
    }
    public void onPlayGameButtonClick(View v) {
        Intent intent = new Intent(this, GameActivity.class);
        startActivity(intent);
    }

    public void onFoodEntryButtonClick(View v) {
        Intent intent = new Intent(this, FoodEntryActivity.class);
        startActivity(intent);
    }

    public void onViewScoresButtonClick(View v) {
        Intent intent = new Intent(this, ViewScoresActivity.class);
        startActivity(intent);
    }
}
