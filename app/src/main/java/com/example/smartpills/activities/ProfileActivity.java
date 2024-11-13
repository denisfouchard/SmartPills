package com.example.smartpills.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.example.smartpills.R;
import com.example.smartpills.database.UserManager;
import com.example.smartpills.databinding.ActivityProfileBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.annotations.Nullable;

public class ProfileActivity extends BaseActivity<ActivityProfileBinding> {

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private DatabaseReference Database = FirebaseDatabase.getInstance("https://smartpills-8194e-default-rtdb.europe-west1.firebasedatabase.app/").getReference();
    private UserManager userManager = UserManager.getInstance();



    @Override
    ActivityProfileBinding getViewBinding() {
        return ActivityProfileBinding.inflate(getLayoutInflater());
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupListeners();
        updateUIWithUserData();
    }


    private void updateUIWithUserData(){
        if(mAuth.getCurrentUser() != null){
            FirebaseUser user = mAuth.getCurrentUser();
            setTextUserData(user);
        }
    }



    private void setTextUserData(FirebaseUser user){

        //Get email & username from User
        String email = TextUtils.isEmpty(user.getEmail()) ? getString(R.string.info_no_email_found) : user.getEmail();
        String username = TextUtils.isEmpty(user.getDisplayName()) ? getString(R.string.info_no_username_found) : user.getDisplayName();

        //Update views with data
        binding.usernameEditText.setText(username);
        binding.emailTextView.setText(email);
    }

    private void setupListeners(){
        // Sign out button
        binding.signOutButton.setOnClickListener(view -> {


                new AlertDialog.Builder(this)
                        .setMessage("Etes vous sur de vouloir vous déconnecter ?")
                        .setPositiveButton(R.string.popup_message_choice_yes, (dialogInterface, i) ->
                                userManager.signOut(ProfileActivity.this)
                                        .addOnSuccessListener(aVoid -> {
                                                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                    intent.putExtra("EXIT", true);
                                                    startActivity(intent);
                                                }
                                        )
                        )
                        .setNegativeButton(R.string.popup_message_choice_no, null)
                        .show();


            });


    // Delete button
        binding.deleteButton.setOnClickListener(view -> {

            new AlertDialog.Builder(this)
                    .setMessage(R.string.popup_message_confirmation_delete_account)
                    .setPositiveButton(R.string.popup_message_choice_yes, (dialogInterface, i) ->
                            userManager.deleteUser(ProfileActivity.this)
                                    .addOnSuccessListener(aVoid -> {
                                                Database.child("users").child(mAuth.getCurrentUser().getUid()).removeValue();
                                                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                intent.putExtra("EXIT", true);
                                                startActivity(intent);
                                            }
                                    )
                    )
                    .setNegativeButton(R.string.popup_message_choice_no, null)
                    .show();


        });
    }

    private void startMainActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

}


