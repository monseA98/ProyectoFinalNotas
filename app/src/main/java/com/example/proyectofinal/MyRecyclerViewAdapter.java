package com.example.proyectofinal;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;
import androidx.annotation.NonNull;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.IOException;
import java.util.ArrayList;

public class MyRecyclerViewAdapter extends RecyclerView.Adapter {

    //private Uri[] mData;
    ArrayList<Model> listaRutas;
    //private LayoutInflater mInflater;
    //private ItemClickListener mClickListener;
    Context context;
    int total_types;
    MediaPlayer mPlayer;
    private boolean fabStateVolume = false;

    MyRecyclerViewAdapter(Context context, ArrayList<Model> data) {
        //this.mInflater = LayoutInflater.from(context);
        this.listaRutas = data;
        this.context = context;
        total_types = data.size();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //View view = mInflater.inflate(R.layout.recyclerview_item, parent, false);
        //return new ViewHolder(view);

        View view;
        switch (viewType) {

            case Model.IMAGE_TYPE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_item, parent, false);
                return new ViewHolder(view);
            case Model.AUDIO_TYPE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.audio_type, parent, false);
                return new ViewHolderAudio(view);
        }

        return null;

    }

    @Override
    public int getItemViewType(int position) {

        switch (listaRutas.get(position).type) {
            case 0:
                return Model.IMAGE_TYPE;
            case 1:
                return Model.AUDIO_TYPE;
            default:
                return -1;
        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        //holder.myImageView.setImageURI(listaRutas.get(position).data);
        //holder.textImage.setText(listaRutas.get(position).text);

        final Model object = listaRutas.get(position);
        if (object != null) {

            switch (object.type) {

                case Model.IMAGE_TYPE:
                    ((ViewHolder) holder).textImage.setText(object.text);
                    ((ViewHolder) holder).myImageView.setImageURI(object.data);
                    break;
                case Model.AUDIO_TYPE:

                    ((ViewHolderAudio) holder).textAudio.setText(object.text);

                    ((ViewHolderAudio) holder).audio.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View view) {

                            if (fabStateVolume) {

                                if(mPlayer.isPlaying()) {

                                    mPlayer.stop();

                                }

                            } else {

                                mPlayer = new MediaPlayer();
                                mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                                try {
                                    mPlayer.setDataSource(context, object.data);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                try {
                                    mPlayer.prepare();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                mPlayer.start();
                                fabStateVolume = true;

                            }

                        }

                    });
                    break;

            }

        }

    }

    @Override
    public int getItemCount() {
        return listaRutas.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView myImageView;
        TextView textImage;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            myImageView = itemView.findViewById(R.id.image_selected);
            textImage = itemView.findViewById(R.id.type);
        }
    }

    public static class ViewHolderAudio extends RecyclerView.ViewHolder {
        FloatingActionButton audio;
        TextView textAudio;
        public ViewHolderAudio(@NonNull View itemView) {
            super(itemView);
            audio = itemView.findViewById(R.id.fab);
            textAudio = itemView.findViewById(R.id.type);
        }
    }

    Uri getItem(int id) {
        return listaRutas.get(id).data;
    }




}
