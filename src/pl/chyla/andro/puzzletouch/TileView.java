package pl.chyla.andro.puzzletouch;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.view.View;

/**
 * TileView: a View-variant designed for handling arrays of "icons" or other
 * drawables.
 * 
 * Stolen from snake example.
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
  protected static int mTilesInImageCount;

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
  private Paint mFramePaint = createFramePaint(0x88ccaa, 2);

  public TileView(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
    setTilesCount(context, attrs);
  }

  private Paint createFramePaint(int color, int radius) {
    Paint mDrawPaint = new Paint(Paint.DITHER_FLAG);
    mDrawPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC)) ;
    mDrawPaint.setColor(color);
    mDrawPaint.setStyle(Style.STROKE);
    mDrawPaint.setStrokeWidth(radius);
    return mDrawPaint;
  }

  private void setTilesCount(Context context, AttributeSet attrs) {
    mXTilesCount = 3;
    mYTilesCount = 3;
    mImageWidth = 480;
    mImageHeight = 480;
    mTileSizeX = mImageWidth / mXTilesCount;
    mTileSizeY = mImageHeight / mYTilesCount;
    mTileGrid = new int[mXTilesCount][mYTilesCount];
    mTilesInImageCount = mXTilesCount * mYTilesCount;
    //requestLayout();
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
    int size = Math.min(w, h);
    
    mImageWidth = size; mImageHeight = size;
    mTileSizeX = (int) (size / mXTilesCount);
    mTileSizeY = (int) (size / mYTilesCount);

    mXOffset = ((w - (mTileSizeX * mXTilesCount)) / 2);
    mYOffset = ((h - (mTileSizeY * mYTilesCount)) / 2);

    clearTiles();

  }


  protected void loadTiles(int[] drawableIds) {
    for (int i = 0; i < drawableIds.length; i++) {
      int startIndex = i * mTilesInImageCount; 
      int drawableId = drawableIds[i];
      fillTilesArrayStartingFrom(startIndex, drawableId);
    }
  }

  private void fillTilesArrayStartingFrom(int startIndex, int drawableId) {
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

  /**
   *
   * @param xloc
   * @param yloc
   * @return -1 when x, y does not dengetTileCoordForPosiote any tile
   */
  protected Coordinate getTileCoordForPos(int xloc, int yloc) {
    float resX= (float)(xloc - mXOffset) / mTileSizeX;
    float resY = (float)(yloc - mYOffset) / mTileSizeY;
    if (resX < 0 || resY < 0) {
      return null;
    } else
      return new Coordinate((int)resX, (int)resY);
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
  }

  public void setTile(int tileindex, int x, int y) {
    mTileGrid[x][y] = tileindex;
  }

  @Override
  public void onDraw(Canvas canvas) {
    super.onDraw(canvas);
    drawTiles(canvas);
    drawFrames(canvas);

  }

  private void drawFrames(Canvas canvas) {
    for (int x = 0; x < mXTilesCount; x++) {
      for (int y = 0; y < mYTilesCount; y++) {
        int px = mXOffset + x * mTileSizeX;
        int py = mYOffset + y * mTileSizeY;
        canvas.drawRect(px, py, px + mTileSizeX, py + mTileSizeY, mFramePaint);
      }
    }
  }

  private void drawTiles(Canvas canvas) {
    for (int x = 0; x < mXTilesCount; x++) {
      for (int y = 0; y < mYTilesCount; y++) {
        if (mTileGrid[x][y] > 0) {
          int px = mXOffset + x * mTileSizeX;
          int py = mYOffset + y * mTileSizeY;
          int bitmapIndex = mTileGrid[x][y] - 1;
          Bitmap bitmap = mTileArray[bitmapIndex];
          canvas.drawBitmap(bitmap, px, py, mPaint);
          
        }
      }
    }
  }
  protected int getTile(int x, int y) {
    return mTileGrid[x][y];
  }
  protected boolean isGrowingIndexValue() {
    boolean isGrowing = true;
    int x = 0, y = 0;
    int prev = mTileGrid[0][0];
    while (isGrowing) {
      x++;
      if (x == mXTilesCount) {
        x = 0; y++;
        if (y == mYTilesCount) {
          break;
        }
      }
      int next = mTileGrid[x][y];
      isGrowing = (next == prev + 1);
      prev = next;
    }
    return isGrowing;
    
  }
  /**
   * Stolen from snake example:
   *
   */
  protected  class Coordinate {
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

}
