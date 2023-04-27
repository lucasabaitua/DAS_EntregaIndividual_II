package com.example.myapplication;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

public class InicioApp extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //dependiendo de la orientación del teléfono se muestra un layout u otro
        if (getResources().getConfiguration().orientation== Configuration.ORIENTATION_LANDSCAPE){
            setContentView(R.layout.portada_land);
        }
        else{
            setContentView(R.layout.portada);
        }
        Button iniciar = findViewById(R.id.buttonInicio);
        //si se da click a comenzar
        iniciar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(InicioApp.this, Identificacion.class);
                startActivity(intent);
            }
        });

        Button salir = findViewById(R.id.buttonSalir);
        //si se da click a la X
        salir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //se llama al diálogo
                DialogFragment dialogoalerta= new ClaseDialogo();
                dialogoalerta.show(getSupportFragmentManager(), "etiqueta");
            }
        });


    }
}
