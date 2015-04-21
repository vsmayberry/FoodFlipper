package arizona.edu.foodflipper;

/**
 * Created by vsmayberry on 4/2/15.
 */
public class User {

    int uid = -1;
    String password = "";
    String user = "";

    public User(String user, String password) {
        this.user = user;
        this.password = hash(password);
    }

    public User(String user) {
        this.user = user;
    }

    public int getUID() {
        return this.uid;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUser() {
        return this.user;
    }

    public String getPassword() {
        return this.password;
    }

    //TODO implement hashing function for seure password storage
    private String hash(String password) {
        return password + "HASHED";
    }

    //compares plaintext password to hashed password stored by user object / db;
    public boolean checkPassword(String password) {
        return this.password.equals(hash(password));
    }

}
