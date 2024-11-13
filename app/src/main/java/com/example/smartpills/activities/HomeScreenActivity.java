package com.example.smartpills.activities;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.smartpills.ItemClickListener;
import com.example.smartpills.MainAdapter;
import com.example.smartpills.R;
import com.example.smartpills.database.PilulierDatabase;
import com.example.smartpills.database.UserDatabase;
import com.example.smartpills.database.UserManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class HomeScreenActivity extends AppCompatActivity {
    private DatabaseReference Database = FirebaseDatabase.getInstance("https://smartpills-8194e-default-rtdb.europe-west1.firebasedatabase.app/").getReference();
    private UserDatabase userDatabase;

    private DrawerLayout drawerLayout;
    private Button addPilulier;

    // affichage de la liste des piluliers :
    private ConstraintLayout pilulierLayout;
    private ExpandableListView pilulierList;
    private ArrayList<PilulierDatabase> mySmartPills;
    private HashMap<PilulierDatabase, ArrayList<String>> listChild = new HashMap<>();
    private DataSnapshot snapshot;
    private MainAdapter adapter;
    private ArrayList<Boolean> takenTab;
    ItemClickListener itemClickListener;

    private TextView noPilulier;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);


        drawerLayout = findViewById(R.id.LinearLayout);

        pilulierLayout = findViewById(R.id.PiluliersLayout);

        pilulierList = findViewById(R.id.pilulier_list);

        noPilulier = findViewById(R.id.noPilulierText);

        userDatabase = new UserDatabase();

        Database.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                }
                else {
                    snapshot = task.getResult();
                    mySmartPills = userDatabase.getMySmartPills(snapshot);
                    setUpListeners();
                }}});

        itemClickListener = new ItemClickListener() {
            @Override
            public void onClick(String phoneNumber) {
                if (phoneNumber == null){
                    Toast.makeText(getApplicationContext(), "Aucun numéro de télephone enregistrée pour cet utilisateur", Toast.LENGTH_SHORT).show();
                }
                else if(checkPermission()){
                    Intent intent = new Intent(Intent.ACTION_CALL);
                    intent.setData(Uri.parse("tel:" + phoneNumber));
                    startActivity(intent);
                }
            }

            @Override
            public void onClick(int position, char delete){
                PilulierDatabase currentSmartPills = userDatabase.getMySmartPills(snapshot).get(position);
                //userDatabase.deleteFromAccount(currentSmartPills.getSmartPillsID());

                if (true){
                    AlertDialog.Builder builder = new AlertDialog.Builder(HomeScreenActivity.this);
                    builder.setMessage("Etes-vous sûr de supprimer ce pilulier ?");
                    builder.setPositiveButton("OUI", (dialog, i) -> {

                            DatabaseReference dbPilulier = Database.child("users")
                                    .child(userDatabase.getUid())
                                    .child("mySmartPills")
                                    .child(String.valueOf(position));

                            dbPilulier.removeValue().addOnCompleteListener(task -> {

                                if (!task.isSuccessful()) {
                                    Log.e("firebase", "Error getting data", task.getException());

                                } else {
                                }

                            });
                            dialog.dismiss();
                            switchActivities(HomeScreenActivity.class);

                    });

                    builder.setNegativeButton("NON", (dialog, i) -> {
                       dialog.dismiss();
                        });
                    builder.show();
                }


                }


            @Override
            public void onClick(int position, boolean modify) {
                Intent intent;
                PilulierDatabase currentSmartPills = userDatabase.getMySmartPills(snapshot).get(position);

                if (modify){
                    intent = new Intent(HomeScreenActivity.this, ModifyAccountActivity.class);
                }
                else {
                    intent = new Intent(HomeScreenActivity.this, HistoriqueActivity.class);
                }

                System.out.println("HomeScreenActivity: correctly passed SmartPills with ID " + currentSmartPills.getSmartPillsID());
                intent.putExtra("currentSmartPills", currentSmartPills);
                startActivity(intent);
            }
        };

    }
    public void setUpListeners() {

        addPilulier = findViewById(R.id.buttonAddPilulier);
        addPilulier.setVisibility(View.VISIBLE);

        addPilulier.setOnClickListener(view -> switchActivities(ManualAddActivity.class));


        Database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (userDatabase.getSmartPillsList().isEmpty()) {

                    noPilulier.setVisibility(View.VISIBLE);

                } else {

                    setUpUI(snapshot);

                    takenTab = userDatabase.getTakenTab(snapshot);
                    adapter = new MainAdapter(mySmartPills, takenTab, listChild, itemClickListener);
                    pilulierList.setAdapter(adapter);

                    noPilulier.setVisibility(View.INVISIBLE);
                    pilulierLayout.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


    public void setUpUI(DataSnapshot snapshot){
        mySmartPills = userDatabase.getMySmartPills(snapshot);

        System.out.println("setUpUI : number of Piluliers="+mySmartPills.size());

        for (int i = 0; i < mySmartPills.size(); i++) {

            PilulierDatabase currentPilulier = mySmartPills.get(i);
            ArrayList<String> arrayList = new ArrayList<>();
            arrayList.add("Appeler");
            arrayList.add("Voir Historique");
            arrayList.add("Modifier");
            arrayList.add("Supprimer");
            listChild.put(currentPilulier, arrayList);


        }
    }

    public void ClickMenu(View view){
        openDrawer(drawerLayout);
    }

    public static void openDrawer(DrawerLayout drawerLayout) {
        drawerLayout.openDrawer(GravityCompat.START);
    }

    public void ClickLogo(View view){
        closeDrawer(drawerLayout);
    }

    public static void closeDrawer(DrawerLayout drawerLayout) {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){

            drawerLayout.closeDrawer(GravityCompat.START);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.S)
    public void ClickHome(View view){
        closeDrawer(drawerLayout);
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
                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.putExtra("EXIT", true);
                        startActivity(intent);
                    }
            );
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
        closeDrawer(drawerLayout);
    }

    private boolean checkPermission(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, 100);
            return false;
        }
        else {
            return true;
        }
    }

}




