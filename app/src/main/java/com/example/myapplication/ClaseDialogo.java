package com.example.myapplication;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class ClaseDialogo extends DialogFragment {
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);
        //Personalizamos el diálogo añadiendole un título, un mensaje y las opciones a marcar
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Salir");
        builder.setMessage("¿Quieres salir de la aplicación?");
        //si se pulsa "Por supuesto"
        builder.setPositiveButton("Por supuesto", new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
            //salimos de la app
            getActivity().finish();
        }
    });
        //si se pulsa "Se ma ido"
        builder.setNeutralButton("Se ma ido", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //nos muestra un mensaje Toast
                Toast.makeText(getActivity(), "Que no vuelva a pasar", Toast.LENGTH_SHORT).show();
            }
        });
        //si se pulsa "Nonono"
        builder.setNegativeButton("Nonono", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //Nos lleva a la pagina de identificación
                Toast.makeText(getActivity(), "Pues entra a tu armario anda", Toast.LENGTH_SHORT).show();
                Intent in = new Intent(getActivity(), Identificacion.class);
                startActivity(in);
            }
        });
        return builder.create();
    }
}
