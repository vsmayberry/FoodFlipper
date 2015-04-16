package arizona.edu.foodflipper;

/**
 * Created by Josh on 4/9/15.
 */
public class Score {

    int uid = -1;
    int score = -1;
    int datetime = -1;
    String email = "";

    public Score(int uid, int score, int datetime, String email) {
        this.uid = uid;
        this.score = score;
        this.datetime = datetime;
        this.email = email;
    }

    public int getID() {
        return uid;
    }

    public int getScore() {
        return score;
    }

    public int getDatetime() {
        return datetime;
    }

    public String getEmail() {
        return email;
    }
}
