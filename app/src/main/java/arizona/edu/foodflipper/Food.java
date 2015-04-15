package arizona.edu.foodflipper;

/**
 * Created by Josh on 4/9/15.
 */
public class Food {

    private int fid      = -1;
    private int uid      = -1;
    private String name  = "";
    private int calories = -1;
    private int carbs    = -1;
    private int fat      = -1;
    private int protein  = -1;
    //TODO: Food image storage + getter / setter


    public Food(int fid, int uid, String name, int calories, int carbs, int fat, int protein ) {
        this.fid      = fid;
        this.uid      = uid;
        this.name     = name;
        this.calories = calories;
        this.carbs    = carbs;
        this.fat      = fat;
        this.protein  = protein;
    }

    public int getFID() { return this.fid; }

    public int getUID() { return this.uid; }

    public String getName() { return this.name; }

    public int getCalories() { return this.calories; }

    public int getCarbs() { return this.carbs; }

    public int getFat() { return this.fat; }

    public int getProtein() { return this.protein; }

    public void setName(String name) { this.name = name; }

    public void setCalories(int calories) { this.calories = calories; }

    public void setCarbs(int carbs) { this.carbs = carbs; }

    public void setFat(int fat) { this.fat = fat; }

    public void setProtein(int protein) { this.protein = protein; }
}
