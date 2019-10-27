package com.example.proyectofinal.DAOS;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.proyectofinal.BD;
import com.example.proyectofinal.Recordatorio;

import java.util.ArrayList;

public class DAORecordatorios {
    SQLiteDatabase _sqLiteDatabase;
    Context ctx;

    public DAORecordatorios(Context ctx) {
        this.ctx = ctx;
        _sqLiteDatabase =
                new BD(ctx).getWritableDatabase();
    }

    public long insert(Recordatorio recordatorio){
        ContentValues contentValues
                = new ContentValues();

        contentValues.put(BD.COLUMNS_NAME_RECORDATORIOS[1],
                recordatorio.getFecha());
        contentValues.put(BD.COLUMNS_NAME_RECORDATORIOS[2],
                recordatorio.getHora());
        contentValues.put(BD.COLUMNS_NAME_RECORDATORIOS[3],
                recordatorio.getIdTarea());

        return  _sqLiteDatabase.insert(BD.TABLE_NAME_RECORDATORIOS,
                null, contentValues);
    }

    public ArrayList<Recordatorio> buscar(String[] t){
        ArrayList<Recordatorio> recordatorios = new ArrayList<>();

        String[] columnasAConsultar = {BD.COLUMNS_NAME_RECORDATORIOS[0], BD.COLUMNS_NAME_RECORDATORIOS[1], BD.COLUMNS_NAME_RECORDATORIOS[2], BD.COLUMNS_NAME_RECORDATORIOS[3]};
        Cursor cursor = _sqLiteDatabase.query(BD.TABLE_NAME_RECORDATORIOS, columnasAConsultar, "t = ?", t, null, null, null);

        if(t[0].equals("")){

            cursor = _sqLiteDatabase.query(BD.TABLE_NAME_RECORDATORIOS, columnasAConsultar, null, null, null, null, null);
        }

        if (cursor == null){
            return recordatorios;
        }

        if (!cursor.moveToFirst()) return recordatorios;

        do {

            int idObtenidoDeBD = cursor.getInt(0);
            String fechaObtenidoDeBD = cursor.getString(1);
            String horaObtenidoDeBD = cursor.getString(2);
            int idTareaObtenidoDeBD = cursor.getInt(3);

            Recordatorio recordatorioObtenidoDeBD = new Recordatorio(idObtenidoDeBD, fechaObtenidoDeBD, horaObtenidoDeBD, idTareaObtenidoDeBD);
            recordatorios.add(recordatorioObtenidoDeBD);

        } while (cursor.moveToNext());

        cursor.close();
        return recordatorios;
    }
}
