package tech.rahulsriram.care;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.EditText;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class LoginActivity extends AppCompatActivity {
    String TAG = "care-logger";
    EditText phoneNumber, countryCode;
    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        sp = getSharedPreferences("Care", MODE_PRIVATE);
        countryCode = (EditText) findViewById(R.id.countryCode);
        phoneNumber = (EditText) findViewById(R.id.mobileNumber);
        if (phoneNumber.requestFocus()) {
            this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
        String deviceId = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("id", deviceId);
        editor.apply();
    }
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.next, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        String code = countryCode.getText().toString();

        if (phoneNumber.hasFocus()) {
            phoneNumber.clearFocus();
        }

        if (countryCode.hasFocus()) {
            countryCode.clearFocus();
        }

        if (code.isEmpty()) {
            code = getString(R.string.default_country_code);
        }

        String number = code + phoneNumber.getText().toString();
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("number", number);
        editor.apply();

        if (number.length() >= 12) {
            new RequestSms(LoginActivity.this).execute();
        } else {
            Snackbar.make(findViewById(R.id.LoginLayout), "Please type your mobile number", Snackbar.LENGTH_LONG).show();
        }
        return false;
    }

    class RequestSms extends AsyncTask<Void, Void, String> {
        Context context;
        ProgressDialog dialog = null;

        RequestSms(Context c) {
            context = c;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(context);
            dialog.setMessage("Waiting for verification sms");
            dialog.show();
            dialog.setCanceledOnTouchOutside(false);
        }

        @Override
        protected String doInBackground(Void... params) {
            StringBuilder sb = new StringBuilder();

            try {
                String link = "http://" + getString(R.string.website) + "/request_sms";
                String data = "number=" + URLEncoder.encode(sp.getString("number", ""), "UTF-8");
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
                    Log.i(TAG, line);
                }
                httpURLConnection.disconnect();
            } catch (Exception e) {

            }

            return sb.toString();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            if (result.equals("ok")) {
                startActivity(new Intent(LoginActivity.this, SmsVerificationActivity.class));
                finish();
            } else {
                Snackbar.make(findViewById(R.id.LoginLayout), "Please try again", Snackbar.LENGTH_LONG).show();
            }

            dialog.dismiss();
        }
    }
}