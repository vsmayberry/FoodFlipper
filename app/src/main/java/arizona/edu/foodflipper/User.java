package arizona.edu.foodflipper;

/**
 * Created by vsmayberry on 4/2/15.
 */
public class User {

    String password = "";
    String email = "";

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return this.email;
    }

    public String getPassword() {
        return this.password;
    }
}
