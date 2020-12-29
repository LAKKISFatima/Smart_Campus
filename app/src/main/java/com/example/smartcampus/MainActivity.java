package com.example.smartcampus;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import android.Manifest;
import android.app.Activity;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends Activity implements LocationListener {

    protected LocationManager locationManager;
    protected LocationListener locationListener;
    static double x, y;
    public Date time;
    TextView tv;
    static int MY_PERMISSIONS_REQUEST_LOCATION=99;
    static Student s;
    private JobScheduler mScheduler;
    private static final int JOB_ID = 0;
    static TextView tvc;
    static int ct = 0;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        boolean gps_enabled=false;
        boolean network_enabled=false;

        tv = (TextView) findViewById(R.id.tv1);
        //tvc = (TextView) findViewById(R.id.test);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_LOCATION);
            }

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        /*if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
            gps_enabled = true;
        if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
            network_enabled = true;
        if(gps_enabled)
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        else {
            if (network_enabled)
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
            else locationManager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, 0, 0, this);
        }*/

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);

        s = initialize();

        try {
            s.WriteXML();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        ListView lv = findViewById(R.id.list) ;

        ArrayList<String> al = new ArrayList<String>();

        for (int i =0; i<s.myCourses.size(); i++){
            al.add(s.myCourses.get(i).toString());
        }

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1,
                al);

        lv.setAdapter(arrayAdapter);

        MQTTPublisher mP = new MQTTPublisher();
        TextView tv2 = findViewById(R.id.tv2);
        tv2.setText(mP.clientId);

        ///////////////////////////////////////////////////////

        mScheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);

        ComponentName serviceName = new ComponentName(getPackageName(),
                PublishJobService.class.getName());
        JobInfo.Builder builder = new JobInfo.Builder(JOB_ID, serviceName);

        boolean constraintSet = true;

        //builder.setMinimumLatency(10 * 1000);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            builder.setPeriodic(15 * 60 * 1000, 20 * 1000);
        }


        if(constraintSet) {
            //Schedule the job and notify the user
            JobInfo myJobInfo = builder.build();
            mScheduler.schedule(myJobInfo);
            //Toast.makeText(this, "Job Scheduled, job will run when " + "the constraints are met.", Toast.LENGTH_SHORT).show();
        }

        //mP.start();
    }

    @Override
    public void onLocationChanged(Location location) {
        tv = (TextView) findViewById(R.id.tv1);
        x = location.getLongitude();
        y = location.getLatitude();
        time = new Date(location.getTime());
        SimpleDateFormat DateFor = new SimpleDateFormat("E, dd/MMM/yyyy \nHH:mm");
        String stringDate = DateFor.format(time);

        String s = stringDate + "\n\n"
            + "Latitude: " + location.getLatitude() + "\nLongitude: " + location.getLongitude() + "\n";
        tv.setText(s);

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    public static Student initialize() {
        ArrayList<Course> c = new ArrayList<Course>();

        ArrayList<Date> dstart = new ArrayList<Date>();
        dstart.add(new Date(2020, 12, 29, 10, 00));
        dstart.add(new Date(2020, 12, 31, 8, 0));

        ArrayList<Date> dend = new ArrayList<Date>();
        dend.add(new Date(2020, 12, 29, 11, 0));
        dend.add(new Date(2020, 12, 31, 9, 0));
        Course c1 = new Course("I3300", "DataS", dstart, dend, 33.828470, 33.829086, 35.521337, 35.523058);

        ArrayList<Date> dstart2 = new ArrayList<Date>();
        dstart2.add(new Date(2020, 12, 28, 23, 30));
        dstart2.add(new Date(2020, 12, 31, 10, 0));

        ArrayList<Date> dend2 = new ArrayList<Date>();
        dend2.add(new Date(2020, 12, 29, 13, 0));
        dend2.add(new Date(2020, 12, 31, 11, 0));
        Course c2 = new Course("E2200", "Mechanics", dstart2, dend2, 33.825023, 33.825829, 35.520459, 35.521943);

        c.add(c1);
        c.add(c2);

        return new Student("1234", "Hello", c);

    }

}