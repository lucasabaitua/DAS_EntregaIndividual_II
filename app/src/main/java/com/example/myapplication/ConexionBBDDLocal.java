package com.example.myapplication;

import static com.example.myapplication.ComandosSQL.CREAR_PRENDA;
import static com.example.myapplication.ComandosSQL.DESCRIP;
import static com.example.myapplication.ComandosSQL.FECHA;
import static com.example.myapplication.ComandosSQL.TITULO;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.example.myapplication.Entidades.Prenda;

import java.util.ArrayList;
import java.util.List;

public class ConexionBBDDLocal extends SQLiteOpenHelper {
    public ConexionBBDDLocal(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    // cuando se crea una instancia de la clase se ejecuta el método de CREAR_PRENDA que crea una BD
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREAR_PRENDA);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        //nada que hacer ya que no cambiamos de versión la BD
    }

    //método para añadir prendas a la BD
    public long anadir_prenda(String titulo, String descr, String fecha) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(ComandosSQL.TITULO, titulo);
        values.put(ComandosSQL.DESCRIP, descr);
        values.put(ComandosSQL.FECHA, fecha);

        //insertamos las prendas en la BD con su respectiva info
        long rowId = db.insert("prendas", null, values);

        db.close();

        return rowId;
    }

}
