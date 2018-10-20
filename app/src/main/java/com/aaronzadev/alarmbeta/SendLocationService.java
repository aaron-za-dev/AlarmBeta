package com.aaronzadev.alarmbeta;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by Aaron Zuniga on 06/06/2016.
 */
public class SendLocationService extends Service implements LocationListener {

    private final String DEBUG_TAG = "[GPS Ping]";

    private LocationManager lm;
    private double latitude;
    private double longitude;
    private double accuracy;
    private Location location;
    private boolean isEnabled = false;
    private int NumContacts = 0;
    private int smsPerContact;
    private SmsManager smsManager;
    private int counterSMS = 1;
    private int counterLops = 0;
    private long timeXsms;
    private String msjAlarm;
    private SmsBroadcastReceiver smsBroadcastReceiver;
    private static final String URLADDRESS = "https://maps.google.com.mx/maps?q=";


    @Override
    public void onLocationChanged(Location location) {
        Log.d(DEBUG_TAG, "onLocationChanged");

        latitude = location.getLatitude();
        longitude = location.getLongitude();
        accuracy = location.getAccuracy();
    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.d(DEBUG_TAG, "onProviderDisabled");
        Toast.makeText(
                getApplicationContext(),
                "Attempted to ping your location, and GPS was disabled.",
                Toast.LENGTH_LONG).show();
    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.d(DEBUG_TAG, "onProviderEnabled");
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 10f, this);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.d(DEBUG_TAG, "onStatusChanged");

    }

    @Override
    public void onCreate() {
        Log.d(DEBUG_TAG, "onCreate");

    }

    @Override
    public void onDestroy() {
        lm.removeUpdates(this);
        Log.d(DEBUG_TAG, "onDestroy");


    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(DEBUG_TAG, "onBind");

        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onStart(final Intent intent, int startid) {
        Log.d(DEBUG_TAG, "onStart");

        isEnabled = true;

        NumContacts = intent.getIntExtra("TotalContacts", 1);
        smsPerContact = intent.getIntExtra("QtySMS", 3);
        timeXsms = intent.getLongExtra("TIMEXALARM", 180000);
        msjAlarm = intent.getStringExtra("MSJALARM");


        Location location = getLocation();
        latitude = location.getLatitude();
        longitude = location.getLongitude();


        final Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (isEnabled && counterLops < smsPerContact) {
                    if (latitude != 0.0 && longitude != 0.0) {
                        do {
                            Log.d("MSJ", intent.getStringExtra("C" + counterSMS) + msjAlarm + latitude + longitude);
                            sendSMS(intent.getStringExtra("C" + counterSMS), msjAlarm, latitude, longitude);
                            counterSMS++;
                        } while (counterSMS <= NumContacts);
                        counterSMS = 1;
                    } else {
                        Log.d("MSJ", "Latitud o Longitud es igual a cero!");
                    }
                    handler.postDelayed(this, timeXsms);
                    counterLops++;
                } else {
                    handler.removeCallbacks(this);
                    Log.d("MSJ", "Mensajes enviados: " + (counterLops * NumContacts));
                    isEnabled = false;
                    counterLops = 0;
                    Log.d("MSJ", "Servicio Finalizado!");
                    stopSelf();
                }

            }
        };

        handler.post(runnable);
    }


    private void sendSMS(String phoneNumber, String message, double lat, double longi) {

        String result = "Activity.RESULT_OK";

        PendingIntent sentPI = PendingIntent.getBroadcast(this, 0, new Intent(result), 0);

        smsBroadcastReceiver = new SmsBroadcastReceiver();

        registerReceiver(smsBroadcastReceiver, new IntentFilter(result));

        try {

            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNumber, null, message + ", " + URLADDRESS + lat + "," + longi, sentPI, null);

        } catch (Exception e) {

            e.printStackTrace();
            Toast.makeText(getBaseContext(), "SMS sending failed...", Toast.LENGTH_SHORT).show();
        }

    }


    public Location getLocation() {

        try {
            lm = (LocationManager) this.getSystemService(LOCATION_SERVICE);

            // getting GPS status
            boolean isGPSEnabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);

            // getting network status
            boolean isNetworkEnabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isGPSEnabled && !isNetworkEnabled) {
                // no network provider is enabled
            } else {
                if (isNetworkEnabled) {
                    lm.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            1000,
                            10f, this);
                    Log.d("Network", "Network Enabled");
                    if (lm != null) {
                        location = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (location != null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                        }
                    }
                }
                // if GPS Enabled get lat/long using GPS Services
                if (isGPSEnabled) {
                    if (location == null) {
                        lm.requestLocationUpdates(
                                LocationManager.GPS_PROVIDER,
                                1000,
                                10f, this);
                        Log.d("GPS", "GPS Enabled");
                        if (lm != null) {
                            location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            if (location != null) {
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                            }
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return location;
    }


}
