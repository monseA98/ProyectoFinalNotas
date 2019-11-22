package com.example.proyectofinal;

import android.net.Uri;

public class Model {

    public static final int IMAGE_TYPE=0;
    public static final int AUDIO_TYPE=1;
    public static final int VIDEO_TYPE=2;

    public int type;
    public Uri data;
    public String text;

    public Model(int type, String text, Uri data)
    {

        this.type = type;
        this.data = data;
        this.text = text;

    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Uri getData() {
        return data;
    }

    public void setData(Uri data) {
        this.data = data;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
