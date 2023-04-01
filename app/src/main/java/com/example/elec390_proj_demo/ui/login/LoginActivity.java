package com.example.elec390_proj_demo.ui.login;


import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.elec390_proj_demo.databinding.ActivityLoginBinding;
import com.example.elec390_proj_demo.myPlantsActivity;

import com.example.elec390_proj_demo.Plants;
import com.example.elec390_proj_demo.R;
import com.example.elec390_proj_demo.RegisterActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Calendar;

/**
 * https://firebase.google.com/docs/database/android/read-and-write
 * TODO: Create Objects for Plants so can push easily and and update easily. (can only update objects)
 *
 */

public class LoginActivity extends AppCompatActivity {
    FirebaseAuth mAuth;

    private LoginViewModel loginViewModel;
    private ActivityLoginBinding binding;
    //private FireBaseDatabase database = FirebaseDatabase.getInstance();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference root = database.getReference("users");
    DatabaseReference best_sellers = root.child("Best_Sellers");
    DatabaseReference plants = root.child("Plants");

    DatabaseReference active = plants.child("Active");
    DatabaseReference element_test = best_sellers.child("-NO_tty67z-rn950GVHz");

    DatabaseReference chr = plants.child("Chrysanthemum");
    Date date = Calendar.getInstance().getTime();
    DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
    String strDate = dateFormat.format(date);
    TextView registerNavText;
    EditText emailDia, passwordDia;
    Button submitDia;
    Dialog dialog;


    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            //open main activity that needs
            Intent intent = new Intent(getApplicationContext(), myPlantsActivity.class);
            startActivity(intent);
            finish();
            //reload();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        loginViewModel = new ViewModelProvider(this, new LoginViewModelFactory())
                .get(LoginViewModel.class);
        mAuth = FirebaseAuth.getInstance();

        final EditText usernameEditText = binding.username;
        final EditText passwordEditText = binding.password;
        final Button loginButton = binding.login;
        final Button resetPass = binding.resetPass;
        //final Button sendButton = binding.sendData;
        final Button acButton = binding.active;
        final Button getButton = binding.getData;
        final ProgressBar loadingProgressBar = binding.loading;
        //dialog
        dialog = new Dialog(LoginActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.custom_dialog);
        emailDia = dialog.findViewById(R.id.email_dia);
        passwordDia = dialog.findViewById(R.id.password_dia);
        submitDia = dialog.findViewById(R.id.submit_dia);
        //remove to init
        //setPlantsStack();


        registerNavText = findViewById(R.id.registerPage);
        registerNavText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(intent);
                finish();
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadingProgressBar.setVisibility(View.VISIBLE);
                String email, password;
                email = String.valueOf(usernameEditText.getText());
                password = String.valueOf((passwordEditText.getText()));

                if (TextUtils.isEmpty(email)){
                    Toast.makeText(LoginActivity.this, "Enter email", Toast.LENGTH_LONG).show();
                    return;
                }
                if (TextUtils.isEmpty(password)){
                    Toast.makeText(LoginActivity.this, "Enter Password", Toast.LENGTH_LONG).show();
                    return;
                }

                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener( new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                loadingProgressBar.setVisibility(View.GONE);
                                if (task.isSuccessful()) {
                                    Toast.makeText(getApplicationContext(),"Login Successful!", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(getApplicationContext(), myPlantsActivity.class);
                                    startActivity(intent);
                                    finish();
                                    // Sign in success, update UI with the signed-in user's information
                                    //updateUI(user);
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Toast.makeText(LoginActivity.this, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();

                                }
                            }
                        });

            }
        });


        resetPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopUp();
            }
        });




