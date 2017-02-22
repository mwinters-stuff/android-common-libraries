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
import nz.org.winters.android.common.colourselector.GridColorValueView.OnGSVColorChangedListener;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class GridSelectorView extends LinearLayout implements OnGSVColorChangedListener, OnColorChangedListener
{
  private GridColorValueView     gridColorValueView;
  private TextView textColourName;
  private ColorKeeper mColorKeeper;
  private OnColorChangedListener mColorChangedListener;

  public GridSelectorView(Context context, ColorKeeper colorKeeper)
  {
    super(context);
    mColorKeeper = colorKeeper;
    init();
  }

  public GridSelectorView(Context context, AttributeSet attrs)
  {
    super(context, attrs);
    init();
  }

  private void init()
  {
    buildUI();
    
    
  }

  private void buildUI()
  {
    LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    View gridView = inflater.inflate(R.layout.color_grid, null);
    this.addView(gridView, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
    
    gridColorValueView = (GridColorValueView) gridView.findViewById(R.id.color_grid_value);
    gridColorValueView.setColorKeeper(mColorKeeper);
    
    gridColorValueView.setOnColorChangedListener(this);
    gridColorValueView.setOnGSVColorChangedListener(this);
    
    textColourName = (TextView) gridView.findViewById(R.id.colour_name);
    
  }

  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
  {
    super.onMeasure(widthMeasureSpec, heightMeasureSpec);
  }

  public void tabShow()
  {
    gridColorValueView.tabShow();

    
  }
  
  
  @Override
  public void colorChanged(String name)
  {
    textColourName.setText(name);
  }

  public void setOnColorChangedListener(OnColorChangedListener listener)
  {
    mColorChangedListener = listener;
    
  }

  @Override
  public void colorChanged(int color)
  {
    if(mColorChangedListener != null)
    {
      mColorChangedListener.colorChanged(color);
    }
    
  }

}
