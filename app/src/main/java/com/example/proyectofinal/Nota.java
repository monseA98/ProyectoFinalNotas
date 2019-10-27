package com.example.proyectofinal;

import java.io.Serializable;

public class Nota implements Serializable {
    int id;
    String titulo;
    String descripcion;

    public Nota(int id, String titulo, String descripcion) {
        this.id = id;
        this.titulo = titulo;
        this.descripcion = descripcion;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String toString(){
        return "Nota " + id + "\n" + titulo + "\n" + descripcion;
    }
}
