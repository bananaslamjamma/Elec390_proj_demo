package com.example.elec390_proj_demo;

import static android.content.ContentValues.TAG;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.elec390_proj_demo.ui.login.LoggedInUserView;
import com.example.elec390_proj_demo.ui.login.LoginActivity;
import com.example.elec390_proj_demo.ui.login.LoginFormState;
import com.example.elec390_proj_demo.ui.login.LoginResult;
import com.example.elec390_proj_demo.ui.login.LoginViewModel;
import com.example.elec390_proj_demo.ui.login.LoginViewModelFactory;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

/**
 * ELEC-390 Soil Sense
 * Created by Evan Yu on 04/01/2023.
 * @RegisterActivity.java
 * Activity to handle the registration for a user
 */

public class RegisterActivity extends AppCompatActivity {

    EditText editEmail, editPw, editUser;
    Button registerButton;
    FirebaseAuth mAuth;
    ProgressBar progressBar;
    TextView loginNavText;
    private LoginViewModel loginViewModel;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference root = database.getReference("users");

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
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView((R.layout.activity_register));
        loginViewModel = new ViewModelProvider(this, new LoginViewModelFactory())
                .get(LoginViewModel.class);
        mAuth = FirebaseAuth.getInstance();
        editEmail = findViewById(R.id.regEmail);
        editPw = findViewById(R.id.regPW);
        editUser = findViewById(R.id.user_name);
        registerButton = findViewById(R.id.registerButton);
        progressBar = findViewById(R.id.loading);
        loginNavText = findViewById(R.id.loginPage);

        loginNavText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });


        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                String email, password, user_name;
                progressBar.setVisibility(View.VISIBLE);
                email = String.valueOf(editEmail.getText());
                password = String.valueOf(editPw.getText());
                user_name = String.valueOf(editUser.getText());

                if (TextUtils.isEmpty(email)){
                    Toast.makeText(RegisterActivity.this, "Enter email", Toast.LENGTH_LONG).show();
                    return;
                }
                if (TextUtils.isEmpty(password)){
                    Toast.makeText(RegisterActivity.this, "Enter Password", Toast.LENGTH_LONG).show();
                    return;
                }

                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                progressBar.setVisibility(View.GONE);
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Toast.makeText(RegisterActivity.this, "User Registered.",
                                            Toast.LENGTH_SHORT).show();
                                    Log.d(TAG, "createUserWithEmail:success");
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    //Create a blank Active Plant profile for the user
                                    ActivePlant blank_prof = new ActivePlant();
                                    Map<String, Object> activePlantValues = blank_prof.toMap();
                                    Map<String,Object> childUpdates = new HashMap<>();
                                    childUpdates.put("Active Plant", activePlantValues);
                                    //note this redirects you to the loginActivity, which should redirect to home if logged in
                                    //creating a user in db
                                    Map<String, String> users = new HashMap<>();
                                    users.put("user_name" , user_name);
                                    users.put("u_id", user.getUid());
                                    //create a child with this name/u_id:
                                    root.child(user.getUid());
                                    //set write to this new address
                                    root = root.child(user.getUid());
                                    //write to here | does not overwrite data in /users/
                                    root.setValue(users);
                                    //write new blank profile
                                    root.updateChildren(childUpdates);
                                    //return back to login
                                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                                    startActivity(intent);
                                    finish();
                                    //updateUI(user);
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                    Toast.makeText(RegisterActivity.this, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }

        });

        loginViewModel.getLoginFormState().observe(this, new Observer<LoginFormState>() {
            @Override
            public void onChanged(@Nullable LoginFormState loginFormState) {
                if (loginFormState == null) {
                    return;
                }
                registerButton.setEnabled(loginFormState.isDataValid());
                if (loginFormState.getUsernameError() != null) {
                    editEmail.setError(getString(loginFormState.getUsernameError()));
                }
                if (loginFormState.getPasswordError() != null) {
                    editPw.setError(getString(loginFormState.getPasswordError()));
                }
            }
        });

        loginViewModel.getLoginResult().observe(this, new Observer<LoginResult>() {
            @Override
            public void onChanged(@Nullable LoginResult loginResult) {
                if (loginResult == null) {
                    return;
                }
                progressBar.setVisibility(View.GONE);
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
                loginViewModel.loginDataChanged(editEmail.getText().toString(),
                        editPw.getText().toString());
            }
        };
        editEmail.addTextChangedListener(afterTextChangedListener);
        editPw.addTextChangedListener(afterTextChangedListener);
        editPw.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    loginViewModel.login(editEmail.getText().toString(),
                            editPw.getText().toString());
                }
                return false;
            }
        });
    }

    private void updateUiWithUser(LoggedInUserView model) {
        String welcome = getString(R.string.welcome) + model.getDisplayName();
        Toast.makeText(getApplicationContext(), welcome, Toast.LENGTH_LONG).show();
    }

    private void showLoginFailed(@StringRes Integer errorString) {
        Toast.makeText(getApplicationContext(), errorString, Toast.LENGTH_SHORT).show();
    }

}
