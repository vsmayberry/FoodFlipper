package arizona.edu.foodflipper;

import java.util.Random;

/**
 * Created by Josh on 4/10/15.
 */
public class GameQuestion {

    private Food food;
    private String hintTypes[] = new String[3];
    private int hintVals[] = new int[3];
    private String questionType = "";
    private int questionVal = -1;
    private int answerVal = -1;
    private boolean answeredCorrectly = false;


    public GameQuestion(Food food) {
        this.food = food;

        while(answerVal != -1) {

            int question = new Random().nextInt(3);

            switch (question) {
                case 0:
                    questionType = "Calories";
                    answerVal    = food.getCalories();
                    questionVal  = generateQuestionVal(answerVal);
                    hintTypes[0] = "Carbs";
                    hintVals[0]  = food.getCarbs();
                    hintTypes[1] = "Fat";
                    hintVals[1]  = food.getFat();
                    hintTypes[2] = "Protein";
                    hintVals[2]  = food.getProtein();
                    break;
                case 1:
                    hintTypes[0] = "Calories";
                    hintVals[0]  = food.getCalories();
                    questionType = "Carbs";
                    answerVal    = food.getCarbs();
                    questionVal  = generateQuestionVal(answerVal);
                    hintTypes[1] = "Fat";
                    hintVals[1]  = food.getFat();
                    hintTypes[2] = "Protein";
                    hintVals[2]  = food.getProtein();
                    break;
                case 2:
                    hintTypes[0] = "Calories";
                    hintVals[0]  = food.getCalories();
                    hintTypes[1] = "Carbs";
                    hintVals[1]  = food.getCarbs();
                    questionType = "Fat";
                    answerVal    = food.getFat();
                    questionVal     = generateQuestionVal(answerVal);
                    hintTypes[2] = "Protein";
                    hintVals[2]  = food.getProtein();
                    break;
                case 3:
                    hintTypes[0] = "Calories";
                    hintVals[0]  = food.getCalories();
                    hintTypes[1] = "Carbs";
                    hintVals[1]  = food.getCarbs();
                    hintTypes[2] = "Fat";
                    hintVals[2]  = food.getFat();
                    questionType = "Protein";
                    answerVal    = food.getProtein();
                    questionVal     = generateQuestionVal(answerVal);
                    break;
            }//end switch

        }//end while

    }//end constructor

    //getters
    public Food getFood() { return food; }

    public String[] getHintTypes() { return hintTypes; }

    public int[] getHintVals() { return hintVals; }

    public String getQuestionType() { return questionType; }

    public int getQuestionVal() { return questionVal; }

    public int getAnswerVal() { return answerVal; }

    public boolean isAnsweredCorrectly() { return answeredCorrectly; }

    public void setAnsweredCorrectly(boolean answeredCorrectly) {
        this.answeredCorrectly = answeredCorrectly;
    }

    //Generates a random, yet reasonable question value, that != the answer
    private int generateQuestionVal(int answer){
        // 0.5*Answer <= Question <= 1.5*answer
        int question;
        do{
           question = new Random().nextInt(answer) + (answer / 2);
        }while(question != answer);
        return question;
    }//end generateQuestionVal
}
