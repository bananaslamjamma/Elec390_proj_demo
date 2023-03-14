package com.example.elec390_proj_demo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
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

public class plantProfileActivity extends AppCompatActivity {

    FirebaseAuth auth;
    FirebaseUser user;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference u_root = database.getReference("users");
    DatabaseReference p_root;
    ImageView img;
    TextView plant_view, description_view, date_view, status_view;
    Button save_change_button, move_config;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
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
        plant_view = findViewById(R.id.p_name);
        description_view = findViewById(R.id.p_descrp);
        date_view = findViewById(R.id.p_date);
        status_view = findViewById(R.id.status);
        //Buttons
        save_change_button = findViewById(R.id.save_button);
        move_config = findViewById(R.id.mov_config);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        if (user == null) {
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
            finish();
        } else {
            uid_loc = user.getUid();
            u_root = database.getReference("users/" + uid_loc);
        }

        //retrieve data from other activity
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            //The key argument here must match that used in the other activity
            String value = extras.getString("plant_url");
            p_root = FirebaseDatabase.getInstance().getReferenceFromUrl(value);
        }

        //get data once
        p_root.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    System.out.println(("firebase " + " Error getting data " + task.getException()));
                }
                else {
                    System.out.println(("firebase " + String.valueOf(task.getResult().getValue())));
                    Plants p = task.getResult().getValue(Plants.class);
                    System.out.println(p);
                    plant_view.setText("Plant Name: " + String.valueOf(p.getName()));
                    status_view.setText("Plant Status: " +String.valueOf(p.getFlag()));
                    //description_view.setText(p.setDescription());
                    date_view.setText("Plant Date: " +String.valueOf(p.getDate()));
                    getSupportActionBar().setTitle(String.valueOf(p.getName()));
                }
            }
        });

    }
}