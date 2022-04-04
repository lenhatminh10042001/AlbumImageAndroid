package com.example.lessonone;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.File;

public class MImage {
    private String name;
    private Bitmap bitmap;

    public MImage(String name,Bitmap bitmap){
        this.name=name;
        this.bitmap=bitmap;
    }

    public MImage(File file){
        this.name=file.getName();
        this.bitmap=BitmapFactory.decodeFile(file.getPath());
    }

    public Bitmap getBitmap(){
        return this.bitmap;
    }

    public String getName(){
        return this.name;
    }
}
