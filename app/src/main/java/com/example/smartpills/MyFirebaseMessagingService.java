package com.example.smartpills;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService { //on hérite directement du FMS
    private static final String CANAL = "MonCanal" ;


    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
       /* Map<String, Object> tokenData = new HashMap<>();
        tokenData.put("token ", s);
        Log.d("NEW_TOKEN",s); */
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://smartpills-8194e-default-rtdb.europe-west1.firebasedatabase.app/%22");
                Log.d("TOKEN",s);
        database.getReference("/pilulier/64839763/token").setValue(s); //trouver un équivalent de set avec real time database
        Log.d("TOKEN3",s);
        System.out.println("Token : " + s);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) { //fonctionne que en premier plan ??
        super.onMessageReceived(remoteMessage);

        String myMessage = remoteMessage.getNotification().getBody(); // on récupère l'information
        Log.d("FirebaseMessage", "Message reçu : " + myMessage);  // tag pour trouver le message dans la console


        //création d'une notif
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, CANAL );
        notificationBuilder.setOngoing(true);


        notificationBuilder.setContentTitle("Ma notif");
        notificationBuilder.setContentText(myMessage);



        //notificationBuilder.setOngoing(true); //sensé laisser la notif permanente
        //on ajoute l'icone


        // envoyer la notif
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) { // il faut mettre le test car en dessous de API 26 n'est pas contenu

            String channelID = getString(R.string.notification_channel_id);
            String channelTitle = getString(R.string.notification_channel_title);
            String channelDescription = getString(R.string.notification_channel_desc);
            NotificationChannel channel = new NotificationChannel(channelID, channelTitle, NotificationManager.IMPORTANCE_MIN);
            channel.setDescription(channelDescription);
            /*NotificationManager.create; */
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);

            notificationBuilder.setChannelId(channelID);

        }
            NotificationManager NotificationManager = (android.app.NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            NotificationManager.notify(1, notificationBuilder.build());

        }




    }

