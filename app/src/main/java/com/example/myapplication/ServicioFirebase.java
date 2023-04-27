package com.example.myapplication;

import android.app.ActivityManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.Manifest;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class ServicioFirebase extends FirebaseMessagingService {
    public ServicioFirebase(){}

    @Override
    public void onNewToken(@NonNull String token) {
        Log.d("fcm", "Nuevo token: " + token);
        FirebaseMessaging.getInstance().subscribeToTopic("RECORDATORIOS");
    }
    public void onMessageReceived(RemoteMessage remoteMessage) {
        if (remoteMessage.getNotification() != null) {
            Log.d("Prueba_Mensaje", "El mensaje es --> " + remoteMessage.getNotification().getBody());

            NotificationManager elManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationCompat.Builder elBuilder = new NotificationCompat.Builder(ServicioFirebase.this, "id_canal");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                NotificationChannel elCanal = new NotificationChannel("id_canal", "Mensajeria_FCM", NotificationManager.IMPORTANCE_DEFAULT);
                elManager.createNotificationChannel(elCanal);
                elBuilder.setSmallIcon(android.R.drawable.stat_sys_warning)
                        .setContentTitle(remoteMessage.getNotification().getTitle()) //Titulo del mensaje FCM
                        .setContentText(remoteMessage.getNotification().getBody()) //Cuerpo del mensaje FCM
                        .setVibrate(new long[] {0, 1000, 500, 1000})
                        .setAutoCancel(false);
                elManager.notify(1, elBuilder.build());
            }


        }
            //Cuando se obtiene un nuevo token se notifica localmente
            /*NotificationManager elManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationCompat.Builder elBuilder = new NotificationCompat.Builder(ServicioFirebase.this, "IdCanal");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel elCanal = new NotificationChannel("IdCanal", "NombreCanal",
                        NotificationManager.IMPORTANCE_DEFAULT);
                elManager.createNotificationChannel(elCanal);
                //editamos el contenido de la notificación
                elBuilder.setSmallIcon(android.R.drawable.stat_sys_warning)
                        .setContentTitle("FCM:")
                        .setContentText("Se ha generado un nuevo token!!")
                        .setSubText("Entra en la aplicación para verlo")
                        .setVibrate(new long[]{0, 1000, 500, 1000})
                        .setAutoCancel(true);
                //editamos el canal de la notificación poniendo luces led cuando lleguen
                elCanal.setDescription("Nuevo Token");
                elCanal.enableLights(true);
                elCanal.setLightColor(Color.RED);
                elCanal.setVibrationPattern(new long[]{0, 1000, 500, 1000});
                elCanal.enableVibration(true);
        }*/

    }

    //CLAVE FCM Servidor: BGbgKkntYBtgRQWmRSCJXfz6i-ENIK2PMEHhkg2Riyr4DdRGSOXh97ZcPNOVAWFe1AhItkzMfkLKRVK0XhzMj5g

    private void generarNotificacion(String cuerpo, String titulo){
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
        Uri sonidoUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        //NotificationCompat
    }
}
