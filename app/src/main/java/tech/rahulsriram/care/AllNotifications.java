package tech.rahulsriram.care;

import android.app.Activity;
import android.app.Dialog;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TabHost;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

public class AllNotifications extends AppCompatActivity implements GestureDetector.OnGestureListener, View.OnClickListener {
    public static View.OnClickListener myOnClickListener;

    float lastX;

    static RecyclerView.Adapter adapter1;
    RecyclerView.LayoutManager layoutManager1;
    static RecyclerView recyclerView1;
    static ArrayList<DataModel> data1;

    static RecyclerView.Adapter adapter2;
    RecyclerView.LayoutManager layoutManager2;
    static RecyclerView recyclerView2;
    static ArrayList<DataModel> data2;

    static RecyclerView.Adapter adapter3;
    RecyclerView.LayoutManager layoutManager3;
    static RecyclerView recyclerView3;
    static ArrayList<DataModel> data3;


    ArrayList<String> name1 = new ArrayList<>();
    ArrayList<String> description1 = new ArrayList<>();
    ArrayList<String> item1 = new ArrayList<>();
    ArrayList<String> number1 = new ArrayList<>();
    ArrayList<String> latitude1 = new ArrayList<>();
    ArrayList<String> longitude1 = new ArrayList<>();
    ArrayList<String> itemid1 = new ArrayList<>();

    ArrayList<String> name2 = new ArrayList<>();
    ArrayList<String> description2 = new ArrayList<>();
    ArrayList<String> item2 = new ArrayList<>();
    ArrayList<String> number2 = new ArrayList<>();
    ArrayList<String> latitude2 = new ArrayList<>();
    ArrayList<String> longitude2 = new ArrayList<>();
    ArrayList<String> itemid2 = new ArrayList<>();

    ArrayList<String> name3 = new ArrayList<>();
    ArrayList<String> description3 = new ArrayList<>();
    ArrayList<String> item3 = new ArrayList<>();
    ArrayList<String> number3 = new ArrayList<>();
    ArrayList<String> latitude3 = new ArrayList<>();
    ArrayList<String> longitude3 = new ArrayList<>();
    ArrayList<String> itemid3 = new ArrayList<>();

    TabHost tabHost;
    GestureDetector gestureDetector;

    SwipeRefreshLayout swipeRefreshLayout;

    SharedPreferences sp;

    String la,lo;

    String secretnumber3;

    ProgressDialog progressDialog3;

    Dialog dialog3;

    AlertDialog.Builder alertDialogBuilder3;

    EditText editText3;

    Button button3;

    public static String gettabname;

    NotificationManager mNotificationManager;

