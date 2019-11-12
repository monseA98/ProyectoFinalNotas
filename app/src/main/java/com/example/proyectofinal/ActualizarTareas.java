package com.example.proyectofinal;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.proyectofinal.DAOS.DAORutas;
import com.example.proyectofinal.DAOS.DAOTareas;

import java.util.Calendar;

public class ActualizarTareas extends AppCompatActivity implements View.OnClickListener{

    ImageButton btnAdjuntar;
    ImageButton btnFoto;
    Button btnFecha;
    Button btnActualizar;
    EditText txtTitulo, txtDescripcion;

    Tarea tarea;
    private int day, month, year, hour, min;
    String m,d,fecha, hora, minutos, hr;
    Uri ruta;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actualizar_tareas);


        tarea = (Tarea)getIntent().getExtras().getSerializable("tarea");

        txtTitulo = findViewById(R.id.txtTituloTareaAct);
        txtTitulo.setText(tarea.getTitulo().toString());

        txtDescripcion = findViewById(R.id.txtDescripTareaAct);
        txtDescripcion.setText(tarea.getDescripcion().toString());

        btnFecha = findViewById(R.id.btnFechaAct);
        btnFecha.setText(tarea.getFecha()+""+tarea.getHora());

        btnAdjuntar = (ImageButton) findViewById(R.id.btnAdjuntarTareaAct);
        btnFoto = (ImageButton) findViewById(R.id.btnFotoTareaAct);
        btnActualizar = findViewById(R.id.btnActualizarTarea);
        btnAdjuntar.setOnClickListener(this);
        btnFecha.setOnClickListener(this);
        btnFoto.setOnClickListener(this);
        btnActualizar.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if(view == btnActualizar){
            actualizar(view);
            insertRutas(ruta);
        }

        if(view == btnFecha){
            abrirReloj();
            abrirCalenadario();
        }

        if(view == btnAdjuntar) {
            cargarImagen();
        }
    }

    private void actualizar(View view){
        tarea.setTitulo(txtTitulo.getText().toString());
        tarea.setDescripcion(txtDescripcion.getText().toString());
        tarea.setFecha(fecha);
        tarea.setHora(hr);

        DAOTareas dao = new DAOTareas(this);

        switch (view.getId()) {
            case R.id.btnActualizarTarea:
                dao.update(tarea);
                finish();
        }
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
        startActivityForResult(intentGaleria.createChooser(intentGaleria,"Seleccione la aplicacion"),10);
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

            ruta =  data.getData(); //obtiene la ruta de la imagen seleccionada
            Toast.makeText(this, ""+ruta, Toast.LENGTH_SHORT).show();
        }

    }
}
