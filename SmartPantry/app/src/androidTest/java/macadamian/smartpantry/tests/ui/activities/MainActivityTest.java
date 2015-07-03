package macadamian.smartpantry.tests.ui.activities;

import android.app.Activity;
import android.app.Instrumentation;
import android.support.v7.widget.RecyclerView;
import android.test.ActivityInstrumentationTestCase2;
import android.test.UiThreadTest;
import android.view.KeyEvent;

import com.macadamian.smartpantry.R;
import com.macadamian.smartpantry.ui.activities.AboutActivity;
import com.macadamian.smartpantry.ui.activities.InsertItemActivity;
import com.macadamian.smartpantry.ui.activities.MainActivity;
import com.melnykov.fab.FloatingActionButton;

import junit.framework.Assert;

public class MainActivityTest extends ActivityInstrumentationTestCase2<MainActivity> {

    private RecyclerView mRecyclerView;
    private FloatingActionButton mAddItemButton;

    public MainActivityTest() {
        super(MainActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mRecyclerView = (RecyclerView)getActivity().findViewById(R.id.pantry_list_view);
        mAddItemButton = (FloatingActionButton) getActivity().findViewById(R.id.add_item_button);
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    @UiThreadTest
    public void testUIComponentsNotNullAndVisibility() {
        Assert.assertNotNull("ERROR : RecyclerView should not be null", mRecyclerView);
        Assert.assertNotNull("ERROR : RecyclerView's adapter should not be null", mRecyclerView.getAdapter());
        Assert.assertNotNull("ERROR : RecyclerView's layout manager should not be null", mRecyclerView.getLayoutManager());
        Assert.assertNotNull("ERROR : AddItemButtom should not be null", mAddItemButton);
    }

    public void testManualAddButtonClick() {
        final Instrumentation.ActivityMonitor monitor = getInstrumentation().addMonitor(InsertItemActivity.class.getName(), null, false);
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mAddItemButton.performClick();
            }
        });
        final Activity currentActivity = getInstrumentation().waitForMonitor(monitor);
        getInstrumentation().removeMonitor(monitor);
        Assert.assertTrue("ERROR : Current activity should be InsertActivity", currentActivity instanceof InsertItemActivity);
        currentActivity.finish();
    }
}