    public static Activity allNotifications;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tabview);

        View someView = findViewById(R.id.TabView);
        View root = someView.getRootView();

        sp = getSharedPreferences("Care", MODE_PRIVATE);

        allNotifications=this;
        SharedPreferences.Editor edit=sp.edit();
        edit.putBoolean("AllNotifications",true);
        edit.apply();

        mNotificationManager = (NotificationManager) this.getApplicationContext().getSystemService(this.NOTIFICATION_SERVICE);
        mNotificationManager.cancel(324255);

        gestureDetector = new GestureDetector(this);

        swipeRefreshLayout=(SwipeRefreshLayout)findViewById(R.id.swipe_refresh_layout);

        recyclerView1 = (RecyclerView) findViewById(R.id.my_recycler_view1);
        recyclerView1.setHasFixedSize(true);
        layoutManager1 = new LinearLayoutManager(this);
        recyclerView1.setLayoutManager(layoutManager1);
        recyclerView1.setItemAnimator(new DefaultItemAnimator());

        recyclerView2 = (RecyclerView) findViewById(R.id.my_recycler_view2);
        recyclerView2.setHasFixedSize(true);
        layoutManager2 = new LinearLayoutManager(this);
        recyclerView2.setLayoutManager(layoutManager2);
        recyclerView2.setItemAnimator(new DefaultItemAnimator());

        recyclerView3 = (RecyclerView) findViewById(R.id.my_recycler_view3);
        recyclerView3.setHasFixedSize(true);
        layoutManager3 = new LinearLayoutManager(this);
        recyclerView3.setLayoutManager(layoutManager3);
        recyclerView3.setItemAnimator(new DefaultItemAnimator());

        myOnClickListener = new MyOnClickListener();

        progressDialog3 = new ProgressDialog(this);
        progressDialog3.setMessage("Verifying");
        progressDialog3.setCanceledOnTouchOutside(false);

        dialog3 = new Dialog(this);
        dialog3.setContentView(R.layout.dialogbox);
        dialog3.setTitle("Verify...");
        button3=(Button)dialog3.findViewById(R.id.dialogbutton);
        editText3=(EditText)dialog3.findViewById(R.id.dialogedittext);

        alertDialogBuilder3 = new AlertDialog.Builder(this);

        tabHost = (TabHost) findViewById(R.id.tabHost);
        tabHost.setup();

        TabHost.TabSpec tabSpec;

        if(sp.getBoolean("volunteer",false)) {
            root.setBackgroundColor(getResources().getColor(R.color.White));
            tabSpec = tabHost.newTabSpec("notification");
            tabSpec.setContent(R.id.linearLayout1);
//            tabSpec.setIndicator("Open Donations");
            tabSpec.setIndicator("", getResources().getDrawable(R.drawable.opendonations));
            tabHost.addTab(tabSpec);

            tabSpec = tabHost.newTabSpec("accepteddonations");
            tabSpec.setContent(R.id.linearLayout2);
//            tabSpec.setIndicator("Accepted Donations");
            tabSpec.setIndicator("", getResources().getDrawable(R.drawable.accepteddonations));
            tabHost.addTab(tabSpec);

            tabSpec = tabHost.newTabSpec("mydonations");
            tabSpec.setContent(R.id.linearLayout3);
//        tabSpec.setIndicator("My Donations");
            tabSpec.setIndicator("", getResources().getDrawable(R.drawable.mydonate));
            tabHost.addTab(tabSpec);
        }
        else {
            root.setBackgroundColor(getResources().getColor(R.color.White));
            tabSpec = tabHost.newTabSpec("mydonations");
            tabSpec.setContent(R.id.linearLayout3);
//        tabSpec.setIndicator("My Donations");
            tabSpec.setIndicator("", getResources().getDrawable(R.drawable.mydonate));
            tabHost.addTab(tabSpec);
        }
        tabHost.setCurrentTab(0);

        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorPrimary),
                getResources().getColor(R.color.colorPrimaryDark),
                getResources().getColor(R.color.colorAccent));

        button3.setOnClickListener(this);

        FloatingActionButton donate1 = (FloatingActionButton) findViewById(R.id.donateButton1);
        assert donate1 != null;
        donate1.setOnClickListener(this);

        FloatingActionButton donate2 = (FloatingActionButton) findViewById(R.id.donateButton2);
        assert donate2 != null;
        donate2.setOnClickListener(this);

        FloatingActionButton donate3 = (FloatingActionButton) findViewById(R.id.donateButton3);
        assert donate3 != null;
        donate3.setOnClickListener(this);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mNotificationManager.cancel(324255);
                if(tabHost.getCurrentTabTag().equals("notification")){
                    Log.i("jebin",tabHost.getCurrentTabTag());
                    new OpenDonations().execute();
                }
                else if(tabHost.getCurrentTabTag().equals("accepteddonations")){
                    Log.i("jebin",tabHost.getCurrentTabTag());
                    new AcceptedDonations().execute();
                }
                else if(tabHost.getCurrentTabTag().equals("mydonations")){
                    Log.i("jebin",tabHost.getCurrentTabTag());
                    new MyDonations().execute();
                }
            }
        });
    }

    class MyOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if(tabHost.getCurrentTabTag().equals("accepteddonations")){
                gettabname=tabHost.getCurrentTabTag();
                int selectedItemPosition2 = recyclerView2.getChildPosition(v);
                RecyclerView.ViewHolder viewHolder2 = recyclerView2.findViewHolderForPosition(selectedItemPosition2);
                TextView textDescription2 = (TextView) viewHolder2.itemView.findViewById(R.id.textDescription);
                String selectedName2 = (String) textDescription2.getText();
                for(int i=0;i<description2.size();i++) {
                    if (selectedName2.equals(description2.get(i))) {
//                        secretnumber3=itemid2.get(i);
                        la = latitude2.get(i);
                        lo = longitude2.get(i);
                        SharedPreferences.Editor editor=sp.edit();
                        editor.putString("tempuserla",la);
                        editor.putString("tempuserlo",lo);
                        editor.putString("tempusername1",name2.get(i));
                        editor.putString("tempuserdescription1",description2.get(i));
                        editor.putString("tempuseritem1",item2.get(i));
                        editor.putString("tempusernumber1",number2.get(i));
                        editor.putString("tempuseritemid1",itemid2.get(i));
                        editor.apply();
                        break;
                    }
                }
                startActivity(new Intent(AllNotifications.this,Details.class));
                finish();
//                dialog3.show();
            }
            else if(tabHost.getCurrentTabTag().equals("notification")){
                gettabname=tabHost.getCurrentTabTag();
                int selectedItemPosition1 = recyclerView1.getChildPosition(v);
                RecyclerView.ViewHolder viewHolder1 = recyclerView1.findViewHolderForPosition(selectedItemPosition1);
                TextView textDescription1 = (TextView) viewHolder1.itemView.findViewById(R.id.textDescription);
                String selectedName1 = (String) textDescription1.getText();
                for(int i=0;i<description1.size();i++) {
                    if (selectedName1.equals(description1.get(i))) {
                        la = latitude1.get(i);
                        lo = longitude1.get(i);
                        SharedPreferences.Editor editor=sp.edit();
                        editor.putString("tempuserla",la);
                        editor.putString("tempuserlo",lo);
                        editor.putString("tempusername1",name1.get(i));
                        editor.putString("tempuserdescription1",description1.get(i));
                        editor.putString("tempuseritem1",item1.get(i));
                        editor.putString("tempusernumber1",number1.get(i));
                        editor.putString("tempuseritemid1",itemid1.get(i));
                        editor.apply();
                        break;
                    }
                }
                startActivity(new Intent(AllNotifications.this,Details.class));
                finish();
            }
            else if(tabHost.getCurrentTabTag().equals("mydonations")){
                gettabname=tabHost.getCurrentTabTag();
                int selectedItemPosition3 = recyclerView3.getChildPosition(v);
                RecyclerView.ViewHolder viewHolder3 = recyclerView3.findViewHolderForPosition(selectedItemPosition3);
                TextView textDescription3 = (TextView) viewHolder3.itemView.findViewById(R.id.textDescription);
                String selectedName3 = (String) textDescription3.getText();
                for(int i=0;i<description3.size();i++) {
                    if (selectedName3.equals(description3.get(i))) {
                        secretnumber3=itemid3.get(i);
//                        la = latitude3.get(i);
//                        lo = longitude3.get(i);
                        SharedPreferences.Editor editor=sp.edit();
                        editor.putString("tempuserla","");
                        editor.putString("tempuserlo","");
                        editor.putString("tempusername1",name3.get(i));
                        editor.putString("tempuserdescription1",description3.get(i));
                        editor.putString("tempuseritem1",item3.get(i));
                        editor.putString("tempusernumber1",sp.getString("number",""));
                        editor.putString("tempuseritemid1",itemid3.get(i));
                        editor.apply();
                        break;
                    }
                }
                startActivity(new Intent(AllNotifications.this,Details.class));
                finish();
//                dialog3.show();
            }
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.setting, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        startActivity(new Intent(AllNotifications.this, Settings.class));
        finish();
        return false;
    }

    public boolean onTouchEvent(MotionEvent m) {
//        lastX = m.getX();
//        Log.i("jebin", String.valueOf(lastX));
//        switch (m.getAction()) {
//             when user first touches the screen to swap
//            case MotionEvent.ACTION_DOWN: {
//                lastX = m.getX();
//                Log.i("jebin", String.valueOf(lastX));
//                break;
//            }
//            case MotionEvent.ACTION_UP: {
//                float currentX = m.getX();
//
//                 if left to right swipe on screen
//                if (lastX < currentX) {
//
//                    switchTabs(false);
//                }
//
//                 if right to left swipe on screen
//                if (lastX > currentX) {
//                    switchTabs(true);
//                }
//
//                break;
//            }
//        }
        return false;
//        return gestureDetector.onTouchEvent(m);
    }

