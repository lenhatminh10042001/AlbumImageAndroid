package com.example.lessonone;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

public class SecondActivity extends AppCompatActivity {

    private EditText edtFolderName;
    private TextView txvErrorMessage;
    private Spinner spnFolders;
    private ListView lsvFolders;
    private Context context;

    private void loadLsvFolders(){
        String root = this.getFilesDir().toString();
        File dir = new File(root);
        File[] files = dir.listFiles();
        ArrayList<MFolder> folders = new ArrayList<MFolder>();
        for (int i = 0; i < files.length; ++i) {
            if (files[i].isDirectory()) {
                File[] subFiles = files[i].listFiles();
                MFolder folder = new MFolder(files[i].getName(), (subFiles == null) ? 0 : subFiles.length);
                folders.add(folder);
            }
        }
        ArrayAdapter<MFolder> adapter = new ArrayAdapter(context, R.layout.listitem_folder, folders) {
            public View getView(int position, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    MFolder folder = folders.get(position);
                    LayoutInflater layoutInflater = LayoutInflater.from(context);
                    convertView = layoutInflater.inflate(R.layout.listitem_folder, null);
                    TextView txvFolderName=convertView.findViewById(R.id.txvFolderName);
                    txvFolderName.setText(folder.Name);
                    TextView txvItemNumber=convertView.findViewById(R.id.txvItemNumber);
                    if(folder.ItemNumber<2)
                        txvItemNumber.setText(Integer.toString(folder.ItemNumber) +" Item");
                    else
                        txvItemNumber.setText(Integer.toString(folder.ItemNumber) +" Items");
                }
                return convertView;
            }
        };
        lsvFolders.setAdapter(adapter);
        adaptListViewHeight(R.id.lsvFolders,adapter);
    }

    private void FindViews() {
        edtFolderName = (EditText) findViewById(R.id.edtFolderName);
        txvErrorMessage = (TextView) findViewById(R.id.txvErrorMessage);
        spnFolders=(Spinner) findViewById(R.id.spnFolders);
        lsvFolders=(ListView) findViewById(R.id.lsvFolders);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        this.context=this;
        FindViews();
        loadLsvFolders();
    }

    public void btnCreate_clicked(View button) {
        if (edtFolderName.getText().toString().equals("")) {
           txvErrorMessage.setText("Please enter Folder name");
           return;
        }
        String root = this.getFilesDir().toString();
        File dir = new File(root, edtFolderName.getText().toString());
        if (dir.exists()) {
            txvErrorMessage.setText("Folder exists");
            return;
        }
        dir.mkdir();
        txvErrorMessage.setText("The folder has been created successfully");
        loadLsvFolders();
    }

    public void btnCount_clicked(View button) {
        String root = this.getFilesDir().toString();
        File dir = new File(root);
        File[] files = dir.listFiles();
        ArrayList<MFolder> folders = new ArrayList<MFolder>();
        for (int i = 0; i < files.length; ++i) {
            if (files[i].isDirectory()) {
                File[] subFiles = files[i].listFiles();
                MFolder folder = new MFolder(files[i].getName(), (subFiles == null) ? 0 : subFiles.length);
                folders.add(folder);
            }
        }
        ArrayAdapter<MFolder> adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, folders) {
            public Object getItem(int position)
            {
                MFolder folder = folders.get(position);
                return folder.Name;
            }
        };
        spnFolders.setAdapter(adapter);
    }

    protected void adaptListViewHeight(int id, ArrayAdapter adapter) {
        ListView listView = findViewById(id);
        int totalHeight = 0;
        for (int i = 0; i < adapter.getCount(); i++) {
            View listItem = adapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * adapter.getCount());
        listView.setLayoutParams(params);
    }
}