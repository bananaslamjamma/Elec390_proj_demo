package com.example.elec390_proj_demo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
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

public class plantProfileActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    FirebaseAuth auth;
    FirebaseUser user;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference u_root = database.getReference("users");
    DatabaseReference p_root;
    DatabaseReference active_root;
    ImageView img;
    TextView p_current_moisture, date_view;
    Button save_change_button, move_config;
    EditText dia_target_moisture, dia_watering_amount,
            target_moisture, watering_amount,
            current_moisture, plantName,
            sci_name, water_frequency, sunlight;
    Button submitDia;
    Dialog dialog, helpDialog;
    CheckBox activePlantCheck, manualWaterCheck, autoRefreshCheck;
    String current_plant = "", noWS_c_plant;
    //global objects
    Plants g_plants;

    Spinner spin;
    ActivePlant g_active;

    int moistureLevelValue  = 0;
    int plantMoistureLevel = 0;
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem switch_item = menu.findItem(R.id.dark_mode_switch);
        switch_item.setVisible(false);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.help_mode:
                helpDialog.show();
                break;
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
        sunlight = findViewById(R.id.p_sunlight);
        water_frequency = findViewById(R.id.p_watering_freq);
        sci_name = findViewById(R.id.p_family_name);
        //Buttons
        //save_change_button = findViewById(R.id.upload_button);
        move_config = findViewById(R.id.mov_config);
        activePlantCheck = findViewById(R.id.active_plants_check);
        //dialog
        dialog = new Dialog(plantProfileActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.plant_profile_dialog);
        //helpDialog
        helpDialog =  new Dialog(plantProfileActivity.this);
        helpDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        helpDialog.setCancelable(true);
        helpDialog.setContentView(R.layout.dialog_text_popup);
        dia_target_moisture = dialog.findViewById(R.id.target_moisture_dia);
        dia_watering_amount = dialog.findViewById(R.id.watering_amount_dia);
        manualWaterCheck = dialog.findViewById(R.id.manual_watering_check);
        autoRefreshCheck = dialog.findViewById(R.id.auto_refresh_check);
        submitDia = dialog.findViewById(R.id.profile_submit_dia);

        dia_watering_amount.setFilters(new InputFilter[] {
                new InputSanitizer(1,20)});


        //drop down list
        spin = (Spinner) dialog.findViewById(R.id.target_moisture_options);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.target_moisture_settings, android.R.layout.simple_spinner_dropdown_item);
        spin.setAdapter(adapter);
        spin.setOnItemSelectedListener(this);
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
            String img_url = extras.getString("plant_img");
            current_plant = extras.getString("plant_name");
            noWS_c_plant = extras.getString("p_nameWhite");
            p_root = FirebaseDatabase.getInstance().getReferenceFromUrl(value);
            img = findViewById(R.id.imageView);
            if(!img_url.equals("")){
                Glide.with(this).load(
                        img_url +
                                "").into(img);
            }
        }

        //get data once
        p_root.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if(task.getResult().exists()){
                    if (!task.isSuccessful()) {
                        System.out.println(("firebase " + " Error getting data " + task.getException()));
                    } else {
                        System.out.println(("firebase " + task.getResult().getValue()));
                        Plants p = task.getResult().getValue(Plants.class);
                        g_plants = p;
                        //strs
                        plantName.setText(String.valueOf(p.getName()));
                        date_view.setText(String.valueOf(p.getDate()));
                        sunlight.setText(String.valueOf(p.getSunlight()));
                        water_frequency.setText(String.valueOf(p.getWatering_freq()));
                        sci_name.setText(String.valueOf(p.getScientific_name()));

                        //ints
                        plantMoistureLevel =  p.calcCurrentMoisture();
                        target_moisture.setText(String.valueOf(targetMoistureInterpreter(plantMoistureLevel)));
                        watering_amount.setText(String.valueOf(p.getWatering_amount()));
                        getSupportActionBar().setTitle(String.valueOf(p.getName()));
                    }
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
                                p.setAddress("/Plants/" + noWS_c_plant);
                                Map<String, Object> postValues = p.toMap();
                                //System.out.println("VALUES OF CHANGING ACTIVE PLANT");
                                active_root.updateChildren(postValues);
                                Intent myIntent = new Intent(getApplicationContext(), myPlantsActivity.class);
                                startActivity(myIntent);
                                finish();
                            }
                        });
                    }
                    else{
                        System.out.println(p.getAddress());
                        String[] arrValues = p.getAddress().split(Pattern.quote("/"));
                        int simpleResult = p.calcCurrentMoisture();
                        if(!(noWS_c_plant.equals(arrValues[2]))){
                            //System.out.println("NOT ACTIVE PLANT");
                            activePlantCheck.setVisibility(View.VISIBLE);
                            move_config.setVisibility(View.INVISIBLE);

                            activePlantCheck.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    //if you're swapping over active_plants wipe the current settings
                                    //p.wipeProfile();
                                    p.setAddress("Plants/" + current_plant);
                                    Map<String, Object> postValues = p.toMap();
                                    active_root.updateChildren(postValues);
                                    System.out.println("UPDATED ACTIVE_PLANT");
                                    active_root.child("address").setValue("/Plants/" + noWS_c_plant);
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
                            //simpleResult = 0;
                            current_moisture.setText(String.valueOf(targetMoistureInterpreter(simpleResult)));
                            activePlantCheck.setVisibility(View.INVISIBLE);
                            move_config.setVisibility(View.VISIBLE);
                        }
                    }

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
        spin.setSelection(plantMoistureLevel-1);
        if(!watering_amount.getText().toString().equals("0")){
            dia_watering_amount.setText(String.valueOf(watering_amount.getText()));
        }

        manualWaterCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!(dia_watering_amount.getText().toString().equals("") || dia_watering_amount.getText().toString().equals("0") )){
                    active_root.child("arduinoRefresh").setValue(true);
                    dialog.dismiss();
                    //refresh the current activity
                    recreate();
                } else {
                    manualWaterCheck.setChecked(false);
                    Toast.makeText(view.getContext(),
                            "Please enter a watering time!",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
        autoRefreshCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!(dia_watering_amount.getText().toString().equals("") || dia_watering_amount.getText().toString().equals("0") )){
                    active_root.child("arduinoRefresh").setValue(true);
                    dialog.dismiss();
                    //refresh the current activity
                    recreate();
                } else {
                    autoRefreshCheck.setChecked(false);
                    Toast.makeText(view.getContext(),
                            "Please enter a watering time!",
                            Toast.LENGTH_SHORT).show();

                }
            }
        });
        submitDia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!(dia_watering_amount.getText().toString().equals("") || dia_watering_amount.getText().toString().equals("0") )){
                    //int moisture_input = Integer.valueOf(dia_target_moisture.getText().toString());
                    int moisture_input = moistureLevelValue;
                    int water_amt_input = Integer.valueOf(dia_watering_amount.getText().toString());
                    spin.getSelectedItem().toString();
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
                else {
                    Toast.makeText(view.getContext(),
                            "Please enter a watering time!",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });



        dialog.show();
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        int rValue = 0;
        /** DEBUG
        Toast.makeText(adapterView.getContext(),
                "OnItemSelectedListener : " +  adapterView.getItemAtPosition(i).toString(),
                Toast.LENGTH_SHORT).show();
        **/
        switch(i){
            case 0: rValue = 656;
            break;
            case 1: rValue = 560;
            break;
            case 2: rValue = 500;
            break;
            case 3: rValue = 450;
            break;
            case 4: rValue = 340;
            break;
        }
        moistureLevelValue = rValue;
    }
    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    public String targetMoistureInterpreter(int i){
        String str = "";
        switch(i){
            case 1:
                str = getString(R.string.level_1);
                break;
            case 2: str = getString(R.string.level_2);
                break;
            case 3: str = getString(R.string.level_3);
                break;
            case 4: str = getString(R.string.level_4);
                break;
            case 5: str = getString(R.string.level_5);
                break;
        }
        return str;
    }
}