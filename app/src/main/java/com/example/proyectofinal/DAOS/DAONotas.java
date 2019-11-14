package com.example.proyectofinal.DAOS;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.proyectofinal.BD;
import com.example.proyectofinal.Nota;

import java.util.ArrayList;

public class DAONotas {
    SQLiteDatabase _sqLiteDatabase;
    Context ctx;

    public DAONotas(Context ctx) {
        this.ctx = ctx;
        _sqLiteDatabase =
                new BD(ctx).getWritableDatabase();
    }

    public long insert(Nota nota){
        ContentValues contentValues
                = new ContentValues();

        contentValues.put(BD.COLUMNS_NAME_NOTAS[1],
                nota.getTitulo());
        contentValues.put(BD.COLUMNS_NAME_NOTAS[2],
                nota.getDescripcion());

        return  _sqLiteDatabase.insert(BD.TABLE_NAME_NOTAS,
                null, contentValues);
    }

    public ArrayList<Nota> getAll (){
        ArrayList<Nota> lst = new ArrayList<Nota>();
        String[] columnasAConsultar = {BD.COLUMNS_NAME_NOTAS[0], BD.COLUMNS_NAME_NOTAS[1], BD.COLUMNS_NAME_NOTAS[2]};
        Cursor c = _sqLiteDatabase.query(BD.TABLE_NAME_NOTAS,
                columnasAConsultar,
                null,
                null,
                null,
                null,
                null,
                null);

        if (c.moveToFirst() ){
            do {
                Nota nota =
                        new Nota(c.getInt(0), c.getString(1),
                                c.getString(2));
                lst.add(nota);

            }while(c.moveToNext());
        }
        return  lst;
    }


    public int update (Nota nota){

        ContentValues valoresParaActualizar =  new ContentValues();
        valoresParaActualizar.put(BD.COLUMNS_NAME_NOTAS[1], nota.getTitulo());
        valoresParaActualizar.put(BD.COLUMNS_NAME_NOTAS[2], nota.getDescripcion());
        String campoParaActualizar = "_id = ?";
        String[] argumentosParaActualizar = {String.valueOf(nota.getId())};
        return _sqLiteDatabase.update(BD.TABLE_NAME_NOTAS, valoresParaActualizar, campoParaActualizar, argumentosParaActualizar);

    }

    public int eliminar (int id){

        String[] argumentos = {String.valueOf(id)};
        return _sqLiteDatabase.delete(BD.TABLE_NAME_NOTAS, "_id = ?", argumentos);

    }

    public ArrayList<Nota> buscarporTitulo(String[] titulo){
        ArrayList<Nota> notas = new ArrayList<>();

        String[] columnasAConsultar = {BD.COLUMNS_NAME_NOTAS[0], BD.COLUMNS_NAME_NOTAS[1], BD.COLUMNS_NAME_NOTAS[2]};
        Cursor cursor = _sqLiteDatabase.query(BD.TABLE_NAME_NOTAS, columnasAConsultar, "titulo = ? OR descripcion = ?", titulo, null, null, null);

        if(titulo[0].equals("")){

            cursor = _sqLiteDatabase.query(BD.TABLE_NAME_NOTAS, columnasAConsultar, null, null, null, null, null);
        }

        if (cursor == null){
            return notas;
        }

        if (!cursor.moveToFirst()) return notas;

        do {

            int idObtenidoDeBD = cursor.getInt(0);
            String tituloObtenidoDeBD = cursor.getString(1);
            String descripcionObtenidoDeBD = cursor.getString(2);

            Nota notaObtenidoDeBD = new Nota(idObtenidoDeBD, tituloObtenidoDeBD, descripcionObtenidoDeBD);
            notas.add(notaObtenidoDeBD);

        } while (cursor.moveToNext());

        cursor.close();
        return notas;
    }

}
