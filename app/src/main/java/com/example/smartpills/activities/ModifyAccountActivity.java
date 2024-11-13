package com.example.smartpills.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smartpills.R;
import com.example.smartpills.database.PilulierDatabase;
import com.example.smartpills.database.UserDatabase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class ModifyAccountActivity extends AppCompatActivity {
    private UserDatabase userDatabase;

    private TextView titre;
    private Button valider,retour;
    private EditText userName, phone, password, passwordVerif;
    private ArrayList<PilulierDatabase> mySmartPills;
    private int position;
    private DatabaseReference Database = FirebaseDatabase.getInstance("https://smartpills-8194e-default-rtdb.europe-west1.firebasedatabase.app/").getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_account);

        titre = findViewById(R.id.title);
        userName = findViewById(R.id.modifSmartPillsUserName);
        phone = findViewById(R.id.modifSmartPillsPhone);
        password = findViewById(R.id.modifSmartPillsPassword);
        passwordVerif = findViewById(R.id.modifSmartPillsPasswordVerif);

        userDatabase = new UserDatabase();

        PilulierDatabase pilulier = getIntent().getParcelableExtra("currentSmartPills");
        System.out.println("ModifyAccountActivity : SmartPills correctly received with ID "+pilulier.getSmartPillsID());

        //System.out.println("ModifyAccount : Smartpills List is of size="+userDatabase.getSmartPillsList());

        //PilulierDatabase pilulier = userDatabase.getSmartPillsList().get(position);
        titre.setText("Modifier le pilulier de" + pilulier.getUtlisateur());

        retour = findViewById(R.id.retourButton);
        retour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(v.getContext());
                alert.setMessage("Voulez vous vraiment annuler la modification du pilulier ?");
                alert.setPositiveButton("OUI", (dialogInterface, i) -> switchActivities(HomeScreenActivity.class));
                alert.setNegativeButton("Annuler", null);
                alert.show();
            }
        });

        userName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if ( !charSequence.toString().isEmpty() && !charSequence.toString().matches("[a-zA-Z ]+")){
                    userName.setError("veuillez utiliser seulement des caractéres");
                } else {
                    userName.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        phone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if ( !charSequence.toString().isEmpty() && !charSequence.toString().matches("[0-9]+")){
                    phone.setError("veuillez utiliser seulement des nombre");
                } else {
                    phone.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                password.setError(null);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        passwordVerif.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                passwordVerif.setError(null);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        valider = findViewById(R.id.validateButton);
        valider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String typedUserName = userName.getText().toString();
                String typedPhone = phone.getText().toString();
                String typedPassword = password.getText().toString();
                String typedVerifiedPassword = passwordVerif.getText().toString();

                if(!typedPassword.equals(typedVerifiedPassword) && !typedPassword.isEmpty()){

                    passwordVerif.getText().clear();
                    password.getText().clear();

                    password.setError("Le mot de passe ne correspond pas");

                } else {
                    if (!typedUserName.isEmpty()) {
                        boolean userNameAlreadyExist = false;
                        userDatabase = new UserDatabase();
                        mySmartPills = userDatabase.getSmartPillsList();

                        for (int i = 0; i < mySmartPills.size(); i++) {
                            if (mySmartPills.get(i).getUtlisateur().equals(typedUserName)) {
                                userNameAlreadyExist = true;
                            }
                        }
                        if(userNameAlreadyExist){
                            userName.getText().clear();

                            userName.setError("Un autre pilulier existe déja avec ce nom.");
                        }else {
                            if (typedUserName.isEmpty()){
                                typedUserName = pilulier.getUtlisateur();
                            }
                            if(typedPassword.isEmpty()){
                                typedPassword = pilulier.getPassword();
                            }
                            if(typedPhone.isEmpty()){
                                typedPhone = pilulier.getphoneNumber();
                            }
                            PilulierDatabase modifiedPilulier = new PilulierDatabase(pilulier.getSmartPillsID(),typedPassword, typedUserName, typedPhone );
                            //TODO denis mettre a jour le pilulier a la position "position" de la liste des pilulier par modifiedPilulier

                            // Boite de dialogue de confirmation

                            AlertDialog.Builder alert = new AlertDialog.Builder(view.getContext());
                            alert.setMessage("Etes vous sur de vouloir modifier les paramètres du pilulier ?");
                            alert.setPositiveButton("OUI", (dialogInterface, i) -> {
                                modifiedPilulier.updateDataBase();
                                switchActivities(HomeScreenActivity.class);
                            });
                            alert.setNegativeButton("Annuler", null);
                            alert.show();


                        }

                    }


                }

            }});

    }

    public void switchActivities(Class aClass) {
        Intent switchActivityIntent = new Intent(this, aClass);
        startActivity(switchActivityIntent);
    }


    @Override
    protected void onPause() {
        super.onPause();
        //HomeScreenActivity.closeDrawer(drawerLayout);
    }
}