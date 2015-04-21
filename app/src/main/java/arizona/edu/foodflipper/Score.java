package arizona.edu.foodflipper;

/**
 * Created by Josh on 4/9/15.
 */
public class Score {

    int uid = -1;
    int score = -1;
    int datetime = -1;
    String user = "";

    public Score(int score, String email) {
        this.uid = uid;
        this.score = score;
        this.datetime = datetime;
        this.user = user;
    }

    public String toString() {
        return user + " : " + score;
    }

    public int getID() {
        return uid;
    }


    public int getScore() {
        return score;
    }

    public void setEmail(String user) {
        this.user = user;
    }

    public int getDatetime() {
        return datetime;
    }

    public String getUser() {
        return user;
    }
}
