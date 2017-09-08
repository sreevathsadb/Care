package tech.rahulsriram.care;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

public class ItemSelectionActivity  extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener {
    private ArrayList<String> selection = new ArrayList<>();
    private String description, finalDonate;
    private EditText itemDescription;
    private SharedPreferences sp;
    private ProgressDialog progressDialog;
    private AlertDialog.Builder alertDialogBuilder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_selection);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        sp = getSharedPreferences("Care", MODE_PRIVATE);

        itemDescription = (EditText) findViewById(R.id.itemDescription);

        progressDialog = new ProgressDialog(this);

        alertDialogBuilder = new AlertDialog.Builder(this);

        Switch homeFoodSwitch = (Switch) findViewById(R.id.homeFoodSwitch);
        homeFoodSwitch.setOnCheckedChangeListener(this);

        Switch packedFoodSwitch = (Switch) findViewById(R.id.packedFoodSwitch);
        packedFoodSwitch.setOnCheckedChangeListener(this);

        Switch clothesSwitch = (Switch) findViewById(R.id.clothesSwitch);
        clothesSwitch.setOnCheckedChangeListener(this);

        Switch bookSwitch = (Switch) findViewById(R.id.bookSwitch);
        bookSwitch.setOnCheckedChangeListener(this);

        final FloatingActionButton donate = (FloatingActionButton) findViewById(R.id.donateButton);
        donate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finalDonate = "";
                for (String selections : selection) {
                    finalDonate += selections;
                }

                description = itemDescription.getText().toString();
                itemDescription.clearFocus();

                if (!description.isEmpty()) {
                    if (!finalDonate.isEmpty()) {
                        if (!sp.getString("location", "").isEmpty()) {
                            new DonateTask().execute();
                        } else {
                            Snackbar.make(v, "Couldn't fetch your location. Please check if your GPS is on", Snackbar.LENGTH_LONG).show();
                        }
                    } else {
                        Snackbar.make(v, "Please select at least one type", Snackbar.LENGTH_LONG).show();
                    }
                } else {
                    Snackbar.make(v, "Please enter description", Snackbar.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public void onCheckedChanged(CompoundButton switchText, boolean isChecked) {
        if(isChecked) {
            switch (switchText.getText().toString()) {
                case "Home Made Food":
                    selection.add("0");
                    break;

                case "Packed Food":
                    selection.add("1");
                    break;

                case "Clothes":
                    selection.add("2");
                    break;

                case "Books":
                    selection.add("3");
                    break;
            }
        } else {
            switch (switchText.getText().toString()) {
                case "Home Made Food":
                    selection.remove("0");
                    break;

                case "Packed Food":
                    selection.remove("1");
                    break;

                case "Clothes":
                    selection.remove("2");
                    break;

                case "Books":
                    selection.remove("3");
                    break;
            }
        }
    }

    class DonateTask extends AsyncTask<Void, Void, String> {
        @Override

        protected void onPreExecute(){
            progressDialog.setMessage("Requesting");
            progressDialog.show();
            alertDialogBuilder.show();
        }

        protected String doInBackground(Void... params) {
            StringBuilder sb = new StringBuilder();

            try {
                String link = "http://" + getString(R.string.website) + "/donate";
                String data = "id=" + URLEncoder.encode(sp.getString("id", ""), "UTF-8") + "&number=" + URLEncoder.encode(sp.getString("number", ""), "UTF-8") + "&location=" + URLEncoder.encode(sp.getString("location", ""), "UTF-8") + "&items=" + URLEncoder.encode(finalDonate, "UTF-8") + "&description=" + URLEncoder.encode(description, "UTF-8");
                URL url = new URL(link);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
                writer.write(data);
                writer.flush();
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String line;

                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }

            } catch (Exception e) {
                return "error";
            }

            return sb.toString();
        }

        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            progressDialog.dismiss();
            if(result.equals("ok")) {
                alertDialogBuilder.setMessage("Item Added Thanks to you");
                alertDialogBuilder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                alertDialogBuilder.show();
//                Snackbar.make(findViewById(R.id.ItemSelectionLayout), "Done", Snackbar.LENGTH_LONG).show();
            } else {
                alertDialogBuilder.setMessage("Connection Problem");
                alertDialogBuilder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                alertDialogBuilder.show();
//                Snackbar.make(findViewById(R.id.ItemSelectionLayout), "Please try again", Snackbar.LENGTH_LONG).show();
            }
        }
    }
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.onsetting, menu);
        return true;
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case android.R.id.home:
                startActivity(new Intent(ItemSelectionActivity.this,AllNotifications.class));
                finish();
                break;
        }
        return false;
    }
    public void onBackPressed(){
        startActivity(new Intent(ItemSelectionActivity.this,AllNotifications.class));
        finish();
        super.onBackPressed();
    }
}
