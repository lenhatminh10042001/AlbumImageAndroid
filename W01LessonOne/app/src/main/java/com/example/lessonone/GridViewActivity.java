package com.example.lessonone;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class GridViewActivity extends AppCompatActivity {
    private GridView grvFolders;
    private Context context;
    ArrayList<MFolder> folders;

    private ArrayList<Integer> selectedIndexes=new ArrayList<>();

    private void FindViews(){
        grvFolders=(GridView) findViewById(R.id.grvFolders);
    }

    private void LoadGrvFolders(){
        String root = this.getFilesDir().toString();
        File dir = new File(root);
        File[] files = dir.listFiles();
        folders = new ArrayList<MFolder>();
        for (int i = 0; i < files.length; ++i) {
            if (files[i].isDirectory()) {
                File[] subFiles = files[i].listFiles();
                MFolder folder = new MFolder(files[i].getName(), (subFiles == null) ? 0 : subFiles.length);
                folders.add(folder);
            }
        }

        ArrayAdapter<MFolder> adapter = new ArrayAdapter(context, R.layout.griditem_folder, folders) {
            public View getView(int position, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    MFolder folder = folders.get(position);
                    LayoutInflater layoutInflater = LayoutInflater.from(context);
                    convertView = layoutInflater.inflate(R.layout.griditem_folder, null);

                    CheckBox chbFolderName =convertView.findViewById(R.id.chbFolderName);
                    chbFolderName.setText(folder.Name);
                    chbFolderName.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            CheckBox chb = (CheckBox) view;
                            if (chb.isChecked())
                                selectedIndexes.add(Integer.parseInt(Integer.toString(position)));
                            else
                                selectedIndexes.remove((Object) Integer.parseInt(Integer.toString(position)));
                        }
                    });


                    TextView txvGridItemNumber=convertView.findViewById(R.id.txvGridItemNumber);
                    if(folder.ItemNumber<2)
                        txvGridItemNumber.setText(Integer.toString(folder.ItemNumber) +" Item");
                    else
                        txvGridItemNumber.setText(Integer.toString(folder.ItemNumber) +" Items");

                    ImageView imvFolder=convertView.findViewById(R.id.imvFolder);
                    imvFolder.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View view) {
                            Intent intent=new Intent(context,DisplayFolderActivity.class);
                            intent.putExtra("FolderName",folder.Name);
                            startActivity(intent);
                        }
                    });
                }
                return convertView;
            }
        };
        grvFolders.setAdapter(adapter);
        adaptGridViewHeight(R.id.grvFolders,adapter);

        selectedIndexes=new ArrayList<>();
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.gridview_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mniNewFolder:
                Intent intent = new Intent(this,MakeFolderActivity.class);
                startMakeFolderActivityLauncher.launch(intent);
                return true;
            case R.id.mniRefresh:
                LoadGrvFolders();
                return true;
            case R.id.mniRemoveFolder:
                for (int i=0;i<selectedIndexes.size();i++)
                    IOLibrary.RemoveFolder(context,folders.get((int)selectedIndexes.get(i)).Name);
                LoadGrvFolders();
                return true;
            default:
                return true;
        }
    }

    private ActivityResultLauncher<Intent> startMakeFolderActivityLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        LoadGrvFolders();
                    }
                }});

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grid_view);
        this.context=this;
        FindViews();
        LoadGrvFolders();
    }
}