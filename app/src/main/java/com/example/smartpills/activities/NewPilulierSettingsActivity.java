package com.example.smartpills.activities;

import static com.firebase.ui.auth.util.data.PhoneNumberUtils.isValid;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.smartpills.R;
import com.example.smartpills.database.PilulierDatabase;
import com.example.smartpills.database.UserDatabase;
import com.example.smartpills.database.UserManager;

public class NewPilulierSettingsActivity extends AppCompatActivity {
    DrawerLayout drawerLayout;
    EditText newPilulierNom,newPilulierPrenom, newPhoneNumber, spPwd;
    Button btConfirm;
    PilulierDatabase typedPilulier;
    UserDatabase currentUser = new UserDatabase();
    //todo : Sérialiser l'utilisateur et le faire passer entre les activités
    boolean prenomValide = false;
    boolean nomValide = false;
    boolean phoneNumberValide = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_pilulier_settings);

        drawerLayout = findViewById(R.id.LinearLayout);

        //Récupération de l'objet pilulié saisi à l'activité précédente
        if(getIntent().getExtras() != null) {
            typedPilulier = (PilulierDatabase) getIntent().getParcelableExtra("typedPilulier");
        }

        //Récupération des éléments d'interface
        newPilulierNom = findViewById(R.id.newPilulierNom);
        newPilulierPrenom = findViewById(R.id.newPilulierPrenom);
        spPwd = findViewById(R.id.smartPillsPasswordTextView);
        btConfirm = findViewById(R.id.bt_confirm);
        newPhoneNumber = findViewById(R.id.phoneNumber);



        newPilulierNom.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                if (!s.toString().matches("[a-zA-Z ]+")){
                    newPilulierNom.setError("Uniquement du texte est autorisé");
                    nomValide = false;
                }
                else {
                    if (s.toString().isEmpty() ){
                        nomValide = false;
                        newPilulierNom.setError("Champ vide");
                    }
                    else{
                        nomValide = true;
                        newPilulierNom.setError(null);
                    }

                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        newPilulierPrenom.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                if (!s.toString().matches("[a-zA-Z ]+")){
                    prenomValide = false;
                    newPilulierPrenom.setError("Uniquement du texte est autorisé");
                }
                else {
                    if (s.toString().isEmpty() ){
                        prenomValide = false;
                        newPilulierPrenom.setError("Champ vide");
                    }
                    else{
                        prenomValide = true;
                        newPilulierPrenom.setError(null);
                    }

                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        /*
        newMedicine.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                if (!s.toString().isEmpty() && !s.toString().contains(";")){
                    medicineValide = false;
                    newMedicine.setError("Il manque des informations : médicament ; nombre de pilules");
                }
                else {
                    if (s.toString().isEmpty() ){
                        medicineValide = false;
                    }
                    else{
                        medicineValide = true;
                    }
                    newMedicine.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });  */

        newPhoneNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int i, int i1, int i2) {

            }

            @SuppressLint("RestrictedApi")
            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                String typedNumber = s.toString();
                if (!typedNumber.isEmpty() && !typedNumber.contains(";") && isValid(typedNumber)){
                    phoneNumberValide = false;
                    newPhoneNumber.setError("Veuillez insérer un numéro de téléphone valide");
                }
                else {

                    phoneNumberValide = true;
                    newPhoneNumber.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });



        btConfirm.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View view) {


                if (phoneNumberValide && nomValide && prenomValide) {

                    //Settings for new Pilulier
                    String prenom = newPilulierPrenom.getText().toString();
                    String nom = newPilulierNom.getText().toString();
                    String phoneNumber = newPhoneNumber.getText().toString();
                    typedPilulier.setUtlisateur(prenom + " " + nom);
                    typedPilulier.setPhoneNumber(phoneNumber);

                    //Add pilulier to
                    currentUser.addSmartPillsToAccount(typedPilulier);

                    new AlertDialog.Builder(NewPilulierSettingsActivity.this)
                            .setMessage("Le pilulié a été enregistré avec succès !")
                            .setPositiveButton("OK", (dialogInterface, i) -> {
                                switchActivities(HomeScreenActivity.class);


                                    }
                            )
                            .show();


                } else {
                    new AlertDialog.Builder(NewPilulierSettingsActivity.this)
                            .setMessage("Données incorrectes")
                            .setPositiveButton("Modifier", (dialogInterface, i) -> {
                                        newPilulierNom.getText().clear();
                                        newPilulierPrenom.getText().clear();
                                        newPhoneNumber.getText().clear();


                                    }
                            )
                            .show();
                    //todo : Faire un bouton Annuler
                }
            }
        });

    }

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

    public void switchActivities(Class aClass) {
        Intent switchActivityIntent = new Intent(this, aClass);
        startActivity(switchActivityIntent);
    }

    @Override
    protected void onPause() {
        super.onPause();
        HomeScreenActivity.closeDrawer(drawerLayout);
    }
}