package arizona.edu.foodflipper;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


/*
 * The gameplay activity that loads a list of food, converts it to a list of
 * questions, then displays / cycles through the questions until time runs
 * out or all of the questions have been answered.
 */
public class GameActivity extends Activity {

    /*
     * Tasks, Preferences, etc.
     */
    public static final String PREFS_NAME = "MyPrefsFile";
    public static final boolean LESS_THAN = false;
    public static final boolean GREATER_THAN = true;
    int timerDelay = 1500;
    DataHelper dh;
    private Game game;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        dh = new DataHelper(this);

        //initialize game
        ArrayList<Food> food = (ArrayList) dh.getListOfFood();
        game = new Game(food, this);


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





    /*
     * Game
     *
     *  Handles gameplay and the state of the game.
     */
    private class Game {

        final static int STARTING = 1;
        final static int RUNNING = 2;
        final static int ENDING = 3;
        Context context;
        int gameState = -1;
        private ArrayList<GameQuestion> questions;
        private int questionNumber;


        private int questionVal;
        private int answerVal;

        private int score = 0;

        public Game(ArrayList<Food> food, Context context) {
            this.context = context;
            questions = foodToQuestions(food);
            questionNumber = -1;
            gameState = STARTING;
            advance();
            gameState = RUNNING;
        }//end Constructor



        /*
         * makeGuess
         *
         * Called by the swipe buttons. This is the main driving method
         * of the game; it advances the Game's Question list,
         * updates the score, questionType, questionVal, hintTypes, and all other
         * values that the UI uses.
         */
        public void makeGuess(boolean guess) {
            CountDownTimer timer = new CountDownTimer(timerDelay, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {

                }

                @Override
                public void onFinish() {
                    //update game state
                    advance();
                }
            };
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

            timer.start();
        }//end makeGuess

        /*
         * Moves to the next question,
         * Checks for end of game,
         * updates game state to match that of next question
         */
        private void advance() {

            //Set new Question
            this.questionNumber++;
            if (questions.size() == 0 || questionNumber > questions.size()) {
                gameOver();
            }//end if
            try {
                GameQuestion currentQuestion = questions.get(questionNumber);
                questionVal = currentQuestion.getQuestionVal();
                answerVal = currentQuestion.getAnswerVal();
                //update image
                ImageView view = (ImageView) findViewById(R.id.food_image);
                view.setImageBitmap(currentQuestion.getImage());
                view.getLayoutParams().height = 600;
                view.getLayoutParams().width = 600;
                TextView questionView = (TextView) findViewById(R.id.question);
                questionView.setText(currentQuestion.getQuestionType() + ":      " + currentQuestion.getQuestionVal());
            } catch (Exception e) {
                gameOver();
            }

        }//end advance

        /*
         * Cleans up shop and launches GameOverActivity
         */
        public void gameOver() {

            gameState = ENDING;

            SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
            String email = settings.getString("userName", "Anonymous");
            Score s = new Score(score, email);
            dh.insertScore(s);

            finish();
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