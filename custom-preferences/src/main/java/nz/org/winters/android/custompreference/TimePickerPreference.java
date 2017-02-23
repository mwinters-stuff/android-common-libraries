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
import android.preference.DialogPreference;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TimePicker;

public class TimePickerPreference extends DialogPreference
{

  /**
   * The validation expression for this preference
   */
  private static final String VALIDATION_EXPRESSION = "[0-2]*[0-9]:[0-5]*[0-9]";

  /**
   * The default value for this preference
   */
  private String              defaultValue;
  /**
   * Store the original value, in case the user chooses to abort the
   * {@link DialogPreference} after making a change.
   */
  private int                 originalHour          = 0;
  /**
   * Store the original value, in case the user chooses to abort the
   * {@link DialogPreference} after making a change.
   */
  private int                 originalMinute        = 0;
  private TimePicker timePicker;

  /**
   * @param context
   * @param attrs
   */
  public TimePickerPreference(final Context context, final AttributeSet attrs)
  {
    super(context, attrs);
    initialize();
  }

  /**
   * @param context
   * @param attrs
   * @param defStyle
   */
  public TimePickerPreference(final Context context, final AttributeSet attrs, final int defStyle)
  {
    super(context, attrs, defStyle);
    initialize();
  }

  /**
   * Initialize this preference
   */
  private void initialize()
  {
    setPersistent(true);
  }

  /*
   * (non-Javadoc)
   * 
   * @see android.preference.DialogPreference#onCreateDialogView()
   */
  @Override
  protected View onCreateDialogView()
  {

    timePicker = new TimePicker(getContext());
    timePicker.setIs24HourView(DateFormat.is24HourFormat(getContext()));

    originalHour = getHour();
    originalMinute = getMinute();
    if (originalHour >= 0 && originalMinute >= 0)
    {
      timePicker.setCurrentHour(originalHour);
      timePicker.setCurrentMinute(originalMinute);
    }

    return timePicker;
  }



  /**
   * If not a positive result, restore the original value before going to
   * super.onDialogClosed(positiveResult).
   */
  @Override
  protected void onDialogClosed(boolean positiveResult)
  {


    if (positiveResult)
    {
      int hour = timePicker.getCurrentHour();
      int minute = timePicker.getCurrentMinute();
      persistString(String.format("%02d:%02d", hour, minute));
      callChangeListener(String.format("%02d:%02d", hour, minute));

    }else
    {
      persistString(String.format("%02d:%02d", originalHour, originalMinute));
      callChangeListener(String.format("%02d:%02d", originalHour, originalMinute));
    }
    super.onDialogClosed(positiveResult);
  }

  /**
   * @see android.preference.Preference#setDefaultValue(java.lang.Object)
   */
  @Override
  public void setDefaultValue(final Object defaultValue)
  {
    // BUG this method is never called if you use the 'android:defaultValue'
    // attribute in your XML preference file, not sure why it isn't

    super.setDefaultValue(defaultValue);

    if (!(defaultValue instanceof String))
    {
      return;
    }

    if (!((String) defaultValue).matches(VALIDATION_EXPRESSION))
    {
      return;
    }

    this.defaultValue = (String) defaultValue;
  }

  /**
   * Get the hour value (in 24 hour time)
   * 
   * @return The hour value, will be 0 to 23 (inclusive) or -1 if illegal
   */
  private int getHour()
  {
    String time = getTime();
    if (time == null || !time.matches(VALIDATION_EXPRESSION))
    {
      return -1;
    }

    return Integer.valueOf(time.split(":")[0]);
  }

  /**
   * Get the minute value
   * 
   * @return the minute value, will be 0 to 59 (inclusive) or -1 if illegal
   */
  private int getMinute()
  {
    String time = getTime();
    if (time == null || !time.matches(VALIDATION_EXPRESSION))
    {
      return -1;
    }

    return Integer.valueOf(time.split(":")[1]);
  }

  /**
   * Get the time. It is only legal, if it matches
   * {@link #VALIDATION_EXPRESSION}.
   * 
   * @return the time as hh:mm
   */
  public String getTime()
  {
    return getPersistedString(this.defaultValue);
  }

}
