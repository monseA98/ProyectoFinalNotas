package com.example.proyectofinal.DAOS;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.proyectofinal.BD;
import com.example.proyectofinal.Tarea;

import java.util.ArrayList;

public class DAOTareas {

    SQLiteDatabase _sqLiteDatabase;
    Context ctx;

    public DAOTareas(Context ctx) {
        this.ctx = ctx;
        _sqLiteDatabase =
                new BD(ctx).getWritableDatabase();
    }

    public long insert(Tarea tarea){
        ContentValues contentValues
                = new ContentValues();

        contentValues.put(BD.COLUMNS_NAME_TAREAS[1],
                tarea.getTitulo());
        contentValues.put(BD.COLUMNS_NAME_TAREAS[2],
                tarea.getDescripcion());
        contentValues.put(BD.COLUMNS_NAME_TAREAS[3],
                tarea.getFecha());
        contentValues.put(BD.COLUMNS_NAME_TAREAS[4],
                tarea.getHora());

        return  _sqLiteDatabase.insert(BD.TABLE_NAME_TAREAS,
                null, contentValues);
    }

    public int update (Tarea tarea){

        ContentValues valoresParaActualizar =  new ContentValues();
        valoresParaActualizar.put(BD.COLUMNS_NAME_TAREAS[1], tarea.getTitulo());
        valoresParaActualizar.put(BD.COLUMNS_NAME_TAREAS[2], tarea.getDescripcion());
        valoresParaActualizar.put(BD.COLUMNS_NAME_TAREAS[3], tarea.getFecha());
        valoresParaActualizar.put(BD.COLUMNS_NAME_TAREAS[4], tarea.getHora());
        String campoParaActualizar = "_id = ?";
        String[] argumentosParaActualizar = {String.valueOf(tarea.getId())};
        return _sqLiteDatabase.update(BD.TABLE_NAME_TAREAS, valoresParaActualizar, campoParaActualizar, argumentosParaActualizar);

    }

    public int eliminar (int id){

        String[] argumentos = {String.valueOf(id)};
        return _sqLiteDatabase.delete(BD.TABLE_NAME_TAREAS, "_id = ?", argumentos);

    }

    public ArrayList<Tarea> buscarporTitulo(String[] titulo){
        ArrayList<Tarea> tareas = new ArrayList<>();

        String[] columnasAConsultar = {BD.COLUMNS_NAME_TAREAS[0], BD.COLUMNS_NAME_TAREAS[1], BD.COLUMNS_NAME_TAREAS[2], BD.COLUMNS_NAME_TAREAS[3], BD.COLUMNS_NAME_TAREAS[4]};
        Cursor cursor = _sqLiteDatabase.query(BD.TABLE_NAME_TAREAS, columnasAConsultar, "titulo = ? OR descripcion = ?", titulo, null, null, null);

        if(titulo[0].equals("")){

            cursor = _sqLiteDatabase.query(BD.TABLE_NAME_TAREAS, columnasAConsultar, null, null, null, null, null);
        }

        if (cursor == null){
            return tareas;
        }

        if (!cursor.moveToFirst()) return tareas;

        do {

            int idObtenidoDeBD = cursor.getInt(0);
            String tituloObtenidoDeBD = cursor.getString(1);
            String descripcionObtenidoDeBD = cursor.getString(2);
            String fechaObtenidoDeBD = cursor.getString(3);
            String horaObtenidoDeBD = cursor.getString(4);

            Tarea tareaObtenidoDeBD = new Tarea(idObtenidoDeBD, tituloObtenidoDeBD, descripcionObtenidoDeBD, fechaObtenidoDeBD, horaObtenidoDeBD);
            tareas.add(tareaObtenidoDeBD);

        } while (cursor.moveToNext());

        cursor.close();
        return tareas;
    }

    public ArrayList<Integer> buscarUltimoId(String[] id){
        ArrayList<Integer> tareas = new ArrayList<>();

        String[] columnasAConsultar = {BD.COLUMNS_NAME_TAREAS[0]};
        Cursor cursor = _sqLiteDatabase.query(BD.TABLE_NAME_TAREAS, columnasAConsultar, "_id = ? ", id, null, null, null);

        if(id[0].equals("")){

            cursor = _sqLiteDatabase.query(BD.TABLE_NAME_TAREAS, columnasAConsultar, null, null, null, null, null);
        }

        if (cursor == null){
            return tareas;
        }

        if (!cursor.moveToFirst()) return tareas;

        do {

            int idObtenidoDeBD = cursor.getInt(0);

            tareas.add(idObtenidoDeBD);

        } while (cursor.moveToNext());

        cursor.close();
        return tareas;
    }
}
