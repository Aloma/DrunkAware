package com.example.aloma.project_2;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Created by ratisaxena on 03-12-2016.
 */

public class ViewLockedApplication extends Activity {
    ArrayAdapter<String> adapter;
    EditText editText;
    ArrayList<String> itemList;
    ArrayList<String> savedList;
    ArrayList<String> runningapps;
    HashMap<String,String> apps = new HashMap<String, String>();
    SQLiteAdapter db;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.locked_apps);
        runningapps = new ArrayList<String>();
        savedList =  new ArrayList<String>();
        itemList = new ArrayList<String>();

     //   String str = null;

//        final ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
//        final List<ActivityManager.RunningAppProcessInfo> recentTasks = activityManager.getRunningAppProcesses();

//        new AsyncTask<Void, Void, List<ProcessManager.Process>>() {
//
//            long startTime;
//            StringBuilder sb;
//
//            @Override
//            protected List<ProcessManager.Process> doInBackground(Void... params) {
//                startTime = System.currentTimeMillis();
//                return ProcessManager.getRunningApps();
//            }
//
//            @Override
//            protected void onPostExecute(List<ProcessManager.Process> processes) {
//                sb = new StringBuilder();
//                sb.append("Execution time: ").append(System.currentTimeMillis() - startTime).append("ms\n");
//                sb.append("Running apps:\n");
//                for (ProcessManager.Process process : processes) {
//                    sb.append('\n').append(process.name);
//                    //runningapps.add(process.name);
//                    //Log.e("running app", process.name);
//                }
//
//                //new AlertDialog.Builder(MainActivity.this).setMessage(sb.toString()).show();
//            }
//
//            public String getSb() {
//                return sb.toString();
//            }
//
//        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        StringBuilder sb = new StringBuilder();
        List<ProcessManager.Process> runningProcesses;
        runningProcesses = ProcessManager.getRunningApps();
        for (ProcessManager.Process process : runningProcesses) {
            runningapps.add(process.name);
        }

//        for (int i = 0; i < recentTasks.size(); i++)
//        {
//            runningapps.add(recentTasks.get(i).processName);
//            Log.d("Executed app", "Application executed : " +recentTasks.get(i).processName);
//        }

        for(String s : runningapps)
        {
            Log.e("items in running apps", s);
        }

        db = new SQLiteAdapter(this);
        String[] items = {""};
        if(db.isTableExists("applications", true)) {
            if (db.countApplications() > 0) {
                Log.e("counting applications", String.valueOf(db.countApplications()));
                itemList = db.getApplications();
                for(String s:itemList)
                {
                    if(runningapps.contains(s)){
                        apps.put(s,"running");
                   }
                    else{
                        apps.put(s,"not");
                    }

                }

            } else {
                itemList = new ArrayList<String>(Arrays.asList(items));
            }
        }
        else{
            itemList = new ArrayList<String>(Arrays.asList(items));
        }

        adapter = new ArrayAdapter<String>(this, R.layout.list_item, R.id.txtview, itemList);
        ListView listV = (ListView) findViewById(R.id.list_apps);
        listV.setAdapter(adapter);
        listV.setAdapter(new ArrayAdapter<String>(this, R.layout.list_item, R.id.txtview, itemList){
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View row = super.getView(position, convertView, parent);
            String element = getItem(position);
            if(apps.size()!=0){
            if(apps.get(element).equals("running"))
            {
                row.setBackgroundColor(Color.GREEN);
            }
            else
            {
                // default state
                row.setBackgroundColor(Color.WHITE); // default coloe
            }}
            return row;
        }
    });

        Button btAdd = (Button) findViewById(R.id.add_app_name);
        btAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent appLock = new Intent(ViewLockedApplication.this,
                        LockApplication.class);
                startActivity(appLock);

            }
        });
    }

    public void onBackPressed() {
        Intent appLock = new Intent(ViewLockedApplication.this,
                MainActivity.class);
        startActivity(appLock);
    }

}
