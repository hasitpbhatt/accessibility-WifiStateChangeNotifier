package tk.hasitpbhatt.wifistatechangenotifier;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.accessibility.AccessibilityManager;
import android.widget.Toast;

/**
 * Created by finch on 16/5/15.
 */
public class StateChangeReceiver extends BroadcastReceiver{

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent == null || intent.getAction() == null)
            return;
        AccessibilityManager am = (AccessibilityManager) context.getSystemService(Context.ACCESSIBILITY_SERVICE);
        // Don't keep on posting notifications and tickers if accessibility is not on
        if(!am.isEnabled())
            return;
        if(intent.getAction().equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)){
            NetworkInfo info = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
            Log.d(getClass().getCanonicalName(), "Network State Change Received");
            if(info != null) {
                boolean connected = false;
                connected = info.isConnected();
                SharedPreferences sharedPreferences = context.getSharedPreferences(Utils.SHARED_PREF_NAME,Context.MODE_PRIVATE);
                StringBuilder s = new StringBuilder();
                if(!connected) {
                    if(!sharedPreferences.getBoolean(Utils.CHECK_CONNECTION_PREF,false)) {
                        s.append(context.getString(R.string.wifi_connection_lost));
                        Notification notification = new Notification.Builder(context)
                                .setContentTitle(context.getString(R.string.wifi_state_change))
                                .setContentText(s.toString())
                                .setSmallIcon(R.drawable.ic_launcher)
                                .setTicker(s.toString())
                                .setAutoCancel(true)
                                .build();
                        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                        nm.notify(Utils.NOTIFICATION_ID,notification);
                        sharedPreferences.edit().putBoolean("isConnected",false).apply();
                    }
                    return;
                }
                WifiManager wm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                s.append(context.getString(R.string.wifi_connection_established));
                if(wm != null && wm.getConnectionInfo() != null) {
                    s.append(" to " + wm.getConnectionInfo().getSSID());
                    Notification notification = new Notification.Builder(context)
                            .setContentTitle("Wifi state change")
                            .setContentText(s.toString())
                            .setSmallIcon(R.drawable.ic_launcher)
                            .setTicker(s.toString())
                            .setAutoCancel(true)
                            .build();
                    NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                    nm.notify(Utils.NOTIFICATION_ID,notification);
                    sharedPreferences.edit().putBoolean("isConnected",true).apply();
                }

            }
        }
    }
}
