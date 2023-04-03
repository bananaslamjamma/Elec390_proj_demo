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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.elec390_proj_demo.ui.login.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.apache.commons.text.WordUtils;

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
    Date date = new Date(System.currentTimeMillis());
    DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    String strDate = dateFormat.format(date);
    PerenualHandler asyncTask = new PerenualHandler();
    Dialog dialog, confirm;
    LinearLayout layout;
    CheckBox confirm_add;

    //ArrayList<Plants> apiPlantsList = new ArrayList<Plants>();

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        //make "Help " hidden
        MenuItem item = menu.findItem(R.id.help_mode);
        item.setVisible(false);
        //menu.getItem(2).setVisible(false);
        MenuItem switch_item = menu.findItem(R.id.dark_mode_switch);
        switch_item.setVisible(false);
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
        //dialog #1
        dialog = new Dialog(plantRegistrationActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.dialog_api);
        layout = dialog.findViewById(R.id.container_api);
        //dialog #2
        confirm = new Dialog(plantRegistrationActivity.this);
        confirm.requestWindowFeature(Window.FEATURE_NO_TITLE);
        confirm.setCancelable(true);
        confirm.setContentView(R.layout.dialog_confirm_add);
        //other views
        field2 =  findViewById(R.id.field2);
        homeNavText = findViewById(R.id.plantProfileReturn);
        submitButton = findViewById(R.id.submitButton);
        apiButton = findViewById(R.id.testButton);
        n_plant = findViewById(R.id.name_plant);
        //auth
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
                //destroy previous views
                layout = dialog.findViewById(R.id.container_api);
                layout.removeAllViews();
                String input = "rose";
                input = String.valueOf(field2.getText());
                syncTasks(input);
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
                childUpdates.put(plantName.replaceAll("\\s+",""), plantValues);
                u_root.updateChildren(childUpdates);
                recreate();
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
        TextView sci_name = view.findViewById(R.id.plant_name_species);

        RelativeLayout container = view.findViewById(R.id.container_relative);
        container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirm_add = confirm.findViewById(R.id.confirm_check);
                confirm_add.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String plantName;
                        plantName = p.getName();
                        if(plantName.equals("Active Plant")){
                            Toast.makeText(plantRegistrationActivity.this, "Invalid Name!.",
                                    Toast.LENGTH_SHORT).show();
                            return;
                        }
                        //Plants plant = new Plants(plantName,strDate, 0);
                        //see Plants class for more info
                        Map<String, Object> plantValues = p.toMap();
                        Map<String,Object> childUpdates = new HashMap<>();
                        childUpdates.put(plantName.replaceAll("\\s+",""), plantValues);
                        u_root.updateChildren(childUpdates);
                        confirm.dismiss();
                        dialog.dismiss();
                        Toast.makeText(plantRegistrationActivity.this, "New Plant Added!",
                                Toast.LENGTH_SHORT).show();
                        confirm_add.setChecked(false);
                    }
                });
                confirm.show();
            }
        });
        ImageView icon = view.findViewById(R.id.plant_image);
        text.setText(WordUtils.capitalize(String.valueOf(p.getName())));
        sci_name.setText(WordUtils.capitalize(String.valueOf(p.getScientific_name())));
        Glide.with(this).load(
                p.getPlant_url() +
                "").into(icon);
        layout.addView(view);
    }

    @Override
    public void processFinish(ArrayList<Plants> output) {
        ArrayList<Plants> apiPlantsList = new ArrayList<Plants>(output);
        for(int i = 0; i< apiPlantsList.size(); i++){
            addSearchItem(apiPlantsList.get(i));
        }
        if(apiPlantsList.isEmpty()){
            Toast.makeText(plantRegistrationActivity.this, "No Results Found!",
                    Toast.LENGTH_SHORT).show();
        }
        dialog.show();
        progressDialog.dismiss();
    }

    @Override
    public void beforeProcess(ArrayList<Plants> apiPlantsList){

    }
}