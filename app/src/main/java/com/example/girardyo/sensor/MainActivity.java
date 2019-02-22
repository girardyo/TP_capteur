package com.example.girardyo.sensor;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements SensorEventListener{

    List<Sensor> sensor;
    ListView lv;
    TextView textView;
    TextView textView2;
    SensorEventListener proximitySensorListener;
    SensorManager sensorManager;
    SensorManager sensorManager2;
    SensorManager sensorManager3;
    Sensor prox;
    Sensor accelerometer;
    Sensor gyro;
    float X,Y,Z;
    Boolean checkFlash=false;
    Camera camera;
    Camera.Parameters fparams;
    Boolean shake=false;
    Boolean i = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = (TextView) this.findViewById(R.id.textView);
        textView2 = (TextView) this.findViewById(R.id.textView2);


        //Exercice 1
        sensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        sensorManager2 = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        sensorManager3 = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getSensorList(Sensor.TYPE_ALL);
        lv = (ListView) findViewById(R.id.listSensor);
        lv.setAdapter(new ArrayAdapter<Sensor>(this, android.R.layout.simple_list_item_1, sensor));

        //Exercice 2
        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);

        builder1.setCancelable(true);


        Sensor thermo = sensorManager.getDefaultSensor(Sensor.TYPE_TEMPERATURE);
        if(thermo !=null) {
            builder1.setMessage("Capteur de température disponible");
        }

        else {
            builder1.setMessage("Capteur de température introuvable");
        }
        AlertDialog alert11 = builder1.create();
        alert11.show();

        //Exercice 3

        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this,accelerometer, SensorManager.SENSOR_DELAY_NORMAL);

        gyro = sensorManager2.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        sensorManager2.registerListener(this,gyro, SensorManager.SENSOR_DELAY_NORMAL);

        prox = sensorManager3.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        sensorManager3.registerListener(this,prox, SensorManager.SENSOR_DELAY_NORMAL);
        }

        @Override
        public void onSensorChanged(SensorEvent event) {
            if(event.sensor.getType() != Sensor.TYPE_ACCELEROMETER){
                return;
            }
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            double moy = (Math.sqrt(x*x)+Math.sqrt(y*y)+Math.sqrt(z*z))/3;
            double angle = (Math.atan2(y, Math.sqrt(x*x+z*z))/ (Math.PI / 180));
            StringBuilder sb = new StringBuilder().append("x:").append(x).append("\n");
            sb.append("y:").append(y).append("\n");
            sb.append("z:").append(z).append("\n");
            sb.append("degree:").append(angle).append("\n");
            sb.append("Moyenne : ").append(moy);



            if (moy < 5){
                textView2.setBackgroundResource(R.color.white);
                shake=false;
            }
            else if (moy > 5 && moy < 7){
                textView2.setBackgroundResource(R.color.green);
                shake=false;

            }
            else if (moy > 7 && moy < 10){
                textView2.setBackgroundResource(R.color.red);
                shake=false;

            }
            else if (moy > 11){
                textView2.setBackgroundResource(R.color.black);
                shake=true;
            }
            textView2.setText(sb.toString());

            if(shake==true){
                Boolean bool = this.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
                if (bool==true){
                    if(checkFlash==true){
                        turnOFFFlash();
                    }
                    if (checkFlash==false) {
                        turnOnFlash();
                    }
                }
            }

            //exercice 4
            if(event.sensor.getType() != Sensor.TYPE_GYROSCOPE){
                float x2 = event.values[0];
                float y2 = event.values[1];
                float z2 = event.values[2];
                String vertical = "";
                String horizontal = "";
                if (x2 < -6){
                    horizontal = "Droite";
                }
                else if (x2 > 6){
                    horizontal = "Gauche";
                }
                else {
                    horizontal = "";
                }

                if (y2 < -6){
                    vertical = "Haut";
                }
                else if (y2 > 6){
                    vertical = "Bas";
                }
                else {
                    vertical = "";
                }

                textView.setText(horizontal + vertical);
/*
                StringBuilder sb2 = new StringBuilder().append("x:").append(x2).append("\n");
                sb2.append("y:").append(y2).append("\n");
                sb2.append("z:").append(z2);
                textView2.setText(sb2.toString());
*/
            }

            //Exercice 6


            if (prox==null){
                Toast.makeText(this, "Capteur de proimité non disponible.", Toast.LENGTH_LONG).show();
            }

            else {
                //probleme de if 
                if (i == false){
                    sensorManager3.registerListener(proximitySensorListener, prox, SensorManager.SENSOR_DELAY_NORMAL);
                    i = true;
                }
                else{
                    sensorManager3.registerListener(proximitySensorListener, prox, SensorManager.SENSOR_DELAY_NORMAL);
                }
            }


            proximitySensorListener = new SensorEventListener(){

                @Override
                public void onSensorChanged(SensorEvent event) {
                    if (event.values[0] < prox.getMaximumRange()) {
                        getWindow().getDecorView().setBackgroundColor(Color.RED);
                    }

                    else {
                        getWindow().getDecorView().setBackgroundColor(Color.GREEN);
                    }
                }

                @Override
                public void onAccuracyChanged(Sensor sensor, int accuracy) {

                }
            };


        }




        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }






    @Override
    protected void onPause() {
        // unregister the sensor (désenregistrer le capteur)
        super.onPause();
        sensorManager3.unregisterListener(proximitySensorListener);
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    private void turnOnFlash() {
        CameraManager cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            String CameraId = cameraManager.getCameraIdList()[0];
            cameraManager.setTorchMode(CameraId, true);
        }
        catch (CameraAccessException e){
        }

        checkFlash=true;

            }

    private void turnOFFFlash() {
        CameraManager cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            String CameraId = cameraManager.getCameraIdList()[0];
            cameraManager.setTorchMode(CameraId, false);
        }
        catch (CameraAccessException e){
        }
        checkFlash=false;

    }
}
