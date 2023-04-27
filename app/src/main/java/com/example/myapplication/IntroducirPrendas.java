package com.example.myapplication;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;

import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import java.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.Manifest;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.utils.widget.ImageFilterButton;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.myapplication.Entidades.Prenda;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.time.temporal.ValueRange;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Base64;

public class IntroducirPrendas extends AppCompatActivity {

    EditText textoTitulo;
    EditText textoDescrip;
    EditText fecha;
    ImageView elImageView;
    String currentPhotoPath;

    private ConexionBBDDLocal mDatabaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //en base a la orientación utilizamos un layout u otro
        if (getResources().getConfiguration().orientation== Configuration.ORIENTATION_LANDSCAPE){
            setContentView(R.layout.introducir_prendas_land);
        }
        else{
            setContentView(R.layout.introducir_prendas);
        }
        //en Android 12 +
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)!=
                    PackageManager.PERMISSION_GRANTED) {
                //pedir permiso para notificaciones
                ActivityCompat.requestPermissions(this, new
                        String[]{Manifest.permission.POST_NOTIFICATIONS}, 11);
            }
        }

        // se declaran variables para los campos donde se introduce texto
        textoTitulo = findViewById(R.id.titulo_edit_text);
        textoDescrip = findViewById(R.id.desc_edit_text);
        elImageView = findViewById(R.id.imagenPrenda);
        fecha = findViewById(R.id.editTextDate);

        mDatabaseHelper = new ConexionBBDDLocal(getApplicationContext(), "prendas", null, 1);
        Button nueva_prenda = findViewById(R.id.button_guardar);
        nueva_prenda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //pasamos los campos rellenados a String
                String titulo = textoTitulo.getText().toString();
                String desc = textoDescrip.getText().toString();
                String fechaP = fecha.getText().toString();

                //para introducir una prenda nueva hay que rellenar todos los campos
                if (titulo.isEmpty()||fechaP.isEmpty()||desc.isEmpty()){
                    Toast.makeText(IntroducirPrendas.this, "Rellena todos los campos", Toast.LENGTH_SHORT).show();
                }
                else{
                    //obtenemos la imagen puesta en el ImageView y la subimos codificada en base64 a la BBDD remota
                    Bitmap foto = ((BitmapDrawable) elImageView.getDrawable()).getBitmap();
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    foto.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    byte[] fototransformada = stream.toByteArray();
                    String fotoen64 = Base64.getEncoder().encodeToString(fototransformada);
                    subirFoto(fotoen64);

                    // llamamos al método utilizado para añadir una prenda a la BD local
                    long rowId = mDatabaseHelper.anadir_prenda(titulo, desc, fechaP);
                    if (rowId==-1){
                        Toast.makeText(IntroducirPrendas.this, "Introduccion de prenda fallida", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        //Si se ha añadido la prenda correctamente
                        Toast.makeText(IntroducirPrendas.this, "Se ha introducido la prenda", Toast.LENGTH_SHORT).show();
                        //lanzamos la notificación de que hemos introducido una prenda

                        notiPrendaSubida();
                        //subir foto al servidor
                        //se busca en la BD si el usuario existe

                        Intent intent = new Intent(IntroducirPrendas.this, MainActivity.class);
                        startActivity(intent);
                        finish();

                    }
                }

            }
        });

        Button cancelar = findViewById(R.id.button_cancelar);
        cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(IntroducirPrendas.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        //Boton para añadir foto a la prenda
        Button foto = findViewById(R.id.boton_añadir_imagen);
        foto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                permisosCamara();
            }
        });
    }


    private void permisosCamara() {
        //Solicitar permisos de camara
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 100);
            }
            else{
                abrirCamara();
            }
        }
    }

    @Override
    protected void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            try {
                Uri imageUri = data.getData();
                if (imageUri == null) {
                    Bitmap foto = (Bitmap) data.getExtras().get("data");
                    ImageView fotoPerfil = (ImageView) findViewById(R.id.imagenPrenda);
                    fotoPerfil.setImageBitmap(foto);
                } else {
                    InputStream imageStream = getContentResolver().openInputStream(imageUri);
                    Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                    ImageView fotoPerfil = (ImageView) findViewById(R.id.imagenPrenda);
                    fotoPerfil.setImageBitmap(selectedImage);
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(IntroducirPrendas.this, "Has salido de la cámara", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void abrirCamara() {
        Intent i1 = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        Intent i2 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Intent chooser = new Intent(Intent.ACTION_CHOOSER);
        chooser.putExtra(Intent.EXTRA_INTENT, i1);
        Intent[] intentArray = { i2 };
        chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray);
        startActivityForResult(chooser, 1);
    }

    private void subirFoto(String pfotoen64) {
        StringRequest request = new StringRequest(Request.Method.POST, "http://ec2-54-93-62-124.eu-central-1.compute.amazonaws.com/labaitua003/WEB/insertarFoto.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response.equalsIgnoreCase("Foto insertada")) {
                            Toast.makeText(IntroducirPrendas.this, "Se ha insertado la foto", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(IntroducirPrendas.this, "No se ha insertado foto", Toast.LENGTH_SHORT).show();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(IntroducirPrendas.this, "ERROR CON LA CONEXION", Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();

                params.put("id", textoTitulo.getText().toString());
                params.put("foto", pfotoen64);

                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(IntroducirPrendas.this);
        requestQueue.add(request);
    }


    public void notiPrendaSubida(){
        //Creamos notificationManager y Builder para las notificaciones locales
        NotificationManager elManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder elBuilder = new NotificationCompat.Builder(IntroducirPrendas.this, "IdCanal");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel elCanal = new NotificationChannel("IdCanal", "NombreCanal",
                    NotificationManager.IMPORTANCE_DEFAULT);
            elManager.createNotificationChannel(elCanal);
            //editamos el contenido de la notificación
            elBuilder.setSmallIcon(android.R.drawable.stat_sys_warning)
                    .setContentTitle("Llenando tu armario:")
                    .setContentText("Has introducido una nueva prenda!!")
                    .setSubText("Sigue así para llenar tu armario")
                    .setVibrate(new long[]{0, 1000, 500, 1000})
                    .setAutoCancel(true);
            //editamos el canal de la notificación poniendo luces led cuando lleguen
            elCanal.setDescription("Nueva prenda");
            elCanal.enableLights(true);
            elCanal.setLightColor(Color.RED);
            elCanal.setVibrationPattern(new long[]{0, 1000, 500, 1000});
            elCanal.enableVibration(true);
        }
        elManager.notify(1, elBuilder.build());
    }

}
