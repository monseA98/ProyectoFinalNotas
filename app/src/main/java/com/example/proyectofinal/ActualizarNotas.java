package com.example.proyectofinal;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.proyectofinal.DAOS.DAONotas;

public class ActualizarNotas extends AppCompatActivity implements View.OnClickListener{

    ImageButton btnAdjuntar;
    ImageButton btnFoto;
    Button btnActualizar;
    EditText txtTitulo, txtDescripcion;

    Nota nota;
    Uri path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actualizar_notas);

        nota = (Nota)getIntent().getExtras().getSerializable("nota");

        txtTitulo = findViewById(R.id.txtTituloNotaAct);
        txtTitulo.setText(nota.getTitulo().toString());
        
        txtDescripcion = findViewById(R.id.txtDescripNotaAct);
        txtDescripcion.setText(nota.getDescripcion());

        btnAdjuntar = (ImageButton) findViewById(R.id.btnAdjuntarNotaAct);
        btnFoto = (ImageButton) findViewById(R.id.btnFotoNotaAct);
        btnActualizar = findViewById(R.id.btnActualizarNota);

        btnAdjuntar.setOnClickListener(this);
        btnFoto.setOnClickListener(this);
        btnActualizar.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if(view == btnActualizar){
            actualizar(view);
        }

        if(view == btnAdjuntar) {
            cargarImagen();
        }
    }

    private void actualizar(View view){
        nota.setTitulo(txtTitulo.getText().toString());
        nota.setDescripcion(txtDescripcion.getText().toString());

        DAONotas dao = new DAONotas(this);

        switch (view.getId()) {
            case R.id.btnActualizarNota:
                dao.update(nota);
                finish();
        }
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

            path=  data.getData(); //obtiene la ruta de la imagen seleccionada
            Toast.makeText(this, ""+path, Toast.LENGTH_SHORT).show();
        }
    }
}
