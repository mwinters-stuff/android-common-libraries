package nz.org.winters.android.unlockchecker;

import android.app.Service;
import android.content.Intent;
import android.os.*;
import nz.org.winters.android.unlockchecker.gson.TrialPojo;
import nz.org.winters.android.unlockchecker.gson.TrialResponsePojo;
import nz.org.winters.android.unlockchecker.gson.UnlockAppService;
import nz.org.winters.android.unlockchecker.gson.UnlockResponsePojo;
import retrofit.RestAdapter;

import java.util.Date;
import java.util.UUID;

/**
 * Created by mathew on 4/11/13.
 */
public class TrialService extends Service
{

  public              String EXTRA_PACKAGE_NAME        = "nz.org.winters.android.unlockchecker.EXTRA_PACKAGE_NAME";
  public              String EXTRA_APP_KEY             = "nz.org.winters.android.unlockchecker.EXTRA_APP_KEY";
  public              String EXTRA_TRIAL_URL           = "nz.org.winters.android.unlockchecker.EXTRA_TRIAL_URL";
  public static final String EXTRA_TRIAL_CHECK_DATE    = "nz.org.winters.android.unlockchecker.EXTRA_TRIAL_CHECK_DATE";
  public static final String EXTRA_TRIAL_CHECK_STATUS  = "nz.org.winters.android.unlockchecker.TrialService.EXTRA_TRIAL_CHECK_STATUS";
  public static final String EXTRA_TRIAL_CHECK_MESSAGE = "nz.org.winters.android.unlockchecker.TrialService.EXTRA_TRIAL_CHECK_MESSAGE";
  public static final String EXTRA_TRIAL_SCRIPT        = "nz.org.winters.android.unlockchecker.TrialService.EXTRA_TRIAL_SCRIPT";

  public String INTENT_TRIAL_CHECK_START = "nz.org.winters.android.unlockchecker.INTENT_TRIAL_CHECK_START";
  public String INTENT_TRIAL_CHECK_END = "nz.org.winters.android.unlockchecker.INTENT_TRIAL_CHECK_END";


  private String mPackageName;
  private String mAppKey;
  private String mURL;

  private TrialStatus mTrialStatus = TrialStatus.notchecked;
  public         long   mRequestId;
  private static String mErrorMessage;
  private static Date   mExpireDate;


  public enum TrialStatus
  {
    notchecked,
    start,
    trial,
    expired,
    Error;

    public static TrialStatus fromInt(int value)
    {
      for (TrialStatus v : TrialStatus.values())
      {
        if (v.ordinal() == value)
          return v;
      }
      return Error;
    }
  }

  ;

  @Override
  public int onStartCommand(Intent intent, int flags, int startId)
  {

    mPackageName = intent.getStringExtra(EXTRA_PACKAGE_NAME);
    mAppKey = intent.getStringExtra(EXTRA_APP_KEY);
    mURL = intent.getStringExtra(EXTRA_TRIAL_URL);

    DoUnlockTask task = new DoUnlockTask();

    task.execute();

    stopSelf();

    return START_STICKY;
  }

  private class DoUnlockTask  extends AsyncTask<Void, Void, Void>
  {
    @Override
    protected void onPreExecute()
    {
      Intent i = new Intent();
      i.setAction(INTENT_TRIAL_CHECK_START);
      getBaseContext().sendBroadcast(i);
    }
    @Override
    protected Void doInBackground(Void... params)
    {
      try
      {
        DeviceUuidFactory uf = new DeviceUuidFactory(getBaseContext());
        UUID uuid = uf.getDeviceUuid();

        mErrorMessage = "";
        String mUniqueId = Long.toHexString(uuid.getLeastSignificantBits()) + Long.toHexString(uuid.getMostSignificantBits());

        // Log.i("CHECK",mClassName);
        // DatabaseHelper.log(getBaseContext(), "Trial Check: " + mUniqueId,
        // DatabaseHelper.LogType.Info);
        // Log.i("CHECK",mKey);
        //

        TrialPojo request = new TrialPojo();
        request.package_name = getPackageName();
        request.phoneid = mUniqueId;
        mRequestId = SystemClock.elapsedRealtime();
        request.request_id = Long.toHexString(mRequestId);

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(mURL)
                .build();

        UnlockAppService service = restAdapter.create(UnlockAppService.class);

        TrialResponsePojo trialResponsePojo = service.postTrialRequest(request);


        if (trialResponsePojo != null)
        {

          if (!request.request_id.equals(trialResponsePojo.request_id))
          {
            throw new Exception("Protocol Error: request_id mismatch!");
          }

          switch (trialResponsePojo.response)
          {
            case continuing:
              mTrialStatus = TrialStatus.trial;
              mExpireDate = trialResponsePojo.date;
              break;
            case expired:
              mTrialStatus = TrialStatus.expired;
              mExpireDate = trialResponsePojo.date;
              break;
            case fail:
              mTrialStatus = TrialStatus.Error;
              mErrorMessage = trialResponsePojo.message;
              break;
            case start:
              mTrialStatus = TrialStatus.start;
              mExpireDate = trialResponsePojo.date;
              break;
          }
        } else
        {
          throw new Exception("Incorrect Server Response");
        }

      } catch (Exception e)
      {
        // //Log.d("CHECK", "ERROR: " + e.getMessage());
        mTrialStatus = TrialStatus.Error;
        mErrorMessage = e.getMessage();
      }
      return null;


    }

    protected void onPostExecute(Void v)
    {
      Intent i = new Intent();
      i.setAction(INTENT_TRIAL_CHECK_END);
      i.putExtra(EXTRA_TRIAL_CHECK_DATE, mExpireDate);
      i.putExtra(EXTRA_TRIAL_CHECK_STATUS, mTrialStatus);
      i.putExtra(EXTRA_TRIAL_CHECK_MESSAGE, mErrorMessage);
      getBaseContext().sendBroadcast(i);
    }
  }

  @Override
  public IBinder onBind(Intent intent)
  {
    return null;
  }
}
