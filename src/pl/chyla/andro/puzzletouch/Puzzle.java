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



import android.app.*;
import android.os.*;
import android.widget.*;

/**
 * Puzzle: a simple game
 */
public class Puzzle extends Activity {

    private PuzzleView mPuzzleView;

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


        if (savedInstanceState == null) {
            mPuzzleView.setMode(PuzzleView.STATE_RUNNING);
        } else {
            // We are being restored
            Bundle map = savedInstanceState.getBundle(PUZZLE_KEY);
            if (map != null) {
                mPuzzleView.restoreState(map);
            } else {
                mPuzzleView.setMode(PuzzleView.STATE_PAUSE);
            }
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        // Pause the game along with the activity
        mPuzzleView.setMode(PuzzleView.STATE_PAUSE);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        //Store the game state
        outState.putBundle(PUZZLE_KEY, mPuzzleView.saveState());
    }

}