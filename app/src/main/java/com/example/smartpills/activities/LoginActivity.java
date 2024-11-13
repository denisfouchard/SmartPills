package com.example.smartpills.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.example.smartpills.R;
import com.example.smartpills.database.UserDatabase;
import com.example.smartpills.databinding.ActivityLoginBinding;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Collections;
import java.util.List;


public class LoginActivity extends BaseActivity<ActivityLoginBinding> {

    private static final int RC_SIGN_IN = 123;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    public DatabaseReference Database = FirebaseDatabase.getInstance("https://smartpills-8194e-default-rtdb.europe-west1.firebasedatabase.app/").getReference();

    //Création du drawer



    @Override
    ActivityLoginBinding getViewBinding() {
        return ActivityLoginBinding.inflate(getLayoutInflater());
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getIntent().getBooleanExtra("EXIT", false)) {
            updateLoginButton();
        }
        if (mAuth.getCurrentUser() != null){

            updateUser();
            setContentView(R.layout.activity_home_screen);



        } else {

        setupListeners();

        }

    }

    protected void onResume() {
        super.onResume();
        if (mAuth.getCurrentUser() != null){
            startFirstScreenActivity();
        }
        updateLoginButton();

    }



    private void setupListeners(){
        // Login/Profile Button
        binding.loginButton.setOnClickListener(view -> {
            if(mAuth.getCurrentUser() != null){

            }else{
                startSignInActivity();

            }
        });
    }

    private void startSignInActivity(){
        // Choose authentication providers
        List<AuthUI.IdpConfig> providers;
        providers = Collections.singletonList(new AuthUI.IdpConfig.EmailBuilder().build());

        // Launch the activity
        startActivityForResult(AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setTheme(R.style.LoginTheme)
                .setAvailableProviders(providers)
                .setIsSmartLockEnabled(false, true)
                .setLogo(R.drawable.ic_icon)
                .build(), RC_SIGN_IN);
        updateUser();
        startFirstScreenActivity();
    }


    private void startProfileActivity(){
        Intent intent = new Intent(this, ProfileActivity.class);
        startActivity(intent);
    }

    private void startFirstScreenActivity() {

        Intent intent = new Intent(this, HomeScreenActivity.class);

        startActivity(intent);
    }

    private UserDatabase getCurrentUser(){

        return new UserDatabase();

    }

    public void updateUser() {
        //getCurrentUser().updateUser();
    }

    // Update Login Button when activity is resuming
    private void updateLoginButton(){
        binding.loginButton.setText(mAuth.getCurrentUser() != null ? getString(R.string.button_login_text_logged) : getString(R.string.button_login_text_not_logged));
    }
}