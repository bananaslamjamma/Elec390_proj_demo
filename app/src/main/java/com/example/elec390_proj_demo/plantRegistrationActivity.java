package com.example.elec390_proj_demo;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.elec390_proj_demo.ui.login.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class plantRegistrationActivity extends AppCompatActivity implements AsyncResponse{
    TextView test_input, homeNavText;
    EditText field1, field2, n_plant;
    Button submitButton, apiButton;
    FirebaseAuth auth;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference u_root = database.getReference("users");
    FirebaseUser user;
    String uid_loc;
    ProgressDialog progressDialog;
    Date date = Calendar.getInstance().getTime();
    DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
    String strDate = dateFormat.format(date);
    PerenualHandler asyncTask = new PerenualHandler();
    Dialog dialog;
    LinearLayout layout;

    ArrayList<Plants> apiPlantsList = new ArrayList<Plants>();

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        //make "Help " hidden
        MenuItem item = menu.findItem(R.id.help_mode);
        item.setVisible(false);
        //menu.getItem(2).setVisible(false);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item){
        System.out.println("ITEM ID");
        System.out.println(item.getItemId());
        switch (item.getItemId()){
            //this is the back button
            case android.R.id.home:
                System.out.println("HOME");
                Intent myIntent = new Intent(getApplicationContext(), myPlantsActivity.class);
                startActivity(myIntent);
                break;
            case R.id.log_out:
                System.out.println("LOGOUT");
                //logout
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plant_registration);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        dialog = new Dialog(plantRegistrationActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.dialog_api);
        layout = dialog.findViewById(R.id.container_api);
        field2 =  findViewById(R.id.field2);
        homeNavText = findViewById(R.id.plantProfileReturn);
        submitButton = findViewById(R.id.submitButton);
        apiButton = findViewById(R.id.testButton);
        n_plant = findViewById(R.id.name_plant);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        if (user == null) {
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
            finish();
        }
        else {
            uid_loc = user.getUid();
            u_root = database.getReference("users/" + uid_loc + "/Plants");
        }

        homeNavText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), myPlantsActivity.class);
                startActivity(intent);
                finish();
            }
        });


        apiButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String input = "rose";
                input = String.valueOf(field2.getText());
                syncTasks(input);
                System.out.println("SIZE " +  apiPlantsList.size());
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String plantName;
                plantName = String.valueOf(n_plant.getText());
                if(plantName.equals("Active Plant")){
                    Toast.makeText(plantRegistrationActivity.this, "Invalid Name!.",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                Plants plant = new Plants(plantName,strDate, 0);
                //see Plants class for more info
                Map<String, Object> plantValues = plant.toMap();
                Map<String,Object> childUpdates = new HashMap<>();
                childUpdates.put(plantName, plantValues);
                u_root.updateChildren(childUpdates);
            }
        });
    }


    private void syncTasks(String input) {
        try {
            if (asyncTask.getStatus() != AsyncTask.Status.RUNNING){   // check if asyncTasks is running
                asyncTask.cancel(true); // asyncTasks not running => cancel it
                asyncTask = new PerenualHandler(); // reset task
                //needed
                asyncTask.delegate = this;
                progressDialog = new ProgressDialog(plantRegistrationActivity.this);
                progressDialog.setMessage("Loading results");
                progressDialog.setCancelable(false);
                progressDialog.show();
                asyncTask.execute(input); // execute new task (the same task)
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("MainActivity_TSK", "Error: "+e.toString());
        }
    }

    private void addSearchItem(Plants p){
        layout = dialog.findViewById(R.id.container_api);
        View view = getLayoutInflater().inflate(R.layout.search_results, null);
        TextView text = view.findViewById(R.id.plant_name);
        ImageView icon = view.findViewById(R.id.plant_image);

        text.setText(String.valueOf(p.getName()));
        Glide.with(this).load(
                p.getPlant_url() +
                "").into(icon);

        layout.addView(view);
    }

    @Override
    public void processFinish(ArrayList<Plants> output) {
        apiPlantsList = output;
        for(int i = 0; i< apiPlantsList.size(); i++){
            addSearchItem(apiPlantsList.get(i));
        }
        dialog.show();
        /**
         *
        String singleParsed = "";
        String dataParsed = "";
        String common_name = "";
        String watering = "";
        String url = "";
        System.out.println("API OUTPUT");
        System.out.println(output);
        try{
            JSONObject jsonObject = new JSONObject(output);
            JSONArray jsonArray = jsonObject.getJSONArray("data");
            for (int i = 0; i < 5; i++) {
                String sunlight = "";
                JSONObject item = jsonArray.getJSONObject(i);
                JSONArray sun = item.getJSONArray("sunlight");
                //concat all the sunlight elements together
                if(sun.length() >= 0){
                    for(int j = 0; j < sun.length(); j++){
                        sunlight = sunlight + sun.get(j) + ",";
                    }
                }
                JSONObject image = new JSONObject(item.getString("default_image"));
                singleParsed = "ID:" + item.get("id") + "\n" +
                                "COMMON NAME:" + item.get("common_name") + "\n" +
                                "WATERING:" + item.get("watering") + "\n" +
                                "SUNLIGHT:" + sunlight + "\n" +
                                "default_image:" + image.get("thumbnail") + "\n";

                common_name = item.getString("common_name");
                watering = item.getString("watering");
                url = image.getString("thumbnail");

                Plants p = new Plants(common_name, strDate, sunlight, watering, url);
                apiPlantsList.add(p);
            }
        } catch(JSONException e){
            e.printStackTrace();
            Toast.makeText(plantRegistrationActivity.this,
                    "Whoops something went wrong! " ,
                    Toast.LENGTH_SHORT).show();
        }

        System.out.println(singleParsed);
         */
        progressDialog.dismiss();
    }

    @Override
    public void onPreExecute(){
        apiPlantsList.clear();
    }
}