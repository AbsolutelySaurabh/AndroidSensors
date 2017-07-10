package com.example.absolutelysaurabh.sensorproximity;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends Activity implements SensorEventListener {

    private SensorManager mSensorManager;
    private Sensor proximitySensor, gyroscopeSensor, accelero,linearaccn, LightSensor;
    ImageView iv;
    TextView textLIGHT_available, textLIGHT_reading, gyro1,gyro2,gyro3, accelero1, accelero2,proximityText, accelero3, LIGHT_max, linear;

    private boolean color = false;
    private View view;
    private long lastUpdate;

    public static float prevProximity = 1;
    public float maxRange;
    public static float accelerometer,linearacceleration, currproximity, light, gyroscope;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        proximitySensor = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        textLIGHT_reading = (TextView)findViewById(R.id.LIGHT_reading);

        accelero1 = (TextView) findViewById(R.id.accelero1);
        LIGHT_max = (TextView) findViewById(R.id.maxvalue);

        proximityText = (TextView) findViewById(R.id.proximityText);
        linear = (TextView) findViewById(R.id.linearaccn);
        gyro1 = (TextView)findViewById(R.id.gyro1);

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        lastUpdate = System.currentTimeMillis();
        LightSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        if(LightSensor != null){

            maxRange = LightSensor.getMaximumRange();
            LIGHT_max.setText(String.valueOf(maxRange));

            mSensorManager.registerListener(
                    SensorListener,
                    LightSensor,
                    SensorManager.SENSOR_STATUS_ACCURACY_HIGH);

        }
        gyroscopeSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        if(gyroscopeSensor !=null){

            mSensorManager.registerListener(
                    SensorListener,
                    gyroscopeSensor,
                    SensorManager.SENSOR_STATUS_ACCURACY_HIGH);
        }

        accelero = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if(accelero !=null){

            mSensorManager.registerListener(
                    SensorListener,
                    accelero,
                    SensorManager.SENSOR_STATUS_ACCURACY_HIGH);
        }

        linearaccn = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        if(linearaccn !=null){
            mSensorManager.registerListener(
                    SensorListener,
                    linearaccn,
                    SensorManager.SENSOR_STATUS_ACCURACY_HIGH);
        }

        proximitySensor = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        if(proximitySensor !=null){
            mSensorManager.registerListener(
                    SensorListener,
                    proximitySensor,
                    SensorManager.SENSOR_STATUS_ACCURACY_HIGH);
        }
    }
        private final SensorEventListener SensorListener = new SensorEventListener(){

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {
            }
            @Override
            public void onSensorChanged(SensorEvent event) {

                if (event.sensor.getType() == Sensor.TYPE_LIGHT) {

                    getLight(event);

                }
                if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {

                    float[] values = event.values;
                    // Movement
                    float x = values[0];
                    float y = values[1];
                    float z = values[2];

                    gyroscope = (float) (57.3 * (float) Math.sqrt(x * x + y * y + z * z));
                    gyro1.setText(String.valueOf(gyroscope));

                }
                if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {

                    getAccelerometer(event);

                }
                if(event.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION){

                        float[] values = event.values;
                        float x = values[0];
                        float y = values[1];
                        float z = values[2];

                        linearacceleration = (float) Math.sqrt(x * x + y * y + z * z);
                        linear.setText(String.valueOf(linearacceleration));
                }
                if(event.sensor.getType() == Sensor.TYPE_PROXIMITY){

                        getProximity(event);
                }


                if (light < (maxRange/1638) && (currproximity-prevProximity)>=0 ){

                    if(gyroscope > 80 && linearacceleration > 3 ){
                        Log.d("gyroscope : ", String.valueOf(gyroscope));
                        Log.d("linearacceleration : ", String.valueOf(linearacceleration));
                        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                        v.vibrate(2000);
                    }
                }
//                prevProximity = currproximity;
//                comp_filter(accelerometer, gyroscope);
            }
        };

    public void comp_filter(float accelerometer, float gyroscope) {

        float filterAngle = 0;
        float dt= (float) 0.02;
        float x1;
        float x2;
        float x3 = 0;
        float timeConstant;

        timeConstant= (float) 0.5;
        x1 = (accelerometer - filterAngle) * timeConstant * timeConstant;
        x3 += x1 * dt;
        x2 = x3 + ((accelerometer - filterAngle) * 2 * timeConstant) + gyroscope;
        filterAngle = (x2 * dt) + filterAngle;
        linear.setText(String.valueOf(filterAngle));

    }
        private void getProximity(SensorEvent event){

            if(event.values[0] == 0){
                currproximity = 0;
                proximityText.setText("Proximity : " +String.valueOf(currproximity));

            }else{
                currproximity = 1;
                proximityText.setText("PROXIMITY : " +String.valueOf(currproximity));
            }
        }

    private void getLight(SensorEvent event){

        light = event.values[0];
        textLIGHT_reading.setText("LIGHT: " + event.values[0]);
    }

    private void getAccelerometer(SensorEvent event) {

        float[] values = event.values;
        // Movement
        float x = values[0];
        float y = values[1];
        float z = values[2];
        accelerometer = (float) Math.sqrt(x * x + y * y + z * z);
        accelero1.setText(String.valueOf(accelerometer));
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, gyroscopeSensor, SensorManager.SENSOR_STATUS_ACCURACY_HIGH);
        mSensorManager.registerListener(this, accelero, SensorManager.SENSOR_STATUS_ACCURACY_HIGH);
        mSensorManager.registerListener(this, proximitySensor, SensorManager.SENSOR_STATUS_ACCURACY_HIGH);
        mSensorManager.registerListener(this, linearaccn, SensorManager.SENSOR_STATUS_ACCURACY_HIGH);
        mSensorManager.registerListener(this, LightSensor, SensorManager.SENSOR_STATUS_ACCURACY_HIGH);
    }
    @Override
    protected void onPause() {
        // important to unregister the sensor when the activity pauses.
        super.onPause();
        mSensorManager.unregisterListener(this);
    }
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
    public void onSensorChanged(SensorEvent event) {

    }
}