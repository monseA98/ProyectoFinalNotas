package com.example.proyectofinal;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
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
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class InsertarNotas extends AppCompatActivity implements View.OnClickListener {

    ImageButton btnAdjuntar;
    ImageButton btnFoto;
    Button btnInsertar;
    EditText txtTitulo, txtDescripcion;
    RecyclerView recyclerView;
    ImageView imageView;
    MyRecyclerViewAdapter Imagesadapter;

    Nota nota;
    Uri path;
    ArrayAdapter<Uri> adapter;
    ArrayList<Uri> listaRutas = new ArrayList<>();

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
        btnInsertar = findViewById(R.id.btnInsertarNota);

        recyclerView = findViewById(R.id.recycler);

        btnAdjuntar.setOnClickListener(this);
        btnFoto.setOnClickListener(this);
        btnInsertar.setOnClickListener(this);

        if (validarPermisos()) {
            btnFoto.setEnabled(true);
        } else {
            btnFoto.setEnabled(false);
        }

        //imageView = findViewById(R.id.imageView);

        adapter = new ArrayAdapter<Uri>(this, android.R.layout.simple_list_item_1, listaRutas);
        //recyclerView.setAdapter(adapter);

        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

    }


    @Override
    public void onClick(View view) {
        if(view == btnInsertar){
            insert(view);
            insertRutas();

        }

        if(view == btnAdjuntar) {
            dialogoAdjuntar();
        }

        if(view == btnFoto) {
            dialogoTomar();
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

    private void insertRutas(){

        String[] Notas1 = {""};

        DAONotas daoNotas = new DAONotas(this);

        ArrayList<Integer> arrayIds = new ArrayList<>();
        arrayIds = daoNotas.buscarUltimoId(Notas1); //El array que me gusrda todos los ids de las Notas

        if(listaRutas!=null) {
            for (int i = 0; i < listaRutas.size(); i++) {

                Ruta ruta = new Ruta(0, listaRutas.get(i), 1 ,arrayIds.get(arrayIds.size()-1));
                DAORutas dao = new DAORutas(this);

                dao.insert(ruta);
                //Toast.makeText(this, "Se inserto la ruta " + listaRutas.get(i), Toast.LENGTH_SHORT).show();
                //Log.i("RUTAS", ""+listaRutas.get(i) +" idNota "+nota.getId());
                Log.i("RUTAS", ""+ruta.getId() +" path= "+ruta.getRuta()+"tipo= 1"+" idNota= "+ruta.getIdTarea());

            }
        }else{
            //finish();
        }
        finish();

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

    private static final int cod_adjuntar = 10;
    private static final int cod_adjuntarVideo = 20;

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
            listaRutas.add(path);

            Imagesadapter = new MyRecyclerViewAdapter(this, listaRutas);
            recyclerView.setAdapter(Imagesadapter);

            Toast.makeText(this, ""+path, Toast.LENGTH_SHORT).show();
        }

        if(requestCode== REQUEST_TAKE_PHOTO && resultCode==RESULT_OK){
            listaRutas.add(Uri.parse(currentPhotoPath));

            Imagesadapter = new MyRecyclerViewAdapter(this, listaRutas);
            recyclerView.setAdapter(Imagesadapter);
        }

        //TOMAR FOTO O VIDEO
        if (requestCode == cod_adjuntarVideo && resultCode == RESULT_OK) {
            Uri videoUri = data.getData();//videoView.setVideoURI(videoUri);
            listaRutas.add(videoUri);

            Imagesadapter = new MyRecyclerViewAdapter(this, listaRutas);
            recyclerView.setAdapter(Imagesadapter);

            Toast.makeText(this, ""+videoUri, Toast.LENGTH_SHORT).show();
        }

        //tomar desde la camara
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
}
