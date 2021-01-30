package com.example.smartcampus;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class PublishJobService extends JobService {

    NotificationManager mNotifyManager;
    private JobScheduler mScheduler;
    private static final int JOB_ID = 0;
    public static Student myStudent;
    private static final String TAG = "BOOMBOOMTESTGPS";
    private LocationManager mLocationManager = null;
    private static final int LOCATION_INTERVAL = 1000;
    private static final float LOCATION_DISTANCE = 10f;


    // Notification channel ID.
    private static final String PRIMARY_CHANNEL_ID =
            "primary_notification_channel";


    private class LocationListener implements android.location.LocationListener {
        Location mLastLocation;

        public LocationListener(String provider) {
            Log.e(TAG, "LocationListener " + provider);
            mLastLocation = new Location(provider);
        }

        @Override
        public void onLocationChanged(Location location) {
            Log.e(TAG, "onLocationChanged: " + location);
            mLastLocation.set(location);
        }


        @Override
        public void onProviderDisabled(String provider) {
            Log.e(TAG, "onProviderDisabled: " + provider);
        }

        @Override
        public void onProviderEnabled(String provider) {
            Log.e(TAG, "onProviderEnabled: " + provider);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.e(TAG, "onStatusChanged: " + provider);
        }
    }

    LocationListener[] mLocationListeners = new LocationListener[]{
            new LocationListener(LocationManager.GPS_PROVIDER),
            new LocationListener(LocationManager.NETWORK_PROVIDER)
    };


    @Override
    public boolean onStartJob(JobParameters params) {

        myStudent = Student.initialize();

        boolean haveClassNow = false;
        Course currC = new Course();
        Course c11 = null;
        Calendar dCurr = Calendar.getInstance();
        //dCurr.clear();
        //dCurr.set(2021, 1, 18);

        Calendar dstart = Calendar.getInstance();
        for (int i = 0; i < myStudent.myCourses.size(); i++) {
            c11 = myStudent.myCourses.get(i);
            for (int j = 0; j < myStudent.myCourses.get(i).start.size(); j++) {
                if (myStudent.myCourses.get(i).start.get(j).before(dCurr)
                       && myStudent.myCourses.get(i).end.get(j).after(dCurr)) {
                    haveClassNow = true;
                    currC = myStudent.myCourses.get(i);
                    dstart = myStudent.myCourses.get(i).start.get(j);
                    //dstart = new Date(myStudent.myCourses.get(i).start.get(j).getTime());
                    break;
                }

            }
            if (haveClassNow) break;
        }
        Log.e("testCourse", c11 + "");
        haveClassNow=true;

        if (haveClassNow) {

            //Location
            Log.e(TAG, "onCreate");
            initializeLocationManager();
            try {
                mLocationManager.requestLocationUpdates(
                        LocationManager.NETWORK_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                        mLocationListeners[1]);
            } catch (java.lang.SecurityException ex) {
                Log.i(TAG, "fail to request location update, ignore", ex);
            } catch (IllegalArgumentException ex) {
                Log.d(TAG, "network provider does not exist, " + ex.getMessage());
            }
            try {
                mLocationManager.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                        mLocationListeners[0]);
            } catch (java.lang.SecurityException ex) {
                Log.i(TAG, "fail to request location update, ignore", ex);
            } catch (IllegalArgumentException ex) {
                Log.d(TAG, "gps provider does not exist " + ex.getMessage());
            }

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                //return TODO;
            }

            Location myLocation = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            if (myLocation == null)
                myLocation = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

            //Create the notification channel
            createNotificationChannel(myLocation);

            //if (MyLocation.isInArea(myLocation, currC.xMin, currC.xMax, currC.yMin, currC.yMax)) {

                SimpleDateFormat DateFor = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String stringDate = DateFor.format(dstart.getTime());

                MQTTPublisher mP = new MQTTPublisher(this);
                String message = "ID:" + myStudent.ID + ";CID:" + currC.code + ";Time:" + stringDate;

                mP.start(message);

                String s = "" + mP.clientId;
                createNotificationChannel(s);

                ///////////////////////////////

                Toast.makeText(this, "Location published", Toast.LENGTH_LONG).show();
            //}
        }
       return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return false;
    }

    /**
     * Creates a Notification channel, for OREO and higher.
     */
    public void createNotificationChannel(Location l) {

        // Define notification manager object.
        mNotifyManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        // Notification channels are only available in OREO and higher.
        // So, add a check on SDK version.
        if (android.os.Build.VERSION.SDK_INT >=
                android.os.Build.VERSION_CODES.O) {

            // Create the NotificationChannel with all the parameters.
            NotificationChannel notificationChannel = new NotificationChannel
                    (PRIMARY_CHANNEL_ID,
                            "Job Service notification",
                            NotificationManager.IMPORTANCE_HIGH);

            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(true);
            notificationChannel.setDescription
                    ("Notifications from Job Service");

            mNotifyManager.createNotificationChannel(notificationChannel);

            ////////////////////////////////////////////////////////////////////////////
            //Set up the notification content intent to launch the app when clicked
            PendingIntent contentPendingIntent = PendingIntent.getActivity
                    (this, 0, new Intent(this, MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);

            NotificationCompat.Builder builder = new NotificationCompat.Builder
                    (this, PRIMARY_CHANNEL_ID)
                    .setContentTitle("Session Reminder")
                    .setContentText("You are in session now!" +
                            "\nYou location ( " + l.getLongitude() + " , " + l.getLatitude() + " ) has been published.")
                    .setContentIntent(contentPendingIntent)
                    .setSmallIcon(R.drawable.ic_job_running)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setDefaults(NotificationCompat.DEFAULT_ALL)
                    .setAutoCancel(true);

            mNotifyManager.notify(0, builder.build());
        }
    }

    public void createNotificationChannel(String s) {

        // Define notification manager object.
        mNotifyManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        // Notification channels are only available in OREO and higher.
        // So, add a check on SDK version.
        if (android.os.Build.VERSION.SDK_INT >=
                android.os.Build.VERSION_CODES.O) {

            // Create the NotificationChannel with all the parameters.
            NotificationChannel notificationChannel = new NotificationChannel
                    (PRIMARY_CHANNEL_ID,
                            "Job Service notification",
                            NotificationManager.IMPORTANCE_HIGH);

            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(true);
            notificationChannel.setDescription
                    ("Notifications from Job Service");

            mNotifyManager.createNotificationChannel(notificationChannel);

            ////////////////////////////////////////////////////////////////////////////
            //Set up the notification content intent to launch the app when clicked
            PendingIntent contentPendingIntent = PendingIntent.getActivity
                    (this, 0, new Intent(this, MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);

            NotificationCompat.Builder builder = new NotificationCompat.Builder
                    (this, PRIMARY_CHANNEL_ID)
                    .setContentTitle("Session Reminder")
                    .setContentText(s)
                    .setContentIntent(contentPendingIntent)
                    .setSmallIcon(R.drawable.ic_job_running)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setDefaults(NotificationCompat.DEFAULT_ALL)
                    .setAutoCancel(true);

            mNotifyManager.notify(0, builder.build());
        }
    }

    /*
    public Date findNextDate(){

        Course nearestc;
        //List<Date> dates = new ArrayList<Date>();
        Date dMin = new Date(2050, 5, 1), dCurr = new Date();
        for(int i=0; i<MainActivity.s.myCourses.size(); i++) {
            for (int j=0; j<MainActivity.s.myCourses.get(i).start.size(); j++){
                if (dMin.after(MainActivity.s.myCourses.get(i).start.get(j))
                        && MainActivity.s.myCourses.get(i).start.get(j).after(dCurr))
                    dMin = MainActivity.s.myCourses.get(i).start.get(j);
            }
        }

        /*long now = System.currentTimeMillis();
        Date closest = Collections.min(dates, new Comparator<Date>() {
            public int compare(Date d1, Date d2) {
                long diff1 = Math.abs(d1.getTime() - now);
                long diff2 = Math.abs(d2.getTime() - now);
                return Long.compare(diff1, diff2);
            }
        });


        return dMin;
    }
    */

    private void initializeLocationManager() {
        Log.e(TAG, "initializeLocationManager");
        if (mLocationManager == null) {
            mLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        }
    }

}
