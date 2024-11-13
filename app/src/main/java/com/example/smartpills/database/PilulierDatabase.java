package com.example.smartpills.database;

import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Calendar;

public class PilulierDatabase implements Parcelable {

    private DatabaseReference Database = FirebaseDatabase.getInstance("https://smartpills-8194e-default-rtdb.europe-west1.firebasedatabase.app/").getReference();


    private String SmartPillsID;
    private String password;
    private String utilisateur; //Nom de la personne qui utilise le pilulier
    private String phoneNumber;
    private ArrayList<String> traitements;
    private boolean upToDate = true;
    private boolean pwdCorrect;
    private boolean exists;

    public PilulierDatabase(String smartPillsID, String password, String smartPillsUser, String phoneNumber) {
        this.SmartPillsID = smartPillsID;
        this.password = password;
        this.utilisateur = smartPillsUser;
        this.phoneNumber = phoneNumber;


        /*
        checkIfExists();
        if (exists){
            checkIfpasswordIsCorrect();

        }
        */
    }

    public PilulierDatabase(String smartPillsID, String password) {
        this.SmartPillsID = smartPillsID;
        this.password = password;

        /*

        checkIfExists();
        if (exists){
            checkIfpasswordIsCorrect();
        }
        */

    }

    public PilulierDatabase(DataSnapshot snapshot){
        //Initie un nouvel objet à partir de la bdd

        try {
            this.SmartPillsID = snapshot.getKey();
            this.password = snapshot.child("password").getValue().toString();
            this.utilisateur = snapshot.child("utilisateur").getValue().toString();
            this.phoneNumber = snapshot.child("phoneNumber").getValue().toString();
        } catch (Exception e){
            e.printStackTrace();
        }

    }

    // ---------- Pour le passage entre activités (Parceling) ---------- //

    protected PilulierDatabase(Parcel in) {

        SmartPillsID = in.readString();
        password = in.readString();
        utilisateur = in.readString();
        phoneNumber = in.readString();
        pwdCorrect = in.readByte() != 0;
        exists = in.readByte() != 0;
    }

