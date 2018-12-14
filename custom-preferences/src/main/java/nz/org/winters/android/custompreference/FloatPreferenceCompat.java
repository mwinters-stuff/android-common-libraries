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
import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.preference.Preference;
import androidx.fragment.app.FragmentActivity;
import android.util.AttributeSet;
import android.view.View;
import com.doomonafireball.betterpickers.numberpicker.NumberPickerBuilder;
import com.doomonafireball.betterpickers.numberpicker.NumberPickerDialogFragment;
import nz.org.winters.android.custompreference.R;

public class FloatPreferenceCompat extends Preference implements NumberPickerDialogFragment.NumberPickerDialogHandler
{

  float mValue;

  public FloatPreferenceCompat(Context context)
  {
    super(context);
    initialize();
  }

  public FloatPreferenceCompat(Context context, AttributeSet attrs)
  {
    super(context, attrs);
    initialize();
  }

  public FloatPreferenceCompat(Context context, AttributeSet attrs, int defStyle)
  {
    super(context, attrs, defStyle);
    initialize();
  }

  private void initialize()
  {
    setPersistent(true);
  }
  
  @Override
  protected Object onGetDefaultValue(TypedArray a, int index) {
      return a.getFloat(index,(float)0.0);
  }

  @Override
  protected void onSetInitialValue(boolean restoreValue, Object defaultValue)
  {
    setValue(restoreValue ? getPersistedFloat(0) : (Float) defaultValue);
  }

  public String getFragmentTag()
  {
    return "float_" + getKey();
  }

  public float getValue()
  {
    return mValue;
  }

  public void setValue(float value) {
    if (callChangeListener(value)) {
        mValue = value;
        persistFloat(value);
        notifyChanged();
    }
  }


  @Override
  protected void onBindView(View view)
  {
   // TextView mSummaryView = (TextView) view.findViewById(android.R.id.summary);
    setSummary(Float.toString(getValue()));

    
    super.onBindView(view);

  }


  @Override
  protected void onClick() {
    super.onClick();

    FragmentActivity activity = (FragmentActivity) getContext();

    NumberPickerBuilder numberPickerBuilder = new NumberPickerBuilder();
    numberPickerBuilder.setFragmentManager(activity.getSupportFragmentManager());
    numberPickerBuilder.setStyleResId(R.style.BetterPickersDialogFragment_Light);
    numberPickerBuilder.setPlusMinusVisibility(View.GONE);
    numberPickerBuilder.setDecimalVisibility(View.VISIBLE);
    numberPickerBuilder.addNumberPickerDialogHandler(this);
    numberPickerBuilder.setLabelText(getTitle().toString());
    numberPickerBuilder.show();
  }

  @Override
  public void onDialogNumberSet(int reference, int number, double decimal, boolean isNegative, double fullNumber)
  {
    setValue((float)fullNumber);
  }



}
