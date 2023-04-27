package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.work.Data;

import android.Manifest;
import android.app.ActivityManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Blob;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.myapplication.Adaptadores.prendasOverview;
import com.example.myapplication.Entidades.Prenda;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class MainActivity extends AppCompatActivity {
    //Inicialización de atributos
    ArrayList<Prenda> listaPrendas = new ArrayList<Prenda>();
    prendasOverview prendaOverview;
    ConexionBBDDLocal ddbb;
    ArrayList<Bitmap> imagenes = new ArrayList<Bitmap>();


    // cuando se inicializa la clase
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //dependiendo de la orientación del teléfono se muestra un layout u otro
        super.onCreate(savedInstanceState);
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            setContentView(R.layout.activity_main_land);
        } else {
            setContentView(R.layout.activity_main);
        }
        //
        //Permisos Firebase
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) !=
                    PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new
                        String[]{Manifest.permission.POST_NOTIFICATIONS}, 11);
            }
        }

        //Notificar para habilitar background
        ActivityManager am = (ActivityManager) this.getSystemService(Context.ACTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            if (am.isBackgroundRestricted() == true) {
                NotificationManager elManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                NotificationCompat.Builder elBuilder = new NotificationCompat.Builder(MainActivity.this, "IdCanal");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    NotificationChannel elCanal = new NotificationChannel("IdCanal", "NombreCanal",
                            NotificationManager.IMPORTANCE_DEFAULT);
                    elManager.createNotificationChannel(elCanal);
                    //editamos el contenido de la notificación
                    elBuilder.setSmallIcon(android.R.drawable.stat_sys_warning)
                            .setContentTitle("PROBLEMA:")
                            .setContentText("Habilita el modo background!!")
                            .setSubText("Para utilizar FCM")
                            .setVibrate(new long[]{0, 1000, 500, 1000})
                            .setAutoCancel(true);
                    //editamos el canal de la notificación poniendo luces led cuando lleguen
                    elCanal.setDescription("Background deshabilitado");
                    elCanal.enableLights(true);
                    elCanal.setLightColor(Color.RED);
                    elCanal.setVibrationPattern(new long[]{0, 1000, 500, 1000});
                    elCanal.enableVibration(true);
                }
            }
        }

        // se llama al método crearLista que muestra en la interfaz la lista de prendas que hay
        //ya registradas anteriormente


        crearLista();
        prendaOverview = new prendasOverview(listaPrendas, getApplicationContext());

        ListView prendas = (ListView) findViewById(R.id.lPrendas);

        // utilizamos un listener para saber cuando se desea eliminar una prenda de la lista
        prendaOverview.setOnItemDeleteListener(new prendasOverview.OnItemDeleteListener() {
            @Override
            public void onItemDelete(int pos) {
                ConexionBBDDLocal bbdd = new ConexionBBDDLocal(getApplicationContext(), "prendas", null, 1);
                SQLiteDatabase sql = bbdd.getWritableDatabase();
                // se borran las prendas que tengan la misma fecha, tanto de la interfaz como de la BD
                String token = String.valueOf(FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.d("FIREBASE", task.getException().toString());
                        }
                        String token = task.getResult();
                        Log.d("token_id", token);
                        notiEliminadoFCM(token);
                    }
                }));
                Log.d("token_id", token);
                //notiEliminadoFCM(token);
                sql.delete("prendas", "fecha = ?", new String[]{listaPrendas.get(pos).getFechaColgado()});
                listaPrendas.remove(pos);
                prendaOverview.notifyDataSetChanged();

            }
        });
        // se adapta la lista de Prendas a la manera en la que queremos que se muestre por pantalla
        prendas.setAdapter(prendaOverview);

        Button nuevaPrenda = findViewById(R.id.botonCrearPrenda);
        // cuando deseamos introducir una nueva prenda nos lleva a la clase para introducirla
        nuevaPrenda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, IntroducirPrendas.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void notiEliminadoFCM(String token) {
        StringRequest request = new StringRequest(Request.Method.POST, "http://ec2-54-93-62-124.eu-central-1.compute.amazonaws.com/labaitua003/WEB/fcm_notificaciones.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response.equalsIgnoreCase("200")) {
                            Toast.makeText(MainActivity.this, "Ha saltado la noti", Toast.LENGTH_SHORT).show();
                        } else if (response.equalsIgnoreCase("400")) {
                            Toast.makeText(MainActivity.this, "Error de JSON", Toast.LENGTH_SHORT).show();
                        } else if (response.equalsIgnoreCase("401")) {
                            Toast.makeText(MainActivity.this, "Error de API", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(MainActivity.this, "Error de Servidor", Toast.LENGTH_SHORT).show();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, "ERROR CON LA CONEXION", Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();

                params.put("token", token);

                return params;
            }
        };
    }

    private void crearLista() {
        ListView prendas = (ListView) findViewById(R.id.lPrendas);
        listaPrendas = new ArrayList<Prenda>();
        // se conecta con una instancia de la clase que conecta con la BD, en este caso "prendas"
        ddbb = new ConexionBBDDLocal(getApplicationContext(), "prendas", null, 1);
        // se llama al método en el que se obtienen todas las prendas de la DB prendas
        obtenerTodasLasPrendas();
    }

    public void obtenerTodasLasPrendas() {
        SQLiteDatabase db = ddbb.getReadableDatabase();
        Prenda p = null;
        //Para la carga de las imagenes se debería haber utilizado la variable j
        //int j = 0;
        // se obtienen todos los datos de las prendas
        Cursor c = db.rawQuery("SELECT * FROM prendas", null);
        while (c.moveToNext()) {
            // se crean nuevas instancias de prenda con los datos obtenidos
            // OBTENER LA FOTO DE LA BBDD EN BASE AL TITULO

            Log.d("nombreFoto", c.getString(1));
            //   Se debería haber cambiado lo siguiente:                            ponerFoto(j);
            p = new Prenda(c.getString(1), c.getString(2), c.getString(0), R.drawable.iconoprendas);
            // se añaden en la lista que vamos a mostrar por pantalla en la interfaz
            listaPrendas.add(p);
            //j=j+1;
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        cargarFotos();
    }

    private void cargarFotos() {
        //Utilizamos un Thread inicializado en el método onStart()
        //Obtenemos el JSON con todas las fotos codificadas en base64 para luego colocarlas
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String url = "http://ec2-54-93-62-124.eu-central-1.compute.amazonaws.com/labaitua003/WEB/obtenerFoto.php";
                    HttpURLConnection urlConnection = null;
                    try {
                        //se realiza la conexion con el php donde se obtienen los datos de las fotos en JSON
                        URL destino = new URL(url);
                        urlConnection = (HttpURLConnection) destino.openConnection();
                        urlConnection.setConnectTimeout(5000);
                        urlConnection.setReadTimeout(5000);
                        urlConnection.setRequestMethod("POST");
                        urlConnection.setDoOutput(true);
                        urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                        PrintWriter out = new PrintWriter(urlConnection.getOutputStream());
                        out.close();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    int statusCode = 0;
                    try {
                        statusCode = urlConnection.getResponseCode();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    String response = "";
                    if (statusCode == 200) {
                        BufferedInputStream inputStream = null;
                        try {
                            inputStream = new BufferedInputStream(urlConnection.getInputStream());
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        BufferedReader bufferedReader = null;
                        try {
                            bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
                        } catch (UnsupportedEncodingException e) {
                            throw new RuntimeException(e);
                        }
                        String line = "";
                        while (true) {
                            try {
                                if ((line = bufferedReader.readLine()) == null) break;
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                            response += line;
                        }
                        try {
                            inputStream.close();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        if (!response.isEmpty()) {
                            JSONArray jsonArray = null;
                            try {
                                jsonArray = new JSONArray(response);
                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }
                            //por cada foto codificada en el JSON
                            for (int i = 0; i < jsonArray.length(); i++) {
                                Bitmap imagen;
                                String img;
                                try {
                                    //se obtiene el apartado de foto que es donde se encuentra en la BBDD remota
                                    img = jsonArray.getJSONObject(i).getString("foto");
                                } catch (JSONException e) {
                                    throw new RuntimeException(e);
                                }
                                String r = img.replaceAll("\n", "");
                                String r2 = r.replaceAll(" ", "+");
                                // se descodifica la foto de base64 a Bitmap
                                byte[] decodedString = Base64.decode(r2, Base64.DEFAULT);
                                imagen = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                                Log.d("fotoPic", imagen.toString());
                                // se añade el Bitmap al arrayList de Bitmap creado
                                imagenes.add(imagen);
                            }
                            if (imagenes.isEmpty()) {
                                Toast.makeText(MainActivity.this, "No hay imagenes", Toast.LENGTH_SHORT).show();
                            }
                        }
                    } else {
                        Log.d("pillarFotos", "No funciona");
                    }
                } catch (RuntimeException e) {
                }
            }
        });

        thread.start();
    }

    //Método que se utilizaría para obtener la foto relacionada con la prenda que vamos construyendo
    private Bitmap ponerFoto(int i) {
        return imagenes.get(i);
    }

    //METODO QUE SE HUBIERA UTILIZADO PARA REDIMENSIONAR LAS FOTOS AL TAMAÑO DEL IMAGEVIEW
    /*
    public void cargarFotos(){
        int anchoDestino = elImageView.getWidth();
        int altoDestino = elImageView.getHeight();
        int anchoImagen = bitmapFoto.getWidth();
        int altoImagen = bitmapFoto.getHeight();
        float ratioImagen = (float) anchoImagen / (float) altoImagen;
        float ratioDestino = (float) anchoDestino / (float) altoDestino;
        int anchoFinal = anchoDestino;
        int altoFinal = altoDestino;
        if (ratioDestino > ratioImagen) {
            anchoFinal = (int) ((float)altoDestino * ratioImagen);
        } else {
            altoFinal = (int) ((float)anchoDestino / ratioImagen);
        }
        Bitmap bitmapredimensionado = Bitmap.createScaledBitmap(bitmapFoto,anchoFinal,altoFinal,true);
    }*/



    public void onClick(View view) {
            Intent miIntent = new Intent(MainActivity.this, IntroducirPrendas.class);
            startActivity(miIntent);
            finish();
    }
    /*
    public void cargarFotos(){
        int anchoDestino = elImageView.getWidth();
        int altoDestino = elImageView.getHeight();
        int anchoImagen = bitmapFoto.getWidth();
        int altoImagen = bitmapFoto.getHeight();
        float ratioImagen = (float) anchoImagen / (float) altoImagen;
        float ratioDestino = (float) anchoDestino / (float) altoDestino;
        int anchoFinal = anchoDestino;
        int altoFinal = altoDestino;
        if (ratioDestino > ratioImagen) {
            anchoFinal = (int) ((float)altoDestino * ratioImagen);
        } else {
            altoFinal = (int) ((float)anchoDestino / ratioImagen);
        }
        Bitmap bitmapredimensionado = Bitmap.createScaledBitmap(bitmapFoto,anchoFinal,altoFinal,true);
    }*/

}