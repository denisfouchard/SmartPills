package com.example.smartpills.activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartpills.MainRecyclerAdapter;
import com.example.smartpills.R;
import com.example.smartpills.database.PilulierDatabase;
import com.example.smartpills.database.UserDatabase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class HistoriqueActivity extends AppCompatActivity {
    private UserDatabase userDatabase;
    private TextView title;
    private int position;
    private Button retour;
    private MainRecyclerAdapter adapter;
    private RecyclerView historique;
    private DatabaseReference Database = FirebaseDatabase.getInstance("https://smartpills-8194e-default-rtdb.europe-west1.firebasedatabase.app/").getReference();

    private ArrayList<String> medicaments;
    private ArrayList<String> horaires;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historique);

        title = findViewById(R.id.historiqueDe);
        retour = findViewById(R.id.retourButton);
        historique = findViewById(R.id.historique_list);

        retour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchActivities(HomeScreenActivity.class);
            }
        });

        userDatabase = new UserDatabase();
        historique.setLayoutManager(new LinearLayoutManager(this));

        PilulierDatabase pilulier = getIntent().getParcelableExtra("currentSmartPills");
        System.out.println("HistoriqueActivity : SmartPills correctly received with ID "+pilulier.getSmartPillsID());

        title.setText("Historique de " + pilulier.getUtlisateur());

        horaires = new ArrayList<>();


        DatabaseReference dbTraitements = Database.child("pilulier").child(pilulier.getSmartPillsID()).child("traitements");
        dbTraitements.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                DataSnapshot snapshot = task.getResult();
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                }
                else {
                    medicaments = pilulier.getTraitementName(snapshot);
                    horaires = pilulier.getTraitementHoraire(snapshot);

                    System.out.println("HistoriqueActivity : meds " + medicaments.toString());
                    adapter = new MainRecyclerAdapter(medicaments, horaires);
                    historique.setAdapter(adapter);
                }
            }
        });
    }

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