package com.example.lessonone;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;

public class DisplayFolderActivity extends AppCompatActivity {
    private String folderName;
    private Context context;
    private ArrayList<MImage> images;

    private ArrayList<Integer> selectedIndexes=new ArrayList<>();

    private GridView grvFiles;
    private TextView txvTest;

    private void findViews(){
        grvFiles=(GridView) findViewById(R.id.grvFiles);
        txvTest= (TextView) findViewById(R.id.txvTest);
    }


    private void LoadGrvFiles(){
        ArrayList<File> fs=IOLibrary.ListFiles(context,folderName);
        this.images=new ArrayList<>();
        for (int i=0;i<fs.size();i++){
            MImage image=new MImage(fs.get(i));
            this.images.add(image);
        }

        ArrayAdapter<MImage> adapter=new ArrayAdapter<MImage>(context,R.layout.griditem_file,this.images){
            public View getView(int position, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    MImage image = images.get(position);
                    LayoutInflater layoutInflater = LayoutInflater.from(context);
                    convertView = layoutInflater.inflate(R.layout.griditem_file, null);
                    TextView txvFilename=convertView.findViewById(R.id.txvFilename);
                    txvFilename.setText(image.getName());

                    ImageView imvIcon=(ImageView) convertView.findViewById(R.id.imvIcon);
                    imvIcon.setImageBitmap(image.getBitmap());
                    imvIcon.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent=new Intent(context,DisplayImageActivity.class);
                            intent.putExtra("albumName",folderName);
                            intent.putExtra("photoName",image.getName());
                            startActivity(intent);
                        }
                    });

                    CheckBox chbSelected=(CheckBox) convertView.findViewById(R.id.chdSelected);
                    chbSelected.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            CheckBox chb = (CheckBox) view;
                            if (chb.isChecked())
                                selectedIndexes.add(Integer.parseInt(Integer.toString(position)));
                            else
                                selectedIndexes.remove((Object) Integer.parseInt(Integer.toString(position)));
                        }
                    });
                }
                return convertView;
            }
        };
        grvFiles.setAdapter(adapter);
        adaptGridViewHeight(R.id.grvFiles,adapter);

        selectedIndexes=new ArrayList<>();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_folder);
        this.context=this;
        findViews();

        Intent intent=getIntent();
        folderName=intent.getStringExtra("FolderName");
        LoadGrvFiles();
    }


    private ActivityResultLauncher<Intent> startCaptureActivityLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        Bitmap bitmap = (Bitmap) data.getExtras().get("data");

                        String error=IOLibrary.SaveBitmap(context,folderName,bitmap);
                        if (!error.equals(""))
                            Toast.makeText(context,error,Toast.LENGTH_SHORT).show();
                        else
                            LoadGrvFiles();
                    }
                }});


    private ActivityResultLauncher<Intent> startPickActivityLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        Uri uri = null;
                        if (data.getClipData() != null) {
                            int count = data.getClipData().getItemCount();
                            for (int i=0; i<count; i++) {
                                uri = data.getClipData().getItemAt(i).getUri();
                                try {
                                    InputStream imageStream = getContentResolver().openInputStream(uri);
                                    final Bitmap bitmap = BitmapFactory.decodeStream(imageStream);

                                    String error= IOLibrary.SaveBitmap(context,folderName,bitmap);
                                    if (!error.equals(""))
                                        Toast.makeText(context,error,Toast.LENGTH_SHORT).show();
                                } catch (Exception e) { }
                            }
                            LoadGrvFiles();
                        }
                    }
                }
            });

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.display_folder_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.mniCapture:
                intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startCaptureActivityLauncher.launch(intent);
                return true;
            case R.id.mniImport:
                intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                startPickActivityLauncher.launch(intent);
                return true;
            case R.id.mniDelete:
                for (int i=0;i<selectedIndexes.size();i++)
                    txvTest.setText(IOLibrary.RemoveFile(context,folderName+"/"+images.get((int)selectedIndexes.get(i)).getName()));
                LoadGrvFiles();
                return true;
            default:
                return true;
        }
    }


    protected void adaptGridViewHeight(int id, ArrayAdapter adapter) {
        GridView gridView = findViewById(id);
        int totalHeight = 0;
        Integer[] itemHeights = {0,0,0};
        for (int i = 0; i < adapter.getCount(); i++) {
            View gridItem = adapter.getView(i, null, gridView);
            gridItem.measure(0, 0);
            itemHeights[i%3]=gridItem.getMeasuredHeight();
            if (i%3==2){
                int max = (int) Collections.max(Arrays.asList(itemHeights));
                totalHeight += max;
                for (int j=0; j < 3;j++)
                    itemHeights[j]=0;
            }
            else if (i==adapter.getCount()-1){
                int max = (int) Collections.max(Arrays.asList(itemHeights));
                totalHeight += max;
            }
        }
        ViewGroup.LayoutParams params = gridView.getLayoutParams();
        params.height = totalHeight;
        gridView.setLayoutParams(params);
    }

//    private void setGridViewHeightBasedOnChildren(GridView gridView, int noOfColumns) {
//        ListAdapter gridViewAdapter = gridView.getAdapter();
//        if (gridViewAdapter == null) {
//            // adapter is not set yet
//            return;
//        }
//
//        int totalHeight; //total height to set on grid view
//        int items = gridViewAdapter.getCount(); //no. of items in the grid
//        int rows; //no. of rows in grid
//
//        View listItem = gridViewAdapter.getView(0, null, gridView);
//        listItem.measure(0, 0);
//        totalHeight = listItem.getMeasuredHeight();
//
//        float x;
//        if( items > noOfColumns ){
//            x = items/noOfColumns;
//
//            //Check if exact no. of rows of rows are available, if not adding 1 extra row
//            if(items%noOfColumns != 0) {
//                rows = (int) (x + 1);
//            }else {
//                rows = (int) (x);
//            }
//            totalHeight *= rows;
//
//            //Adding any vertical space set on grid view
//            totalHeight += gridView.getVerticalSpacing() * rows;
//        }
//
//        //Setting height on grid view
//        ViewGroup.LayoutParams params = gridView.getLayoutParams();
//        params.height = totalHeight;
//        gridView.setLayoutParams(params);
//    }
}