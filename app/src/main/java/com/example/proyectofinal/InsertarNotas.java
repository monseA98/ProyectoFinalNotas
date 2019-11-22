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
import android.app.Dialog;
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
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.proyectofinal.DAOS.DAONotas;
import com.example.proyectofinal.DAOS.DAORutas;
import com.example.proyectofinal.DAOS.DAORutasNotas;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class InsertarNotas extends AppCompatActivity implements View.OnClickListener, ExampleDialog.ExampleDilaogListener {

    ImageButton btnAdjuntar;
    ImageButton btnFoto;
    ImageButton btnAudio;
    Button btnInsertar;
    EditText txtTitulo, txtDescripcion;
    RecyclerView recyclerView;
    ImageView imageView;
    MyRecyclerViewAdapter Imagesadapter;

    Nota nota;
    Uri path;
    ArrayAdapter<Model> adapter;
    ArrayList<Model> listaRutas = new ArrayList<>();
    String descripcion;

    static final int REQUEST_TAKE_PHOTO = 2;
    String currentPhotoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insertar_notas);

        txtTitulo = findViewById(R.id.txtTituloNota);
        txtDescripcion = findViewById(R.id.txtDescripNota);

        btnAdjuntar = (ImageButton) findViewById(R.id.btnAdjuntarNota);
        btnFoto = (ImageButton) findViewById(R.id.btnFotoNota);
        btnAudio = findViewById(R.id.btnAudioNota);
        btnInsertar = findViewById(R.id.btnInsertarNota);

        recyclerView = findViewById(R.id.recycler);

        btnAdjuntar.setOnClickListener(this);
        btnFoto.setOnClickListener(this);
        btnInsertar.setOnClickListener(this);
        btnAudio.setOnClickListener(this);

        if (validarPermisos()) {
            btnFoto.setEnabled(true);
        } else {
            btnFoto.setEnabled(false);
        }

        //imageView = findViewById(R.id.imageView);

        adapter = new ArrayAdapter<Model>(this, android.R.layout.simple_list_item_1, listaRutas);
        //recyclerView.setAdapter(adapter);

        recyclerView.setLayoutManager(new GridLayoutManager(this, 1));

    }


    @Override
    public void onClick(View view) {
        if(view == btnInsertar){
            insert(view);
            insertRutas(view);

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


    private void insert(View view){
        nota = new Nota(0, txtTitulo.getText().toString(), txtDescripcion.getText().toString());

        DAONotas dao = new DAONotas(this);

        switch (view.getId()) {
            case R.id.btnInsertarNota:
                dao.insert(nota);
                //finish();
        }

        //Toast.makeText(this, "Se inserto la nota "+txtTitulo.getText().toString(), Toast.LENGTH_SHORT).show();
    }

    private void insertRutas(View view){

        String[] Notas1 = {""};

        DAONotas daoNotas = new DAONotas(this);

        ArrayList<Integer> arrayIds = new ArrayList<>();
        arrayIds = daoNotas.buscarUltimoId(Notas1); //El array que me gusrda todos los ids de las Notas

        if(listaRutas!=null) {
            for (int i = 0; i < listaRutas.size(); i++) {


                Ruta ruta = new Ruta(0, listaRutas.get(i).data,null ,arrayIds.get(arrayIds.size()-1));
                DAORutasNotas daoRutasNotas = new DAORutasNotas(this);

                switch (view.getId()) {
                    case R.id.btnInsertarNota:
                        daoRutasNotas.insert(ruta);
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

    private static final int cod_adjuntar = 10;
    private static final int cod_adjuntarVideo = 20;
    private static final int cod_adjuntarAudio = 30;

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



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //adjuntar
        if(requestCode== cod_adjuntar && resultCode==RESULT_OK){
            Uri path =  data.getData(); //obtiene la ruta de la imagen seleccionada
            Model model = new Model(Model.IMAGE_TYPE,"",path);
            listaRutas.add(model);

            Imagesadapter = new MyRecyclerViewAdapter(this, listaRutas);
            recyclerView.setAdapter(Imagesadapter);

            Toast.makeText(this, ""+path, Toast.LENGTH_SHORT).show();
        }

        if(requestCode== REQUEST_TAKE_PHOTO && resultCode==RESULT_OK){
            //listaRutas.add(Uri.parse(currentPhotoPath));
            Model model = new Model(0,"",path);
            listaRutas.add(model);
            //Imagesadapter = new MyRecyclerViewAdapter(this, listaRutas);
            recyclerView.setAdapter(Imagesadapter);
        }

        //TOMAR FOTO O VIDEO
        if (requestCode == cod_adjuntarVideo && resultCode == RESULT_OK) {
            Uri videoUri = data.getData();//videoView.setVideoURI(videoUri);
            //listaRutas.add(videoUri);

            //Imagesadapter = new MyRecyclerViewAdapter(this, listaRutas);
            recyclerView.setAdapter(Imagesadapter);

            Toast.makeText(this, ""+videoUri, Toast.LENGTH_SHORT).show();
        }

        //tomar desde la camara
        if (requestCode == REQUEST_VIDEO_CAPTURE && resultCode == RESULT_OK) {
            Uri videoUri = data.getData();//videoView.setVideoURI(videoUri);
            //listaRutas.add(videoUri);

            //Imagesadapter = new MyRecyclerViewAdapter(this, listaRutas);
            recyclerView.setAdapter(Imagesadapter);

            Toast.makeText(this, ""+videoUri, Toast.LENGTH_SHORT).show();
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
            requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE,CAMERA}, 100);
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

    public void grabarAudio(View view){
        if(grabacion==null){
            archivoSalida = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Grabacion.mp3";
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

}
