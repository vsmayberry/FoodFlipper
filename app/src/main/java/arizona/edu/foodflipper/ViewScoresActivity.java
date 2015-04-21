package arizona.edu.foodflipper;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.List;


public class ViewScoresActivity extends ActionBarActivity {

    DataHelper dh;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dh = new DataHelper(this);
        try {
            List<Score> scoreList;
            scoreList = dh.getListOfScores();

            ArrayAdapter<Score> scoreArrayAdapter = new ArrayAdapter<Score>(this, android.R.layout.simple_list_item_1, scoreList);
            ListView lv = (ListView) findViewById(R.id.scoreList);
            lv.setAdapter(scoreArrayAdapter);
        } catch (Exception e) {

        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_view_scores, menu);
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
