package com.example.lessonone;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

public class DisplayImageActivity extends AppCompatActivity {
    private String albumName;
    private String photoName;
    private ImageView imvPhoto;

    private Context context;
    private Bitmap bitmap;

    private void findViews(){
        imvPhoto=(ImageView) findViewById(R.id.imvPhoto);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.context=this;
        setContentView(R.layout.activity_display_image);
        findViews();

        Intent intent= getIntent();
        this.albumName=intent.getStringExtra("albumName");
        this.photoName=intent.getStringExtra("photoName");

        bitmap=ImageProcessing.ConvertPathToBitmap(context,albumName+"/"+photoName);
        imvPhoto.setImageBitmap(bitmap);

        ViewGroup.LayoutParams params = imvPhoto.getLayoutParams();
        imvPhoto.measure(0, 0);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;

        double screenScale=(width+0.0)/height;
        double scale= (bitmap.getWidth()+0.0) / bitmap.getHeight();

        if (scale > screenScale)
            params.width = width;
        else
            params.height = height;
//        params.height = (int) (imvPhoto.getMeasuredHeight()*1.5);
//        params.width= (int) (imvPhoto.getMeasuredWidth()*1.5);
        imvPhoto.setLayoutParams(params);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.display_image_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        ViewGroup.LayoutParams params = imvPhoto.getLayoutParams();
        imvPhoto.measure(0, 0);
        switch (item.getItemId()) {
            case R.id.mniZoomIn:
                params.width= (int) (params.width*1.2);
                params.height= (int) (params.height*1.2);
                imvPhoto.setLayoutParams(params);
                return true;
            case R.id.mniZoomOut:
                params.width=(int)(params.width*0.8);
                params.height= (int) (params.height*0.8);
                imvPhoto.setLayoutParams(params);
                return true;
            case R.id.mniFitScreen:
                DisplayMetrics displayMetrics = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                int height = displayMetrics.heightPixels;
                int width = displayMetrics.widthPixels;

                double screenScale=(width+0.0)/height;
                double scale= (imvPhoto.getWidth()+0.0) / imvPhoto.getHeight();

                if (scale > screenScale){
                    params.width = width;
                    params.height = (int) (params.width/scale);
                }
                else{
                    params.height = height;
                     params.width = (int) (params.height * scale);
                }
                imvPhoto.setLayoutParams(params);
                Toast.makeText(context,Double.toString(params.height),Toast.LENGTH_SHORT).show();
                return true;
            case R.id.mniBrighter:
                bitmap = ImageProcessing.Brighter(bitmap);
                imvPhoto.setImageBitmap(bitmap);
                return true;
            default:
                return true;
        }
    }
}