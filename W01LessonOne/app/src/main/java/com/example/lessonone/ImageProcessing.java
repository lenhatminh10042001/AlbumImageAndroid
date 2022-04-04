package com.example.lessonone;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;

import java.io.File;

public class ImageProcessing {

    public static Bitmap ConvertPathToBitmap(Context context, String path){
        String root = context.getFilesDir().toString();
        File file = new File(root,path);

        return BitmapFactory.decodeFile(file.getPath());
    }

    public static byte[][][] ConvertBitmapToByteArray(Bitmap bitmap){
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        byte[][][] bytes = new byte[3][height][width];
        for (int x=0;x < width; x++){
            for (int y=0;y<height;y++){
                int pixel = bitmap.getPixel(x,y);
                bytes[0][y][x] = (byte) Color.red(pixel);
                bytes[1][y][x] = (byte) Color.green(pixel);
                bytes[2][y][x] = (byte) Color.blue(pixel);
            }
        }
        return bytes;
    }

    public static Bitmap ConvertByteArrayToBitmap(byte[][][] bytes,int height,int width){
        Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        for(int x=0; x<width; x++) {
            for(int y=0; y<height; y++) {
                int red = bytes[0][y][x];
                int green = bytes[1][y][x];
                int blue = bytes[2][y][x];

                int color = Color.argb(255, red, green, blue);

                bmp.setPixel(x, y, color);
            }
        }
        return bmp;
    }

    public static Bitmap Brighter(Bitmap bitmap){
        byte[][][] bytes = ConvertBitmapToByteArray(bitmap);
        int height= bitmap.getHeight();
        int width= bitmap.getWidth();

        for (int i =0;i < height;i++){
            for (int j=0;j < width;j++){
                for (int k=0;k<3;k++){
                    bytes[k][i][j] = (byte) Math.min(255, bytes[k][i][j] + 20);
                }
            }
        }
        return ConvertByteArrayToBitmap(bytes,bitmap.getHeight(),bitmap.getWidth());
    }
}
