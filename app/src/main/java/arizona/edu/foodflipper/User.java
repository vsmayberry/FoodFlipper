package arizona.edu.foodflipper;

/**
 * Created by vsmayberry on 4/2/15.
 */
public class User {

    int uid = -1;
    String password = "";
    String email = "";

    public User(int uid, String email, String password) {
        this.uid = uid;
        this. email = email;
        this.password = hash(password);
    }

    public User(int uid, String email) {
        this.uid = uid;
        this. email = email;
    }

    public int getUID() { return this.uid; }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() { return this.email; }

    public String getPassword() {
        return this.password;
    }

    //TODO implement hashing function for seure password storage
    private String hash(String password){ return password + "HASHED";}

    //compares plaintext password to hashed password stored by user object / db;
    public boolean checkPassword(String password){ return this.password.equals(hash(password));}

}
