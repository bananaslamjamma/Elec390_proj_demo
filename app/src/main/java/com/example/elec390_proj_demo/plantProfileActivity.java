package com.example.elec390_proj_demo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.elec390_proj_demo.ui.login.LoginActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Map;
import java.util.regex.Pattern;

public class plantProfileActivity extends AppCompatActivity {

    FirebaseAuth auth;
    FirebaseUser user;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference u_root = database.getReference("users");
    DatabaseReference p_root;

    DatabaseReference active_root;
    ImageView img;
    TextView p_current_moisture, date_view;
    Button save_change_button, move_config;

    EditText dia_target_moisture, dia_watering_amount, target_moisture, watering_amount, current_moisture, plantName;
    Button submitDia;
    Dialog dialog;
    CheckBox activePlantCheck, manualWaterCheck, autoRefreshCheck;
    String current_plant = "";
    //global objects
    Plants g_plants;
    ActivePlant g_active;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent myIntent = new Intent(getApplicationContext(), myPlantsActivity.class);
                startActivity(myIntent);
                finish();
                break;
            case R.id.log_out:
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
        setContentView(R.layout.activity_plant_profile);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        String uid_loc;

        //TextViews
        p_current_moisture = findViewById(R.id.p_current_moisture_text);
        //EditViews
        plantName = findViewById(R.id.p_name);
        target_moisture = findViewById(R.id.p_target_moisture_text);
        date_view = findViewById(R.id.p_date);
        current_moisture = findViewById(R.id.p_current_moisture);
        watering_amount = findViewById(R.id.p_watering_amount);
        //Buttons
        save_change_button = findViewById(R.id.save_button);
        move_config = findViewById(R.id.mov_config);
        activePlantCheck = findViewById(R.id.active_plants_check);
        //dialog
        dialog = new Dialog(plantProfileActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.plant_profile_dialog);
        dia_target_moisture = dialog.findViewById(R.id.target_moisture_dia);
        dia_watering_amount = dialog.findViewById(R.id.watering_amount_dia);
        manualWaterCheck = dialog.findViewById(R.id.manual_watering_check);
        autoRefreshCheck = dialog.findViewById(R.id.auto_refresh_check);
        submitDia = dialog.findViewById(R.id.profile_submit_dia);
        //auth
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        if (user == null) {
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
            finish();
        } else {
            uid_loc = user.getUid();
            u_root = database.getReference("users/" + uid_loc);
            active_root = database.getReference("users/" + uid_loc + "/Active Plant");
        }
        //retrieve data from other activity
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            //The key argument here must match that used in the other activity
            String value = extras.getString("plant_url");
            current_plant = extras.getString("plant_name");
            System.out.println("current Plant");
            System.out.println(current_plant);
            p_root = FirebaseDatabase.getInstance().getReferenceFromUrl(value);
            System.out.println("PPP ROOT");
            System.out.println(p_root);
        }
        //get data once
        p_root.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    System.out.println(("firebase " + " Error getting data " + task.getException()));
                } else {
                    System.out.println(("firebase " + String.valueOf(task.getResult().getValue())));
                    Plants p = task.getResult().getValue(Plants.class);
                    g_plants = p;
                    //strs
                    plantName.setText(String.valueOf(p.getName()));
                    date_view.setText(String.valueOf(p.getDate()));
                    //ints
                    target_moisture.setText(String.valueOf(p.getTarget_moisture()));
                    watering_amount.setText(String.valueOf(p.getWatering_amount()));
                    getSupportActionBar().setTitle(String.valueOf(p.getName()));
                }
            }
        });
        active_root.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    System.out.println(("firebase " + " Error getting data " + task.getException()));
                } else {
                    System.out.println(("firebase " + String.valueOf(task.getResult().getValue())));
                    ActivePlant p = task.getResult().getValue(ActivePlant.class);
                    g_active = p;
                    if(p.getAddress().equals("dummy")){
                        activePlantCheck.setVisibility(View.VISIBLE);
                        move_config.setVisibility(View.INVISIBLE);
                        activePlantCheck.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                p.setAddress("/Plants/" + current_plant);
                                Map<String, Object> postValues = p.toMap();
                                active_root.updateChildren(postValues);
                                System.out.println("UPDATED ACTIVE_PLANT");
                                Intent myIntent = new Intent(getApplicationContext(), myPlantsActivity.class);
                                startActivity(myIntent);
                                finish();
                            }
                        });
                    }
                    else{
                        System.out.println("EVAN FUCK YOU");
                        System.out.println(p.getAddress());
                        String[] arrValues = p.getAddress().split(Pattern.quote("/"));
                        System.out.println("EVAN FUCK YOU 2");
                        System.out.println(arrValues[2]);
                        if(!(current_plant.equals(arrValues[2]))){
                            System.out.println("YOU'RE IN THE WRONG NEIGHBOURHOOD");
                            activePlantCheck.setVisibility(View.VISIBLE);
                            move_config.setVisibility(View.INVISIBLE);
                            activePlantCheck.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    //if you're swapping over active_plants wipe the current settings
                                    //p.wipeProfile();
                                    //p.setAddress("Plants/" + current_plant);
                                    Map<String, Object> postValues = p.toMap();
                                    //active_root.updateChildren(postValues);
                                    System.out.println("UPDATED ACTIVE_PLANT");
                                    active_root.child("address").setValue("/Plants/" + current_plant);
                                    Intent myIntent = new Intent(getApplicationContext(), myPlantsActivity.class);
                                    startActivity(myIntent);
                                    finish();
                                }
                            });
                        }
                        else{
                            //if active plant
                            p_current_moisture.setVisibility(View.VISIBLE);
                            current_moisture.setVisibility(View.VISIBLE);
                            current_moisture.setText(String.valueOf(p.getCurrent_moisture()));
                            activePlantCheck.setVisibility(View.INVISIBLE);
                            move_config.setVisibility(View.VISIBLE);
                        }
                    }

                    /**
                    if (p.getAddress().equals())
                    plant_view.setText("Plant Name: " + String.valueOf(p.));
                    status_view.setText("Plant Status: " + String.valueOf(p.getFlag()));
                    //description_view.setText(p.setDescription());
                    date_view.setText("Plant Date: " + String.valueOf(p.getDate()));
                    getSupportActionBar().setTitle(String.valueOf(p.getName()));
 **/
                }
            }
        });
        move_config.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopUp();
            }
        });
    }

    public void showPopUp() {
        //note: the hardware is listening to one particular child:
        // don't update multiple fields at the same time
        manualWaterCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                active_root.child("manualFlag").setValue(true);
                dialog.dismiss();
                //refresh the current activity
                recreate();
            }
        });
        autoRefreshCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                active_root.child("arduinoRefresh").setValue(true);
                dialog.dismiss();
                //refresh the current activity
                recreate();
            }
        });
        submitDia.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                int moisture_input = Integer.valueOf(dia_target_moisture.getText().toString());
                int water_amt_input = Integer.valueOf(dia_watering_amount.getText().toString());
                g_plants.setTarget_moisture(moisture_input);
                g_plants.setWatering_amount(water_amt_input);
                Map<String, Object> postValues = g_plants.toMap();
                //p_root.updateChildren(postValues);
                p_root.child("watering_amount").setValue(water_amt_input);
                p_root.child("target_moisture").setValue(moisture_input);
                dialog.dismiss();
                //refresh the current activity
                recreate();
            }
        });
        dialog.show();
    }
}