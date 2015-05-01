package macadamian.smartpantry.tests;

import android.app.Application;
import android.test.ApplicationTestCase;

/**
 * Setup a context for the application to run in, and then call createApplication()
 */
public class ApplicationTest extends ApplicationTestCase<Application> {
    public ApplicationTest() {
        super(Application.class);
    }

    /**
     * Instantiate the application under test
     */
    protected void setUp() throws Exception{ super.setUp();}

    //Methods to test the application running the desired context:


}