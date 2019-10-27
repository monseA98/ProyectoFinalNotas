package com.example.proyectofinal;

public class Recordatorio {
    int id;
    String fecha;
    String hora;
    int idTarea;

    public Recordatorio(int id, String fecha, String hora, int idTarea) {
        this.id = id;
        this.fecha = fecha;
        this.hora = hora;
        this.idTarea = idTarea;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public int getIdTarea() {
        return idTarea;
    }

    public void setIdTarea(int idTarea) {
        this.idTarea = idTarea;
    }
}
