package apps.sg.com.WifiProfileCreationTest;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import java.util.List;

import apps.sg.com.wifitest.R;

public class MainActivity extends AppCompatActivity {

    String networkSSID = "5";
    String networkPass = "qwertyuiop";

    int retryCount = 0;
    public static final String LOG_TAG = "WIFI_CREATION";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connect();
            }
        });
    }

    WifiManager wifiManager;

    public WifiConfiguration getWifiConfiguration() {
        WifiConfiguration conf = new WifiConfiguration();
        conf.SSID = "\"" + networkSSID + "\"";
        conf.preSharedKey = "\"" + networkPass + "\"";
        Log.d(LOG_TAG, "Ssid and Pass " + networkSSID + " " + networkPass);
        return conf;
    }

    public void connect() {

        Log.d(LOG_TAG, " Trying to connect");
        registerReceiver();
        WifiConfiguration conf = getWifiConfiguration();

        wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        wifiManager.updateNetwork(conf);
        int netId = wifiManager.addNetwork(conf);
        Log.d(LOG_TAG, "addnetwork " + netId);

        //AddNetwork Error if netID = -1
        if (netId == -1) {
            netId = enableNetwork(networkSSID);
        }
        if (netId != -1) {
            Log.i(LOG_TAG, "Network Creation Successful");
        }
        wifiManager.enableNetwork(netId, true);
        boolean b = wifiManager.reconnect();
        Log.d(LOG_TAG, "connect network " + b);
    }

    /**
     * TODO -This method is used to enable the disbale newtworks - usage later
     */
    private int enableNetwork(String networkSSID) {
        int netId = -1;
        List<WifiConfiguration> list = wifiManager.getConfiguredNetworks();
        for (WifiConfiguration currWifi : list) {
            if (currWifi.SSID != null && currWifi.SSID.equals(networkSSID)) {
                wifiManager.disconnect();
                wifiManager.enableNetwork(currWifi.networkId, true);
                Log.i(LOG_TAG, "enableNetwork - reconnect");
                wifiManager.reconnect();
                netId = currWifi.networkId;
                break;
            }
        }
        return netId;
    }


    private WifiReceiver receiverWifi = null;

    public void registerReceiver() {
        retryCount = 0;
        receiverWifi = new WifiReceiver();
        IntentFilter mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION);
        registerReceiver(receiverWifi, mIntentFilter);
    }

    private void unregister() {
        unregisterReceiver(receiverWifi);
        receiverWifi = null;
    }

    class WifiReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context c, Intent intent) {
            String action = intent.getAction();
            Log.i(LOG_TAG, "Regisster Receiver");
            if (action.equals(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION)) {
                Log.d(LOG_TAG, ">>>>SUPPLICANT_STATE_CHANGED_ACTION<<<<<<");
                SupplicantState supl_state = ((SupplicantState) intent.getParcelableExtra(WifiManager.EXTRA_NEW_STATE));
                switch (supl_state) {
                    case ASSOCIATED:
                        Log.i(LOG_TAG, "ASSOCIATED");
                        break;
                    case ASSOCIATING:
                        Log.i(LOG_TAG, "ASSOCIATING");
                        break;
                    case AUTHENTICATING:
                        Log.i(LOG_TAG, "Authenticating...");
                        break;
                    case COMPLETED:
                        Log.i(LOG_TAG, "Connected");
                        break;
                    case DISCONNECTED:
                        Log.i(LOG_TAG, "Disconnected");
                        break;
                    case DORMANT:
                        Log.i(LOG_TAG, "DORMANT");
                        break;
                    case FOUR_WAY_HANDSHAKE:
                        Log.i(LOG_TAG, "FOUR_WAY_HANDSHAKE");
                        break;
                    case GROUP_HANDSHAKE:
                        Log.i(LOG_TAG, "GROUP_HANDSHAKE");
                        break;
                    case INACTIVE:
                        Log.i(LOG_TAG, "INACTIVE");
                        break;
                    case INTERFACE_DISABLED:
                        Log.i(LOG_TAG, "INTERFACE_DISABLED");
                        break;
                    case INVALID:
                        Log.i(LOG_TAG, "INVALID");
                        break;
                    case SCANNING:
                        Log.i(LOG_TAG, "SCANNING");
                        break;
                    case UNINITIALIZED:
                        Log.i(LOG_TAG, "UNINITIALIZED");
                        break;
                    default:
                        Log.i(LOG_TAG, "Unknown");
                        break;
                }
                int supl_error = intent.getIntExtra(WifiManager.EXTRA_SUPPLICANT_ERROR, -1);
                /**
                 * Authentication Eroor handling.
                 * Password Might be wrong
                 */
                if (supl_error == WifiManager.ERROR_AUTHENTICATING) {
                    Log.i(LOG_TAG, "ERROR_AUTHENTICATING!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                    retryCount++;
                    if (retryCount > 3) {
                        Log.i(LOG_TAG, " retryCount exceeded");
                        wifiManager.disconnect();
                        connect();
                    }
                }
            }
        }
    }
}
