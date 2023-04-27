package com.example.myapplication;

import static com.example.myapplication.ComandosSQL.CREAR_USUARIO;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class ConexionBBDDUsuarios extends SQLiteOpenHelper {
    public ConexionBBDDUsuarios(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        //ejecutamos el comando de crear la tabla de usuarios definida en la clase
        // ComandosSQL
        sqLiteDatabase.execSQL(CREAR_USUARIO);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        //nada que hacer
    }

    //método para añadir usuarios en la bd
    public long anadir_user(String name, String email, String password) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(ComandosSQL.NOM, name);
        values.put(ComandosSQL.EMAIL, email);
        values.put(ComandosSQL.CONTRA, password);

        //añadimos usuarios
        long rowId = db.insert("usuarios", null, values);

        db.close();

        return rowId;
    }

}
