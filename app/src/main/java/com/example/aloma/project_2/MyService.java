package com.example.aloma.project_2;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.provider.CallLog;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.os.ResultReceiver;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class MyService extends Service implements LocationListener {
    public static final String TAG = "MyService";
    HashMap<String, Integer> data = new HashMap<String, Integer>();
    ArrayList<String> eContacts = new ArrayList<>();
    final int threshold = 2;
    ArrayList<String> triggerWords = new ArrayList<>();
    List<String> msgs = new ArrayList<>();
    String locationValue = "";
    protected LocationManager locationManager;
    int eFlag;
    private int mInterval = 10000; // 5 seconds by default, can be changed later
    private Handler mHandler;
    SQLiteAdapter adp;
    boolean cabAlert = true;
    private SQLiteAdapter db= new SQLiteAdapter(this);
    public MyService() {
        mHandler = new Handler();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //TODO do something useful
        adp = new SQLiteAdapter(this);
        eContacts = adp.getContactNumbers();
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        getLocation();
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        startRepeatingTask();
        return Service.START_NOT_STICKY;
    }


    private int checkForEmergency() {
        int eFlag = 0; // no emergency
        triggerWords = adp.getWords();
        triggerWords.add("drunk");
        triggerWords.add("drinking");
        triggerWords.add("safe");
        triggerWords.add("worried");
        for (String s : triggerWords) {
            //Log.d(TAG,s);

            for (String msg : msgs) {
                //Log.d(TAG, msg);
                if (msg.contains(s)) {
                    eFlag++;
                    Log.d(TAG, "Flag incremented" + Integer.toString(eFlag));

                }
            }
        }
        return eFlag;
    }

    // check if the number is in econtact list
    private void getCallDetails() {
        StringBuffer sb = new StringBuffer();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Cursor managedCursor = getContentResolver().query(CallLog.Calls.CONTENT_URI, null, null, null, null);
        int number = managedCursor.getColumnIndex(CallLog.Calls.NUMBER);
        int type = managedCursor.getColumnIndex(CallLog.Calls.TYPE);
        int date = managedCursor.getColumnIndex(CallLog.Calls.DATE);
        int duration = managedCursor.getColumnIndex(CallLog.Calls.DURATION);
        while (managedCursor.moveToNext()) {
            String phNumber = managedCursor.getString(number);
            String callType = managedCursor.getString(type);
            String callDate = managedCursor.getString(date);
            Date callDayTime = new Date(Long.valueOf(callDate));
            Date currentTime = new Date();
            //Log.d(TAG,"CALLS being read");

            //Log.d(TAG, "Call time"+callDayTime.getTime());
            //Log.d(TAG, "current time"+currentTime.getTime());
            String dir = null;
            int dircode = Integer.parseInt(callType);
            long diff = currentTime.getTime() - callDayTime.getTime();
            long diffMinutes = diff / (60000);
            //Log.d(TAG, "difference"+diff);
            if (dircode == CallLog.Calls.MISSED_TYPE && diffMinutes <= 30) {
                Log.d(TAG, "difference"+phNumber);

                if (data.containsKey(phNumber)) {
                    data.put(phNumber, data.get(phNumber) + 1);
                } else {
                    data.put(phNumber, 1);
                }
            }
        }

        managedCursor.close();
        //Log.d(TAG, data.toString());
    }

    private void readMessageFromEmContact(ArrayList<String> eContacts) {

        Cursor cursor = getContentResolver().query(Uri.parse("content://sms/inbox"), null, null, null, null);
        if (cursor.moveToFirst()) { // must check the result to prevent exception
            do {
                String msgData = "";
                for (String eContact : eContacts) {
                    //Log.d(TAG,"Messages being read");
                    if (cursor.getString(cursor.getColumnIndex("address")).equals(eContact)) {
                        msgData = cursor.getString(cursor.getColumnIndex("body"));
                        msgs.add(msgData);
                    }
                }
            } while (cursor.moveToNext());
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void sendMessage(String eContact) {
        String smsMessage = locationValue;
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(eContact, null, smsMessage, null, null);
            Toast.makeText(getApplicationContext(), "SMS sent.",
                    Toast.LENGTH_LONG).show();
            Intent launchIntentMessages = getPackageManager().getLaunchIntentForPackage("me.lyft.android");// Change the variable here for uber
            PendingIntent pIntent = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), launchIntentMessages, 0);

            // Build notification
            // Actions are just fake
            if(true) {

                Intent launchIntent = getPackageManager().getLaunchIntentForPackage("me.lyft.android");// Change the variable here for uber
                PendingIntent lIntent = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), launchIntent, 0);
                Notification noti = new Notification.Builder(this)
                        .setSmallIcon(R.drawable.noti_icon)
                        .setContentText("Alert mode On! Call a Cab!")
                        .setContentTitle("DISTRESS ALERT!")
                        .setContentIntent(lIntent)
                        .build();

                NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                // hide the notification after its selected
                noti.flags |= Notification.FLAG_AUTO_CANCEL;

                notificationManager.notify(0, noti);
            }
            Notification noti = new Notification.Builder(this)
                    .setSmallIcon(R.drawable.noti_icon)
                    .setContentText("Emergency message sent to" + eContact)
                    .setContentTitle("DISTRESS ALERT!")
                    .setContentIntent(pIntent)
                    .build();

            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            // hide the notification after its selected
            noti.flags |= Notification.FLAG_AUTO_CANCEL;

            notificationManager.notify(0, noti);


        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    // LOCATION CODE
    public void getLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //objMain.requestPermissions(, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
            //      MY_PERMISSION_ACCESS_COARSE_LOCATION);
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
    }

    @Override
    public void onLocationChanged(Location location) {
        locationValue = "Latitude:" + location.getLatitude() + ", Longitude:" + location.getLongitude();
        Log.d(TAG, "Location: " + locationValue);
    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.d("Latitude", "disable");
    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.d("Latitude", "enable");
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.d("Latitude", "status");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopRepeatingTask();
    }

    Runnable mStatusChecker = new Runnable() {
        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
        @Override
        public void run() {
            try {
                Log.d(TAG,"HERE");
                eContacts = adp.getContactNumbers();
                eContacts.add("+14803765790");
                Log.d(TAG,"Emergency"+eContacts.toString());
                getCallDetails();
                readMessageFromEmContact(eContacts);
                eFlag = checkForEmergency();
                Iterator it = data.entrySet().iterator();
                while (it.hasNext()) {
                    // if phNum is in e.contact and freq>threshold, send mesage
                    // receive contact list from main activity
                    Map.Entry pair = (Map.Entry) it.next();
                    Log.d(TAG, "Call log"+pair.getKey().toString());
                    if (eContacts.contains(pair.getKey()) && ((int) pair.getValue() >= threshold) && eFlag > 2) {
                        sendMessage(pair.getKey().toString());
                    }
                }
                // updateStatus(); //this function can change value of mInterval.
            } finally {
                // 100% guarantee that this always happens, even if
                // your update method throws an exception
                mHandler.postDelayed(mStatusChecker, mInterval);
            }
        }
    };

    void startRepeatingTask() {
        mStatusChecker.run();
    }

    void stopRepeatingTask() {
        mHandler.removeCallbacks(mStatusChecker);
    }
}