    public static final Creator<PilulierDatabase> CREATOR = new Creator<PilulierDatabase>() {
        @Override
        public PilulierDatabase createFromParcel(Parcel in) {
            return new PilulierDatabase(in);
        }

        @Override
        public PilulierDatabase[] newArray(int size) {
            return new PilulierDatabase[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(SmartPillsID);
        dest.writeString(password);
        dest.writeString(utilisateur);
        dest.writeString(phoneNumber);
        dest.writeByte((byte) (pwdCorrect ? 1 : 0));
        dest.writeByte((byte) (exists ? 1 : 0));
    }



    // ---------- Getters/Setters ----------

    public String getSmartPillsID() {
        return SmartPillsID;
    }

    public String getPassword() {
        return password;
    }

    public String getUtlisateur() {
        return utilisateur;
    }

    public String getphoneNumber() {
        return phoneNumber;
    }

    public void setUtlisateur(String utlisateur) {
        this.utilisateur = utlisateur;
    }

    public void addUser(PilulierDatabase pillulier) {
        Database.child("pilulier").child(pillulier.getSmartPillsID()).setValue(pillulier);
    }

    public void checkIfExists() {
        DatabaseReference rootRef = Database.child("pilulier");


        rootRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                }
                else {
                    Log.d("firebase", String.valueOf(task.getResult().getValue()));
                    exists = task.getResult().hasChild(SmartPillsID);
                    System.out.println("Pilulier.java : Est ce que il existe ?" + exists);
                }
            }
        });


    }
    // ---------- Récupération du traitement ---------- //

    public ArrayList<String> getTraitementName(DataSnapshot traitementsSnapshot){


        ArrayList<String> traitementsNames = new ArrayList<>();

        Calendar c = Calendar.getInstance();
        String day = String.valueOf(c.get(Calendar.DAY_OF_WEEK));

        long forToday;

        for (DataSnapshot traitement :traitementsSnapshot.getChildren()){
            forToday = (long) traitement.child("jours").child(day).getValue();
            if (forToday == 1) {
                for (DataSnapshot prise :  traitement.child("horraires").getChildren()){
                    traitementsNames.add(traitement.child("nom").getValue().toString());
                }


            }

        }

        return traitementsNames;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public ArrayList<String> getTraitementHoraire(DataSnapshot traitementsSnapshot){

        ArrayList<String> traitementsHoraires = new ArrayList<>();

        Calendar c = Calendar.getInstance();
        String day = String.valueOf(c.get(Calendar.DAY_OF_WEEK));

        long forToday;

        for (DataSnapshot traitement :traitementsSnapshot.getChildren()){
            forToday = (long) traitement.child("jours").child(day).getValue();



            if (forToday==1) {
                boolean first = true;
                for (DataSnapshot prise :  traitement.child("horraires").getChildren()) {
                    String h = prise.child("debut").getValue().toString();
                    LocalTime t = LocalTime.parse(h);
                    LocalTime tmax = LocalTime.now().plusHours(2);


                    if ((boolean) traitement.child("pris").getValue() || !first) {
                        traitementsHoraires.add(h);
                    } else if (t.compareTo(tmax)  < 0) {
                        traitementsHoraires.add("Alerte");
                    } else {
                        traitementsHoraires.add("En attente");
                    }
                    first = false;

                }
            }

        }

        return traitementsHoraires;
    }

    public ArrayList<String> getTraitementData(DataSnapshot traitementsSnapshot){

        ArrayList<String> traitementsHoraires = new ArrayList<>();

        Calendar c = Calendar.getInstance();
        String day = String.valueOf(c.get(Calendar.DAY_OF_WEEK));

        boolean forToday;

        for (DataSnapshot traitement :traitementsSnapshot.getChildren()){
            forToday = (boolean) traitement.child("jours").child(day).getValue();
            if (forToday){
                if ((boolean) traitement.child("pris").getValue()) {
                    traitementsHoraires.add(traitement.child("horaire").getValue().toString());
                } else {
                    traitementsHoraires.add("En attente");
                }
            }
        }

        return traitementsHoraires;
    }






    // ---------- Tests de validié BDD ---------- //

    public void checkIfpasswordIsCorrect() {
        DatabaseReference databaseReference = Database.child("pilulier").child(SmartPillsID).child("password");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String pilulierPassword = snapshot.getValue().toString();
                //System.out.println("Given password is : " + password);
                //System.out.println("Pilulier password is : " + pilulierPassword);
                pwdCorrect = (pilulierPassword.compareTo(password) == 0 );
                //System.out.println("Correct : " + pwdCorrect);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //DO nothing
            }
        });
        //System.out.println("PASSWORD : " +pwdCorrect);

    }

    public boolean isExists(){
        checkIfExists();
        return exists;
    }

    public void setExists(boolean b){
        exists = b;
    }
    public boolean isPwdCorrect(){
        return pwdCorrect;
    }

    public boolean isUpToDate(DataSnapshot snapshot){
        checkIfUpToDate(snapshot);
        return upToDate;
    }

    public ArrayList<String> getNotTaken(DataSnapshot snapshot){
        return checkIfUpToDate(snapshot);
    }

    public void writeToDataBase(){
        Database.child("pilulier").child(SmartPillsID).setValue(this);

    }

    public ArrayList<String> checkIfUpToDate(DataSnapshot snapshot){
        //Vérifie les traitements qui n'ont pas encore été pris

        ArrayList<String> notTaken = new ArrayList<>();

        DataSnapshot traitementDataBase = snapshot.child("pilulier").child(SmartPillsID).child("traitements");
        for (DataSnapshot traitement : traitementDataBase.getChildren()){
            //boolean pris = (boolean) traitement.child("pris").getValue();
            boolean pris = false;
            if (!pris){
                String traitementName = (String) traitement.child("nom").getValue();
                notTaken.add(traitementName);
            }
        }
        return notTaken;
        };



    public void setPasswordCorrect(boolean b) {
        pwdCorrect = b;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }


    // ---------- Mise à jour de la BDD ---------- //

    public void updateDataBase() {

        //Update SmartPills settings in the database
        DatabaseReference dbPilulier = Database.child("pilulier").child(SmartPillsID);
        dbPilulier.child("password").setValue(password);
        dbPilulier.child("phoneNumber").setValue(phoneNumber);
        dbPilulier.child("utilisateur").setValue(utilisateur);

    }

    // ---------- Récupération des traitements ---------- //

    public ArrayList<String> getTraitements(){


        return null;
    }


}








