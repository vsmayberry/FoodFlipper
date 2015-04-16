package arizona.edu.foodflipper;

import android.graphics.Bitmap;

/**
 * Created by Josh on 4/9/15.
 */
public class Food {

    int fid = -1;
    int uid = -1;
    String name = "";
    int calories = -1;
    int carbs = -1;
    int fat = -1;
    int protein = -1;
    Bitmap image;
    //TODO: Food image storage + getter / setter


    public Food(String name, int calories, int carbs, int fat, int protein) {
        this.name = name;
        this.calories = calories;
        this.carbs = carbs;
        this.fat = fat;
        this.protein = protein;
    }

    public int getFID() {
        return this.fid;
    }

    public int getUID() {
        return this.uid;
    }

    public String getName() {
        return this.name;
    }

    public int getCalories() {
        return this.calories;
    }

    public int getCarbs() {
        return this.carbs;
    }

    public int getFat() {
        return this.fat;
    }

    public int getProtein() {
        return this.protein;
    }

    public Bitmap getImage() {
        return this.image;
    }

    public void setFID(int fid) {
        this.fid = fid;
    }

    public void setUID(int uid) {
        this.uid = uid;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCalories(int calories) {
        this.calories = calories;
    }

    public void setCarbs(int carbs) {
        this.carbs = carbs;
    }

    public void setFat(int fat) {
        this.fat = fat;
    }

    public void setProtein(int protein) {
        this.protein = protein;
    }

    public void setImage(Bitmap bitmap) {
        this.image = bitmap;
    }
}
