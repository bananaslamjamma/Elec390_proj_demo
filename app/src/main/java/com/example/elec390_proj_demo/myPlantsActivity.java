package com.example.elec390_proj_demo;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.elec390_proj_demo.ui.login.LoginActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class myPlantsActivity extends AppCompatActivity {
    FirebaseAuth auth;
    Button button;
    TextView textView, homeNavText;
    FloatingActionButton fab_add_plant;
    // Parent layout
    LinearLayout parentLayout;
    // Layout inflater
    LayoutInflater layoutInflater;
    FirebaseUser user;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference u_root = database.getReference("users");

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView((R.layout.activity_myplants));
        String uid_loc;

        auth = FirebaseAuth.getInstance();
        button = findViewById(R.id.logout);
        textView = findViewById(R.id.user_info);
        fab_add_plant = findViewById(R.id.fab_plant_add);

        user = auth.getCurrentUser();
        if (user == null) {
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
            finish();
        }
        else {
            uid_loc = user.getUid();
            u_root = database.getReference("users/" + uid_loc);
            textView.setText(user.getEmail());
        }
        final int[] count = new int[1];
        u_root.child("/Plants").
                addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        if(snapshot.exists()){
                            int size = (int) snapshot.getChildrenCount();
                            //count[0] =(int) snapshot.getChildrenCount();
                            System.out.println("SIZE" + size);
                            // Parent layout
                            parentLayout = (LinearLayout) findViewById(R.id.parent_layout);
                            // Layout inflater
                            layoutInflater = getLayoutInflater();
                            View view;
                            for (DataSnapshot snap : snapshot.getChildren()) {
                                //NOTE: THE CLASS MAPPER REQUIRES SETTERS NAMED EXACTLY AS DEFINED IN:
                                // public Map<String, Object> toMap
                                Plants p = snap.getValue(Plants.class);
                                System.out.println(p.name);
                                // Add the text layout to the parent layout
                                view = layoutInflater.inflate(R.layout.text_layout, parentLayout, false);
                                // In order to get the view we have to use the new view with text_layout in it
                                TextView textView = (TextView) view.findViewById(R.id.text);
                                textView.setText(p.getName());
                                // Add the text view to the parent layout
                                parentLayout.addView(textView);
                            }
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
        Log.d(TAG, "count[0]");
        //should encapsulate this into a function
        fab_add_plant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), plantRegistrationActivity.class);
                startActivity(intent);
                finish();
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //log user out
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
