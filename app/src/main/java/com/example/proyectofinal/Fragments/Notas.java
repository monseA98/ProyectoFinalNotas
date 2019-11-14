package com.example.proyectofinal.Fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.example.proyectofinal.ActualizarNotas;
import com.example.proyectofinal.DAOS.DAONotas;
import com.example.proyectofinal.InsertarNotas;
import com.example.proyectofinal.Nota;
import com.example.proyectofinal.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Notas.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Notas#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Notas extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private ListView lvNotas;
    private DAONotas daoNotas;
    private ArrayList<Nota> notas;
    private ArrayAdapter<Nota> adapter;
    private EditText editText;

    private OnFragmentInteractionListener mListener;

    public Notas() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Notas.
     */
    // TODO: Rename and change types and number of parameters
    public static Notas newInstance(String param1, String param2) {
        Notas fragment = new Notas();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_notas, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        FloatingActionButton fab = (FloatingActionButton) getView().findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), InsertarNotas.class);
                startActivity(intent);
            }
        });



        Button button = (Button) getView().findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                editText = (EditText) getActivity().findViewById(R.id.editText);

                String[] Notas1 = {editText.getText().toString(), editText.getText().toString()};

                daoNotas = new DAONotas(getActivity());

                notas = daoNotas.buscarporTitulo(Notas1);

                adapter = new ArrayAdapter<Nota>(getActivity(), android.R.layout.simple_list_item_1, notas);

                lvNotas = (ListView) getActivity().findViewById(R.id.lvNotas);

                lvNotas.setAdapter(adapter);
            }
        });



        String[] Notas1 = {"", ""};

        daoNotas = new DAONotas(getActivity());

        notas = daoNotas.buscarporTitulo(Notas1);

        adapter = new ArrayAdapter<Nota>(getActivity(), android.R.layout.simple_list_item_1, notas);

        lvNotas = (ListView) getActivity().findViewById(R.id.lvNotas);

        lvNotas.setAdapter(adapter);

        registerForContextMenu(lvNotas);

    }

    @Override
    public void onResume() {
        super.onResume();

        String[] Notas1 = {""};

        daoNotas = new DAONotas(getActivity());

        notas = daoNotas.buscarporTitulo(Notas1);

        adapter = new ArrayAdapter<Nota>(getActivity(), android.R.layout.simple_list_item_1, notas);

        lvNotas = (ListView) getActivity().findViewById(R.id.lvNotas);

        lvNotas.setAdapter(adapter);

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View lv, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, lv, menuInfo);
        MenuInflater inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.context_menu, menu);
    }

    public boolean onContextItemSelected(MenuItem item) {
        daoNotas = new DAONotas(getActivity());

        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        lvNotas = (ListView) getActivity().findViewById(R.id.lvNotas);

        Nota nota = (Nota) lvNotas.getItemAtPosition(info.position);

        switch (item.getItemId()) {
            case R.id.eliminar:
                daoNotas.eliminar(nota.getId());

                String[] Notas1 = {""};

                daoNotas = new DAONotas(getActivity());

                notas = daoNotas.buscarporTitulo(Notas1);

                adapter = new ArrayAdapter<Nota>(getActivity(), android.R.layout.simple_list_item_1, notas);

                lvNotas = (ListView) getActivity().findViewById(R.id.lvNotas);

                lvNotas.setAdapter(adapter);

                return true;
            case R.id.actualizar:
                Intent intent = new Intent(getActivity(), ActualizarNotas.class);
                intent.putExtra("nota", nota);
                startActivity(intent);
                return true;
            default:
                return super.onContextItemSelected(item);
        }

    }



    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
