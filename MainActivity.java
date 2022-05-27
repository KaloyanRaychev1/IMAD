package com.example.kaloyan_raychev_swd61b_tca;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    TextView txtvUser;
    TextView txtvPrevUserDetails;
    TextView txtvPrevUserBanner;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        txtvUser = findViewById(R.id.txtvUser);
        txtvPrevUserDetails = findViewById(R.id.txtvPrevUserDetails);
        txtvPrevUserBanner = findViewById(R.id.txtvPrevUserBanner);
        txtvPrevUserBanner.setVisibility(TextView.INVISIBLE);
        txtvPrevUserDetails.setVisibility(TextView.INVISIBLE);
        setTitle("User Data");

        sharedPreferences = this.getSharedPreferences("User_File", MODE_PRIVATE);

        new JSONAsyncTask(txtvUser, sharedPreferences).execute();
    }

    public void load() {
        String user = sharedPreferences.getString("user", "default User");
        txtvPrevUserDetails.setText(user);
    }

    public void onResume() {
        super.onResume();
        new JSONAsyncTask(txtvUser, sharedPreferences).execute();
    }


    public void onPause() {
        super.onPause();
        load();
        try {
            Thread.sleep(1000);
            txtvPrevUserBanner.setVisibility(TextView.VISIBLE);
            txtvPrevUserDetails.setVisibility(TextView.VISIBLE);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}

class JSONAsyncTask  extends AsyncTask<String, Void, String> {

    TextView txtv;
    SharedPreferences sp;

    public JSONAsyncTask (TextView txtv, SharedPreferences sp) {
        this.txtv = txtv;
        this.sp = sp;
    }

    public void store(String user){
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("user", user);

        editor.apply();
    }

    @Override
    protected void onPreExecute() { super.onPreExecute(); }

    @Override
    protected String doInBackground(String... urls){

        HttpURLConnection urlConnection = null;
        String output = "";
        try {
            URL url = new URL("https://gorest.co.in/public/v2/users");
            urlConnection = (HttpURLConnection) url.openConnection();
            int code = urlConnection.getResponseCode();
            if (code != 200) {
                throw new IOException("Invalid Response From Server : " + code);
            }
            BufferedReader rd = new BufferedReader(new InputStreamReader(
                    urlConnection.getInputStream()
            ));
            String line;
            while ((line = rd.readLine()) != null) {
                output += line;
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }

        String user = "";


        try {
            JSONArray data = new JSONArray(output);
            JSONObject user1 = data.getJSONObject(0);
            user =  "ID: " + user1.getInt("id") + "\n" +
                    "NAME: " + user1.getString("name") + "\n" +
                    "EMAIL: " + user1.getString("email") + "\n" +
                    "GENDER : " + user1.getString("gender") + "\n" +
                    "STATUS: " + user1.getString("status");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return user;
    }

    @Override
    protected void onPostExecute(String result) {
        txtv.setText(result);
        store(result);
    }
}