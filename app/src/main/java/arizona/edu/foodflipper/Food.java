package arizona.edu.foodflipper;

import android.graphics.Bitmap;

/**
 * Created by Josh on 4/9/15.
 */
public class Food {

    int fid      = -1;
    int uid      = -1;
    String name  = "";
    int calories = -1;
    int carbs    = -1;
    int fat      = -1;
    int protein  = -1;
    Bitmap image;

    public Food(int uid, String name, int calories, int carbs, int fat, int protein) {
        this.uid      = uid;
        this.name     = name;
        this.calories = calories;
        this.carbs    = carbs;
        this.fat      = fat;
        this.protein  = protein;
    }//end constructor for creating new food in the Food Entry Form

    public Food(int fid, int uid, String name, int calories, int carbs, int fat, int protein) {

        this.fid      = fid;
        this.uid      = uid;
        this.name     = name;
        this.calories = calories;
        this.carbs    = carbs;
        this.fat      = fat;
        this.protein  = protein;
    }//end constructor for receiving food from the Database

    public int getFID() { return this.fid; }

    public int getUID() { return this.uid; }

    public String getName() { return this.name; }

    public Bitmap getImage() { return image; }

    public int getCalories() { return this.calories; }

    public int getCarbs() { return this.carbs; }

    public int getFat() { return this.fat; }

    public int getProtein() { return this.protein; }

    public void setName(String name) { this.name = name; }

    public void setCalories(int calories) { this.calories = calories; }

    public void setCarbs(int carbs) { this.carbs = carbs; }

    public void setFat(int fat) { this.fat = fat; }

    public void setProtein(int protein) { this.protein = protein; }

    public void setImage(Bitmap image) { this.image = image; }

}//end Food