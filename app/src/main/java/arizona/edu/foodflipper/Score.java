package arizona.edu.foodflipper;

/**
 * Created by Josh on 4/9/15.
 */
public class Score {

    int uid = -1;
    int score = -1;
    int datetime = -1;
    String user = "";

    public Score(int score, String user) {

        this.score = score;

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

    public int getDatetime() {
        return datetime;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }
}
