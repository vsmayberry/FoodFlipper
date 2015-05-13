package arizona.edu.foodflipper;

/**
 * Created by vsmayberry on 4/2/15.
 */
public class User {

    int uid = -1;
    String email = "";
    String password = "";
    String address = "";

    public User(String email, String password, String address) {
        this.email = email;
        this.password = hash(password);
        this.address = address;
    }

    public User(int uid, String email, String password, String address) {
        this.uid = uid;
        this.email = email;
        this.password = hash(password);
        this.address = address;
    }

    public User(String email) {
        this.email = email;
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

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    //compares plaintext password to hashed password stored by email object / db;
    public boolean checkPassword(String password) {
        return this.password.equals(hash(password));
    }

}
