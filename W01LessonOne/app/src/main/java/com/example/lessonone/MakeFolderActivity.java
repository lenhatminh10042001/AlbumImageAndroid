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
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.InputStream;
import java.security.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;

public class MakeFolderActivity extends AppCompatActivity {
    private EditText edtNewFolderName;
    private TextView txvError;

    private ArrayList<MImage> images = new ArrayList<>();
    private Context context;
    private GridView grvImages;

    private ArrayList<Integer> selectedIndexes = new ArrayList<>();

    private void FindViews() {
        edtNewFolderName = (EditText) findViewById(R.id.edtNewFolderName);
        txvError = (TextView) findViewById(R.id.txvError);
        grvImages = (GridView) findViewById(R.id.grvImages);
    }


    private void LoadGrvImages() {
        ArrayAdapter<MImage> adapter = new ArrayAdapter(context, R.layout.griditem_file, images) {
            public View getView(int position, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    MImage image = images.get(position);
                    LayoutInflater layoutInflater = LayoutInflater.from(context);
                    convertView = layoutInflater.inflate(R.layout.griditem_file, null);

                    ImageView imvIcon = convertView.findViewById(R.id.imvIcon);
                    imvIcon.setImageBitmap(image.getBitmap());
                    TextView txvFilename = convertView.findViewById(R.id.txvFilename);
                    txvFilename.setText(image.getName());

                    CheckBox chdSelected = (CheckBox) convertView.findViewById(R.id.chdSelected);
                    chdSelected.setOnClickListener(new View.OnClickListener() {
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
        grvImages.setAdapter(adapter);
        adaptGridViewHeight(R.id.grvImages, adapter);
    }

    public void btnCreateFolder_clicked(View view) {
        if (edtNewFolderName.getText().toString().equals("")) {
            txvError.setText("Please enter folder name");
            return;
        }
        String error = IOLibrary.MakeFolderWithImage(this, edtNewFolderName.getText().toString(), images,selectedIndexes);
        if (!error.equals(""))
            txvError.setText(error);
        else {
            Intent intent = new Intent();
            setResult(RESULT_OK, intent);
            this.finish();
        }
    }

    public void btnCancel_clicked(View view) {
        Intent intent = new Intent();
        setResult(RESULT_CANCELED, intent);
        this.finish();
    }

    private ActivityResultLauncher<Intent> startCaptureActivityLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                        MImage image = new MImage(Long.toString(new Date().getTime()) + ".jpg", bitmap);
                        images.add(image);
                        LoadGrvImages();
                    }
                }
            });


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
                            for (int i = 0; i < count; i++) {
                                uri = data.getClipData().getItemAt(i).getUri();
                                try {
                                    InputStream imageStream = getContentResolver().openInputStream(uri);
                                    final Bitmap bitmap = BitmapFactory.decodeStream(imageStream);
                                    MImage image = new MImage(Long.toString(new Date().getTime()) + ".jpg", bitmap);
                                    images.add(image);
                                } catch (Exception e) {
                                }
                            }
                        }
                        LoadGrvImages();
                    }
                }
            });

    public void btnCapture_clicked(View view) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startCaptureActivityLauncher.launch(intent);
    }

    public void btnImport_clicked(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startPickActivityLauncher.launch(intent);
    }

    public void btnDelete_clicked(View view) {
        for (int i = 0; i < selectedIndexes.size(); i++)
            images.remove((int) selectedIndexes.get(i));
        LoadGrvImages();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_folder);
        this.context = this;
        FindViews();
    }

    protected void adaptGridViewHeight(int id, ArrayAdapter adapter) {
        GridView gridView = findViewById(id);
        int totalHeight = 0;
        Integer[] itemHeights = {0, 0, 0};
        for (int i = 0; i < adapter.getCount(); i++) {
            View gridItem = adapter.getView(i, null, gridView);
            gridItem.measure(0, 0);
            itemHeights[i % 3] = gridItem.getMeasuredHeight();
            if (i % 3 == 2) {
                int max = (int) Collections.max(Arrays.asList(itemHeights));
                totalHeight += max;
                for (int j = 0; j < 3; j++)
                    itemHeights[j] = 0;
            } else if (i == adapter.getCount() - 1) {
                int max = (int) Collections.max(Arrays.asList(itemHeights));
                totalHeight += max;
            }
        }
        ViewGroup.LayoutParams params = gridView.getLayoutParams();
        params.height = totalHeight;
        gridView.setLayoutParams(params);
    }
}