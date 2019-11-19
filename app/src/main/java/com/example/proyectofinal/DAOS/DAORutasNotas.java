package com.example.proyectofinal.DAOS;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.example.proyectofinal.BD;
import com.example.proyectofinal.Ruta;

import java.util.ArrayList;

public class DAORutasNotas {
    SQLiteDatabase _sqLiteDatabase;
    Context ctx;

    public DAORutasNotas(Context ctx) {
        this.ctx = ctx;
        _sqLiteDatabase =
                new BD(ctx).getWritableDatabase();
    }

    public long insert(Ruta ruta){
        ContentValues contentValues
                = new ContentValues();

        String path = ruta.getRuta().toString();
        contentValues.put(BD.COLUMNS_NAME_RUTASN[1],
                path);
        contentValues.put(BD.COLUMNS_NAME_RUTASN[2],
                ruta.getIdTarea());

        return  _sqLiteDatabase.insert(BD.TABLE_NAME_RUTASN,
                null, contentValues);
    }

    public ArrayList<Uri> buscar(String[] id){
        ArrayList<Uri> rutas = new ArrayList<>();

        ////////////////
        String[] columnasAConsultar = {BD.COLUMNS_NAME_RUTASN[1]};
        Cursor cursor = _sqLiteDatabase.query(BD.TABLE_NAME_RUTASN, columnasAConsultar, "_idNota = ?", id, null, null, null);

        if(id[0].equals("")){

            cursor = _sqLiteDatabase.query(BD.TABLE_NAME_RUTASN, columnasAConsultar, null, null, null, null, null);
        }

        if (cursor == null){
            return rutas;
        }

        if (!cursor.moveToFirst()) return rutas;

        do {

            //int idObtenidoDeBD = cursor.getInt(0);
            Uri pathObtenidoDeBD = Uri.parse(cursor.getString(1));
            //int idTareaObtenidoDeBD = cursor.getInt(2);

            //Ruta rutaObtenidoDeBD = new Ruta(idObtenidoDeBD, pathObtenidoDeBD, idTareaObtenidoDeBD);
            rutas.add(pathObtenidoDeBD);

        } while (cursor.moveToNext());

        cursor.close();
        return rutas;
    }

    public int eliminar (int id){

        String[] argumentos = {String.valueOf(id)};
        return _sqLiteDatabase.delete(BD.TABLE_NAME_RUTASN, "_id = ?", argumentos);

    }
}
