package com.example.smartpills.database;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Pilulier implements Parcelable {

    private DatabaseReference Database = FirebaseDatabase.getInstance("https://smartpills-8194e-default-rtdb.europe-west1.firebasedatabase.app/").getReference();

    private String SmartPillsID;
    private String password;
    private String utilisateur; //Nom de la personne qui utilise le pilulier
    private String phoneNumber;

    private boolean pwdCorrect = true;
    private boolean exists = true;

    public Pilulier(String smartPillsID, String password, String smartPillsUser, String phoneNumber) {
        this.SmartPillsID = smartPillsID;
        this.password = password;
        this.utilisateur = smartPillsUser;
        this.phoneNumber = phoneNumber;
        checkIfExists();
        checkIfpasswordIsCorrect();
    }

    public Pilulier(String smartPillsID, String password) {
        this.SmartPillsID = smartPillsID;
        this.password = password;

        checkIfExists();
        checkIfpasswordIsCorrect();

    }

    public Pilulier(DataSnapshot snapshot){
        //Initie un nouvel objet à partir de la bdd

        try {
            this.SmartPillsID = snapshot.getValue().toString();
            this.password = snapshot.child("password").getValue().toString();
            this.utilisateur = snapshot.child("utilisateur").getValue().toString();
            this.phoneNumber = snapshot.child("phoneNumber").getValue().toString();
        } catch (Exception e){
            e.printStackTrace();
        }

    }

    // ---------- Pour le passage entre activités (Parceling) ---------- //

    protected Pilulier(Parcel in) {

        SmartPillsID = in.readString();
        password = in.readString();
        utilisateur = in.readString();
        phoneNumber = in.readString();
        pwdCorrect = in.readByte() != 0;
        exists = in.readByte() != 0;
    }

    public static final Creator<Pilulier> CREATOR = new Creator<Pilulier>() {
        @Override
        public Pilulier createFromParcel(Parcel in) {
            return new Pilulier(in);
        }

        @Override
        public Pilulier[] newArray(int size) {
            return new Pilulier[size];
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

    public void setUtlisateur(String utlisateur) {
        this.utilisateur = utlisateur;
    }

    public void addUser(Pilulier pillulier) {
        Database.child("pilulier").child(pillulier.getSmartPillsID()).setValue(pillulier);
    }

    public void checkIfExists() {
        DatabaseReference rootRef = Database.child("pilulier");

        /*

        Database.child("pilulier").child(SmartPillsID).get().addListenerForSingleValueEvent(task -> {
            if (task.isSuccessful()) {
                exists = true;
                System.out.println("ON A CHECK SI CA EXITSE");
            } else {System.out.println("PAS MARCHE LOL");}

        }); */




        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot snapshot) {

                exists = snapshot.hasChild(SmartPillsID);
                System.out.println("Est ce que il existe ?" + exists);
                Log.d("lol", "mdr");

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Do nothing
                System.out.println("ECHOUE");
            }
        });
        System.out.println("Exists : CHECKED");

    }


    // ---------- Tests de validié BDD ---------- //

    public void checkIfpasswordIsCorrect() {


        DatabaseReference dataSnapshot = Database.child("pilulier").child(SmartPillsID).child("password");


        dataSnapshot.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                System.out.println("Password is :" + dataSnapshot.get().getResult().getValue().toString());
                pwdCorrect = (dataSnapshot.get().getResult().getValue().toString() == password);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //DO nothing
            }
        });
        System.out.println("PASSWORD : " +pwdCorrect);

    }

    public boolean isExists(){
        return exists;
    }
    public boolean isPwdCorrect(){
        return pwdCorrect;
    }

    public void writeToDataBase(){
        Database.child("pilulier").child(SmartPillsID).setValue(this);

    }





}




