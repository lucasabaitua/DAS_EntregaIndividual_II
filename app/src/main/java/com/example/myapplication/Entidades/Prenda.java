package com.example.myapplication.Entidades;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.Image;

import java.io.Serializable;

public class Prenda implements Serializable {
    // private Bitmap img <-- ASI DEBERÍA HABER SIDO
    private int img;

    private String tituloPrenda;
    private String descripcion;
    private String fechaColgado;

    //Constructora de la clase prenda como entidad
    //                                                      Bitmap pFoto
    public Prenda(String pTit, String pDesc,String pFechaC, int pFoto){
        this.tituloPrenda=pTit;
        this.descripcion=pDesc;
        this.fechaColgado=pFechaC;
        this.img=pFoto;
    }


    public String getTituloPrenda(){
        return this.tituloPrenda;
    }

    public String getDescripcion(){
        return this.descripcion;
    }

    public String getFechaColgado(){
        return this.fechaColgado;
    }

    //public Bitmap getFoto{
    //     return this.img;   <-- ASI DEBERÍA
    //   }
    public int getFoto(){
        return this.img;
    }
}