//    public void switchTabs(boolean direction) {
//        if (direction) // true = move left
//        {
//            if (tabHost.getCurrentTabTag().equals("notification"))
//                tabHost.setCurrentTabByTag("accepteddonations");
//            else if(tabHost.getCurrentTabTag().equals("accepteddonations"))
//                tabHost.setCurrentTabByTag("mydonations");
//            else if(tabHost.getCurrentTabTag().equals("accepteddonations"))
//                tabHost.setCurrentTabByTag("notification");
//        } else
//        // move right
//        {
//            if (tabHost.getCurrentTabTag().equals("notification"))
//                tabHost.setCurrentTabByTag("accepteddonations");
//            else if(tabHost.getCurrentTabTag().equals("accepteddonations"))
//                tabHost.setCurrentTabByTag("mydonations");
//            else if(tabHost.getCurrentTabTag().equals("accepteddonations"))
//                tabHost.setCurrentTabByTag("notification");
//        }
//    }

    @Override
    public boolean onDown(MotionEvent e) {
        //  Toast.makeText(this,"down swipe",Toast.LENGTH_LONG).show();
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {

        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return false;
    }

    class OpenDonations extends AsyncTask<String, String, String> {
        protected void onPreExecute() {
            number1.clear();
            name1.clear();
            latitude1.clear();
            longitude1.clear();
            item1.clear();
            description1.clear();
            itemid1.clear();
        }

        @Override
        protected String doInBackground(String... arg0) {
            String link1 = "http://" + getString(R.string.website) + "/recent_history", line1;
            try {
                String data1 = "id=" + URLEncoder.encode(sp.getString("id", ""), "UTF-8")
                        + "&number=" + URLEncoder.encode(sp.getString("number", ""), "UTF-8")
                        + "&location=" + URLEncoder.encode(sp.getString("location", ""), "UTF-8")
                        + "&radius=" + URLEncoder.encode(String.valueOf(sp.getInt("radius", 10)), "UTF-8")
                        + "&status="+ URLEncoder.encode("open", "UTF-8");
                URL url = new URL(link1);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
                writer.write(data1);
                writer.flush();
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                while ((line1 = reader.readLine()) != null) {
                    if(!line1.equals("ok")) {
                        String[] lineString = line1.split(",");
                        number1.add(lineString[0]);
                        name1.add(lineString[1]);
                        latitude1.add(lineString[2]);
                        longitude1.add(lineString[3]);
                        item1.add(itemNameParser(lineString[4]));//problem here
                        description1.add(lineString[5]);
                        itemid1.add(lineString[6]);
                    } else {
                        return "ok";
                    }
                }
                conn.disconnect();
            } catch (Exception e) {
                return "error";
            }

            return "ok";
        }

        protected void onPostExecute(String result) {
            swipeRefreshLayout.setRefreshing(false);
            if(result.equals("ok")) {
                data1 = new ArrayList<>();
                for (int j = 0; j < name1.size(); j++) {
                    data1.add(new DataModel(number1.get(j), name1.get(j), latitude1.get(j), longitude1.get(j), item1.get(j), description1.get(j), itemid1.get(j)));
                }

                adapter1 = new CustomAdapter(data1);
                recyclerView1.setAdapter(adapter1);
            }
            else{
                Snackbar.make(findViewById(R.id.TabView),"Please try again",Snackbar.LENGTH_LONG).show();
            }
        }
    }

    class AcceptedDonations extends AsyncTask<String, String, String> {
        protected void onPreExecute() {
            number2.clear();
            name2.clear();
            latitude2.clear();
            longitude2.clear();
            item2.clear();
            description2.clear();
            itemid2.clear();
        }

        @Override
        protected String doInBackground(String... arg0) {
            String link2 = "http://" + getString(R.string.website) + "/list_donations", line2;
            try {
                String data2 = "id=" + URLEncoder.encode(sp.getString("id", ""), "UTF-8")
                        + "&number=" + URLEncoder.encode(sp.getString("number", ""), "UTF-8")
                        + "&type=" + URLEncoder.encode("volunteered","UTF-8");
                URL url = new URL(link2);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
                writer.write(data2);
                writer.flush();
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                while ((line2 = reader.readLine()) != null) {
                    if(!line2.equals("ok")) {
                        String[] lineString = line2.split(",");
                        number2.add(lineString[0]);
                        name2.add(lineString[1]);
                        latitude2.add(lineString[2]);
                        longitude2.add(lineString[3]);
                        item2.add(itemNameParser(lineString[4]));
                        description2.add(lineString[5]);
                        itemid2.add(lineString[6]);
                    } else {
                        return "ok";
                    }
                }
                conn.disconnect();
            } catch (Exception e) {
                return "error";
            }

            return "ok";
        }

        protected void onPostExecute(String result) {
            swipeRefreshLayout.setRefreshing(false);
            if(result.equals("ok")) {
                data2 = new ArrayList<>();
                for (int j = 0; j < name2.size(); j++) {
                    data2.add(new DataModel(number2.get(j), name2.get(j), latitude2.get(j), longitude2.get(j), item2.get(j), description2.get(j), itemid2.get(j)));
                }

                adapter2 = new CustomAdapter(data2);
                recyclerView2.setAdapter(adapter2);
            }
            else{
                Snackbar.make(findViewById(R.id.TabView),"Please try again",Snackbar.LENGTH_LONG).show();
            }
        }
    }

    class MyDonations extends AsyncTask<String, String, String> {
        protected void onPreExecute() {
            name3.clear();
            item3.clear();
            description3.clear();
            itemid3.clear();
        }

        @Override
        protected String doInBackground(String... arg0) {
            StringBuilder sb = new StringBuilder();
            String link0 = "http://" + getString(R.string.website) + "/list_donations", line0;
            try {
                String data0 = "id=" + URLEncoder.encode(sp.getString("id", ""), "UTF-8")
                        + "&number=" + URLEncoder.encode(sp.getString("number", ""), "UTF-8")
                        + "&type=" + URLEncoder.encode("donated","UTF-8");
                URL url = new URL(link0);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
                writer.write(data0);
                writer.flush();
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                while ((line0 = reader.readLine()) != null) {
                    String[] line00=line0.split(",");
                    sb.append(line0);
                    if(!line0.equals("ok")) {
                        name3.add(line00[0]);
                        item3.add(itemNameParser(line00[1]));
                        description3.add(line00[2]);
                        itemid3.add(line00[3]);
                    }else{
                        return "ok";
                    }
                }
                conn.disconnect();
            } catch (Exception e) {
                return "error";
            }
            return "ok";
        }

        protected void onPostExecute(String result) {
            swipeRefreshLayout.setRefreshing(false);
            if(result.equals("ok")) {
                data3 = new ArrayList<>();
                for (int j = 0; j < name3.size(); j++) {
                    data3.add(new DataModel("", name3.get(j), "", "", item3.get(j), description3.get(j), itemid3.get(j)));
                }

                adapter3 = new CustomAdapter(data3);
                recyclerView3.setAdapter(adapter3);
            }
            else{
                Snackbar.make(findViewById(R.id.TabView),"Please try again",Snackbar.LENGTH_LONG).show();
            }
        }
    }

    class CloseDonations extends AsyncTask<String, String, String> {
        protected void onPreExecute() {
            progressDialog3.show();
        }

        @Override
        protected String doInBackground(String... arg0) {
            StringBuilder sb = new StringBuilder();
            String link0 = "http://" + getString(R.string.website) + "/close_donation", line0;
            try {
                String data0 = "id=" + URLEncoder.encode(sp.getString("id", ""), "UTF-8")
                        + "&number=" + URLEncoder.encode(sp.getString("number", ""), "UTF-8")
                        + "&donationId=" + URLEncoder.encode(arg0[0],"UTF-8")
                        + "&code=" + URLEncoder.encode(arg0[1],"UTF-8");
                URL url = new URL(link0);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
                writer.write(data0);
                writer.flush();
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                while ((line0 = reader.readLine()) != null) {
                    sb.append(line0);
                }
                conn.disconnect();
            } catch (Exception e) {
                return "error";
            }
            return sb.toString();
        }

        protected void onPostExecute(String result) {
            swipeRefreshLayout.setRefreshing(false);
            progressDialog3.dismiss();
            if(result.equals("ok")) {
                alertDialogBuilder3.setMessage("Verified");
                        alertDialogBuilder3.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                alertDialogBuilder3.show();
            }
            else{
                alertDialogBuilder3.setMessage("Try Again");
                alertDialogBuilder3.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
                alertDialogBuilder3.show();
            }
            dialog3.dismiss();
        }
    }

    public String itemNameParser(String items) {
        String reply = "error";

        if (Integer.parseInt(items) >= 0) {
            StringBuilder sb = new StringBuilder();

            for (int i = 0; i <items.length(); i++) {
                switch (items.charAt(i)) {
                    case '0':
                        sb.append("Home Made Food, ");
                        break;

                    case '1':
                        sb.append("Packed Food, ");
                        break;

                    case '2':
                        sb.append("Clothes, ");
                        break;

                    case '3':
                        sb.append("Books, ");
                        break;
                    default:
                        sb.append("unknown");
                        break;
                }
            }

            reply = sb.substring(0, sb.length() - 2);
        }
        return reply.toString();
    }
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dialogbutton:
                if (editText3.getText().length() == 6) {
                    Log.i("jebin",secretnumber3+editText3.toString());
                    new CloseDonations().execute(secretnumber3, editText3.getText().toString());
                }
                else{
                    Snackbar.make(findViewById(R.id.TabView),"Check the code",Snackbar.LENGTH_LONG).show();
                }
                break;
            default:
                startActivity(new Intent(AllNotifications.this, ItemSelectionActivity.class));
                finish();
                break;
        }
    }
    @Override
    public void onStop(){
        SharedPreferences.Editor edit=sp.edit();
        edit.putBoolean("AllNotifications",false);
        edit.apply();
        super.onStop();
    }
    public void onPause(){
        SharedPreferences.Editor edit=sp.edit();
        edit.putBoolean("AllNotifications",false);
        edit.apply();
        super.onPause();
    }
    public void onResume(){
        SharedPreferences.Editor edit=sp.edit();
        edit.putBoolean("AllNotifications",true);
        edit.apply();
        super.onStop();
    }
}

