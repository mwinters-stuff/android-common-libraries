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
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.preference.Preference;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import nz.org.winters.android.custompreference.numberpicker.NumberPickerBuilder;
import nz.org.winters.android.custompreference.numberpicker.NumberPickerDialogFragment;
import nz.org.winters.android.custompreference.R;

public class IntegerPreference extends Preference implements NumberPickerDialogFragment.NumberPickerDialogHandler
{
  int mValue;
    int mTitleResId;

  public IntegerPreference(Context context)
  {
    super(context);

  }

  public IntegerPreference(Context context, AttributeSet attrs)
  {
    super(context, attrs);
      final TypedArray a = context.obtainStyledAttributes(
              attrs, R.styleable.IntegerPreference);
      for (int i = a.getIndexCount() - 1; i >= 0; i--) {
          int attr = a.getIndex(i);
          if (attr == R.styleable.IntegerPreference_dialogTitle){
              mTitleResId = a.getResourceId(attr, 0);
              break;
          }
      }
      a.recycle();
      setPersistent(true);

  }

  public IntegerPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes)
  {
    super(context, attrs, defStyleRes);
      final TypedArray a = context.obtainStyledAttributes(
              attrs, R.styleable.IntegerPreference, defStyleAttr, defStyleRes);
      for (int i = a.getIndexCount() - 1; i >= 0; i--) {
          int attr = a.getIndex(i);
          if (attr == R.styleable.IntegerPreference_dialogTitle){
                  mTitleResId = a.getResourceId(attr, 0);
                  break;
          }
      }
      a.recycle();
      setPersistent(true);
  }



  @Override
  protected Object onGetDefaultValue(TypedArray a, int index) {
    return a.getInteger(index,(int)0);
  }

  @Override
  protected void onSetInitialValue(boolean restoreValue, Object defaultValue)
  {
    try
    {
      setValue(restoreValue ? getPersistedInt(0) : (Integer) defaultValue);
    }catch(Exception ignore)
    {

    }
  }

  public String getFragmentTag()
  {
    return "int_" + getKey();
  }

  public int getValue()
  {
    return mValue;
  }

  public void setValue(int value) {
    if (callChangeListener(value)) {
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
  protected void onClick() {
    super.onClick();

    Activity activity = (Activity) getContext();

    NumberPickerBuilder numberPickerBuilder = new NumberPickerBuilder();
    numberPickerBuilder.setFragmentManager(activity.getFragmentManager());
    numberPickerBuilder.setStyleResId(R.style.BetterPickersDialogFragment_Light);
    numberPickerBuilder.setPlusMinusVisibility(View.GONE);
    numberPickerBuilder.setDecimalVisibility(View.GONE);
    numberPickerBuilder.addNumberPickerDialogHandler(this);
    numberPickerBuilder.setLabelText(getContext().getString(mTitleResId));
    numberPickerBuilder.show();
  }

  @Override
  public void onDialogNumberSet(int reference, int number, double decimal, boolean isNegative, double fullNumber)
  {
    setValue(number);
  }




}
