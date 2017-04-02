package com.example.aloma.project_2;
import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Arrays;
import static android.content.ContentValues.TAG;
public class FamilyContact extends Activity {
    private Button mBtnContacts;
    private final int PICK = 1;
    String[] items = {""};
    ArrayList<String> itemList;
    ArrayAdapter<String> adapter;
    ListView listV;
    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 1;
    private Uri uriContact;
    private String contactID;
    private SQLiteAdapter db;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.family_contact);
        db = new SQLiteAdapter(this);
        if(db.isTableExists("contacts", true)) {
            if (db.countContacts("family") > 0) {
                itemList = db.getContacts("family");
            } else {
                itemList = new ArrayList<String>(Arrays.asList(items));
            }
        }
        else{
            itemList = new ArrayList<String>(Arrays.asList(items));
        }
        adapter = new ArrayAdapter<String>(this, R.layout.list_item, R.id.txtview, itemList);
        listV = (ListView) findViewById(R.id.list_family_contacts);
        listV.setAdapter(adapter);
        showContacts();
        mBtnContacts = (Button) findViewById(R.id.family_conts);
        mBtnContacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK,
                        ContactsContract.Contacts.CONTENT_URI);
                // calling OnActivityResult with intenet And Some conatct for Identifie
                startActivityForResult(intent, PICK);
            }
        });
    }
    private void showContacts() {
        // Check the SDK version and whether the permission is already granted or not.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, PERMISSIONS_REQUEST_READ_CONTACTS);
            //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        if (requestCode != PERMISSIONS_REQUEST_READ_CONTACTS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Until you grant the permission, we add contacts", Toast.LENGTH_SHORT).show();
            }
        }
    }
    @Override
    public void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);
        String con = null;
        if (reqCode == PERMISSIONS_REQUEST_READ_CONTACTS && resultCode == RESULT_OK) {
            Log.d(TAG, "Response: " + data.toString());
            uriContact = data.getData();
            String name = retrieveContactName();
            String number = retrieveContactNumber();
            Contact contact = new Contact(name,number,"family");
            db.addContact(contact);

            Toast.makeText(this, "Contact of " + name+" has been added.", Toast.LENGTH_SHORT).show();
            con= name + ": " + number;
        }
        itemList.add(con);
        adapter.notifyDataSetChanged();

    }
    private String retrieveContactNumber() {
        String contactNumber = null;
        Cursor cursorID = getContentResolver().query(uriContact,
                new String[]{ContactsContract.Contacts._ID},
                null, null, null);
        if (cursorID.moveToFirst()) {
            contactID = cursorID.getString(cursorID.getColumnIndex(ContactsContract.Contacts._ID));
        }
        cursorID.close();
        Log.d(TAG, "Contact ID: " + contactID);
        Cursor cursorPhone = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER},
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ? AND " +
                        ContactsContract.CommonDataKinds.Phone.TYPE + " = " +
                        ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE,
                new String[]{contactID},
                null);
        if (cursorPhone.moveToFirst()) {
            contactNumber = cursorPhone.getString(cursorPhone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
        }
        cursorPhone.close();
        Log.d(TAG, "Contact Phone Number: " + contactNumber);
        return contactNumber;
    }
    private String retrieveContactName() {
        String contactName = null;
        Cursor cursor = getContentResolver().query(uriContact, null, null, null, null);
        if (cursor.moveToFirst()) {
            contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
        }
        cursor.close();
        Log.d(TAG, "Contact Name: " + contactName);
        return contactName;
    }
}