/**
 *

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Map<String, String> message = new HashMap<>();
                Map<String, Integer> message2 = new HashMap<>();

                //message2.put("Flag" , 1);
                message.put("Active" , "/Books/Plants/Chrysanthemum");
                //root.push().setValue(message);
                // System.out.println(root);
                //note that if child "plants doesn't exist, it'll force create one.
                plants.child("Active Plants").setValue(message);
                //plants.push().setValue(message);
            }
        });

 **/
        acButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Map<String, String> message = new HashMap<>();
                message.put("Active" , "/Books/Plants/Daylily");
                //note that if child "plants doesn't exist, it'll force create one.
                plants.child("Active Plants").setValue(message);
            }
        });
        getButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //this just flips the flag to 0.
                Map<String, Object> message2 = new HashMap<>();
                DatabaseReference childUpdate = plants.child("Chrysanthemum");
                message2.put("Chrysanthemum/Flag", 0);
                System.out.println(element_test);
                plants.updateChildren(message2);
            }
        });

        /**
         * deprecated, changed data structure
         * detects if value was changed in db, this will get the entire Plants node

        plants.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.i("NODE", dataSnapshot.child("Chrysanthemum/name").getValue(String.class));
                System.out.println("DATA SNAPSHOTTED");
                System.out.println(dataSnapshot.child("Chrysanthemum/Flag").getValue(Integer.class));
                Map<String, Object> reset = new HashMap<>();
                reset.put("Chrysanthemum/Flag", 1);
                plants.updateChildren(reset);
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("ERROR", "onCancelled", databaseError.toException());
            }
        });
         */

        loginViewModel.getLoginFormState().observe(this, new Observer<LoginFormState>() {
            @Override
            public void onChanged(@Nullable LoginFormState loginFormState) {
                if (loginFormState == null) {
                    return;
                }
                loginButton.setEnabled(loginFormState.isDataValid());
                if (loginFormState.getUsernameError() != null) {
                    usernameEditText.setError(getString(loginFormState.getUsernameError()));
                }
                if (loginFormState.getPasswordError() != null) {
                    passwordEditText.setError(getString(loginFormState.getPasswordError()));
                }
            }
        });

        loginViewModel.getLoginResult().observe(this, new Observer<LoginResult>() {
            @Override
            public void onChanged(@Nullable LoginResult loginResult) {
                if (loginResult == null) {
                    return;
                }
                loadingProgressBar.setVisibility(View.GONE);
                if (loginResult.getError() != null) {
                    showLoginFailed(loginResult.getError());
                }
                if (loginResult.getSuccess() != null) {
                    updateUiWithUser(loginResult.getSuccess());
                }
                setResult(Activity.RESULT_OK);

                //Complete and destroy login activity once successful
                finish();
            }
        });

        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // ignore
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // ignore
            }

            @Override
            public void afterTextChanged(Editable s) {
                loginViewModel.loginDataChanged(usernameEditText.getText().toString(),
                        passwordEditText.getText().toString());
            }
        };
        usernameEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.addTextChangedListener(afterTextChangedListener);
        emailDia.addTextChangedListener(afterTextChangedListener);
        passwordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    loginViewModel.login(usernameEditText.getText().toString(),
                            passwordEditText.getText().toString());
                }
                return false;
            }
        });

/**
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingProgressBar.setVisibility(View.VISIBLE);
                loginViewModel.login(usernameEditText.getText().toString(),
                        passwordEditText.getText().toString());
            }
        });
 **/
    }

    private void updateUiWithUser(LoggedInUserView model) {
        String welcome = getString(R.string.welcome) + model.getDisplayName();
        // TODO : initiate successful logged in experience
        Toast.makeText(getApplicationContext(), welcome, Toast.LENGTH_LONG).show();
    }

    private void showLoginFailed(@StringRes Integer errorString) {
        Toast.makeText(getApplicationContext(), errorString, Toast.LENGTH_SHORT).show();
    }
    private void setPlantsStack(){
        Map<String, Plants> plantsMap = new HashMap<>();
        plantsMap.put("Chrysanthemum", new Plants("Chrysanthemum",strDate, 0));
        plantsMap.put("Rose", new Plants("Rose",strDate, 0));
        plantsMap.put("Daylily", new Plants("Daylily",strDate, 0));
        plantsMap.put("Hyacinth ", new Plants("Hyacinth",strDate, 0));
        plantsMap.put("Carnation", new Plants("Carnation",strDate, 0));
        plants.setValue(plantsMap);
    }

    private void resetEmail(String emailAddress) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.setLanguageCode("en");
        //String emailAddress = "user@example.com";
        auth.sendPasswordResetEmail(emailAddress)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this, "Email sent!",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(LoginActivity.this, "Not a registered email!",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void showPopUp(){
        submitDia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email = String.valueOf(emailDia.getText());
                //String password = String.valueOf(passwordDia.getText());
                resetEmail(email);
                dialog.dismiss();
            }
        });
        dialog.show();
    }

}