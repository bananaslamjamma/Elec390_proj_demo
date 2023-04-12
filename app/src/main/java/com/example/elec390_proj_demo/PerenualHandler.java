package com.example.elec390_proj_demo;

import android.os.AsyncTask;
import android.widget.Toast;

import org.apache.commons.text.WordUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * ELEC-390 Soil Sense
 * Created by Evan Yu on 04/01/2023.
 * @PerenualHandler.java Asynchrnous Task Handling for Perenual API usage
 */

public class PerenualHandler extends AsyncTask<String, Void, String> {
    Date date = Calendar.getInstance().getTime();
    DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
    String strDate = dateFormat.format(date);
    public AsyncResponse delegate = null;
    String BASIC_URL = "https://perenual.com/api/species-list?";
    String KEY = "key=sk-RPim64249281b6c46388";
    String QUERY = "https://perenual.com/api/species-list?key=sk-RPim64249281b6c46388&q=";
    String QUERY_EXAMPLE = "https://perenual.com/api/species-list?key=sk-RPim64249281b6c46388&q=rose";
    String data = "";
    ArrayList<Plants> apiPlantsList = new ArrayList<Plants>();

    @Override
    protected String doInBackground(String... params) {
        HttpURLConnection httpURLConnection = null;
        String inputString = params[0];
        String endpoint = QUERY + inputString;
        try {
            URL url = new URL(endpoint);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            InputStream inputStream = httpURLConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line = "";
            while (line != null) {
                line = bufferedReader.readLine();
                data = data + line;
            }
            //return data to onPostExecute method
            return data;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }
        }
        return data;
    }

    @Override
    protected void onPostExecute(String s) {
        String singleParsed = "";
        String common_name = "";
        String watering = "";
        String url = "";
        System.out.println("API OUTPUT");
        System.out.println(s);
        try{
            JSONObject jsonObject = new JSONObject(s);
            JSONArray jsonArray = jsonObject.getJSONArray("data");
            for (int i = 0; i < 5; i++) {
                String sunlight = "";
                String sci_name = "";
                JSONObject item = jsonArray.getJSONObject(i);
                JSONArray sun = item.getJSONArray("sunlight");
                JSONArray scientific_name = item.getJSONArray("scientific_name");
                //concat all the sunlight elements together
                if(sun.length() >= 0){
                    for(int j = 0; j < sun.length(); j++){
                        sunlight = sunlight + sun.get(j) + ", ";
                    }
                }
                JSONObject image = new JSONObject(item.getString("default_image"));
                common_name = item.getString("common_name");
                watering = item.getString("watering");

                if(image.has("thumbnail")){
                    url = image.getString("thumbnail");
                }else
                    url = image.getString("original_url");

                if(!(scientific_name.isNull(0))){
                    sci_name = scientific_name.getString(0);
                }else
                    sci_name = "not found";

                Plants p = new Plants(WordUtils.capitalize(common_name), strDate, sunlight, watering, url, sci_name);
                apiPlantsList.add(p);
            }
        } catch(JSONException e){
            e.printStackTrace();
        }
        delegate.processFinish(apiPlantsList);
    }
    @Override
    protected void onPreExecute() {
        delegate.beforeProcess(apiPlantsList);
    }
}



