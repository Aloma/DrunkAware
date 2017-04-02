package com.example.aloma.project_2;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.sql.SQLData;
import java.util.ArrayList;

/**
 * Created by Aloma on 11/20/2016.
 */
public class SQLiteAdapter extends SQLiteOpenHelper {

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "mydatabase";
    private static final String TABLE_NAME_WORDS = "words";
    private static final String TABLE_NAME_CONTACTS = "contacts";
    private static final String TABLE_NAME_PROFILE = "profile";
    private static final String TABLE_NAME_APPLICATIONS = "applications";
    private static final String TABLE_WORDS = "create table words(word text);";
    private static final String TABLE_CONTACTS = "create table contacts(id text,name text PRIMARY KEY,number text, type text);";
    private static final String TABLE_PROFILE = "create table if not exists profile(id int, name text, address text, emailid text, number text, cabbookingapp text);";


    public SQLiteAdapter(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_WORDS);
        db.execSQL(TABLE_CONTACTS);
        db.execSQL(TABLE_PROFILE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_WORDS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_CONTACTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_PROFILE);

        // Create tables again
        onCreate(db);
    }

    void addWord(String listItem) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        Log.e("vlaue inserting==", "" + listItem);
        values.put("word", listItem);
        db.insert(TABLE_NAME_WORDS, null, values);
        db.close(); // Closing database connection
    }

    ArrayList<String> getWords() {
        String selectQuery = "SELECT  * FROM " + TABLE_NAME_WORDS +";";
        ArrayList<String> words = new ArrayList<String>();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cur = db.rawQuery(selectQuery, null);
        cur.moveToFirst();
        while(cur.moveToNext()){
            String value = cur.getString(cur.getColumnIndex("word"));
            words.add(value);
        }
        cur.close();
        db.close();
        return words;
    }

    int countWords(){
        SQLiteDatabase db = this.getWritableDatabase();
        String selectQuery ="select count(*) from words;";
        Cursor cursor = db.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        int count= cursor.getInt(0);
        cursor.close();
        return count;
    }

    void addProfile() {

        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(TABLE_PROFILE);
        String insertQuery = "insert into profile values(1, 'na', 'na', 'na', 'na', 'UBER');";
        db.execSQL(insertQuery);

        db.close(); // Closing database connection
    }

    void updateProfile(String name, String address, String emailid, String number, String cabname)
    {
        Log.e("cab name ", cabname);
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "update profile set name='"+name+"', address='"+address+"', emailid='"+emailid+"', number='"+number+"', cabbookingapp='"+cabname+"' where id =1;";
        db.execSQL(query);
        db.close();
    }

    Cursor viewProfile(){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "select * from "+TABLE_NAME_PROFILE+" where id =1;";
        Cursor cursor =db.rawQuery(query, null);
        cursor.moveToFirst();
        return  cursor;
    }



    void dropTable(String name)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("drop table if exists "+name );
    }

    void addContact(Contact contact)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        db.execSQL("create table if not exists contacts(name text PRIMARY KEY,number text,type text);");
        String name = contact.getName();
        String num = contact.getNumber();
        String t = contact.getType();
        Log.e("contact==", "" + name);
        values.put("name", name);
        values.put("number",num);
        values.put("type",t);
        db.insert(TABLE_NAME_CONTACTS, null, values);
        Log.e("contact inserting in==", "" + name);
        db.close();
    }

    ArrayList<String> getContacts(String type) {
        String selectQuery = "select * from " + TABLE_NAME_CONTACTS + " where type='"+ type+"';";
        ArrayList<String> contacts = new ArrayList<String>();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        while(cursor.moveToNext()){
            String name = cursor.getString(cursor.getColumnIndex("name"));
            String number = cursor.getString(cursor.getColumnIndex("number"));
            Log.e("contact displaying in==", "" + name);
            Log.e("contact displaying in==", "" + number);
            Contact c = new Contact(name,number,type);
            contacts.add(c.toString());
        }
        db.close();
        return contacts;
    }

    ArrayList<String> getContactNumbers() {
        String selectQuery = "select number from " + TABLE_NAME_CONTACTS + " where type='emergency';";
        ArrayList<String> contactNumbers = new ArrayList<String>();
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("create table if not exists contacts(name text PRIMARY KEY,number text,type text);");
        Cursor cursor = db.rawQuery(selectQuery, null);
        //if(cursor.getCount()==0)
        //contactNumbers.add("+14803765790");
        cursor.moveToFirst();
        while(cursor.moveToNext()){
            String number = cursor.getString(cursor.getColumnIndex("number"));
            number = number.replaceAll("[^\\d.]", "");
            number = "+" + number;
            Log.e("contact displaying in==", "" + number);
            contactNumbers.add(number);
        }
        db.close();
        return contactNumbers;
    }

    int countContacts(String type){
        SQLiteDatabase db = this.getWritableDatabase();
        String selectQuery ="select count(*) from contacts where type='" + type + "';";
        Cursor cursor = db.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        int count= cursor.getInt(0);
        cursor.close();
        return count;
    }

    void addApplicationName(String appName)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        db.execSQL("create table if not exists applications(name text);");
        Log.e("appName==", "" + appName);
        values.put("name", appName);
        db.insert(TABLE_NAME_APPLICATIONS, null, values);
        Log.e("application inserting==", "" + appName);
        db.close();
    }

    ArrayList<String> getApplications() {
        String selectQuery = "select * from " + TABLE_NAME_APPLICATIONS+";";
        ArrayList<String> applications = new ArrayList<String>();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        while(cursor.moveToNext()){
            String name = cursor.getString(cursor.getColumnIndex("name"));
            applications.add(name);
        }
        db.close();
        return applications;
    }

    int countApplications(){
        SQLiteDatabase db = this.getWritableDatabase();
        String selectQuery ="select count(*) from applications" + ";";
        Cursor cursor = db.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        int count= cursor.getInt(0);
        cursor.close();
        return count;
    }


    public boolean isTableExists(String tableName, boolean openDb) {
        SQLiteDatabase mDatabase = this.getWritableDatabase();
//            if(openDb) {
//                if(mDatabase == null || !mDatabase.isOpen()) {
//                    mDatabase = getReadableDatabase();
//                }
//                if(!mDatabase.isReadOnly()) {
//                    mDatabase.close();
//                    mDatabase = getReadableDatabase();
//                }
//            }
        Cursor cursor = mDatabase.rawQuery("select DISTINCT tbl_name from sqlite_master where tbl_name = '"+tableName+"'", null);
        if(cursor!=null) {
            if(cursor.getCount()>0) {
                cursor.close();
                return true;
            }
            cursor.close();
        }
        return false;
    }

    public void createTablesandInsert() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("create table if not exists contacts(name text PRIMARY KEY,number text,type text);");
        db.execSQL(TABLE_WORDS);
        db.execSQL(TABLE_PROFILE);
        addWord("help");
        addWord("emergency");
        addWord("call 911");
        db.close();
    }
}
