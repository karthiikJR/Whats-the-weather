package com.example.whatstheweather;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    // Components declarations 
    EditText searchBar;
    TextView loc, deg, condition, feelsLike, wind, prec, humidity,tvtime;
    ImageView img;

    // Components to connect to web
    URL url;
    HttpURLConnection con = null;
    
    // Get input from user when typed on search bar
    String usrInp="";


    // Initialization of all the UI components
    public void initi() {
        searchBar = findViewById(R.id.etSearch);
        loc = findViewById(R.id.tvLocation);
        deg = findViewById(R.id.tvTemp);
        condition = findViewById(R.id.tvCondition);
        feelsLike = findViewById(R.id.tvFeelsLike);
        wind = findViewById(R.id.tvWind);
        prec = findViewById(R.id.tvPrec);
        humidity = findViewById(R.id.tvHumidity);
        tvtime = findViewById(R.id.tvTime);
        img = findViewById(R.id.ivImg);
    }

    // After completing the task done by AsyncTask setting the data to the required components
    public void settingVal(String json) {
        try {
            // JSON text extraction 
            JSONObject js = new JSONObject(json);
            String temp = "",temp1 = "";
            temp = js.getString("location");
            temp1 = js.getString("current");
            js = new JSONObject(temp);
            
            // Setting values
            loc.setText(js.getString("name"));
            tvtime.setText(js.getString("localtime"));
            
            // JSON text extraction
            js = new JSONObject(temp1);
            
            // Setting values
            deg.setText(js.getString("temp_c")+"°C");
            feelsLike.setText(js.getString("feelslike_c")+"°C");
            prec.setText(js.getString("precip_mm")+" mm");
            wind.setText(js.getString("wind_kph")+" km/h");
            humidity.setText(js.getString("humidity")+"%");

            // JSON text extraction
            temp = js.getString("condition");
            js = new JSONObject(temp);
            
            // Setting values
            condition.setText(js.getString("text"));

            // JSON image link extraction
            temp1 = js.getString("icon");
            
            // Setting image using a external library (Getting image using a link)
            // Picasso.get().load("https:"+temp1).into(img);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // When the Go button pressed
    public void go(View view) {

        // Get user Input
        usrInp = searchBar.getText().toString();
        Log.i("Inp",usrInp);

        // Creating the link according to the user and executing using Task class
        String url = "https://api.weatherapi.com/v1/current.json?key=ba7dc74132f548b28e4114756232604&q="+usrInp+"&aqi=no";
        Task t = new Task();
        t.execute(url);

        // When user clicks on the button we need to get rid of the keyboard after user clicks on it
        InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(searchBar.getWindowToken(),0);
    }

    // AsyncTask class to execute in background (Getting data from the web)
    public class Task extends AsyncTask<String,Void,String> {

        @Override
        protected String doInBackground(String... urls) {

            try {
                String temp="";
                url = new URL(urls[0]);
                con = (HttpURLConnection) url.openConnection();
                InputStream in = con.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                int data = reader.read();
                while (data!=-1) {
                    char curr = (char) data;
                    temp+=curr;
                    data = reader.read();
                }
                return temp;
            } catch (Exception e) {
                Log.i("error",e.toString());
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if(s!=null) {
                Log.i("result", s);
                
                // After the execution setting up all the values
                settingVal(s);
            }
            else
                Log.i("null","ok null");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        // Initialization
        initi();
        // Setting initial weather to Tokyo's weather
        Task t = new Task();
        t.execute("https://api.weatherapi.com/v1/current.json?key=ba7dc74132f548b28e4114756232604&q=Tokyo&aqi=no");
    }
}