package com.example.lessonone;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private EditText edtUsername;
    private EditText edtPassword;
    private TextView txvMessage;

    private void FindViews() {
        edtUsername = (EditText) findViewById(R.id.edtUsername);
        edtPassword = (EditText) findViewById(R.id.edtPassword);
        txvMessage = (TextView) findViewById(R.id.txvMessage);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FindViews();
    }

    public void btnLogin_clicked(View button) {
        if(edtUsername.getText().toString().equals("")) {
            txvMessage.setText("Please enter Username");
            return;
        }
        if(edtPassword.getText().toString().equals("")) {
            txvMessage.setText("Please enter Password");
            return;
        }
        if(edtUsername.getText().toString().equals("Admin") && edtPassword.getText().toString().equals("123456")) {
            txvMessage.setText("Login successfully");
            return;
        }
        txvMessage.setText("Please enter correct Username and Password");
    }
}