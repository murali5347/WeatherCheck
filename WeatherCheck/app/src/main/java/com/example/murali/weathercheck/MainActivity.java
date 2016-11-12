package com.example.murali.weathercheck;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {


    private ListView weatherListView;

    private List<Weather> weatherList = new ArrayList<>();

    private WeatherArrayAdapter weatherArrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        weatherListView = (ListView)findViewById(R.id.listView);

        weatherArrayAdapter = new WeatherArrayAdapter(this,weatherList);

        weatherListView.setAdapter(weatherArrayAdapter);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                EditText locationEditText = (EditText)findViewById(R.id.locationEditText);

                URL url=createURL(locationEditText.getText().toString());

                if(url!=null){
                    dismisskeyboard(locationEditText);
                    GetWeatherTask getLocationWeatherTask = new GetWeatherTask();
                    getLocationWeatherTask.execute(url);
                }else

                Snackbar.make(findViewById(R.id.coordinateLayout), "invalid Location", Snackbar.LENGTH_LONG)
                        .show();
            }
        });
    }


    private void dismisskeyboard(View view){

        InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);

        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(),0);
    }

    //create openweathermap.org web service URL using city

    private URL createURL(String city) {
        String apiKey = getString(R.string.api_key);
        String baseURL =getString(R.string.web_service_url);
        try{
            //create url for specified city and imperial units
            String urlString = baseURL + URLEncoder.encode(city,"UTF-8")+"&units=imperial&cnt=16&APPID="+apiKey;
            return new URL(urlString);
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public class GetWeatherTask extends AsyncTask<URL,Void,JSONObject>{


        @Override
        protected JSONObject doInBackground(URL... params) {

            HttpURLConnection connection =null;
            try {
                connection = (HttpURLConnection) params[0].openConnection();
                int response = connection.getResponseCode();
                if (response == HttpURLConnection.HTTP_OK) {
                    StringBuilder builder = new StringBuilder();
                    try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                        String line;
                        while ((line = reader.readLine()) != null) {
                            builder.append(line);
                        }
                    } catch (Exception e) {
                        Snackbar.make(findViewById(R.id.coordinateLayout), R.string.read_error, Snackbar.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
                    return new JSONObject(builder.toString());
                } else {
                    Snackbar.make(findViewById(R.id.coordinateLayout), R.string.connect_error, Snackbar.LENGTH_LONG).show();
                }
            }
            catch (Exception e){
                Snackbar.make(findViewById(R.id.coordinateLayout),R.string.connect_error,Snackbar.LENGTH_LONG).show();
                e.printStackTrace();

            }
            finally {
                connection.disconnect();
            }


            return null;
        }

        //process JSON responce and update listview

        @Override
        protected void onPostExecute(JSONObject weather) {
            convertJSONArrayList(weather);//repopulate weather list
            weatherArrayAdapter.notifyDataSetChanged();//rebind the listview
            weatherListView.smoothScrollToPosition(0);//scroll to top
        }

    }

    private void convertJSONArrayList(JSONObject forecast){
        weatherList.clear();

        try {

            //get forcasts "list" JSONArray
            JSONArray list = forecast.getJSONArray("list");
            ////convert each element of the list to a weather object
            for(int i=0;i<list.length();i++){
                JSONObject day = list.getJSONObject(i);//get one days data

                //get days temperature "temp" JSON object
                JSONObject temperatures = day.getJSONObject("temp");

                //get days "weather" JSON OBJECT for the description and icon
                JSONObject weather = day.getJSONArray("weather").getJSONObject(0);

                //add new weather object to weather list
                weatherList.add(new Weather(day.getLong("dt"),temperatures.getDouble("min"),temperatures.getDouble("max"),day.getDouble("humidity"),weather.getString("description"),weather.getString("icon")));



            }

        }catch (JSONException e){
            e.printStackTrace();

        }
        }


}
