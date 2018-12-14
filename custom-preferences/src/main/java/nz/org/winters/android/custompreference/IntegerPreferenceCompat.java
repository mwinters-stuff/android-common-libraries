package nz.org.winters.android.custompreference;
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
import android.content.res.TypedArray;
import android.preference.Preference;
import androidx.fragment.app.FragmentActivity;
import android.util.AttributeSet;
import android.view.View;
import com.doomonafireball.betterpickers.numberpicker.NumberPickerBuilder;
import com.doomonafireball.betterpickers.numberpicker.NumberPickerDialogFragment;
import nz.org.winters.android.custompreference.R;

public class IntegerPreferenceCompat extends Preference implements NumberPickerDialogFragment.NumberPickerDialogHandler
{
  int mValue;

  public IntegerPreferenceCompat(Context context)
  {
    super(context);
    initialize();
  }

  public IntegerPreferenceCompat(Context context, AttributeSet attrs)
  {
    super(context, attrs);
    initialize();
  }

  public IntegerPreferenceCompat(Context context, AttributeSet attrs, int defStyle)
  {
    super(context, attrs, defStyle);
    initialize();
  }

  private void initialize()
  {
    setPersistent(true);
  }


  @Override
  protected Object onGetDefaultValue(TypedArray a, int index)
  {
    return a.getInt(index, (int) 0);
  }

  @Override
  protected void onSetInitialValue(boolean restoreValue, Object defaultValue)
  {
    try
    {
      setValue(restoreValue ? getPersistedInt(0) : (Integer) defaultValue);
    } catch (Exception ignore)
    {

    }
  }

  public String getFragmentTag()
  {
    return "float_" + getKey();
  }

  public int getValue()
  {
    return mValue;
  }

  public void setValue(int value)
  {
    if (callChangeListener(value))
    {
      mValue = value;
      persistInt(value);
      notifyChanged();
    }
  }


  @Override
  protected void onBindView(View view)
  {
    // TextView mSummaryView = (TextView) view.findViewById(android.R.id.summary);
    setSummary(Integer.toString(getValue()));


    super.onBindView(view);

  }


  @Override
  protected void onClick()
  {
    super.onClick();

    FragmentActivity activity = (FragmentActivity) getContext();

    NumberPickerBuilder numberPickerBuilder = new NumberPickerBuilder();
    numberPickerBuilder.setFragmentManager(activity.getSupportFragmentManager());
    numberPickerBuilder.setStyleResId(R.style.BetterPickersDialogFragment_Light);
    numberPickerBuilder.setPlusMinusVisibility(View.GONE);
    numberPickerBuilder.setDecimalVisibility(View.GONE);
    numberPickerBuilder.addNumberPickerDialogHandler(this);
    numberPickerBuilder.setLabelText(getTitle().toString());
    numberPickerBuilder.show();
  }

  @Override
  public void onDialogNumberSet(int reference, int number, double decimal, boolean isNegative, double fullNumber)
  {
    setValue(number);
  }


}
