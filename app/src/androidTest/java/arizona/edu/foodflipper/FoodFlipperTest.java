package arizona.edu.foodflipper;

import android.app.Application;
import android.test.ApplicationTestCase;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class FoodFlipperTest extends ApplicationTestCase<Application> {
    public FoodFlipperTest() {
        super(Application.class);
    }

    public void TestAreRunning() {
        assertEquals(true, true);
    }
}