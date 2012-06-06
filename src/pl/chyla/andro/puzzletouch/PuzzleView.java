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

import java.util.*;

import android.content.*;
import android.content.res.*;
import android.graphics.drawable.*;
import android.os.*;
import android.util.*;
import android.view.*;
import android.widget.*;

/**
 * PuzzleView: implementation of a simple game of Puzzle
 */
public class PuzzleView extends TileView {

    private static final String TAG = "PuzzleView";

    // game states
    static final int STATE_RUNNING = 1;
    static final int STATE_READY = 2;
    static final int STATE_FINISHED = 3;
    static final int STATE_PAUSE = 4;

    private boolean mScore = false;
    private int mMode = 0;

    /**
     * mStatusText: text shows to the user in some run states
     */
    private TextView mStatusText;

    private ArrayList<Coordinate> mPuzzleCoords1;

    private static final Random RND = new Random();

    private static int mImageCount;

    private static int mTilesCount;





    /**
     * Constructs a PuzzleView based on inflation from XML
     *
     * @param context
     * @param attrs
     */
    public PuzzleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        Log.i("KC","PuzzleView constr 1");
        initTiles();
   }

    public PuzzleView(Context context, AttributeSet attrs, int defStyle) {
    	super(context, attrs, defStyle);
    	Log.i("KC","PuzzleView constr 2");
    	initTiles();
    }

    void initTiles() {
      Log.i("KC","--init tiles--");
        setFocusable(true);

        Resources r = this.getContext().getResources();


        Drawable[] drawables = new Drawable[] {
          r.getDrawable(R.drawable.konik),
          r.getDrawable(R.drawable.kot),
          r.getDrawable(R.drawable.piesek),
        };

        mImageCount = drawables.length;
        mTilesCount = mXTilesCount * mYTilesCount;

        resetTiles(mXTilesCount, mYTilesCount, mImageCount);

        for (int i = 0; i < mImageCount; i++) {
          loadTiles(i * mTilesCount, drawables[i]);
        }

        shuffleTiles();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
      super.onSizeChanged(w, h, oldw, oldh);
      initTiles();
    }


    private void initNewGame() {
        shuffleTiles();
        mScore = false;
    }


    private void shuffleTiles() {
      for (int x = 0; x < mXTilesCount; x++) {
        for (int y = 0; y < mYTilesCount; y++) {
          int tileIndex = getRandomIndexFor(x, y);
          Log.i("KC", String.format("Shuffling: %s %s - tile nr %s", x, y, tileIndex));
          setTile(tileIndex, x, y);
        }
      }
    }

    private RefreshHandler mRedrawHandler = new RefreshHandler();

    private long mMoveDelay = 600;
    private long mLastMove = 0;

    class RefreshHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            PuzzleView.this.update();
            PuzzleView.this.invalidate();
        }

        public void sleep(long delayMillis) {
          this.removeMessages(0);
            sendMessageDelayed(obtainMessage(0), delayMillis);
        }
    };
    /**
     * Handles the basic update loop, checking to see if we are in the running
     * state, determining if a move should be made, updating the snake's location.
     */



    /**
     * Generates index value (as used in mTileArray)
     * for given tile location x y.
     * @param x index of tile (between 0 and mXTilesCount - 1)
     * @param y index of tile (between 0 and mYTilesCount - 1)
     * @return index in mTileArray that point to a bitmap which
     *   will be drawn at location x, y
     */
    private int getRandomIndexFor(int x, int y) {
      int result = 0;
      result = y * mXTilesCount + x + 1; //1-based, 0 means no tile
      result += RND.nextInt(mImageCount) * mTilesCount;
      return result;
    }

    /**
     * Given a ArrayList of coordinates, we need to flatten them into an array of
     * ints before we can stuff them into a map for flattening and storage.
     *
     * @param cvec : a ArrayList of Coordinate objects
     * @return : a simple array containing the x/y values of the coordinates
     * as [x1,y1,x2,y2,x3,y3...]
     */
    private int[] coordArrayListToArray(ArrayList<Coordinate> cvec) {
        int count = cvec.size();
        int[] rawArray = new int[count * 2];
        for (int index = 0; index < count; index++) {
            Coordinate c = cvec.get(index);
            rawArray[2 * index] = c.x;
            rawArray[2 * index + 1] = c.y;
        }
        return rawArray;
    }

    /**
     * Saves state
     * @return a Bundle with this view's state
     */
    public Bundle saveState() {
        Bundle map = new Bundle();
        map.putBoolean("mScore", Boolean.valueOf(mScore));
        map.putIntArray("mPuzzleCoordsl", coordArrayListToArray(mPuzzleCoords1));
        return map;
    }


    public void restoreState(Bundle map) {

      mScore = map.getBoolean("mScore");
      mPuzzleCoords1 = coordArrayToArrayList(map.getIntArray("mPuzzleCoords1"));
    }

    /**
     * Updates the current mode of the application (STATE_RUNNING or PAUSED or the like)
     * as well as sets the visibility of textview for notification
     *
     * @param newMode
     */
    public void setMode(int newMode) {
        int oldMode = mMode;
        mMode = newMode;

        if (newMode == STATE_RUNNING & oldMode != STATE_RUNNING) {
            mStatusText.setVisibility(View.INVISIBLE);
            update();
            return;
        }

        Resources res = getContext().getResources();
        CharSequence str = "";
        if (newMode == STATE_PAUSE) {
          str = res.getText(R.string.mode_paused);
      }

        if (newMode == STATE_FINISHED) {
            str = res.getString(R.string.mode_finished);
        }

        mStatusText.setText(str);
        mStatusText.setVisibility(View.VISIBLE);
    }

    private void update() {

      if (mMode == STATE_RUNNING) {
        long now = System.currentTimeMillis();

        if (now - mLastMove > mMoveDelay) {
            updateScore();
            mLastMove = now;
        }
        mRedrawHandler.sleep(mMoveDelay);
    }


    }

    private void updateScore() {
    }

    /**
     * Given a flattened array of ordinate pairs, we reconstitute them into a
     * ArrayList of Coordinate objects
     *
     * @param rawArray : [x1,y1,x2,y2,...]
     * @return a ArrayList of Coordinates
     */
    private ArrayList<Coordinate> coordArrayToArrayList(int[] rawArray) {
        ArrayList<Coordinate> coordArrayList = new ArrayList<Coordinate>();

        int coordCount = rawArray.length;
        for (int index = 0; index < coordCount; index += 2) {
            Coordinate c = new Coordinate(rawArray[index], rawArray[index + 1]);
            coordArrayList.add(c);
        }
        return coordArrayList;
    }


        /**
     * Simple class containing two integer values and a comparison function.
     * There's probably something I should use instead, but this was quick and
     * easy to build.
     *
     */
    private class Coordinate {
        public int x;
        public int y;

        public Coordinate(int newX, int newY) {
            x = newX;
            y = newY;
        }

        public boolean equals(Coordinate other) {
            if (x == other.x && y == other.y) {
                return true;
            }
            return false;
        }

        @Override
        public String toString() {
            return "Coordinate: [" + x + "," + y + "]";
        }
    }


  public void setTextView(TextView findViewById) {
    mStatusText = findViewById;

  }

}
