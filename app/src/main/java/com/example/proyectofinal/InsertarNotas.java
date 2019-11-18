package com.example.proyectofinal;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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

import java.util.ArrayList;

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
            cargarImagen();
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
        if(listaRutas!=null) {
            for (int i = 0; i < listaRutas.size(); i++) {

                Ruta ruta = new Ruta(0, listaRutas.get(i), nota.getId());
                DAORutasNotas dao = new DAORutasNotas(this);

                dao.insert(ruta);
                Toast.makeText(this, "Se inserto la ruta " + listaRutas.get(i), Toast.LENGTH_SHORT).show();

            }
        }else{
            finish();
        }
        finish();

    }

    private void cargarImagen() {
        Intent intentGaleria = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intentGaleria.setType("image/");
        startActivityForResult(intentGaleria.createChooser(intentGaleria,"Seleccione la aplicacion"),10);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK){

            path =  data.getData(); //obtiene la ruta de la imagen seleccionada

            listaRutas.add(path);
            //imageView.setImageURI(path);

            Imagesadapter = new MyRecyclerViewAdapter(this, listaRutas);
            recyclerView.setAdapter(Imagesadapter);

            Toast.makeText(this, ""+path, Toast.LENGTH_SHORT).show();

        }
    }
}
