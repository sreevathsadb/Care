package tech.rahulsriram.care;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
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

public class NameInput extends AppCompatActivity {
    String TAG = "care-logger";
    EditText username;
    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_name_input);

        sp = getSharedPreferences("Care", MODE_PRIVATE);

        username = (EditText) findViewById(R.id.username);
        if (username.requestFocus()) {
            this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.next,menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        String name = username.getText().toString();

        if (username.hasFocus()) {
            username.clearFocus();
        }

        if (!name.isEmpty()) {
            SharedPreferences.Editor editor = sp.edit();
            editor.putString("name", name);
            editor.apply();
            new SetNameTask(NameInput.this).execute();
        }

        return false;
    }

    class SetNameTask extends AsyncTask<String,Void,String> {
        Context context;
        ProgressDialog dialog = null;

        SetNameTask(Context c) {
            context = c;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog=new ProgressDialog(context);
            dialog.setMessage("Setting up account");
            dialog.show();
            dialog.setCanceledOnTouchOutside(false);
        }

        @Override
        protected String doInBackground(String ...var) {
            StringBuilder sb = new StringBuilder();

            try {
                String link = "http://" + getString(R.string.website) + "/set_name";
                String data = "id=" + URLEncoder.encode(sp.getString("id", ""), "UTF-8") + "&number=" + URLEncoder.encode(sp.getString("number", ""), "UTF-8") + "&name=" + URLEncoder.encode(sp.getString("name", ""), "UTF-8");
                URL url= new URL(link);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                OutputStreamWriter writer =new OutputStreamWriter(httpURLConnection.getOutputStream());
                writer.write(data);
                writer.flush();
                BufferedReader reader=new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
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
                startActivity(new Intent(NameInput.this, AllNotifications.class));
                startService(new Intent(NameInput.this, CareService.class));
                finish();
            } else {
                Snackbar.make(findViewById(R.id.NameInputLayout), "Couldn't set name. Try again", Snackbar.LENGTH_LONG).show();
            }

            dialog.dismiss();
        }
    }
}
