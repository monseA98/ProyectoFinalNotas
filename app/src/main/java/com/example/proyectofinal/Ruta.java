package com.example.proyectofinal;

import android.net.Uri;

public class Ruta {
    int id;
    Uri ruta; // o String?
    int idTarea;

    public Ruta(int id, Uri ruta, int idTarea) {
        this.id = id;
        this.ruta = ruta;
        this.idTarea = idTarea;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Uri getRuta() {
        return ruta;
    }

    public void setRuta(Uri ruta) {
        this.ruta = ruta;
    }

    public int getIdTarea() {
        return idTarea;
    }

    public void setIdTarea(int idTarea) {
        this.idTarea = idTarea;
    }
}
