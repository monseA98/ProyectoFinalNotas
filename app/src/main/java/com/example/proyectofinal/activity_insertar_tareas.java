package com.example.proyectofinal;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.proyectofinal.DAOS.DAORutas;
import com.example.proyectofinal.DAOS.DAOTareas;

import java.io.File;
import java.net.URI;
import java.util.Calendar;

public class activity_insertar_tareas extends AppCompatActivity implements View.OnClickListener {

    ImageButton btnAdjuntar;
    ImageButton btnFoto;
    Button btnFecha;
    Button btnInsertar;
    EditText txtTitulo, txtDescripcion;

    Tarea tarea;
    private int day, month, year, hour, min;
    String m,d,fecha, hora, minutos, hr;
    Uri ruta;

    private static final String carpeta_principal = "misImagenesApp/"; //directorio principal
    private static final String carpeta_imagen = "imag/"; //carpeta donde se guardan las fotos
    private static final String directorio_imagen = carpeta_principal+carpeta_imagen; //directorio principal
    private String path; //almacena la ruta de la imagen
    File fileImagen;
    Bitmap bitmap;

    private static final int cod_adjuntar = 10;
    private static final int cod_foto = 20;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insertar_tareas);

        txtTitulo = findViewById(R.id.txtTituloTarea);
        txtDescripcion = findViewById(R.id.txtDescripTarea);

        btnAdjuntar = (ImageButton) findViewById(R.id.btnAdjuntarTarea);
        btnFoto = (ImageButton) findViewById(R.id.btnFotoTarea);
        btnFecha = findViewById(R.id.btnFecha);
        btnInsertar = findViewById(R.id.btnInsertarTarea);
        btnAdjuntar.setOnClickListener(this);
        btnFecha.setOnClickListener(this);
        btnFoto.setOnClickListener(this);
        btnInsertar.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if(view == btnInsertar){
            insert(view);
            insertRutas(ruta);
        }

        if(view == btnFecha){
            abrirReloj();
            abrirCalenadario();
        }

        if(view == btnAdjuntar) {
            cargarImagen();
        }

        if(view == btnFoto) {
            abrirCamara();
        }
    }

    private void insert(View view){
        tarea = new Tarea(0, txtTitulo.getText().toString(), txtDescripcion.getText().toString(), fecha, hr);

        DAOTareas dao = new DAOTareas(this);

        switch (view.getId()) {
            case R.id.btnInsertarTarea:
                dao.insert(tarea);
                finish();
        }
        Toast.makeText(this, "Se inserto la tarea "+txtTitulo.getText().toString(), Toast.LENGTH_SHORT).show();
    }

    private void insertRutas(Uri uri){
        Ruta ruta = new Ruta(0, uri, tarea.getId());
        DAORutas dao = new DAORutas(this);

        dao.insert(ruta);
        Toast.makeText(this, "Se inserto la ruta "+uri, Toast.LENGTH_SHORT).show();
    }

    private void cargarImagen() {
        Intent intentGaleria = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intentGaleria.setType("image/");
        startActivityForResult(intentGaleria.createChooser(intentGaleria,"Seleccione la aplicacion"),cod_adjuntar);
    }

    private void abrirCamara() {
        File miFile = new File(Environment.getExternalStorageDirectory(),directorio_imagen);
        boolean isCreada = miFile.exists();

        if(isCreada==false){
            isCreada=miFile.mkdirs();
        }

        if(isCreada==true){
            Long consecutivo = System.currentTimeMillis()/1000;
            String nombre = consecutivo.toString() +".jpg";

            path = Environment.getExternalStorageDirectory()+File.separator+directorio_imagen
                    +File.separator+nombre; //indicamos la ruta de almacenamiento

            fileImagen = new File(path);

            Intent intentFoto = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intentFoto.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(fileImagen));

            startActivityForResult(intentFoto,cod_foto);
        }
    }

    private void abrirCalenadario() {
        final Calendar c = Calendar.getInstance();
        day = c.get(Calendar.DAY_OF_MONTH);
        month = c.get(Calendar.MONTH);
        year = c.get(Calendar.YEAR);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                if((month+1)<10){
                    m = "0"+""+(month+1);
                }else{
                    m = ""+(month+1);
                }
                if(dayOfMonth<10){
                    d = "0"+""+dayOfMonth;
                }else{
                    d= ""+dayOfMonth;
                }
                //efecha.setText(dayOfMonth+"/"+(month + 1)+"/"+year);
                //btnFecha.setText(year+"/"+m+"/"+d);
                fecha= ""+year+"/"+m+"/"+d;
            }
        }
                ,day,month,year);
        datePickerDialog.show();
    }

    private void abrirReloj() {
        final Calendar c = Calendar.getInstance();
        hour = c.get(Calendar.HOUR_OF_DAY);
        min = c.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {

            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                if((hourOfDay+1)<10){
                    hora = "0"+""+(hourOfDay+1);
                }else{
                    hora = ""+(hourOfDay+1);
                }
                if(minute<10){
                    minutos = "0"+""+minute;
                }else{
                    minutos= ""+minute;
                }
                hr = hora+":"+minutos;
                btnFecha.setText(fecha+"  "+hr);
            }
        },hour,min,false);
        timePickerDialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK){


            switch(requestCode){

                case cod_adjuntar: //para cuando adjunto la foto
                    ruta =  data.getData(); //obtiene la ruta de la imagen seleccionada
                    Toast.makeText(this, ""+ruta, Toast.LENGTH_SHORT).show();
                    break;

                case cod_foto:
                    MediaScannerConnection.scanFile(this, new String[]{path}, null,
                            new MediaScannerConnection.OnScanCompletedListener() {
                                @Override
                                public void onScanCompleted(String path, Uri uri) {
                                    Log.i("Path",""+path);
                                }
                            });
                    bitmap = BitmapFactory.decodeFile(path);
                    //asignar la foto
            }
        }
    }

}