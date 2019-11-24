package com.example.proyectofinal;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.icu.text.SimpleDateFormat;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.proyectofinal.DAOS.DAORutas;
import com.example.proyectofinal.DAOS.DAOTareas;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class ActualizarTareas extends AppCompatActivity implements View.OnClickListener, ExampleDialog.ExampleDilaogListener {

    ImageButton btnAdjuntar;
    ImageButton btnFoto;
    ImageButton btnAudio;
    Button btnFecha;
    Button btnActualizar;
    EditText txtTitulo, txtDescripcion;
    Uri path;

    ArrayAdapter<Model> adapter;
    ArrayList<Model> listaModelos = new ArrayList<>();
    ArrayList<Model> listaModelosNueva = new ArrayList<>();

    Tarea tarea;
    private int day, month, year, hour, min;
    String m,d,fecha, hora, minutos, hr;
    final Calendar c = Calendar.getInstance();
    Uri ruta;
    String descripcion;

    private static final int cod_adjuntar = 10;
    private static final int cod_adjuntarVideo = 20;
    private static final int cod_adjuntarAudio = 30;

    static final int REQUEST_TAKE_PHOTO = 2;
    String currentPhotoPath;
    RecyclerView recyclerView;
    MyRecyclerViewAdapter Imagesadapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actualizar_tareas);

        tarea = (Tarea)getIntent().getExtras().getSerializable("tarea");

        fecha = tarea.getFecha();
        hr = tarea.getHora();

        txtTitulo = findViewById(R.id.txtTituloTareaAct);
        txtTitulo.setText(tarea.getTitulo().toString());

        txtDescripcion = findViewById(R.id.txtDescripTareaAct);
        txtDescripcion.setText(tarea.getDescripcion().toString());

        btnFecha = findViewById(R.id.btnFechaAct);
        btnFecha.setText(tarea.getFecha()+" "+tarea.getHora());

        btnAdjuntar = (ImageButton) findViewById(R.id.btnAdjuntarTareaAct);
        btnFoto = (ImageButton) findViewById(R.id.btnFotoTareaAct);
        btnAudio = findViewById(R.id.btnAudioTareaAct);
        btnActualizar = findViewById(R.id.btnActualizarTarea);
        recyclerView = findViewById(R.id.recyclerTareaAct);

        btnAdjuntar.setOnClickListener(this);
        btnFecha.setOnClickListener(this);
        btnFoto.setOnClickListener(this);
        btnAudio.setOnClickListener(this);
        btnActualizar.setOnClickListener(this);

        if (validarPermisos()) {
            btnFoto.setEnabled(true);
            btnAdjuntar.setEnabled(true);
        } else {
            btnFoto.setEnabled(false);
            btnAdjuntar.setEnabled(false);
        }

        adapter = new ArrayAdapter<Model>(this, android.R.layout.simple_list_item_1, listaModelos);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 1));

        listaModelos = obtenerRutas();

        Imagesadapter = new MyRecyclerViewAdapter(this, listaModelos);
        recyclerView.setAdapter(Imagesadapter);

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onClick(View view) {
        if(view == btnActualizar){
            actualizar(view);
            insertRutas(view);
        }

        if(view == btnFecha){
            abrirCalenadario(view);
        }

        if(view == btnAdjuntar) {
            dialogoAdjuntar();
        }

        if(view == btnFoto) {
            dialogoTomar();
        }

        if(view == btnAudio) {
            grabarAudio(view);
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

    private ArrayList<Model> obtenerRutas(){
        ArrayList<Model> listaModel = new ArrayList<>();
        DAORutas daoRutasTareas = new DAORutas(this);
        String[] idTarea = {""+tarea.getId()}; //para pasarle el Id de la Nota al de buscar y que busque todas las rutas con el id de la Nota

        for(int i=0; i<daoRutasTareas.buscarObjeto(idTarea).size(); i++){

            Ruta ruta = daoRutasTareas.buscarObjeto(idTarea).get(i);

            listaModel.add(new Model (ruta.getTipo(),
                    ruta.getDescripcion(), ruta.getRuta()));
        }

        Toast.makeText(this, "Entre al obtenerRutas(): "+listaModel.size(), Toast.LENGTH_SHORT).show();
        return listaModel;
    }

    private void insertRutas(View view){

        String[] Tareas1 = {""};
        DAOTareas daoTareas = new DAOTareas(this);
        ArrayList<Integer> arrayIds = new ArrayList<>();
        arrayIds = daoTareas.buscarUltimoId(Tareas1); //El array que me gusrda todos los ids de las Notas

        if(listaModelosNueva !=null) {
            for (int i = 0; i < listaModelosNueva.size(); i++) {

                Ruta ruta = new Ruta(0, listaModelosNueva.get(i).data, listaModelosNueva.get(i).type, listaModelosNueva.get(i).text ,tarea.getId());
                DAORutas daoRutas = new DAORutas(this);

                switch (view.getId()) {
                    case R.id.btnActualizarTarea:
                        daoRutas.insert(ruta);
                        Log.i("RUTAS", ""+ruta.getId() +" path= "+ruta.getRuta()+"idNota= "+ruta.getIdTarea());
                        //finish();
                }
                Log.i("RUTAS", ""+ruta.getId() +" path= "+ruta.getRuta()+ "idNota= "+ruta.getIdTarea());
            }

        }else{
            //finish();
        }
        finish();
    }

    private void dialogoTomar (){
        final CharSequence[] items = {getString(R.string.tomar_foto), getString(R.string.tomar_video)};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.seleccione_opcion));
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
        final CharSequence[] items = {getString(R.string.adjuntar_foto), getString(R.string.adjuntar_video)};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.seleccione_opcion));
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
        startActivityForResult(intentGaleria.createChooser(intentGaleria,getString(R.string.seleccione_aplicacion)),cod_adjuntar);
    }

    private void cargarVideo() {
        Intent intentGaleria = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
        intentGaleria.setType("video/");
        startActivityForResult(intentGaleria.createChooser(intentGaleria,getString(R.string.seleccione_aplicacion)),cod_adjuntarVideo);
    }

    private void cargarAudio() {
        Intent intentGaleria = new Intent(Intent.ACTION_PICK, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
        intentGaleria.setType("audio/");
        startActivityForResult(intentGaleria.createChooser(intentGaleria,getString(R.string.seleccione_aplicacion)),cod_adjuntarAudio);
    }

    static final int REQUEST_VIDEO_CAPTURE = 3;

    private void dispatchTakeVideoIntent() {
        Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        if (takeVideoIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takeVideoIntent, REQUEST_VIDEO_CAPTURE);
        }
    }

    //permisos
    private boolean validarPermisos() {

        if(Build.VERSION.SDK_INT<Build.VERSION_CODES.M){
            return true;
        }


        if( (checkSelfPermission(RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) &&
                (checkSelfPermission(WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)){
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
            requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE,CAMERA,RECORD_AUDIO}, 100);
        }

        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode==100){
            if(grantResults.length==3 && grantResults[0]==PackageManager.PERMISSION_GRANTED &&
                    grantResults[1]==PackageManager.PERMISSION_GRANTED){
                btnFoto.setEnabled(true);
                btnAdjuntar.setEnabled(true);
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


    //para agregar una descripcion despues de adjuntar o tomar algo, con un dialogo
    public void openDialog(){
        ExampleDialog exampleDialog = new ExampleDialog();
        exampleDialog.show(getSupportFragmentManager(), "example dialog");
    }

    @Override
    public void applyTexts(String descripcion) {
        txtDescripcion.setText(descripcion); //solo para probar si obtiene
        this.descripcion = descripcion; // la variable global descripcion obtiene el valor de lo que hay en el input del Dialog
    }

    public void permisosAudio(){ //posiblemente no funciona
        if (ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO}, 1000);
        }
    }

    private MediaRecorder grabacion=null;
    private String archivoSalida = null;

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void grabarAudio(View view){
        if(grabacion==null){
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            archivoSalida = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Grabacion_"+timeStamp+".mp3";
            grabacion = new MediaRecorder();
            grabacion.setAudioSource(MediaRecorder.AudioSource.MIC);
            grabacion.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            grabacion.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
            grabacion.setOutputFile(archivoSalida);

            try{
                grabacion.prepare();
                grabacion.start(); //comenzar a grabar
            }catch (IOException e){

            }
            btnAudio.setColorFilter(Color.argb(255, 255, 0, 0)); // Cuando este grabando lo pongo color rojo
            Toast.makeText(getApplicationContext(),getString(R.string.grabando),Toast.LENGTH_SHORT).show();

        }else if(grabacion!=null){
            grabacion.stop();
            grabacion.release();
            grabacion = null; //para que pueda volver a grabar si se presiona el boton nuevamente
            btnAudio.setColorFilter(Color.argb(255, 0, 0, 0)); // ya no grabando, regresa a color negro

            Model model = new Model(Model.AUDIO_TYPE, "", Uri.parse(archivoSalida));
            listaModelos.add(model);
            listaModelosNueva.add(model); // para que despues no se revuelvan entre las nuevas paths y las viejas
            recyclerView.setAdapter(Imagesadapter);

            Toast.makeText(getApplicationContext(),getString(R.string.grab_finalizada),Toast.LENGTH_SHORT).show();
        }
    }

    public void reproducirAudio(View view){
        MediaPlayer mediaPlayer = new MediaPlayer();

        try{
            mediaPlayer.setDataSource(archivoSalida);
            mediaPlayer.prepare();
        }catch (IOException e){

        }
        mediaPlayer.start();
        Toast.makeText(getApplicationContext(),"Reproduciendo ultimo audio",Toast.LENGTH_SHORT).show();
    }

    private void abrirCalenadario(View view) {
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);
        hour = c.get(Calendar.HOUR_OF_DAY);
        min = c.get(Calendar.MINUTE);

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

                abrirReloj();
            }
        } ,year,month,day);
        datePickerDialog.show();

    }

    private void abrirReloj() {
        hour = c.get(Calendar.HOUR_OF_DAY);
        min = c.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {

            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                if((hourOfDay+1)<10){
                    hora = "0"+""+(hourOfDay);
                }else{
                    hora = ""+(hourOfDay);
                }
                if(minute<10){
                    minutos = "0"+""+minute;
                }else{
                    minutos= ""+minute;
                }
                hr = hora+":"+minutos;
                min = minute;
                hour= hourOfDay;
                btnFecha.setText(fecha+"  "+hr);

            }
        },hour,min,false);
        timePickerDialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //adjuntar
        if(requestCode== cod_adjuntar && resultCode==RESULT_OK){
            Uri path =  data.getData(); //obtiene la ruta de la imagen seleccionada
            Model model = new Model(Model.IMAGE_TYPE,"Imagen",path);
            listaModelos.add(model);
            listaModelosNueva.add(model); // para que despues no se revuelvan entre las nuevas paths y las viejas
            recyclerView.setAdapter(Imagesadapter);

            Toast.makeText(this, ""+path, Toast.LENGTH_SHORT).show();
        }

        if(requestCode== REQUEST_TAKE_PHOTO && resultCode==RESULT_OK){
            //listaModelos.add(Uri.parse(currentPhotoPath));
            Model model = new Model(0,"",Uri.parse(currentPhotoPath));
            listaModelos.add(model);
            listaModelosNueva.add(model); // para que despues no se revuelvan entre las nuevas paths y las viejas
            recyclerView.setAdapter(Imagesadapter);
        }

        //TOMAR FOTO O VIDEO
        if (requestCode == cod_adjuntarVideo && resultCode == RESULT_OK) {
            Uri videoUri = data.getData();//videoView.setVideoURI(videoUri);
            Model model = new Model(Model.VIDEO_TYPE, "", videoUri);
            listaModelos.add(model);
            listaModelosNueva.add(model); // para que despues no se revuelvan entre las nuevas paths y las viejas
            recyclerView.setAdapter(Imagesadapter);

            Toast.makeText(this, ""+videoUri, Toast.LENGTH_SHORT).show();
        }

        //tomar desde la camara
        if (requestCode == REQUEST_VIDEO_CAPTURE && resultCode == RESULT_OK) {
            Uri videoUri = data.getData();//videoView.setVideoURI(videoUri);
            Model model = new Model(Model.VIDEO_TYPE, "", videoUri);
            listaModelos.add(model);
            listaModelosNueva.add(model); // para que despues no se revuelvan entre las nuevas paths y las viejas
            recyclerView.setAdapter(Imagesadapter);

            Toast.makeText(this, ""+videoUri, Toast.LENGTH_SHORT).show();
        }
    }

}
