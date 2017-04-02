package com.example.aloma.project_2;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by Aloma on 11/15/2016.
 */
public class PersonalData extends Activity {

    EditText editTextName;
    EditText editTextAddress;
    EditText editTextNumber;
    EditText editTextEmail;
    private Button saveButton;
    SQLiteAdapter db;
    RadioGroup radioCabGroup;
    RadioButton radioCabButton;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.personal_data);
        db = new SQLiteAdapter(this);
        db.addProfile();
        editTextName = (EditText) findViewById(R.id.name);
        editTextAddress = (EditText) findViewById(R.id.address);
        editTextNumber = (EditText) findViewById(R.id.number);
        editTextEmail = (EditText) findViewById(R.id.email);
        radioCabGroup =(RadioGroup) findViewById(R.id.radioGroup);


        saveButton = (Button) findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = editTextName.getText().toString();
                String address = editTextAddress.getText().toString();
                String number = editTextNumber.getText().toString();
                String email = editTextEmail.getText().toString();
                int cabId = radioCabGroup.getCheckedRadioButtonId();
                radioCabButton=(RadioButton)findViewById(cabId);
                String cab = (String) radioCabButton.getText();

                db.updateProfile(name, address, email, number, cab);
                ToastNotification();
            }

        });

        Cursor c = db.viewProfile();
        editTextName.setText(c.getString(c.getColumnIndex("name")));
        editTextAddress.setText(c.getString(c.getColumnIndex("address")));
        editTextNumber.setText(c.getString(c.getColumnIndex("number")));
        editTextEmail.setText(c.getString(c.getColumnIndex("emailid")));
        String cabName = c.getString(c.getColumnIndex("name"));
        //Toast.makeText(this, cabName, Toast.LENGTH_LONG).show();
    }

    void ToastNotification()
    {
        Toast.makeText(this, "Your profile has been updated.", Toast.LENGTH_SHORT).show();
    }
}