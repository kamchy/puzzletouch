package pl.chyla.andro.activity;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;

public abstract class ShakeActivity extends Activity implements SensorEventListener {

  private static final float SHAKE_THRESHOLD = 800;
  private static final long UPDATE_DELTA_MS = 300;
  protected SensorManager sensorMgr;
  private long lastUpdate;
  private float last_x;
  private float last_y;
  private float last_z;


  protected void initSensors() {
    lastUpdate = System.currentTimeMillis();
    sensorMgr.registerListener(this, 
        sensorMgr.getDefaultSensor(
            SensorManager.SENSOR_ACCELEROMETER),
            SensorManager.SENSOR_DELAY_GAME);
  }

  
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    // TODO Auto-generated method stub
    super.onCreate(savedInstanceState);
    sensorMgr = (SensorManager) getSystemService(SENSOR_SERVICE);
    initSensors();

  }


  public void onAccuracyChanged(Sensor arg0, int arg1) {
    // not implemented
    
  }

  public void onSensorChanged(SensorEvent evt) {
    if (evt.sensor.getType() == SensorManager.SENSOR_ACCELEROMETER) {
      long curTime = System.currentTimeMillis();
      if ((curTime - lastUpdate) > UPDATE_DELTA_MS) {
        long diffTime = (curTime - lastUpdate);
        lastUpdate = curTime;
  
        float x = evt.values[SensorManager.DATA_X];
        float y = evt.values[SensorManager.DATA_Y];
        float z = evt.values[SensorManager.DATA_Z];
  
        float speed = Math.abs(x + y + z - last_x - last_y - last_z) / diffTime * 10000;
  
        if (speed > SHAKE_THRESHOLD) {
          executeShakeAction();
        }
        last_x = x;
        last_y = y;
        last_z = z;
      }
    }
  }

  protected abstract void executeShakeAction();
  
  protected void removeSensors() {
    sensorMgr.unregisterListener(this);
  }

  @Override
  protected void onPause() {
    super.onPause();
    removeSensors();
  }

  
  @Override
  protected void onResume() {
    super.onResume();
    initSensors();
  }

}
