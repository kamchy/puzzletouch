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

import java.util.Random;

import android.content.Context;
import android.content.res.Resources;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

/**
 * PuzzleView: implementation of a simple game of Puzzle
 */
public class PuzzleView extends TileView {


  // game states
  static final int STATE_RUNNING = 1;
  static final int STATE_READY = 2;
  static final int STATE_FINISHED = 3;
  static final int STATE_PAUSE = 4;

  
  private int mMode = 0;
  
  private int mAllTilesCount;

  /**
   * mStatusText: text shows to the user in some run states
   */
  private TextView mStatusText;
  private static int[] animals = new int[] { (R.drawable.konik), (R.drawable.kot),
      (R.drawable.piesek), };
  
  private static int[] toys = new int[] { (R.drawable.lyzeczka), (R.drawable.pileczka),
      (R.drawable.autko), };

  private static int[] nobo = new int[] { (R.drawable.laka), (R.drawable.noboibrat),
    (R.drawable.nobokonik), };

  private static int[][] puzzles = new int[][]{
    toys, animals, nobo };
  
  private int currentId = 0;
  
  private static final Random RND = new Random();
  private static final long mMoveDelay = 10;

  private static int mImageCount;

  /**
   * Constructs a PuzzleView based on inflation from XML
   *
   * @param context
   * @param attrs
   */
  public PuzzleView(Context context, AttributeSet attrs) {
    super(context, attrs);
    initTiles();
  }

  void initTiles() {
    setFocusable(true);

    initTilesFromResources(getRandomPuzzleArray());

    shuffleTiles();
  }

  private int[] getRandomPuzzleArray() {
    currentId = (currentId  + 1) % puzzles.length;
    return puzzles[currentId];
  }

  private void initTilesFromResources(int[] drawableIds) {
    mImageCount = drawableIds.length;
    mAllTilesCount = mTilesInImageCount * mImageCount;
    resetTiles(mXTilesCount, mYTilesCount, mImageCount);
    loadTiles(drawableIds);
  }
  
  public void startNewGame() {
    setMode(STATE_PAUSE);
    setMode(STATE_RUNNING);
    initTilesFromResources(getRandomPuzzleArray());
    shuffleTiles();
  }

  @Override
  public boolean onTouchEvent(MotionEvent event) {
    int x = (int) event.getX();
    int y = (int) event.getY();
    switch (event.getAction()) {
    case MotionEvent.ACTION_DOWN:
    case MotionEvent.ACTION_MOVE:
    case MotionEvent.ACTION_UP:
      Coordinate coord = getCoordForTouchpoint(x, y);
      if ((coord != null) && (mMode == STATE_RUNNING)) {
        int currentTile = getTile(coord.x, coord.y);
        int nextTileIndex = getNextTileIndex(currentTile);
        setTile(nextTileIndex, coord.x, coord.y);
        updateScore();
      } else {
        startNewGame();
      }
      
     default: ;
    }
    update();
    return false;
  }

  private int getNextTileIndex(int currentTile) {
    int nextIdx = (currentTile + mTilesInImageCount) % mAllTilesCount;
    if (nextIdx == 0) {
      nextIdx = mAllTilesCount;
    }
    return nextIdx;
  }

  private Coordinate getCoordForTouchpoint(int x, int y) {
    
    Coordinate tilesCoord = getTileCoordForPos(x,y);
    if (tilesCoord != null && (tilesCoord.x < 0 || tilesCoord.y < 0 || tilesCoord.x >= mXTilesCount || tilesCoord.y >= mYTilesCount)) {
      return null;
    }
    return tilesCoord;
  }

  @Override
  protected void onSizeChanged(int w, int h, int oldw, int oldh) {
    super.onSizeChanged(w, h, oldw, oldh);
    initTiles();
  }

  void shuffleTiles() {
    for (int x = 0; x < mXTilesCount; x++) {
      for (int y = 0; y < mYTilesCount; y++) {
        int tileIndex = getRandomIndexFor(x, y);
        setTile(tileIndex, x, y);
      }
    }
  }

  private RefreshHandler mRedrawHandler = new RefreshHandler();

  

  class RefreshHandler extends Handler {

    @Override
    public void handleMessage(Message msg) {
      PuzzleView.this.invalidate();
    }

    public void sleep(long delayMillis) {
      this.removeMessages(0);
      sendMessageDelayed(obtainMessage(0), delayMillis);
    }
  };

  /**
   * Generates index value (as used in mTileArray) for given tile location x y.
   *
   * @param x
   *          index of tile (between 0 and mXTilesCount - 1)
   * @param y
   *          index of tile (between 0 and mYTilesCount - 1)
   * @return index in mTileArray that point to a bitmap which will be drawn at
   *         location x, y
   */
  private int getRandomIndexFor(int x, int y) {
    int firstImageIndex = y * mXTilesCount + x + 1; // 1-based, 0 means no tile
    int delta = RND.nextInt(mImageCount) * mTilesInImageCount;
    return firstImageIndex + delta;
  }


  /**
   * Updates the current mode of the application (STATE_RUNNING or PAUSED or the
   * like) as well as sets the visibility of textview for notification
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

    if (newMode == STATE_FINISHED) {
      showFinishMessage();
    }

  }

  private void showFinishMessage() {
    Resources res = getContext().getResources();
    CharSequence str = "";

    str = res.getString(R.string.mode_finished);
    mStatusText.setText(str);
    mStatusText.setVisibility(View.VISIBLE);
  }

  private void update() {
    //if (mMode == STATE_RUNNING) {
      mRedrawHandler.sleep(mMoveDelay);
    //}
  }

  private void updateScore() {
    if (isGrowingIndexValue()) {
      setMode(STATE_FINISHED);
    }
  }


  public void setTextView(TextView findViewById) {
    mStatusText = findViewById;

  }

}
