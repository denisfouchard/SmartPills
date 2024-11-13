package com.example.smartpills.database;

import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class UserDatabase {
    private String uid;
    private String fullName;
<<<<<<< HEAD
    private ArrayList<Pilulier> piluliers;
    private DatabaseReference Database = FirebaseDatabase.getInstance("https://smartpills-8194e-default-rtdb.europe-west1.firebasedatabase.app/").getReference();
=======
    private ArrayList<PilulierDatabase> smartPillsList = new ArrayList<>();
    public DatabaseReference mDatabase;
>>>>>>> android
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();


    public UserDatabase() {
        this.uid = mAuth.getCurrentUser().getUid();
        this.fullName = mAuth.getCurrentUser().getDisplayName();
        this.mDatabase = FirebaseDatabase.
                getInstance("https://smartpills-8194e-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference();
    }

    public void updateUser() {
        DatabaseReference DbUser = mDatabase.child("users").child(uid);
        DbUser.setValue(this);
    }

    public String getFullName() {
        return fullName;
    }

    public void getUserName(String uid) {
        Task<DataSnapshot> dataSnapshotTask = mDatabase.child("users").child(uid).get().addOnCompleteListener(
                task -> {
                    if (!task.isSuccessful()) {
                        Log.e("firebase", "Error getting data", task.getException());
                    } else {
                        Log.d("firebase", String.valueOf(task.getResult().getValue()));
                    }
                }
        );
    }

    public String getUid(){
        return uid;
    }

    public ArrayList<PilulierDatabase> getMySmartPills(DataSnapshot snapshot) {
        //Met à jour la liste des ID des Smartpills

        smartPillsList = new ArrayList<>();
        ArrayList<String> smartPillsIDList = new ArrayList<>();

        // Cherche dans la BDD les piluliersList associés au compte

        DataSnapshot snapshotUserPills = snapshot.child("users")
                .child(uid).child("mySmartPills");


        for (DataSnapshot child : snapshotUserPills.getChildren()){
            smartPillsIDList.add(child.getValue().toString());

            //System.out.println(child.getValue().toString());

        }

        System.out.println("UserDatabase : Looking for pilluliers... ");

        for (String smartPillsID : smartPillsIDList){
            DataSnapshot child = snapshot.child("pilulier").child(smartPillsID);
            System.out.println("UserDatabase : generating Pilulier from snapshot of "+ child.getValue().toString());
            PilulierDatabase pill = new PilulierDatabase(child);
            smartPillsList.add(pill);
        }
        System.out.println("La liste des piluliers comporte maintenant " + smartPillsList.size());
        if (!smartPillsList.isEmpty()){
            System.out.println("UserDatabase : password =  "+ smartPillsList.get(0).getPassword());
            System.out.println("UserDatabase : Le nom d'utilistateur est : " + smartPillsList.get(0).getUtlisateur());
        }
        /**
        mDatabase.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                }
                else {
                    DataSnapshot snapshot = task.getResult();
                    System.out.println("Checking for piluliersList at " + uid);


                    DataSnapshot snapshotUserPills = snapshot.child("users")
                            .child(uid).child("mySmartPills");


                    for (DataSnapshot child : snapshotUserPills.getChildren()){
                        smartPillsIDList.add(child.getValue().toString());

                        System.out.println(child.getValue().toString());

                    }

                    for (String smartPillsID : smartPillsIDList){
                        DataSnapshot child = snapshot.child("piluliers").child(smartPillsID);
                        PilulierDatabase pill = new PilulierDatabase(child);
                        smartPillsList.add(pill);
                    }
                    if (!smartPillsList.isEmpty()) {
                        System.out.println("UserDatabase : password =  " + smartPillsList.get(0).getPassword());
                        System.out.println("Le nom d'utilistateur est : " + smartPillsList.get(0).getUtlisateur());
                    }


                    System.out.println("La liste des piluliers comporte maintenant " + smartPillsList.size());
                }
            }
        });




        mDatabase.addValueEventListener(
                new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //System.out.println("Checking for piluliersList at " + uid);


                DataSnapshot snapshotUserPills = snapshot.child("users")
                        .child(uid).child("mySmartPills");


                for (DataSnapshot child : snapshotUserPills.getChildren()){
                    smartPillsIDList.add(child.getValue().toString());

                    //System.out.println(child.getValue().toString());

                }

                System.out.println("Looking for pilluliers... ");

                for (String smartPillsID : smartPillsIDList){
                    DataSnapshot child = snapshot.child("piluliers").child(smartPillsID);
                    PilulierDatabase pill = new PilulierDatabase(child);
                    smartPillsList.add(pill);
                }
                System.out.println("La liste des piluliers comporte maintenant " + smartPillsList.size());
                if (!smartPillsList.isEmpty()){
                    System.out.println("UserDatabase : password =  "+ smartPillsList.get(0).getPassword());
                    System.out.println("UserDatabase : Le nom d'utilistateur est : " + smartPillsList.get(1).getUtlisateur());
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        }); */

        return smartPillsList;
    }

    public void addSmartPills(PilulierDatabase pilulier){
        this.smartPillsList.add(pilulier);
    }

    public void addSmartPillsToAccount(PilulierDatabase pilulierDatabase){
        int pilulierIndex = smartPillsList.size();
        String smartPillsID = pilulierDatabase.getSmartPillsID();
        //Update SmartPills settings in the database
        DatabaseReference dbPilulier = mDatabase.child("pilulier").child(smartPillsID);
        dbPilulier.child("phoneNumber").setValue(pilulierDatabase.getphoneNumber());
        dbPilulier.child("utilisateur").setValue(pilulierDatabase.getUtlisateur());

        //Link SmartPills to user in the database
        mDatabase.child("users").child(uid).child("mySmartPills").child(String.valueOf(pilulierIndex)).setValue(pilulierDatabase.getSmartPillsID());


    }

    public PilulierDatabase createExamplePilulier(){
        PilulierDatabase pilulier = new PilulierDatabase("0111","password", "Josie Anne", "+330763324730");
        return pilulier;
    }

    public ArrayList<PilulierDatabase> getSmartPillsList(){

        return smartPillsList;
    }

    public ArrayList<Boolean> getTakenTab(DataSnapshot snapshot){
        ArrayList<Boolean> takenTab = new ArrayList<>();
        for (PilulierDatabase smartPill : smartPillsList){
            takenTab.add(smartPill.getNotTaken(snapshot).isEmpty());
        }
        return takenTab;
    }

/**
    public void deleteFromAccount(String smartPillsID) {
        DatabaseReference dbPilulier = mDatabase.child("pilulier").child(smartPillsID);
        Task dataRefTask = dbPilulier.removeValue().addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.e("firebase", "Error getting data", task.getException());
                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(HomeScreenActivity.this);
                        builder.setMessage("Etes-vous sûr de supprimer ce pilulier ?");
                        builder.setPositiveButton("OUI", (dialog, i) -> {


                                }
                        );
                        builder.setNegativeButton("NON", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int i) {
                                dialog.dismiss();
                            }
                        });
                    }
                }
        );
    }
 */
}
