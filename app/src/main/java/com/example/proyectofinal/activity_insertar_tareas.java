package com.example.proyectofinal;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.icu.text.SimpleDateFormat;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.AlarmClock;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.proyectofinal.DAOS.DAORutas;
import com.example.proyectofinal.DAOS.DAOTareas;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class activity_insertar_tareas extends AppCompatActivity implements View.OnClickListener {

    ImageButton btnAdjuntar;
    ImageButton btnFoto;
    Button btnFecha;
    Button btnInsertar;
    EditText txtTitulo, txtDescripcion;
    RecyclerView recyclerView;
    MyRecyclerViewAdapter Imagesadapter;

    Tarea tarea;
    private int day, month, year, hour, min;
    String m,d,fecha, hora, minutos, hr;
    Uri ruta;
    Uri path;

    ArrayList<Uri> listaRutas = new ArrayList<>();

    private static final int cod_adjuntar = 10;
    private static final int cod_adjuntarVideo = 20;

    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_TAKE_PHOTO = 2;
    String currentPhotoPath;


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

        if (validarPermisos()) {
            btnFoto.setEnabled(true);
        } else {
            btnFoto.setEnabled(false);
        }

        recyclerView = findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onClick(View view) {
        if(view == btnInsertar){
            insert(view);
            //insertRutas();
        }

        if(view == btnFecha){
            abrirReloj();
            abrirCalenadario();
        }

        if(view == btnAdjuntar) {
            dialogoAdjuntar();
        }

        if(view == btnFoto) {
            dialogoTomar();
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

    /*private void insertRutas(){
        if(listaRutas!=null) {
            for (int i = 0; i < listaRutas.size()-1; i++) {

                Ruta ruta = new Ruta(0, listaRutas.get(i), tarea.getId());
                DAORutas dao = new DAORutas(this);
                dao.insert(ruta);
                Toast.makeText(this, "Se inserto la ruta " + listaRutas.get(i), Toast.LENGTH_SHORT).show();
            }
        }else{
            finish();
        }
        finish();

    }*/

    private void abrirCalenadario() {
        Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);

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
        } ,year,month,day);
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

                //createAlarm("Soy una alarma",hourOfDay,minute);

            }
        },hour,min,false);
        timePickerDialog.show();
    }

    private void dialogoTomar (){
        final CharSequence[] items = {"Tomar foto", "Tomar vídeo"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Seleccione una opción");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if(item==0){
                    dispatchTakePictureIntent();
                }

                if(item==1){
                    dispatchTakeVideoIntent();
                }
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void dialogoAdjuntar (){
        final CharSequence[] items = {"Adjunatar foto", "Adjuntar vídeo"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Seleccione una opción");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if(item==0){
                    cargarImagen();
                }

                if(item==1){
                    cargarVideo();
                }
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.proyectofinal.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void cargarImagen() {
        Intent intentGaleria = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intentGaleria.setType("image/");
        startActivityForResult(intentGaleria.createChooser(intentGaleria,"Seleccione la aplicacion"),cod_adjuntar);
    }

    private void cargarVideo() {
        Intent intentGaleria = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
        intentGaleria.setType("video/");
        startActivityForResult(intentGaleria.createChooser(intentGaleria,"Seleccione la aplicacion"),cod_adjuntarVideo);
    }

    static final int REQUEST_VIDEO_CAPTURE = 3;

    private void dispatchTakeVideoIntent() {
        Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        if (takeVideoIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takeVideoIntent, REQUEST_VIDEO_CAPTURE);
        }
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode ==  REQUEST_TAKE_PHOTO && resultCode== RESULT_OK){
            Toast.makeText(this, "path "+currentPhotoPath, Toast.LENGTH_SHORT).show();

            //no se si funcione
            listaRutas.add(Uri.parse(currentPhotoPath));

            Imagesadapter = new MyRecyclerViewAdapter(this, listaRutas);
            recyclerView.setAdapter(Imagesadapter);
            //
        }

        if(requestCode==cod_adjuntar && resultCode==RESULT_OK){
            path =  data.getData(); //obtiene la ruta de la imagen seleccionada
            listaRutas.add(path);

            Imagesadapter = new MyRecyclerViewAdapter(this, listaRutas);
            recyclerView.setAdapter(Imagesadapter);
        }

        if (requestCode == cod_adjuntarVideo && resultCode == RESULT_OK) {
            Uri videoUri = data.getData();//videoView.setVideoURI(videoUri);
            listaRutas.add(videoUri);

            Imagesadapter = new MyRecyclerViewAdapter(this, listaRutas);
            recyclerView.setAdapter(Imagesadapter);

            Toast.makeText(this, ""+videoUri, Toast.LENGTH_SHORT).show();
        }

        //tomar video desde la camara
        if (requestCode == REQUEST_VIDEO_CAPTURE && resultCode == RESULT_OK) {
            Uri videoUri = data.getData();//videoView.setVideoURI(videoUri);
            listaRutas.add(videoUri);

            Imagesadapter = new MyRecyclerViewAdapter(this, listaRutas);
            recyclerView.setAdapter(Imagesadapter);

            Toast.makeText(this, ""+videoUri, Toast.LENGTH_SHORT).show();
        }
    }



    //permisos
    private boolean validarPermisos() {

        if(Build.VERSION.SDK_INT<Build.VERSION_CODES.M){
            return true;
        }

        if( (checkSelfPermission(CAMERA) == PackageManager.PERMISSION_GRANTED) &&
                (checkSelfPermission(WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)){
            return true;
        }

        if( (shouldShowRequestPermissionRationale(CAMERA)) ||
                (shouldShowRequestPermissionRationale(WRITE_EXTERNAL_STORAGE)) ){
            cargarDialogoRecomendacion();
        }else{
            requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE,CAMERA}, 100);
        }

        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode==100){
            if(grantResults.length==2 && grantResults[0]==PackageManager.PERMISSION_GRANTED &&
                    grantResults[1]==PackageManager.PERMISSION_GRANTED){
                btnFoto.setEnabled(true);
            }else{
                //solicitarPermisosManual();
            }
        }
    }

    private void cargarDialogoRecomendacion() {
        AlertDialog.Builder dialogo =  new AlertDialog.Builder(this);
        dialogo.setTitle("Permisos Desactivados");
        dialogo.setMessage("Chale, debe aceptar los permisos para que funcione");

        dialogo.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M) //esto se agrego porque marcaba error el requestPermissions
            @Override
            public void onClick(DialogInterface dialog, int which) {
                requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE,CAMERA}, 100);
            }
        });
        dialogo.show();
    }

    public void createAlarm(String message, int hour, int minutes) {
        Intent intent = new Intent(AlarmClock.ACTION_SET_ALARM)
                .putExtra(AlarmClock.EXTRA_MESSAGE, message)
                .putExtra(AlarmClock.EXTRA_HOUR, hour)
                .putExtra(AlarmClock.EXTRA_MINUTES, minutes);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

}
