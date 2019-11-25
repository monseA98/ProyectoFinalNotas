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
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.icu.text.SimpleDateFormat;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.AlarmClock;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.proyectofinal.DAOS.DAONotas;
import com.example.proyectofinal.DAOS.DAORecordatorios;
import com.example.proyectofinal.DAOS.DAORutas;
import com.example.proyectofinal.DAOS.DAORutasNotas;
import com.example.proyectofinal.DAOS.DAOTareas;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class activity_insertar_tareas extends AppCompatActivity implements View.OnClickListener, ExampleDialog.ExampleDilaogListener {

    ImageButton btnAdjuntar;
    ImageButton btnFoto;
    ImageButton btnAudio;
    ImageButton prueba;
    Button btnFecha;
    Button btnInsertar;
    Button btnRecordatorios;
    EditText txtTitulo, txtDescripcion;
    RecyclerView recyclerView;
    MyRecyclerViewAdapter Imagesadapter;

    Tarea tarea;
    private int day, month, year, hour, min;
    String m,d,fecha, hora, minutos, hr;
    Uri ruta;
    Uri path;

    ArrayAdapter<Model> adapter;
    ArrayList<Model> listaModelos = new ArrayList<>();
    String descripcion; // descripcion de imagen/video/audio

    private static final int cod_adjuntar = 10;
    private static final int cod_adjuntarVideo = 20;
    private static final int cod_adjuntarAudio = 30;

    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_TAKE_PHOTO = 2;
    String currentPhotoPath;

    final Calendar c = Calendar.getInstance();
    final Calendar calendarRecordatorios = Calendar.getInstance();
    ArrayList <Recordatorio> listaRecordatorios = new ArrayList<>();
    ArrayList <RecordatorioAuxiliar> noSave = new ArrayList<>();
    private int dayRecordaotio, monthRecordatorio, yearRecordatorio, hourRecordatorio, minRecordatorio;
    String mRecordatorio,dRecordatorios,fechaRecordatorio, horaRecordatorio, minutosRecordatorio, hrRecordatorios;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insertar_tareas);

        txtTitulo = findViewById(R.id.txtTituloTarea);
        txtDescripcion = findViewById(R.id.txtDescripTarea);


        btnAdjuntar = (ImageButton) findViewById(R.id.btnAdjuntarTarea);
        btnFoto = (ImageButton) findViewById(R.id.btnFotoTarea);
        btnAudio = findViewById(R.id.btnAudioTarea);
        btnFecha = findViewById(R.id.btnFecha);
        btnInsertar = findViewById(R.id.btnInsertarTarea);
        btnRecordatorios = findViewById(R.id.btnRecordatorios);
        btnAdjuntar.setOnClickListener(this);
        btnFecha.setOnClickListener(this);
        btnFoto.setOnClickListener(this);
        btnAudio.setOnClickListener(this);
        btnInsertar.setOnClickListener(this);
        btnRecordatorios.setOnClickListener(this);
        recyclerView = findViewById(R.id.recycler);

        prueba = findViewById(R.id.btnProbar);
        prueba.setOnClickListener(this);

        if (validarPermisos()) {
            btnFoto.setEnabled(true);
            btnAdjuntar.setEnabled(true);
            btnAudio.setEnabled(true);
        } else {
            btnFoto.setEnabled(false);
            btnAdjuntar.setEnabled(false);
            btnAudio.setEnabled(false);
        }


        adapter = new ArrayAdapter<Model>(this, android.R.layout.simple_list_item_1, listaModelos);
        //recyclerView.setAdapter(adapter);

        recyclerView.setLayoutManager(new GridLayoutManager(this, 1));
        Imagesadapter = new MyRecyclerViewAdapter(this, listaModelos);

    }

    public void crearNotiRecordatorio(int year, int month, int day, int hour, int min){
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent notificationIntent = new Intent(this, AlarmReceiver.class);
        notificationIntent.putExtra("tarea", "Recordatorio de la tarea "+tarea.getTitulo());
        PendingIntent broadcast = PendingIntent.getBroadcast(this, 200, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        calendarRecordatorios.set(year,month,day,hour,min,0);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendarRecordatorios.getTimeInMillis(), broadcast);

        Log.i("Recordatorios", "Hora "+hour+":"+min);
    }

    public void guardarRecordatorios(int year, int month, int day, int hour, int min, String fecha, String hora){
        RecordatorioAuxiliar r = new RecordatorioAuxiliar(year, month, day, hour, min, fecha, hora);
        noSave.add(r);
    }

    public void insertRecordatorios(View view){
        String[] Tareas1 = {""}; //para que me devuelva todas las tareas y yo tomar la ultima
        DAOTareas daoTareas = new DAOTareas(this);

        ArrayList<Integer> arrayIds = new ArrayList<>();
        arrayIds = daoTareas.buscarUltimoId(Tareas1); //El array que me gusrda todos los ids de las Tareas

        if(noSave !=null) {
            for (int i = 0; i < noSave.size(); i++) {

                crearNotiRecordatorio(noSave.get(i).getYear(), noSave.get(i).getMonth(), noSave.get(i).getDay(),noSave.get(i).getHour(), noSave.get(i).getMin());

                Recordatorio recordatorio = new Recordatorio(0, noSave.get(i).getFecha(), noSave.get(i).getHora(), arrayIds.get(arrayIds.size()-1));
                DAORecordatorios daoRecordatorios = new DAORecordatorios(this);

                switch (view.getId()) {
                    case R.id.btnInsertarTarea:
                        daoRecordatorios.insert(recordatorio);
                        Log.i("Recordatorios", ""+recordatorio.getId());
                }
            }

        }else{
            //finish();
        }
        finish();
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void crearNotificacion(int year, int month, int day, int hour, int min){
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        Intent notificationIntent = new Intent(this, AlarmReceiver.class);

        notificationIntent.putExtra("tarea", "Realizar la tarea "+tarea.getTitulo());

        PendingIntent broadcast = PendingIntent.getBroadcast(this, 100, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        //Calendar cal = Calendar.getInstance();

        c.set(year,month,day,hour,min,0);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), broadcast);

        //Toast.makeText(this, "Se creo la notificacion ", Toast.LENGTH_SHORT).show();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onClick(View view) {
        if(view == btnInsertar){
            insert(view);
            crearNotificacion(year,month,day,hour,min);
            insertRutas(view);
            insertRecordatorios(view);

        }

        if(view == btnFecha){
            abrirCalenadario(view);
            //abrirReloj();

        }

        if(view == btnAdjuntar) {
            dialogoAdjuntar();
            openDialog();
        }

        if(view == btnAudio) {
            grabarAudio(view);
            //openDialog();
        }

        if(view == btnFoto) {
            dialogoTomar();
            openDialog();
        }

        if(view == prueba) {
            reproducirAudio(view);
        }

        if(view == btnRecordatorios) {
            abrirCalenadarioRecordatorio(view);
        }
    }

    private void insert(View view){
        tarea = new Tarea(0, txtTitulo.getText().toString(), txtDescripcion.getText().toString(), fecha, hr);

        DAOTareas dao = new DAOTareas(this);

        switch (view.getId()) {
            case R.id.btnInsertarTarea:
                dao.insert(tarea);
                //finish();
        }
        Toast.makeText(this, "Se inserto la tarea "+txtTitulo.getText().toString(), Toast.LENGTH_SHORT).show();
    }

    private void insertRutas(View view){
        String[] Tareas1 = {""}; //para que me devuelva todas las tareas y yo tomar la ultima
        DAOTareas daoTareas = new DAOTareas(this);

        ArrayList<Integer> arrayIds = new ArrayList<>();
        arrayIds = daoTareas.buscarUltimoId(Tareas1); //El array que me gusrda todos los ids de las Tareas

        if(listaModelos !=null) {
            for (int i = 0; i < listaModelos.size(); i++) {


                Ruta ruta = new Ruta(0, listaModelos.get(i).data, listaModelos.get(i).type, listaModelos.get(i).text ,arrayIds.get(arrayIds.size()-1));
                DAORutas daoRutas = new DAORutas(this);

                switch (view.getId()) {
                    case R.id.btnInsertarTarea:
                        daoRutas.insert(ruta);
                        //finish();
                }
                Log.i("RUTAS", ""+ruta.getId() +" path= "+ruta.getRuta()+ "idTarea= "+ruta.getIdTarea());
            }

        }else{
            //finish();
        }
        //finish();
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



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //adjuntar
        if(requestCode== cod_adjuntar && resultCode==RESULT_OK){
            Uri path =  data.getData(); //obtiene la ruta de la imagen seleccionada
            Model model = new Model(Model.IMAGE_TYPE,descripcion,path);
            listaModelos.add(model);

            //Imagesadapter = new MyRecyclerViewAdapter(this, listaModelos);
            recyclerView.setAdapter(Imagesadapter);

            //Toast.makeText(this, ""+path, Toast.LENGTH_SHORT).show();
        }

        if(requestCode== REQUEST_TAKE_PHOTO && resultCode==RESULT_OK){
            //listaModelos.add(Uri.parse(currentPhotoPath));
            Model model = new Model(0,descripcion,Uri.parse(currentPhotoPath));
            listaModelos.add(model);
            //Imagesadapter = new MyRecyclerViewAdapter(this, listaModelos);
            recyclerView.setAdapter(Imagesadapter);
        }

        //TOMAR FOTO O VIDEO
        if (requestCode == cod_adjuntarVideo && resultCode == RESULT_OK) {
            Uri videoUri = data.getData();//videoView.setVideoURI(videoUri);
            Model model = new Model(Model.VIDEO_TYPE, descripcion, videoUri);
            listaModelos.add(model);
            //listaModelos.add(videoUri);

            //Imagesadapter = new MyRecyclerViewAdapter(this, listaModelos);
            recyclerView.setAdapter(Imagesadapter);

            //Toast.makeText(this, ""+videoUri, Toast.LENGTH_SHORT).show();

        }

        //tomar desde la camara
        if (requestCode == REQUEST_VIDEO_CAPTURE && resultCode == RESULT_OK) {
            Uri videoUri = data.getData();//videoView.setVideoURI(videoUri);
            Model model = new Model(Model.VIDEO_TYPE, descripcion, videoUri);
            listaModelos.add(model);
            //listaModelos.add(videoUri);

            //Imagesadapter = new MyRecyclerViewAdapter(this, listaModelos);
            recyclerView.setAdapter(Imagesadapter);

            //Toast.makeText(this, ""+videoUri, Toast.LENGTH_SHORT).show();
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
        Toast.makeText(getApplicationContext(),getString(R.string.reproduciendo),Toast.LENGTH_SHORT).show();
    }

    private void abrirCalenadarioRecordatorio(View view) {
        yearRecordatorio = calendarRecordatorios.get(Calendar.YEAR);
        monthRecordatorio = calendarRecordatorios.get(Calendar.MONTH);
        dayRecordaotio = calendarRecordatorios.get(Calendar.DAY_OF_MONTH);
        hourRecordatorio = calendarRecordatorios.get(Calendar.HOUR_OF_DAY);
        minRecordatorio = calendarRecordatorios.get(Calendar.MINUTE);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                if((month+1)<10){
                    mRecordatorio = "0"+""+(month+1);
                }else{
                    mRecordatorio = ""+(month+1);
                }
                if(dayOfMonth<10){
                    dRecordatorios = "0"+""+dayOfMonth;
                }else{
                    dRecordatorios = ""+dayOfMonth;
                }
                //efecha.setText(dayOfMonth+"/"+(month + 1)+"/"+year);
                //btnFecha.setText(year+"/"+m+"/"+d);
                fechaRecordatorio= ""+year+"/"+mRecordatorio+"/"+dRecordatorios;

                abrirRelojR();
            }
        } ,yearRecordatorio,monthRecordatorio,dayRecordaotio);
        datePickerDialog.show();

    }

    private void abrirRelojR() {
        hourRecordatorio = calendarRecordatorios.get(Calendar.HOUR_OF_DAY);
        minRecordatorio = calendarRecordatorios.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {

            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                if((hourOfDay+1)<10){
                    horaRecordatorio = "0"+""+(hourOfDay);
                }else{
                    horaRecordatorio = ""+(hourOfDay);
                }
                if(minute<10){
                    minutosRecordatorio = "0"+""+minute;
                }else{
                    minutosRecordatorio= ""+minute;
                }
                hrRecordatorios = horaRecordatorio+":"+minutosRecordatorio;
                minRecordatorio = minute;
                hourRecordatorio= hourOfDay;

                guardarRecordatorios(yearRecordatorio,monthRecordatorio,dayRecordaotio,hourRecordatorio,minRecordatorio, fechaRecordatorio, hrRecordatorios);
            }
        },hourRecordatorio,minRecordatorio,false);
        timePickerDialog.show();
    }
}
