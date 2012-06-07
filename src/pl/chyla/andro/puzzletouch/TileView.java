package pl.chyla.andro.puzzletouch;

import android.content.*;
import android.content.res.*;
import android.graphics.*;
import android.util.*;
import android.view.*;

/**
 * TileView: a View-variant designed for handling arrays of "icons" or other
 * drawables.
 *
 */
public class TileView extends View {

  /**
   * Parameters controlling the size of the tiles and their range within view.
   * Width/Height are in pixels, and Drawables will be scaled to fit to these
   * dimensions. X/Y Tile Counts are the number of tiles that will be drawn.
   */
  private static int mXOffset;
  private static int mYOffset;
  protected static int mXTilesCount;
  protected static int mYTilesCount;

  private static int mTileSizeX;
  private static int mTileSizeY;

  /**
   * A hash that maps integer handles specified by the subclasser to the
   * drawable that will be used for that reference
   */
  private Bitmap[] mTileArray;

  /**
   * A two-dimensional array of integers in which the number represents the
   * index of the tile that should be drawn at that locations
   */
  int[][] mTileGrid;

  private final Paint mPaint = new Paint();
  private int mImageWidth;
  private int mImageHeight;

  public TileView(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
    setTilesCount(context, attrs);
  }

  private void setTilesCount(Context context, AttributeSet attrs) {
    TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.TileView);

    mXTilesCount = a.getInt(R.styleable.TileView_tilesX, 3);
    mYTilesCount = a.getInt(R.styleable.TileView_tilesY, 3);
    mImageWidth = a.getInt(R.styleable.TileView_imageWidth, 480);
    mImageHeight = a.getInt(R.styleable.TileView_imageHeight, 480);
    mTileSizeX = mImageWidth / mXTilesCount;
    mTileSizeY = mImageHeight / mYTilesCount;
    mTileGrid = new int[mXTilesCount][mYTilesCount];

    a.recycle();
    requestLayout();
  }

  public TileView(Context context, AttributeSet attrs) {
    super(context, attrs);
    setTilesCount(context, attrs);
  }

  /**
   * Rests the internal array of Bitmaps used for drawing tiles, and sets the
   * maximum index of tiles to be inserted
   *
   * @param tilecount
   */

  public void resetTiles(int xCount, int yCount, int imageCount) {
    mTileArray = new Bitmap[xCount * yCount * imageCount];

  }

  @Override
  protected void onSizeChanged(int w, int h, int oldw, int oldh) {
    Log.i("KC", String.format("* -> Size changed from (%s, %s) to (%s, %s",
        oldw, oldh, w, h));
    int size = Math.min(w, h);
    mTileSizeX = (int) Math.floor(size / mXTilesCount);
    mTileSizeY = (int) Math.floor(size / mYTilesCount);

    mXOffset = ((w - (mTileSizeX * mXTilesCount)) / 2);
    mYOffset = ((h - (mTileSizeY * mYTilesCount)) / 2);

    clearTiles();

  }


  protected void loadTiles(int startIndex, int drawableId) {
    Bitmap bMap = BitmapFactory.decodeResource(getResources(), drawableId);
    Bitmap bMapScaled = Bitmap.createScaledBitmap(bMap, mImageWidth, mImageHeight, true);

    for (int x = 0; x < mXTilesCount; x++) {
      for (int y = 0; y < mYTilesCount; y++) {
        int xPos = x * mTileSizeX;
        int yPos = y * mTileSizeY;
        Bitmap bitmap = Bitmap.createBitmap(bMapScaled, xPos, yPos, mTileSizeX, mTileSizeY);
        int idx = startIndex + y * mXTilesCount + x;
        mTileArray[idx] = bitmap;
      }
    }
  }

  //todo implement|J
  /**
   *
   * @param xloc
   * @param yloc
   * @return -1 when x, y does not denote any tile
   */
  protected int getXTile(int xloc, int yloc) {
    int resIdxX = (int)Math.floor((xloc - mXOffset) / mTileSizeX);
    int resIdxY = (int)Math.floor((yloc - mYOffset) / mTileSizeY);
    return (resIdxX > 0 && resIdxY > 0) ? resIdxX : -1;
  }

  //todo implement
  protected int getYTile(int xloc, int yloc) {
    int resIdxX = (int)Math.floor((xloc - mXOffset) / mTileSizeX);
    int resIdxY = (int)Math.floor((yloc - mYOffset) / mTileSizeY);
    return (resIdxX > 0 && resIdxY > 0) ? resIdxY : -1;

  }
  /**
   * Resets all tiles to 0 (empty)
   *
   */
  public void clearTiles() {
    for (int x = 0; x < mXTilesCount; x++) {
      for (int y = 0; y < mYTilesCount; y++) {
        setTile(0, x, y);
      }
    }
    Log.i("KC", "Tiles clean");
  }

  public void setTile(int tileindex, int x, int y) {
    mTileGrid[x][y] = tileindex;
  }

  @Override
  public void onDraw(Canvas canvas) {
    super.onDraw(canvas);
    for (int x = 0; x < mXTilesCount; x += 1) {
      for (int y = 0; y < mYTilesCount; y += 1) {
        if (mTileGrid[x][y] > 0) {
          int px = mXOffset + x * mTileSizeX;
          int py = mYOffset + y * mTileSizeY;
          canvas.drawBitmap(mTileArray[mTileGrid[x][y] - 1], px, py, mPaint);
        }
      }
    }

  }

}
