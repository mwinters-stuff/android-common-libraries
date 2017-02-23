package nz.org.winters.android.common.colourselector;
/*
 * Copyright 2013 Mathew Winters

 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 vYou may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.NinePatchDrawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.FrameLayout;

public class GridColorValueView extends FrameLayout
{
  private Paint                     paint;

  private Bitmap                    drawCache        = null;
  private Bitmap                    selectorBitmap;
  // private ImageView selectorView;

  private int                       lastMeasuredSize = -1;

  private int                       mNumX;
  private int                       mNumY;

  private int                       mCellSizeX;
  private int                       mCellSizeY;

  private int                       selectorX, selectorY;

  private boolean                   firstDraw        = true;
  private OnGSVColorChangedListener mOnGSVColorChangedListener;
  private ColorKeeper               mColorKeeper;
  private OnColorChangedListener    mOnColorChangedListener;

  public interface OnGSVColorChangedListener
  {
    public void colorChanged(String name);
  }

  public GridColorValueView(Context context)
  {
    super(context);
    init();
  }

  public void setColorKeeper(ColorKeeper keeper)
  {
    mColorKeeper = keeper;
  }

  public GridColorValueView(Context context, AttributeSet attrs)
  {
    super(context, attrs);
    init();
  }

  public GridColorValueView(Context context, AttributeSet attrs, int defStyle)
  {
    super(context, attrs, defStyle);
    init();
  }

  private void init()
  {
    // colorSelector =
    // getContext().getResources().getDrawable(R.drawable.grid_selector);
    // selectorView = new ImageView(getContext());
    // selectorView.setImageDrawable(colorSelector);
    // addView(selectorView, new LayoutParams(colorSelector.getIntrinsicWidth(),
    // colorSelector.getIntrinsicHeight()));
    setWillNotDraw(false);
  }

  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
  {
    super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    lastMeasuredSize = Math.min(getMeasuredHeight(), getMeasuredWidth());
    setMeasuredDimension(lastMeasuredSize, lastMeasuredSize);
    if (drawCache != null && drawCache.getHeight() != getHeight())
    {
      drawCache.recycle();
      drawCache = null;
    }
  }

  public int getBackgroundOffset()
  {
    return 0;// (int) Math.ceil((double) (colorSelector.getIntrinsicHeight() /
             // 2.f));
  }

  // private int getBackgroundSize(int availableSize)
  // {
  // int offset = getBackgroundOffset();
  // return availableSize - 2 * offset;
  // }
  //
  // public int getBackgroundSize()
  // {
  // if (drawCache != null)
  // {
  // ensureCache();
  // return drawCache.getHeight();
  // }
  // return 0;
  // }

  private void ensureCache()
  {
    if (paint == null)
    {
      paint = new Paint();
    }
    // int baseSize = getHeight();
    // if (baseSize <= 0)
    // baseSize = getMeasuredHeight();
    // if (baseSize <= 0)
    // baseSize = lastMeasuredSize;
    // int backgroundSize = getBackgroundSize(baseSize);
    if (drawCache == null && getHeight() > 0)
    {

      mNumX = 10;
      mNumY = (BigColour.values().length / mNumX);
      // Log.d("COLORS",BigColour.values().length + ", " + mNumY);

      mCellSizeX = (getWidth() / mNumX);// - 2;
      mCellSizeY = getHeight() / mNumY;

      drawCache = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
      Canvas cacheCanvas = new Canvas(drawCache);
      BigColour[] colours = BigColour.values();
      int colourIndex = 0;
      paint.setStyle(Paint.Style.FILL_AND_STROKE);

      for (int y = 0; y < mNumY; y++)
      {
        for (int x = 0; x < mNumX; x++)
        {
          paint.setColor(colours[colourIndex++].RGB());
          cacheCanvas.drawRect(new Rect(x * mCellSizeX, y * mCellSizeY, (x * mCellSizeX) + mCellSizeX, (y * mCellSizeY) + mCellSizeY), paint);
        }
      }
      selectorBitmap = Bitmap.createBitmap(mCellSizeX, mCellSizeY, Bitmap.Config.ARGB_8888);
      NinePatchDrawable selectornine = (NinePatchDrawable) getResources().getDrawable(R.drawable.grid_selector);
      if (selectornine != null)
      {
        selectornine.setBounds(0, 0, mCellSizeX, mCellSizeY);
        Canvas selectorCanvas = new Canvas(selectorBitmap);
        selectornine.draw(selectorCanvas);
      }

    }
  }

  @Override
  protected void onDraw(Canvas canvas)
  {
    super.onDraw(canvas);
    ensureCache();
    canvas.drawBitmap(drawCache, getBackgroundOffset(), getBackgroundOffset(), paint);

    if (firstDraw)
    {
      firstDraw = false;
      placeSelector();
    }
    if (selectorX > -1 && selectorY > -1)
    {
      canvas.drawBitmap(selectorBitmap, selectorX, selectorY, paint);
    }
  }

  private boolean down = false;

  @Override
  public boolean onTouchEvent(MotionEvent event)
  {
    if (event.getAction() == MotionEvent.ACTION_DOWN)
    {
      down = true;
      return true;
    }
    if (event.getAction() == MotionEvent.ACTION_UP)
    {
      down = false;
      setSelectorPosition((int) event.getX() - getBackgroundOffset(), (int) event.getY() - getBackgroundOffset(), true);
      return true;
    }
    if (event.getAction() == MotionEvent.ACTION_MOVE && down)
    {
      setSelectorPosition((int) event.getX() - getBackgroundOffset(), (int) event.getY() - getBackgroundOffset(), false);
      return true;
    }
    return super.onTouchEvent(event);
  }

  private void setSatAndValueFromPos(int x, int y, boolean up)
  {

    if(mCellSizeX == 0 || mCellSizeY == 0)
    {
      return;
    }
    int cellx = x / mCellSizeX;
    int celly = y / mCellSizeY;

    int cell = cellx + (celly * mNumX);
    if (cell >= 0 && cell < BigColour.values().length)
    {
      BigColour col = BigColour.values()[cell];

      mColorKeeper.set(col.RGB());
      if (mOnColorChangedListener != null)
      {
        mOnColorChangedListener.colorChanged(col.RGB());
      }
    }

    // Intent intent = new Intent();
    // intent.setAction(ColorSelectorDialog.INTENT_COLOR_CHANGED);
    // getContext().sendBroadcast(intent);

    // onValueChanged(up);
  }

  private void setSelectorPosition(int x, int y, boolean up)
  {
    setSatAndValueFromPos(x, y, up);
    placeSelector();
  }

  private void placeSelector()
  {
    if (mNumX == 0 || mNumY == 0 || mColorKeeper == null)
    {
      return;
    }

    int index = BigColour.indexOf(mColorKeeper.get());
    int x, y;
    if (index < 0)
    {
      selectorX = -1;
      selectorY = -1;

      if (mOnGSVColorChangedListener != null)
      {
        mOnGSVColorChangedListener.colorChanged("Not in palette, RGB #" + Integer.toHexString(mColorKeeper.get()));
      }

      return;
    } else
    {
      x = (int) index % mNumX;
      y = (int) index / mNumX;
    }

    int left = (x * mCellSizeX);
    int top = (y * mCellSizeY);

    // selectorView.setVisibility(View.VISIBLE);
    // selectorView.layout(left, top, left + mCellSizeX, top + mCellSizeY);

    selectorX = (x * mCellSizeX);
    selectorY = (y * mCellSizeY);

    invalidate();

    if (mOnGSVColorChangedListener != null)
    {
      mOnGSVColorChangedListener.colorChanged(BigColour.values()[index].readableName());
    }

  }

  @Override
  protected void onLayout(boolean changed, int left, int top, int right, int bottom)
  {
    super.onLayout(changed, left, top, right, bottom);
    placeSelector();
  }

  // private void setPosFromSatAndValue()
  // {
  // if (drawCache != null)
  // placeSelector();
  // }

  public void tabShow()
  {
    if (drawCache != null)
      placeSelector();
  }

  //
  // public int getValue()
  // {
  // return value;
  // }
  //
  public void setOnGSVColorChangedListener(OnGSVColorChangedListener listener)
  {
    this.mOnGSVColorChangedListener = listener;
  }

  public void setOnColorChangedListener(OnColorChangedListener listener)
  {
    this.mOnColorChangedListener = listener;
  }
  //
  // private void onValueChanged(boolean up)
  // {
  // if (listener != null)
  // listener.valueChanged(this, value, up);
  // }
  //
  // public interface OnValueChanged
  // {
  // public void valueChanged(GridColorValueView sender, int value, boolean up);
  // }
}
