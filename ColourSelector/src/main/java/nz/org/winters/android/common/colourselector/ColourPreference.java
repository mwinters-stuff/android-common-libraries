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
import android.graphics.Color;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

public class ColourPreference extends DialogPreference implements OnColorChangedListener
{

  private TextView  mSummaryView;
  ColorSelectorView mSelectorView;

  public ColourPreference(Context context, AttributeSet attrs)
  {
    super(context, attrs);
    initialize();
  }

  public ColourPreference(Context context, AttributeSet attrs, int defStyle)
  {
    super(context, attrs, defStyle);
    initialize();
  }

  private void initialize()
  {
    setPersistent(true);
//    setPositiveButtonText(R.string.color_new_color);
//    setNegativeButtonText(R.string.color_old_color);
  }

  private String getSummaryColour(int colour)
  {
    if (BigColour.indexOf(colour) < 0)
    {
      return "RGB #" + Integer.toHexString(colour);
    } else
    {
      return BigColour.getColour(colour).readableName();
    }
  }

  @Override
  public CharSequence getSummary()
  {
    int colour = getColour();
    if (mSummaryView != null && colour != Color.BLACK)
    {
      mSummaryView.setTextColor(colour);
    }
    return getSummaryColour(colour);
  }

  @Override
  protected void onBindView(View view)
  {
    mSummaryView = (TextView) view.findViewById(android.R.id.summary);
    setSummary(getSummaryColour(getColour()));

    super.onBindView(view);

  }

  @Override
  protected void onBindDialogView(View view)
  {
    super.onBindDialogView(view);
  }

  @Override
  protected void onDialogClosed(boolean positiveResult)
  {
    if (positiveResult)
    {
      int color = mSelectorView.getColor();
      persistInt(color);
      setSummary(getSummaryColour(color));
    }
    super.onDialogClosed(positiveResult);
  }

  public int getColour()
  {
    return getPersistedInt(BigColour.HotPink.RGB());
  }

  @Override
  protected View onCreateDialogView()
  {
    mSelectorView = new ColorSelectorView(getContext());
    mSelectorView.setColor(getColour());
    mSelectorView.setOnColorChangedListener(this);
    return mSelectorView;
  }

  @Override
  public void colorChanged(int color)
  {
    // TODO Auto-generated method stub

  }
  

}
