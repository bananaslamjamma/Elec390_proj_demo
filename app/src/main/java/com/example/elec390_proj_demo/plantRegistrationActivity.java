package com.example.elec390_proj_demo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.elec390_proj_demo.ui.login.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class plantRegistrationActivity extends AppCompatActivity {
    TextView textView, homeNavText;
    EditText field1, field2, n_plant;
    Button submitButton;
    FirebaseAuth auth;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference u_root = database.getReference("users");
    FirebaseUser user;
    String uid_loc;

    Date date = Calendar.getInstance().getTime();
    DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
    String strDate = dateFormat.format(date);





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plant_registration);
        homeNavText = findViewById(R.id.plantProfileReturn);
        submitButton = findViewById(R.id.submitButton);
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

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String plantName;
                plantName = String.valueOf(n_plant.getText());

                Map<String, Plants> plantsMap = new HashMap<>();
                Plants plant = new Plants(plantName,strDate, 0);
                //see Plants class for more info
                Map<String, Object> plantValues = plant.toMap();
                Map<String,Object> childUpdates = new HashMap<>();
                childUpdates.put(plantName, plantValues);
                u_root.updateChildren(childUpdates);
            }
        });
    }
}