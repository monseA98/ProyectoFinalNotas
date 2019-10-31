package com.example.proyectofinal;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class BD extends SQLiteOpenHelper {


    private String SCRIPT_DB_NOTAS = "create table Notas (" +
            "_id integer primary key autoincrement," +
            "titulo text not null," +
            "descripcion text not null);"
            ;

    public static final String[] COLUMNS_NAME_NOTAS =
            {
                    "_id", "titulo", "descripcion"
            };

    public  static final String TABLE_NAME_NOTAS =
            "Notas";


    //////////TAREAS///////////////////////////////////////////////////////////////////////////////
    private String SCRIPT_DB_TAREAS = "create table Tareas (" +
            "_id integer primary key autoincrement," +
            "titulo text not null," +
            "descripcion text not null," +
            "fecha text ," +
            "hora text);"
            ;

    public static final String[] COLUMNS_NAME_TAREAS =
            {
                    "_id", "titulo", "descripcion", "fecha", "hora"
            };

    public  static final String TABLE_NAME_TAREAS =
            "Tareas";


    ///RECORDATORIOS///////////////////////////////////////////////////////////////////////////////
    private String SCRIPT_DB_RECORDATORIOS = "create table Recordatorios (" +
            "_id integer primary key autoincrement," +
            "fecha text not null," +
            "hora text not null,"+
            "idTarea int not null, foreign key (idTarea) references Tareas(_id));"
            ;

    public static final String[] COLUMNS_NAME_RECORDATORIOS =
            {
                    "_id", "fecha", "hora", "idTarea"
            };

    public  static final String TABLE_NAME_RECORDATORIOS =
            "Recordatorios";
/////////////////////////////////////////////////////////////////////////////////////////

    public BD(@Nullable Context context) {
        super(context,
                "mibaseD",
                null,
                1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(SCRIPT_DB_NOTAS);
        sqLiteDatabase.execSQL(SCRIPT_DB_TAREAS);
        sqLiteDatabase.execSQL(SCRIPT_DB_RECORDATORIOS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}