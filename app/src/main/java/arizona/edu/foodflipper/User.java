package arizona.edu.foodflipper;

/**
 * Created by vsmayberry on 4/2/15.
 */
public class User {

    int uid = -1;
    String password = "";
    String user = "";
    String address = "";

    public User(String user, String password, String address) {
        this.user = user;
        this.password = hash(password);
        this.address = address;
    }

    public User(String user) {
        this.user = user;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getUID() {
        return this.uid;
    }

    public String getUser() {
        return this.user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    //TODO implement hashing function for secure password storage
    private String hash(String password) {
        return password + "HASHED";
    }

    //compares plaintext password to hashed password stored by user object / db;
    public boolean checkPassword(String password) {
        return this.password.equals(hash(password));
    }

}
