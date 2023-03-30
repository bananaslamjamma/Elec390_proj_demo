package com.example.elec390_proj_demo;

import android.os.AsyncTask;

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


/**
 * The type of argues that can be passed
 */
public class PerenualHandler extends AsyncTask<String, Void, String> {

    public AsyncResponse delegate = null;
    String BASIC_URL = "https://perenual.com/api/species-list?";
    String KEY = "key=sk-RPim64249281b6c46388";
    String QUERY = "https://perenual.com/api/species-list?key=sk-RPim64249281b6c46388&q=rose";
    String QUERY_EXAMPLE = "https://perenual.com/api/species-list?key=sk-RPim64249281b6c46388&q=rose";
    String data = "";
    @Override
    protected String doInBackground(String... params) {
        HttpURLConnection httpURLConnection = null;
        try {
            URL url = new URL("https://perenual.com/api/species-list?key=sk-RPim64249281b6c46388&q=rose");
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
        protected void onPostExecute (String s){
        String singleParsed = "";
        String dataParsed = "";
        /**
            try {
                JSONArray jsonArray = new JSONArray(data);
                System.out.println("ARRAY");
                System.out.println(jsonArray);
                int length =  jsonArray.length();
                //JSONObject jsonObjectParent = (JSONObject) jsonArray.get(data);
                for (int i = 0; i < 1; i++) {
                    //get current object
                    JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                    singleParsed = "ID:" + jsonObject.get("id") + "\n";
                    dataParsed = dataParsed + singleParsed + "\n";
                    String id = jsonObject.getString("id");
                    String name = jsonObject.getString("thumbnail");
                }
            } catch (JSONException e){
                e.printStackTrace();
            }
         **/
            System.out.println(dataParsed);
            delegate.processFinish(s);
        }
    }
