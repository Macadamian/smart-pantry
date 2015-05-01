package macadamian.smartpantry.tests;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.test.InstrumentationTestCase;


public class InstrumentationTests extends InstrumentationTestCase {

    //TODO test changes in orientation
    //TODO test changes in data/wifi connectivity

    public void testNoNetworkConnection() throws Exception {
        try{
            setWifi(false);

            //Set up and check conditions

            setWifi(true);
        } catch(SecurityException e){
            //Uncomment once network is being used & permissions have been added
            //Assert.fail("SecurityException. Network access permissions may be missing.");
        }
    }

    protected void setWifi(boolean enabled) throws Exception {
        WifiManager wifiManager = (WifiManager) getInstrumentation()
                .getTargetContext().getSystemService(Context.WIFI_SERVICE);
        wifiManager.setWifiEnabled(enabled);
    }
}
