package tech.rahulsriram.care;

/**
 * Created by Jebin on 23-07-2016.
 */

import android.app.ActivityManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class ConnectivityChangeReceiver extends BroadcastReceiver {
    private static final String TAG = "care-logger";
    public SharedPreferences sp;
    public String a = "s", b = "no", check = null;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "receiver");
        sp = context.getSharedPreferences("Care", context.MODE_PRIVATE);
        checkconnectivity(context);
    }
    public void checkconnectivity(Context context) {
        try {
            Intent intent = new Intent(context.getApplicationContext(), CareService.class);
            ConnectivityManager cm = (ConnectivityManager) context.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo[] info = cm.getAllNetworkInfo();
            for (int i = 0; i < info.length; i++) {
                if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                    context.startService(intent);
                    if(sp.getBoolean("volunteer",false)) {
                        new isDonationsAvailable(context).execute();
                    }
                    else{
                    }
                    Log.i(TAG,"call async");
                    break;
                } else if (isMyServiceRunning(CareService.class, context, intent)) {
                    context.stopService(intent);
                    Log.i(TAG,"closeservice");
                }
                Log.i(TAG,"noservice");
            }
        } catch (Exception e) {
        }
    }
    public boolean isMyServiceRunning(Class ClassName, Context context, Intent intent) {
        ActivityManager manager = (ActivityManager) context.getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (ClassName.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    class isDonationsAvailable extends AsyncTask<String, String, String> {

        Context context;

        isDonationsAvailable(Context c) {
            context = c;
        }

        protected void onPreExecute() {
        }

        @Override
        protected String doInBackground(String... arg0) {
            StringBuilder sb = new StringBuilder();
            String link = "http://" + context.getString(R.string.website) + "/recent_history", line1, b1;
            try {
                String data = "id=" + URLEncoder.encode(sp.getString("id", ""), "UTF-8") + "&number=" + URLEncoder.encode(sp.getString("number", ""), "UTF-8") + "&location=" + URLEncoder.encode(sp.getString("location", ""), "UTF-8") + "&radius=" + URLEncoder.encode(String.valueOf(sp.getInt("radius", 10)), "UTF-8") + "&status=" + URLEncoder.encode("open", "UTF-8");
                URL url = new URL(link);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
                writer.write(data);
                writer.flush();
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                if (((line1 = reader.readLine()) != null)&&(!line1.equals("ok"))) {
                    b1 = "yes";
                } else {
                    b1 = "no";
                }
                conn.disconnect();
            } catch (Exception e) {
                return "error";
            }
            return b1;
        }

        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            b = result;

            if(b=="yes") {
                Intent intent = new Intent(context.getApplicationContext(), CareService.class);
                Intent intentI = new Intent(context.getApplicationContext(), AllNotifications.class);
                NotificationManager mNotificationManager = (NotificationManager) context.getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
                PendingIntent pi = PendingIntent.getActivity(context.getApplicationContext(), 0, intentI, 0);
                NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context.getApplicationContext());
                mBuilder.setSmallIcon(R.mipmap.rrrrr);
                mBuilder.setContentTitle("Notification");
                if (!sp.getBoolean("AllNotifications",false)) {
                    Log.i("jebin","fdgkjfkgj");
                    //if (!("nil".equals(a = checkconnectivity(context)))) {
                    if (isMyServiceRunning(CareService.class, context, intent)) {
                        mBuilder.setContentText("Donations Available");
                        mBuilder.setContentIntent(pi);
                        mNotificationManager.notify(324255, mBuilder.build());
                        //TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(context);
                        //taskStackBuilder.addParentStack(AllNotifications.class);
                        //taskStackBuilder.addNextIntent(intentI);
                    } else {
                        context.startService(intent);
                        mBuilder.setContentText("Donations Available");
                        mBuilder.setContentIntent(pi);
                        mNotificationManager.notify(324255, mBuilder.build());
                        //TaskStackBuilder taskStackBuilder=TaskStackBuilder.create(context);
                    }
                }
            }
        }
    }
}
