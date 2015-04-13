package arizona.edu.foodflipper;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
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
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    //private View gameTimeProgress;
    private TextView hints[] = new TextView[3];
    private TextView question;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        dh = new DataHelper(this);
        ArrayList<Food> food = (ArrayList) dh.getListOfFood();

        /*
         *  Set UI references
         */

        question = (TextView) findViewById(R.id.question);
        hints[0] = (TextView) findViewById(R.id.hint0);
        hints[1] = (TextView) findViewById(R.id.hint1);
        hints[2] = (TextView) findViewById(R.id.hint2);
        //mProgressView  = findViewById(R.id.game_time);

        Button lessThanButton = (Button) findViewById(R.id.less_than);
        lessThanButton.setOnClickListener(new OnClickListener() {
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

        //initialize game
        game = new Game(food);


    }//end onCreate


    private void updateUI(){

        //update question
        question.setText(game.getQuestionType() + ": " + game.getQuestionVal());

        //update hints
        String hintTexts[] = new String[3];

        hintTexts[0] = game.getHintTypes()[0] + ": "
                     + ((game.getHintVals()[0] > 0) ? game.getHintVals()[0] + "" : "N/A");
        hints[0].setText(hintTexts[0]);

        hintTexts[1] = game.getHintTypes()[1] + ": "
                + ((game.getHintVals()[1] > 0) ? game.getHintVals()[1] + "" : "N/A");
        hints[1].setText(hintTexts[1]);

        hintTexts[2] = game.getHintTypes()[2] + ": "
                + ((game.getHintVals()[2] > 0) ? game.getHintVals()[2] + "" : "N/A");
        hints[2].setText(hintTexts[2]);
    }



    /*
     * Game
     *
     *  Handles gameplay and the state of the game.
     */
    private class Game {

        int gameState = -1;
        final static int STARTING = 1;
        final static int RUNNING = 2;
        final static int ENDING = 3;

        private ArrayList<GameQuestion> questions;
        private int questionNumber;

        private String questionType;
        private int questionVal;
        private int answerVal;
        String hintTypes[] = new String[3];
        int hintVals[] = new int[3];

        private int score = 0;

        public Game(ArrayList<Food> food) {
            questions = foodToQuestions(food);
            questionNumber = -1;
            gameState = STARTING;
            advance();
            gameState = RUNNING;
        }//end Constructor


        public int getGameState() { return gameState; }

        public int getQuestionNumber() { return questionNumber; }

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

            //Update score and track correct guesses
            boolean correct = ((questionVal < answerVal) && guess)
                    || ((questionVal > answerVal) && !guess);

            if (correct) {
                score += 10; // TODO: time-based bonuses
                questions.get(questionNumber).setAnsweredCorrectly(true);
            } // Note: GameQuestion.answeredCorrectly is set to false by default

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
            if (questionNumber <= questions.size()) {
                gameOver();
            }//end if
            GameQuestion currentQuestion = questions.get(questionNumber);

            //Update state
            hintTypes    = currentQuestion.getHintTypes();
            hintVals     = currentQuestion.getHintVals();
            questionType = currentQuestion.getQuestionType();
            questionVal  = currentQuestion.getQuestionVal();
            answerVal    = currentQuestion.getAnswerVal();

        }//end advance

        /*
         * Determines final score, calculates other statistics, inserts
         * the score into the database.
         */
        void gameOver() {
            gameState = ENDING;
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