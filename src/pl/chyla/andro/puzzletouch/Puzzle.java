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



import pl.chyla.andro.activity.ShakeActivity;
import pl.chyla.andro.puzzletouch.R;
import pl.chyla.andro.puzzletouch.R.id;
import pl.chyla.andro.puzzletouch.R.layout;
import android.app.Activity;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

/**
 * Puzzle: a simple game
 */
public class Puzzle extends ShakeActivity {

    public PuzzleView mPuzzleView;

    private static String PUZZLE_KEY = "puzzle-view";

    /**
     * Called when Activity is first created. Turns off the title bar, sets up
     * the content views, and fires up the PuzzleView.
     *
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.puzzle_layout);

        mPuzzleView = (PuzzleView) findViewById(R.id.puzzle);
        mPuzzleView.setTextView((TextView) findViewById(R.id.text));

        mPuzzleView.setMode(PuzzleView.STATE_RUNNING);
        
    }

    @Override
    protected void executeShakeAction() {
      mPuzzleView.startNewGame();
      
    }
    
}
