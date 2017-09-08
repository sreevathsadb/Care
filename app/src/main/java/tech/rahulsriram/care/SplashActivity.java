package tech.rahulsriram.care;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class SplashActivity extends AppCompatActivity{
    String TAG = "care-logger";
    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        sp = getSharedPreferences("Care", MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        if(sp.getInt("radius", -1) == -1) {
            editor.putInt("radius", 10);
        }

        if(sp.getInt("update_interval", -1) == -1) {
            editor.putInt("update_interval", 15);
        }

        if(!sp.getBoolean("volunteer", false)) {
            editor.putBoolean("volunteer", false);
        }

        editor.apply();
        new SplashTask().execute();
    }

    class SplashTask extends AsyncTask<String,Void,String> {
        @Override
        protected String doInBackground(String ...var) {
            if (!sp.getString("id", "").isEmpty() && !sp.getString("number", "").isEmpty() && !sp.getString("name", "").isEmpty()) {
                StringBuilder sb = new StringBuilder();

                try {
                    String link = "http://" + getString(R.string.website) + "/login";
                    String data = "id=" + URLEncoder.encode(sp.getString("id", ""), "UTF-8") + "&number=" + URLEncoder.encode(sp.getString("number", ""), "UTF-8");
                    URL url = new URL(link);
                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setRequestMethod("POST");
                    OutputStreamWriter writer = new OutputStreamWriter(httpURLConnection.getOutputStream());
                    writer.write(data);
                    writer.flush();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                    String line;

                    while ((line = reader.readLine()) != null) {
                        sb.append(line);
                    }
                    httpURLConnection.disconnect();
                } catch (Exception e) {
                    return "error";
                }

                return sb.toString();
            } else {
                return "error";
            }
        }
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            if (result.equals("ok")) {
                startActivity(new Intent(SplashActivity.this, AllNotifications.class));
                finish();
            } else {
                startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                finish();
            }
        }
    }
}
