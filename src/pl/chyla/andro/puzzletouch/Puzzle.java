/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package pl.chyla.andro.puzzletouch;



import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Puzzle: a simple game
 */
public class Puzzle extends Activity implements SensorEventListener {

    private static final float SHAKE_THRESHOLD = 800;

    private PuzzleView mPuzzleView;

    private SensorManager sensorMgr;

    private long lastUpdate;

    private float last_x;

    private float last_y;

    private float last_z;

    private static String PUZZLE_KEY = "puzzle-view";

    /**
     * Called when Activity is first created. Turns off the title bar, sets up
     * the content views, and fires up the PuzzleView.
     *
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.snake_layout);

        mPuzzleView = (PuzzleView) findViewById(R.id.puzzle);
        mPuzzleView.setTextView((TextView) findViewById(R.id.text));

        mPuzzleView.setMode(PuzzleView.STATE_RUNNING);
        initSensors();
    }

    private void initSensors() {
      sensorMgr = (SensorManager) getSystemService(SENSOR_SERVICE);
      sensorMgr.registerListener((SensorEventListener)this,
      sensorMgr.getDefaultSensor(SensorManager.SENSOR_ACCELEROMETER),
      SensorManager.SENSOR_DELAY_GAME);
    }

    public void onAccuracyChanged(Sensor arg0, int arg1) {
      // TODO Auto-generated method stub
      
    }

    public void onSensorChanged(SensorEvent evt) {
      if (evt.sensor.getType() == SensorManager.SENSOR_ACCELEROMETER) {
        long curTime = System.currentTimeMillis();
        if ((curTime - lastUpdate) > 100) {
          long diffTime = (curTime - lastUpdate);
          lastUpdate = curTime;

          float x = evt.values[SensorManager.DATA_X];
          float y = evt.values[SensorManager.DATA_Y];
          float z = evt.values[SensorManager.DATA_Z];

          float speed = Math.abs(x + y + z - last_x - last_y - last_z) / diffTime * 10000;

          if (speed > SHAKE_THRESHOLD) {
            Log.d("sensor", "shake detected w/ speed: " + speed);
            Toast.makeText(this, "shake detected w/ speed: " + speed, Toast.LENGTH_SHORT).show();
            mPuzzleView.shuffleTiles();
          }
          last_x = x;
          last_y = y;
          last_z = z;
        }
      }

      
    }
}
