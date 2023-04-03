package com.example.elec390_proj_demo;

import static android.content.ContentValues.TAG;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.bumptech.glide.Glide;
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
    Dialog delete;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference u_root = database.getReference("users");
    DatabaseReference p_root;
    ProgressDialog progressDialog;
    Boolean darkmode;
    Switch switch_view;
    private SharedPreferenceHelper sharedPreferenceHelper;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        //make "Edit Plants" hidden
        MenuItem item = menu.findItem(R.id.help_mode);
        MenuItem switch_item = menu.findItem(R.id.dark_mode_switch);
        Switch actionView = (Switch) switch_item.getActionView();
        actionView.setText("Dark Mode");
        sharedPreferenceHelper = new SharedPreferenceHelper(myPlantsActivity.this);
        darkmode = sharedPreferenceHelper.readDarkMode();
        switch_view = actionView;

        switch (getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) {
            case Configuration.UI_MODE_NIGHT_YES:
                actionView.setChecked(true);
                sharedPreferenceHelper.saveDarkMode(true);
                break;
            case Configuration.UI_MODE_NIGHT_NO:
                actionView.setChecked(false);
                sharedPreferenceHelper.saveDarkMode(false);
                // process
                break;
        }
        //Destruction of activities
        actionView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(sharedPreferenceHelper.readDarkMode() == true){
                    System.out.println(sharedPreferenceHelper.readDarkMode());
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    sharedPreferenceHelper.saveDarkMode(false);
                    actionView.setChecked(false);
                } else if (sharedPreferenceHelper.readDarkMode() == false){
                    System.out.println(sharedPreferenceHelper.readDarkMode());
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    sharedPreferenceHelper.saveDarkMode(true);
                    actionView.setChecked(true);
                }
            }
        });
        item.setVisible(false);
        menu.getItem(2).setVisible(false);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item){
        System.out.println(item.getItemId());
        switch (item.getItemId()){
            case R.id.log_out:
                //logout
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                finish();
                break;
            case R.id.dark_mode_switch:
                System.out.println("EAT POOP");
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView((R.layout.activity_myplants));
        String uid_loc;
        auth = FirebaseAuth.getInstance();
        button = findViewById(R.id.logout);
        textView = findViewById(R.id.user_info);
        fab_add_plant = findViewById(R.id.fab_plant_add);

        delete = new Dialog(myPlantsActivity.this);
        delete.requestWindowFeature(Window.FEATURE_NO_TITLE);
        delete.setCancelable(true);
        delete.setContentView(R.layout.dialog_confirm_delete);

        progressDialog = new ProgressDialog(myPlantsActivity.this);
        progressDialog.setMessage("Loading results");
        progressDialog.setCancelable(false);
        progressDialog.show();
        getSupportActionBar().setTitle("Home");

        user = auth.getCurrentUser();
        if (user == null) {
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
            finish();
        } else {
            System.out.println("USER");
            System.out.println(user);
            uid_loc = user.getUid();
            u_root = database.getReference("users/" + uid_loc);
            textView.setText(user.getEmail());
        }

        u_root.child("/Plants").
                addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            int size = (int) snapshot.getChildrenCount();
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
                                System.out.println(p.getScientific_name());
                                // Add the text layout to the parent layout
                                view = layoutInflater.inflate(R.layout.profile_results,null);
                                //view = layoutInflater.inflate(R.layout.profile_results, parentLayout, false);
                                // In order to get the view we have to use the new view with text_layout in it
                                TextView textView = (TextView) view.findViewById(R.id.plant_name_home);
                                textView.setText(String.valueOf(p.getName()));
                                TextView familyName = view.findViewById(R.id.plant_name_species_home);
                                familyName.setText(p.scientific_name);
                                ImageView img = view.findViewById(R.id.plant_image_home);
                                Glide.with(myPlantsActivity.this).load(
                                        p.getPlant_url() +
                                                "").into(img);
                                RelativeLayout container = view.findViewById(R.id.container_relative);
                                Button deleteButton = view.findViewById(R.id.delete_plant_home);
                                deleteButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        delete.show();
                                        CheckBox checkDelete = delete.findViewById(R.id.delete_check);
                                        checkDelete.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                u_root.child("/Plants/" + p.getName().replaceAll("\\s+","")).removeValue();
                                                Toast.makeText(myPlantsActivity.this, "Plant deleted!",
                                                        Toast.LENGTH_SHORT).show();
                                                delete.dismiss();
                                                recreate();
                                            }
                                        });
                                    }
                                });
                                container.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        String str;
                                        //note for whatever reason, firebase does not like the path_name to have whitespaces
                                        str = (u_root.child("/Plants/" + p.name.replaceAll("\\s+","")).toString());
                                        //retrieve url to plant item and pass it on to intent to init it in new Activity
                                        //p_root = FirebaseDatabase.getInstance().getReferenceFromUrl(str);
                                        Intent intent = new Intent(myPlantsActivity.this, plantProfileActivity.class);
                                        intent.putExtra("plant_url", str);
                                        intent.putExtra("p_nameWhite", p.name.replaceAll("\\s+",""));
                                        intent.putExtra("plant_name", p.name);
                                        intent.putExtra("plant_img", p.getPlant_url());
                                        startActivity(intent);
                                        //finish(); should be able to go back using back button
                                    }
                                });
                                // Add the text view to the parent layout
                                parentLayout.addView(view);
                            }
                        }
                        progressDialog.dismiss();
                    }


                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
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
