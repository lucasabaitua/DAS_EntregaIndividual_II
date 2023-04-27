package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.Manifest;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class Identificacion extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //dependiendo de la orientación del teléfono
        if (getResources().getConfiguration().orientation== Configuration.ORIENTATION_LANDSCAPE){
            setContentView(R.layout.inicio_sesion_land);
        }
        else{
            setContentView(R.layout.inicio_sesion);
        }
        EditText user = findViewById(R.id.usuarioEditText);
        EditText pass = findViewById(R.id.contraEditText);
        Button iniciarSesion = findViewById(R.id.botonLogin);

        //recibir datos del FCM
        if (getIntent().getExtras() != null) {
            String mensaje= getIntent().getExtras().getString("mensaje");
            String fecha= getIntent().getExtras().getString("fecha");
            user.setText(mensaje);
            pass.setText(fecha);
        }

        // si se pulsa el botón de iniciar sesión

        iniciarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //nos comprueba si existe el usuario
                comprobarIniSes(user.getText().toString(), pass.getText().toString());
            }
        });

        Button registrarse = findViewById(R.id.botonRegistro);
        // si se pulsa el botón de registrarse
        registrarse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // nos lleva al menú de registro
                Intent intent = new Intent(Identificacion.this, RegistroUsuarios.class);
                startActivity(intent);
            }
        });
    }



    public void comprobarIniSes(String usuario, String contra){
        //se busca en la BD si el usuario existe
        StringRequest request = new StringRequest(Request.Method.POST, "http://ec2-54-93-62-124.eu-central-1.compute.amazonaws.com/labaitua003/WEB/comprobarLogin.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response.equalsIgnoreCase("No existe usuario")) {
                            Toast.makeText(Identificacion.this, "No existe el usuario", Toast.LENGTH_SHORT).show();
                        } else if (response.equalsIgnoreCase("Usuario existente")) {
                            Intent intent = new Intent(Identificacion.this, MainActivity.class);
                            startActivity(intent);
                        } else {
                            Toast.makeText(Identificacion.this, "Comprobacion fallida", Toast.LENGTH_SHORT).show();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(Identificacion.this, "ERROR CON LA CONEXION", Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();

                params.put("email", usuario);
                params.put("password", contra);

                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(Identificacion.this);
        requestQueue.add(request);

    }

}