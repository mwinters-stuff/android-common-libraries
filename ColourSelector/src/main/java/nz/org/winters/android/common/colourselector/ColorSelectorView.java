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
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabContentFactory;
import android.widget.TabHost.TabSpec;

public class ColorSelectorView extends LinearLayout implements OnTabChangeListener, OnColorChangedListener
{
  private static final String GRD_TAG   = "Grid";
  private static final String RGB_TAG   = "RGB";
  private static final String HEX_TAG   = "HEX";

  private GridSelectorView    gridSelector;
  private RgbSelectorView     rgbSelector;
  private HexSelectorView     hexSelector;
  private TabHost             tabs;
  

  private int                 maxHeight = 0;
  private int                 maxWidth  = 0;

  private ColorKeeper mColorKeeper;
  private OnColorChangedListener mOnColorChangedListener;

  public ColorSelectorView(Context context)
  {
    super(context);
    init();
  }

  public ColorSelectorView(Context context, AttributeSet attrs)
  {
    super(context, attrs);
    init();
  }

  private void init()
  {
    mColorKeeper = new ColorKeeper();
    
    LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    View contentView = inflater.inflate(R.layout.color_selector, null);

    addView(contentView, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));

    gridSelector = new GridSelectorView(getContext(),mColorKeeper);
    gridSelector.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
    gridSelector.setOnColorChangedListener(this);
    

    // hsvSelector = new HsvSelectorView(getContext());
    // hsvSelector.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
    // LayoutParams.FILL_PARENT));
    // hsvSelector.setOnColorChangedListener(new
    // HsvSelectorView.OnColorChangedListener()
    // {
    //
    // public void colorChanged(int color)
    // {
    // setColor(color);
    // }
    // });
    rgbSelector = new RgbSelectorView(getContext(),mColorKeeper);
    rgbSelector.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
    rgbSelector.setOnColorChangedListener(this);
    // rgbSelector.setOnColorChangedListener(new
    // RgbSelectorView.OnColorChangedListener()
    // {
    //
    // public void colorChanged(int color)
    // {
    // setColor(color);
    // }
    // });
    hexSelector = new HexSelectorView(getContext(),mColorKeeper);
    hexSelector.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
    hexSelector.setOnColorChangedListener(this);
    // hexSelector.setOnColorChangedListener(new
    // HexSelectorView.OnColorChangedListener()
    // {
    // public void colorChanged(int color)
    // {
    // setColor(color);
    // }
    // });

    tabs = (TabHost) contentView.findViewById(R.id.colorview_tabColors);
    tabs.setup();
    ColorTabContentFactory factory = new ColorTabContentFactory();
    TabSpec grdTab = tabs.newTabSpec(GRD_TAG).setIndicator("Palette").setContent(factory);
    // TabSpec hsvTab = tabs.newTabSpec(HSV_TAG).setIndicator("HSV",
    // getContext().getResources().getDrawable(R.drawable.hsv32)).setContent(factory);
    TabSpec rgbTab = tabs.newTabSpec(RGB_TAG).setIndicator("RGB").setContent(factory);
    TabSpec hexTab = tabs.newTabSpec(HEX_TAG).setIndicator("HEX").setContent(factory);

    tabs.addTab(grdTab);
    // tabs.addTab(hsvTab);
    tabs.addTab(rgbTab);
    tabs.addTab(hexTab);

    tabs.setOnTabChangedListener(this);
    
  }

  @Override
  public void onTabChanged(String tag)
  {
    // TODO Auto-generated method stub
    if (GRD_TAG.equals(tag))
    {
      gridSelector.tabShow();
    }
    // if (HSV_TAG.equals(tag))
    // {
    // return hsvSelector;
    // }
    if (RGB_TAG.equals(tag))
    {
      rgbSelector.tabShow();
    }
    if (HEX_TAG.equals(tag))
    {
      hexSelector.tabShow();
    }

  }

  class ColorTabContentFactory implements TabContentFactory
  {

    public View createTabContent(String tag)
    {
      if (GRD_TAG.equals(tag))
      {
        return gridSelector;
      }
      // if (HSV_TAG.equals(tag))
      // {
      // return hsvSelector;
      // }
      if (RGB_TAG.equals(tag))
      {
        return rgbSelector;
      }
      if (HEX_TAG.equals(tag))
      {
        return hexSelector;
      }
      return null;
    }
  }

   protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
  {
    super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    if (GRD_TAG.equals(tabs.getCurrentTabTag()))
    {
      maxHeight = getMeasuredHeight();
      maxWidth = getMeasuredWidth();
    }
    setMeasuredDimension(maxWidth, maxHeight);
  }

   public void setColor(int color)
   {
     mColorKeeper.set(color);
   }

   public int getColor()
   {
     return mColorKeeper.get();
   }
   
   public void setOnColorChangedListener(OnColorChangedListener listener)
  {
    mOnColorChangedListener = listener;
    
  }

  @Override
  public void colorChanged(int color)
  {
    if(mOnColorChangedListener != null)
    {
      mOnColorChangedListener.colorChanged(color);
    }
    
  }   
}
