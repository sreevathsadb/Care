package tech.rahulsriram.care;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class SmsVerificationActivity extends AppCompatActivity {
    String TAG = "care-logger";
    SharedPreferences sp;
    EditText smsCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms_verification);

        sp = getSharedPreferences("Care", MODE_PRIVATE);
        smsCode = (EditText) findViewById(R.id.sms_code);
        if (smsCode.requestFocus()) {
            this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }

        Button resendButton = (Button) findViewById(R.id.resend_code_button);
        resendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String code = smsCode.getText().toString();

                if (smsCode.hasFocus()) {
                    smsCode.clearFocus();
                }

                if (!code.isEmpty()) {
                    new Registration(SmsVerificationActivity.this, smsCode.getText().toString()).execute();
                }

            }
        });
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater=getMenuInflater();
        menuInflater.inflate(R.menu.next, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        String code = smsCode.getText().toString();

        if (smsCode.hasFocus()) {
            smsCode.clearFocus();
        }

        if (!code.isEmpty()) {
            new Registration(SmsVerificationActivity.this, smsCode.getText().toString()).execute();
        }

        return false;
    }

    class Registration extends AsyncTask<String,Void,String> {
        Context context;
        String code;
        ProgressDialog dialog = null;

        public Registration(Context ctxt, String cde) {
            context = ctxt;
            code = cde;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(context);
            Toast.makeText(context, code, Toast.LENGTH_SHORT).show();
            dialog.setMessage("Verifying sms code");
            dialog.show();
            dialog.setCanceledOnTouchOutside(false);
        }

        @Override
        protected String doInBackground(String ...var) {
            StringBuilder sb = new StringBuilder();

            try {
                String link = "http://" + getString(R.string.website) + "/register";
                String data = "id=" + URLEncoder.encode(sp.getString("id", ""), "UTF-8") + "&number=" + URLEncoder.encode(sp.getString("number", ""), "UTF-8") + "&code=" + URLEncoder.encode(code, "UTF-8");
                URL url = new URL(link);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                OutputStreamWriter writer = new OutputStreamWriter(httpURLConnection.getOutputStream());
                writer.write(data);
                writer.flush();
                BufferedReader reader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                String line;
                while((line = reader.readLine()) != null) {
                    sb.append(line);
                }

                httpURLConnection.disconnect();
            } catch (Exception e) {
                return "error";
            }

            return sb.toString();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            if (result.equals("ok")) {
                startActivity(new Intent(SmsVerificationActivity.this, NameInput.class));
                finish();
            } else {
                Snackbar.make(findViewById(R.id.SmsVerificationLayout), "Please try again", Snackbar.LENGTH_LONG).show();
            }

            dialog.dismiss();
        }
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
                URL url= new URL(link);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                OutputStreamWriter writer = new OutputStreamWriter(httpURLConnection.getOutputStream());
                writer.write(data);
                writer.flush();
                BufferedReader reader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                String line;
                while((line = reader.readLine()) != null) {
                    sb.append(line);
                }
                httpURLConnection.disconnect();
            } catch (Exception e) {
                return "error";
            }

            return sb.toString();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            if (result.equals("error")) {
                Snackbar.make(findViewById(R.id.SmsVerificationLayout), "Please try again", Snackbar.LENGTH_LONG).show();
            }

            dialog.dismiss();
        }
    }
}
