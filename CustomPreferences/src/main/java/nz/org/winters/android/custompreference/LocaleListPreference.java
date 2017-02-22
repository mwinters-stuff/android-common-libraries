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
import java.util.Locale;

import android.content.Context;
import android.preference.ListPreference;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class LocaleListPreference extends ListPreference
{
  
  private Locale[] mLocals;
  
  public LocaleListPreference(Context context, AttributeSet attrs)
  {
    super(context, attrs);
    initialize();
  }

  public LocaleListPreference(Context context)
  {
    super(context);
    initialize();
  }

  private void initialize()
  {
   setPersistent(true);

   mLocals = Locale.getAvailableLocales();
   
   String[] entries = new String[mLocals.length];
   String[] values = new String[mLocals.length];
   int i = 0;
   for(Locale local: mLocals)
   {
     values[i] = local.toString();
     entries[i] = local.getDisplayName();
     i++;
   }
   Log.d("VALUES",values.toString());
   Log.d("ENTRIES",entries.toString());
   setEntries(entries);
   setEntryValues(values);
    
  }
  
  public static Locale LocaleFromString(String localString)
  {
    String[] values = TextUtils.split(localString, "_");
    switch (values.length)
    {
      case 1:
        return new Locale(values[0]);
      case 2:
        return new Locale(values[0],values[1]);
      case 3:
        return new Locale(values[0],values[1],values[2]);
      default:
        return Locale.getDefault();
    }
  }
  
  @Override
  protected View onCreateDialogView()
  {
    View view = super.onCreateDialogView();
    
    
    return view;
  }  
  
  @Override
  protected void onDialogClosed(boolean positiveResult)
  {
    super.onDialogClosed(positiveResult);
    if(positiveResult)
    {
      setSummary(LocaleFromString((getPersistedString(Locale.getDefault().toString()))).getDisplayName() );
    }
    
  }
  
  @Override
  protected void onBindView(View view)
  {
   // TextView mSummaryView = (TextView) view.findViewById(android.R.id.summary);
    setSummary(LocaleFromString((getPersistedString(Locale.getDefault().toString()))).getDisplayName() );
    super.onBindView(view);

  }
  
}
