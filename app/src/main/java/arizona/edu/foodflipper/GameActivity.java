package arizona.edu.foodflipper;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


/*
 * The gameplay activity that loads a list of food, converts it to a list of
 * questions, then displays / cycles through the questions until time runs
 * out or all of the questions have been answered.
 */
public class GameActivity extends Activity{

    /*
     * Tasks, Preferences, etc.
     */
    public static final String PREFS_NAME = "MyPrefsFile";
    public static final boolean LESS_THAN = false;
    public static final boolean GREATER_THAN = true;
    DataHelper dh;
    private Game game;


    /*
     * UI References
     */
    //private View gameTimeProgress;
    private TextView name;
    private ImageButton image;
    private TextView hints[] = new TextView[3];
    private TextView question;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_over);
        dh = new DataHelper(this);

        //initialize game
        ArrayList<Food> food = (ArrayList) dh.getListOfFood();
        game = new Game(food, this);

        /*
         *  Set UI references
         */
        question = (TextView) findViewById(R.id.question);
        hints[0] = (TextView) findViewById(R.id.hint0);
        hints[1] = (TextView) findViewById(R.id.hint1);
        hints[2] = (TextView) findViewById(R.id.hint2);
        //mProgressView  = findViewById(R.id.game_time);

        Button lessThanButton = (Button) findViewById(R.id.less_than);
        lessThanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                game.makeGuess(LESS_THAN);
            }
        });

        Button greaterThanButton = (Button) findViewById(R.id.greater_than);
        greaterThanButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                game.makeGuess(GREATER_THAN);
            }
        });



    }//end onCreate


    private void updateUI(){

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
    }



    /*
     * Game
     *
     *  Handles gameplay and the state of the game.
     */
    private class Game {

        Context context;
        int gameState = -1;
        final static int STARTING = 1;
        final static int RUNNING = 2;
        final static int ENDING = 3;

        private ArrayList<GameQuestion> questions;
        private int questionNumber;

        private String name;
        private Bitmap image;
        private String questionType;
        private int questionVal;
        private int answerVal;
        private String hintTypes[] = new String[3];
        private int hintVals[] = new int[3];

        private int score = 0;

        public Game(ArrayList<Food> food, Context context) {
            this.context = context;
            questions = foodToQuestions(food);
            questionNumber = -1;
            gameState = STARTING;
            advance();
            gameState = RUNNING;
        }//end Constructor


        public int getGameState() { return gameState; }

        public int getQuestionNumber() { return questionNumber; }

        public String getName() { return name; }

        public Bitmap getImage() { return image; }

        public String getQuestionType() { return questionType; }

        public int getQuestionVal() { return questionVal; }

        public int getScore() { return score; }

        public String[] getHintTypes() { return hintTypes; }

        public int[] getHintVals() { return hintVals; }

        /*
         * makeGuess
         *
         * Called by the swipe buttons. This is the main driving method
         * of the game; it advances the Game's Question list,
         * updates the score, questionType, questionVal, hintTypes, and all other
         * values that the UI uses.
         */
        public void makeGuess(boolean guess) {
            ImageView image = (ImageView) findViewById(R.id.food_image);
            //Update score and track correct guesses
            boolean correct = ((questionVal < answerVal) && guess)
                    || ((questionVal > answerVal) && !guess);

            if (correct) {
                score += 10; // TODO: time-based bonuses
                questions.get(questionNumber).setAnsweredCorrectly(true);
                image.setImageResource(R.drawable.check);
            } // Note: GameQuestion.answeredCorrectly is set to false by default
            else {
                image.setImageResource(R.drawable.x);
            }

            //update game state
            advance();
        }//end makeGuess

        /*
         * Moves to the next question,
         * Checks for end of game,
         * updates game state to match that of next question
         */
        private void advance(){

            //Set new Question
            this.questionNumber++;
            if (questions.size() == 0 || questionNumber > questions.size()) {
                gameOver();
            }//end if
            GameQuestion currentQuestion = questions.get(questionNumber);

            //Update state
            name         = currentQuestion.getName();
            hintTypes    = currentQuestion.getHintTypes();
            hintVals     = currentQuestion.getHintVals();
            questionType = currentQuestion.getQuestionType();
            questionVal  = currentQuestion.getQuestionVal();
            answerVal    = currentQuestion.getAnswerVal();
            ImageView view = (ImageView) findViewById(R.id.food_image);
            view.setImageBitmap(currentQuestion.getImage());

        }//end advance

        /*
         * Cleans up shop and launches GameOverActivity
         */
        public void gameOver() {

            gameState = ENDING;


            Intent intent = new Intent(this.context, MainActivity.class);
            intent.putExtra("questions", questions);
            startActivity(intent);

            //finish();
        }//end gameOver

        private ArrayList<GameQuestion> foodToQuestions(List<Food> food) {

            ArrayList<GameQuestion> questions = new ArrayList<GameQuestion>();

            for (int i = 0; i < food.size(); i++) {
                questions.add(new GameQuestion(food.get(i)));
            }//end for
            return questions;
        }//end foodToQuestions

    }//end private class Game

}//end GameActivity