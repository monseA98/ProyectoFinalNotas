package com.example.proyectofinal;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;
import androidx.annotation.NonNull;

import java.util.ArrayList;

public class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.ViewHolder> {

    //private Uri[] mData;
    ArrayList<Uri> listaRutas;
    private LayoutInflater mInflater;
    //private ItemClickListener mClickListener;

    MyRecyclerViewAdapter(Context context, ArrayList<Uri> data) {
        this.mInflater = LayoutInflater.from(context);
        this.listaRutas = data;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.recyclerview_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.myImageView.setImageURI(listaRutas.get(position));
    }

    @Override
    public int getItemCount() {
        return listaRutas.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView myImageView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            myImageView = itemView.findViewById(R.id.image_selected);
        }
    }

    Uri getItem(int id) {
        return listaRutas.get(id);
    }




}
