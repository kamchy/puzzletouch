package pl.chyla.andro.puzzletouch;

import android.content.*;
import android.content.res.*;
import android.graphics.*;
import android.graphics.drawable.*;
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

  public TileView(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
    setTilesCount(context, attrs);
  }

  private void setTilesCount(Context context, AttributeSet attrs) {
    TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.TileView);

    mXTilesCount = a.getInt(R.styleable.TileView_tilesX, 3);
    mYTilesCount = a.getInt(R.styleable.TileView_tilesY, 3);
    mTileSizeX = 200;
    mTileSizeY = 200;
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

  /**
   * Function to set the specified Drawable as the tile for a particular integer
   * key.
   *
   * @param key
   * @param tile
   */
  public void loadTile(int key, Drawable tile) {
    Bitmap bitmap = Bitmap.createBitmap(mTileSizeX, mTileSizeY,
        Bitmap.Config.ARGB_8888);
    Canvas canvas = new Canvas(bitmap);
    tile.setBounds(0, 0, mTileSizeX, mTileSizeY);
    tile.draw(canvas);

    mTileArray[key] = bitmap;
  }

  protected void loadTiles(int startIndex, Drawable drawable) {
    int len = mTileArray.length;
    Log.i("KC", String.format(
        "loadTileS: [mTileSizeX, mTileSizeY]=[%s, %s]\n[mXTilesCount, mYTilesCount]=[%s, %s]\nmTileAray.len=%s", mTileSizeX, mTileSizeY, mXTilesCount, mYTilesCount, len));

    for (int x = 0; x < mXTilesCount; x++) {
      for (int y = 0; y < mYTilesCount; y++) {
        Bitmap bitmap = Bitmap.createBitmap(mTileSizeX, mTileSizeY,
            Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        int xPos = x * mTileSizeX;
        int yPos = y * mTileSizeY;
        int right = xPos + mTileSizeX - 1;
        int bot = yPos + mTileSizeY - 1;
        drawable.setBounds(xPos, yPos, right, bot);
        drawable.draw(canvas);
        int idx = startIndex + y * mXTilesCount + x;
        mTileArray[idx] = bitmap;
        Log.i("KC", String.format(
            "[x,y]=[%s, %s]\n[xPos, yPos]=[%s, %s]\n[right, bot]=[%s, %s]", x, y, xPos, yPos, right, bot));
      }
    }
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
    int dx = 0, dy = 0;
    for (int x = 0; x < mXTilesCount; x += 1) {
      for (int y = 0; y < mYTilesCount; y += 1) {
        if (mTileGrid[x][y] > 0) {
          int px = mXOffset + x * mTileSizeX + dx;
          int py = mYOffset + y * mTileSizeY + dy;
          canvas.drawBitmap(mTileArray[mTileGrid[x][y] - 1], px, py, mPaint);
        }
      }
    }

  }

}
