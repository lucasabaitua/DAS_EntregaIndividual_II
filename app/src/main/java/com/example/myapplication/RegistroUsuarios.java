package com.example.myapplication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.myapplication.ComandosSQL;
import com.example.myapplication.ConexionBBDDUsuarios;
import com.example.myapplication.R;

import java.util.HashMap;
import java.util.Map;

public class RegistroUsuarios extends AppCompatActivity {

    private EditText mEmailEditText;
    private EditText mPasswordEditText;
    private EditText mNameEditText;
    //private ConexionBBDDUsuarios mDatabaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            setContentView(R.layout.registro_land);
        } else {
            setContentView(R.layout.registro);
        }

        // Inicializar EditText views
        mEmailEditText = findViewById(R.id.email_edit_text);
        mPasswordEditText = findViewById(R.id.password_edit_text);
        mNameEditText = findViewById(R.id.name_edit_text);

        // montar un listener para el botón de registro
        Button registerButton = findViewById(R.id.register_button);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //obtener los campos rellenados de la información del nuevo usuario
                String name = mEmailEditText.getText().toString();
                String email = mNameEditText.getText().toString();
                String password = mPasswordEditText.getText().toString();

                //necesario rellenar todos los campos
                if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(RegistroUsuarios.this, "Rellena todos los campos", Toast.LENGTH_SHORT).show();
                } else {
                    comprobarUsuario();
                    //insertarDatos();
                }
            }
        });
    }

    private void comprobarUsuario(){
        StringRequest request = new StringRequest(Request.Method.POST, "http://ec2-54-93-62-124.eu-central-1.compute.amazonaws.com/labaitua003/WEB/comprobarUsuario.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response.equalsIgnoreCase("No existe usuario")) {
                            insertarDatos();
                        } else if (response.equalsIgnoreCase("Usuario existente")) {
                            Toast.makeText(RegistroUsuarios.this, "Ya existe el usuario", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(RegistroUsuarios.this, "Comprobacion fallida", Toast.LENGTH_SHORT).show();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(RegistroUsuarios.this, "ERROR CON LA CONEXION", Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();

                params.put("email", mNameEditText.getText().toString());
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(RegistroUsuarios.this);
        requestQueue.add(request);
    }



    private void insertarDatos() {
        StringRequest request = new StringRequest(Request.Method.POST, "http://ec2-54-93-62-124.eu-central-1.compute.amazonaws.com/labaitua003/WEB/insertarUsuario.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response.equalsIgnoreCase("Datos insertados")) {
                            Toast.makeText(RegistroUsuarios.this, "Usuario registrado", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(RegistroUsuarios.this, "Registro fallido", Toast.LENGTH_SHORT).show();
                            finish();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(RegistroUsuarios.this, "ERROR CON LA CONEXION", Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();

                params.put("nombre", mEmailEditText.getText().toString());
                params.put("email", mNameEditText.getText().toString());
                params.put("password", mPasswordEditText.getText().toString());

                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(RegistroUsuarios.this);
        requestQueue.add(request);
    }
}

