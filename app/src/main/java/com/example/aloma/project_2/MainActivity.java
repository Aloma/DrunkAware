package com.example.aloma.project_2;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.LocationManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

public class MainActivity extends AppCompatActivity {

    final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 123;
    private static final int MY_PERMISSION_ACCESS_COARSE_LOCATION = 11;
    protected LocationManager locationManager;
    SQLiteAdapter adp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // check if table exists, if it does call service else take to emergency contact
        checkAllPermissions();
        statusCheck();
        adp = new SQLiteAdapter(this);
        if(!adp.isTableExists("contacts", true) && !adp.isTableExists("words", true)){
            adp.createTablesandInsert();
            Intent emergencyIntent = new Intent(MainActivity.this,
                    EmergencyContact.class);
            startActivity(emergencyIntent);
        }

        Intent i= new Intent(this, MyService.class);
        // query emergency contact num, trigger words
        // i.putExtra("KEY1", "Value to be used by the service"); // potentially add data to the intent
        this.startService(i);
        ImageButton emergencyContacts = (ImageButton) findViewById(R.id.emergency_contacts);
        ImageButton familyContacts = (ImageButton) findViewById(R.id.family_contacts);
        ImageButton personalData = (ImageButton) findViewById(R.id.personal_data);
        ImageButton triggerWord = (ImageButton) findViewById(R.id.trigger_words);
        ImageButton lockApplication = (ImageButton) findViewById(R.id.lock_applications);

        emergencyContacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //start Emergency Contacts Activity
                Intent emergencyIntent = new Intent(MainActivity.this,
                        EmergencyContact.class);
                startActivity(emergencyIntent);
            }
        });

        familyContacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //start Emergency Contacts Activity
                Intent familyIntent = new Intent(MainActivity.this,
                        FamilyContact.class);
                startActivity(familyIntent);
            }
        });

        personalData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //start Emergency Contacts Activity
                Intent personalDataIntent = new Intent(MainActivity.this,
                        PersonalData.class);
                startActivity(personalDataIntent);
            }
        });

        triggerWord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //start Emergency Contacts Activity
                Intent triggerWordsIntent = new Intent(MainActivity.this,
                        TriggerWords.class);
                startActivity(triggerWordsIntent);
            }
        });

        lockApplication.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //start Emergency Contacts Activity
                Intent lockApplicationIntent = new Intent(MainActivity.this,
                        ViewLockedApplication.class);
                startActivity(lockApplicationIntent);
            }
        });
    }

    public void statusCheck() {
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();

        }
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    public void checkAllPermissions()
        {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_CONTACTS))
                {
                }
                else {
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.READ_CONTACTS},
                            MY_PERMISSIONS_REQUEST_READ_CONTACTS);
                }
            }
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        MY_PERMISSION_ACCESS_COARSE_LOCATION);
            }
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

            Cursor cursor = getContentResolver().query(Uri.parse("content://sms/inbox"), null, null, null, null);
            cursor.close();
        }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSION_ACCESS_COARSE_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted
                } else {
                    // permission denied
                }
                break;
            }
            case MY_PERMISSIONS_REQUEST_READ_CONTACTS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

}
