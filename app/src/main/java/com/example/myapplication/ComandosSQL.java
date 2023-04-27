package com.example.myapplication;

public class ComandosSQL {

    // atributos y comandos SQL necesarios para la registro de usuarios y login
    public static final String NOM_REGISTRO = "usuario";
    public static final String CONTRA = "contraseña";
    public static final String EMAIL = "email";
    public static final String TABLA_USU = "usuarios";
    public static final String NOM = "nombre";
    public static final String CREAR_USUARIO = "CREATE TABLE " + TABLA_USU +
            "(" + EMAIL + " TEXT, "
            + CONTRA + " TEXT, "
            + NOM + " TEXT " + ")";

    // atributos y comandos SQL necesarios para la introducción y actualización de prendas
    public static final String NOM_TABLA = "prendas";
    public static final String FECHA = "fecha";
    public static final String TITULO = "titulo";
    public static final String DESCRIP = "descripcion";

    public static final String CREAR_PRENDA = "CREATE TABLE " + NOM_TABLA +
            "(" +FECHA + " TEXT, "
            + TITULO + " TEXT, "
            +  DESCRIP + " TEXT)";


}
