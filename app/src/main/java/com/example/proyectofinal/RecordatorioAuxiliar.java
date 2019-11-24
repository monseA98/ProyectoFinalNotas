package com.example.proyectofinal;

public class RecordatorioAuxiliar {
    int year;
    int month;
    int day;
    int hour;
    int min;
    String fecha, hora;

    public RecordatorioAuxiliar(int year, int month, int day, int hour, int min, String fecha, String hora) {
        this.year = year;
        this.month = month;
        this.day = day;
        this.hour = hour;
        this.min = min;
        this.fecha = fecha;
        this.hora = hora;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMin() {
        return min;
    }

    public void setMin(int min) {
        this.min = min;
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
}
