package com.example.smartpills.activities;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.smartpills.R;
import com.example.smartpills.database.PilulierDatabase;
import com.example.smartpills.database.UserDatabase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

@RequiresApi(api = Build.VERSION_CODES.N)
public class ManualAddActivity extends AppCompatActivity {

    private Button retour;
    private TextInputLayout tilPassword;
    private EditText spId,spPwd;
    private Button btConfirm;
    private UserDatabase currentUser = new UserDatabase();
    private final int SmartPillsIdLenght = 10;

    private DatabaseReference Database = FirebaseDatabase.getInstance("https://smartpills-8194e-default-rtdb.europe-west1.firebasedatabase.app/").getReference();

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_manual_add);
        spId = findViewById(R.id.smartPillsIDTextView);
        spPwd = findViewById(R.id.smartPillsPasswordTextView);
        btConfirm = findViewById(R.id.addPilulierButton);

        retour = findViewById(R.id.retourButton);
        retour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(v.getContext());
                alert.setMessage("Voulez vous vraiment annuler l'ajout d'un nouveau pilulier ?");
                alert.setPositiveButton("OUI", (dialogInterface, i) -> switchActivities(HomeScreenActivity.class));
                alert.setNegativeButton("Annuler", null);
                alert.show();

            }
        });

        spId.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                spId.setError(null);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        btConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String typedId = spId.getText().toString();
                String typedPassword = spPwd.getText().toString();

                PilulierDatabase typedPilulier = new PilulierDatabase(typedId, typedPassword);

                DatabaseReference rootRef = Database.child("pilulier");


                rootRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if (!task.isSuccessful()) {
                            Log.e("firebase", "Error getting data", task.getException());
                        }
                        else {
                            Log.d("firebase", String.valueOf(task.getResult().getValue()));
                            typedPilulier.setExists(task.getResult().hasChild(typedId));



                            //Une fois l'existence checkée, on lance le processus

                            if(!typedPilulier.isExists() || typedId.isEmpty()){

                                spId.getText().clear();
                                spPwd.getText().clear();

                                spId.setError("L'identifiant est incorrect");


                            } else {
                                System.out.println("ManualAddActivity : Est ce que il existe ? " + typedPilulier.isExists());


                                String realPassword = task.getResult().child(typedId).child("password").getValue().toString();
                                boolean pwdCorrect = (realPassword.compareTo(typedPassword) == 0);

                                System.out.println("Password réel : " + realPassword);

                                typedPilulier.setPasswordCorrect(pwdCorrect);
                                System.out.println("Password saisi : " + typedPassword);

                                System.out.println("Password is correct : " + pwdCorrect);


                                if (!typedPilulier.isPwdCorrect()){

                                    AlertDialog.Builder alert = new AlertDialog.Builder(view.getContext());
                                    alert.setMessage("L'identifiant ou le mot de passe est incorrect");
                                    alert.setPositiveButton("Ok", null);
                                    alert.show();

                                    spId.getText().clear();
                                    spPwd.getText().clear();

                                    spId.setError("L'identifiant ou le mot de passe est incorrect");
                                    spPwd.setError("L'identifiant ou le mot de passe est incorrect");
                                } else {

                                    currentUser.addSmartPills(typedPilulier);
                                    //currentUser.updateUser();

                                    //Boite de dialogue de validation

                                    new AlertDialog.Builder(ManualAddActivity.this)
                                            .setMessage("Le pilulié a été enregistré avec succès !")
                                            .setPositiveButton("Configurer", (dialogInterface, i) -> {
                                                        Intent intent = new Intent(ManualAddActivity.this, NewPilulierSettingsActivity.class);
                                                        intent.putExtra("typedPilulier", typedPilulier);
                                                        startActivity(intent);
                                                    }
                                                        )
                                            .show();




                                }

                            }

                        }
                    }
                });



        }});}



    /*

    public void ClickMenu(View view){
        HomeScreenActivity.openDrawer(drawerLayout);
    }

    public void ClickLogo(View view){
        HomeScreenActivity.closeDrawer(drawerLayout);
    }

    public void ClickHome(View view){
        switchActivities(HomeScreenActivity.class);
    }

    public void ClickProfile(View view){
        switchActivities(ProfileActivity.class);
    }

    public void ClickDeconnexion(View view){
        logout(this);
    }

    public void logout(Activity activity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Déconnexion");
        builder.setMessage("Etes-vous sûr de vouloir vous déconnecter?");
        builder.setPositiveButton("OUI", (dialog, i) -> {
            UserManager userManager = UserManager.getInstance();
            userManager.signOut(activity).addOnSuccessListener(aVoid -> {
                finish();
            });
        });
        builder.setNegativeButton("NON", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                dialog.dismiss();
            }
        });

        builder.show();
    }
    */


    public void switchActivities(Class aClass) {
        Intent switchActivityIntent = new Intent(this, aClass);
        startActivity(switchActivityIntent);
    }


    @Override
    protected void onPause() {
        super.onPause();
       // HomeScreenActivity.closeDrawer(drawerLayout);
    }
}