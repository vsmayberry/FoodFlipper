package arizona.edu.foodflipper;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;


/*
 * Determines final score, calculates other statistics, inserts
 * the score into the database.
 */
public class GameOverActivity extends Activity {

    private TextView    finalScore;
    private Button      prevQuestion, nextQuestion, reportButton;
    private ImageView   foodImage;
    private TextView    answerLines[];
    private TextView    hintTypes[], hintVals[];
    private Button      mainMenuBtton, newGameButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_over);

        Intent intent = getIntent();
        ArrayList<GameQuestion> gameQuestions = new ArrayList<GameQuestion>();//TODO: intent.getExtras().getParcelableArray("questions");

        /*
         *  Set UI references
         */
        finalScore     = (TextView) findViewById(R.id.final_score);
        prevQuestion   = (Button) findViewById(R.id.prev_button);
        nextQuestion   = (Button) findViewById(R.id.next_button);
        reportButton   = (Button) findViewById(R.id.report_button);
        foodImage      = (ImageView) findViewById(R.id.food_image);
        answerLines[0] = (TextView) findViewById(R.id.answer_line0);
        answerLines[1] = (TextView) findViewById(R.id.answer_line1);
        answerLines[2] = (TextView) findViewById(R.id.answer_line2);
        hintTypes[0]   = (TextView) findViewById(R.id.hint_label0);
        hintTypes[1]   = (TextView) findViewById(R.id.hint_label1);
        hintTypes[2]   = (TextView) findViewById(R.id.hint_label2);
        hintVals[0]    = (TextView) findViewById(R.id.hint_val0);
        hintVals[1]    = (TextView) findViewById(R.id.hint_val1);
        hintVals[2]    = (TextView) findViewById(R.id.hint_val2);
        mainMenuBtton  = (Button) findViewById(R.id.main_menu);
        newGameButton  = (Button) findViewById(R.id.new_game);

        Button lessThanButton = (Button) findViewById(R.id.less_than);
        lessThanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // changeFood(PREV);
            }
        });

        Button greaterThanButton = (Button) findViewById(R.id.greater_than);
        greaterThanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // changeFood(NEXT);
            }
        });


    }


    private void updateUI(){
/*
        //update name
        name.setText(game.getName());

        //update image
        image.setBackground(new BitmapDrawable(getResources(), game.getImage()));

        //update question
        question.setText(game.getQuestionType() + ": " + game.getQuestionVal());

        //update hints
        String hintTexts[] = new String[3];

        hintTexts[0] = game.getHintTypes()[0] + ": "
                + ((game.getHintVals()[0] >= 0) ? game.getHintVals()[0] + "" : "N/A");
        hints[0].setText(hintTexts[0]);

        hintTexts[1] = game.getHintTypes()[1] + ": "
                + ((game.getHintVals()[1] >= 0) ? game.getHintVals()[1] + "" : "N/A");
        hints[1].setText(hintTexts[1]);

        hintTexts[2] = game.getHintTypes()[2] + ": "
                + ((game.getHintVals()[2] >= 0) ? game.getHintVals()[2] + "" : "N/A");
        hints[2].setText(hintTexts[2]);
     */
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
