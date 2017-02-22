package nz.org.winters.android.unlockchecker;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

import nz.org.winters.android.unlockchecker.gson.UnlockAppService;
import nz.org.winters.android.unlockchecker.gson.UnlockPojo;
import nz.org.winters.android.unlockchecker.gson.UnlockResponsePojo;
import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Base64;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import retrofit.RestAdapter;

public class UnlockService extends Service
{
  public enum UnlockStatus
  {
    notChecked,
    Unlocked,
    Disabled,
    Error,
    SelfReset,
    NoSelfReset,
    Expired,
    Invalid;

    public static UnlockStatus fromInt(int value)
    {
      for (UnlockStatus v : UnlockStatus.values())
      {
        if (v.ordinal() == value)
          return v;
      }
      return Error;
    }
  };

  private static final double GSON_VERSION      = 1.0;

  public static final String  EXTRA_KEY         = "KEY";
  public static final String  EXTRA_FORCE_RESET = "FORCE_RESET";

  public static final String  EXTRA_URL         = "URL";
  // static final String BASE_URL =
  // "http://www.winters.org.nz/android/special.php";
  private String              mKey;
  private boolean             mForceReset;
  private UnlockStatus        mUnlockStatus     = UnlockStatus.notChecked;
  private String              mURL;

  public long mRequestId;
  private static String       mErrorMessage;
  private static Handler      mResponseHandler;
  private static int          mNumSelfResets;

  @Override
  public int onStartCommand(Intent intent, int flags, int startId)
  {

    mKey = intent.getStringExtra(EXTRA_KEY);
    mForceReset = intent.getBooleanExtra(EXTRA_FORCE_RESET, false);
    if (intent.hasExtra(EXTRA_URL))
    {
      mURL = intent.getStringExtra(EXTRA_URL);
    } else
    {
      Message message = new Message();
      message.what = UnlockStatus.Error.ordinal();
      message.obj = "Need EXTRA_URL";
      if (mResponseHandler != null)
      {
        mResponseHandler.dispatchMessage(message);
      }
    }

    DoCheckTask task = new DoCheckTask();

    task.execute();

    stopSelf();

    return START_STICKY;
  }

  public static void setResponseHandler(Handler handler)
  {
    mResponseHandler = handler;
  }

  public static String getErrorMessage()
  {
    return mErrorMessage;
  }

  public static int getNumSelfResets()
  {
    return mNumSelfResets;
  }

  @Override
  public IBinder onBind(Intent arg0)
  {
    // TODO Auto-generated method stub
    return null;
  }

  private class DoCheckTask extends AsyncTask<Void, Void, Void>
  {

    private String mUniqueId;
    private String mClassName;

    @Override
    protected void onPreExecute()
    {

    }

    @Override
    protected Void doInBackground(Void... params)
    {
      try
      {
        DeviceUuidFactory uf = new DeviceUuidFactory(getBaseContext());
        UUID uuid = uf.getDeviceUuid();
        
        UnlockPojo unlockrequest = new UnlockPojo();
        
        unlockrequest.package_name = getPackageName();
        mErrorMessage = "";

        unlockrequest.phoneid = Long.toHexString(uuid.getLeastSignificantBits()) + Long.toHexString(uuid.getMostSignificantBits());
        unlockrequest.unlockkey = mKey;
        mRequestId = SystemClock.elapsedRealtime();
        unlockrequest.request_id = Long.toHexString(mRequestId);
        unlockrequest.selfReset = mForceReset;
        

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(mURL)
                .build();

        UnlockAppService service = restAdapter.create(UnlockAppService.class);

        UnlockResponsePojo unlockResponsePojo = service.postUnlockRequest(unlockrequest);
        if(unlockResponsePojo != null)
        {
          
          if(!unlockrequest.request_id.equals(unlockResponsePojo.request_id))
          {
            throw new Exception("Protocol Error: request_id mismatch!");
          }
          
        
           switch(unlockResponsePojo.response)
           {
            case expired:
              mUnlockStatus = UnlockStatus.Expired;
              mErrorMessage = unlockResponsePojo.message;
              break;
            case happy:
            case extrahappy:
                mUnlockStatus = UnlockStatus.Unlocked;
              break;
            case fail:
              mUnlockStatus = UnlockStatus.Error;
              mErrorMessage = unlockResponsePojo.message;
              break;
            case invalid:
              mUnlockStatus = UnlockStatus.Invalid;
              mErrorMessage = unlockResponsePojo.message;
              break;
            case nounlocks:
              mUnlockStatus = UnlockStatus.NoSelfReset;
              mErrorMessage = "No remaining key resets, please email android@winters.org.nz with your key and app name to request a total reset.";
              break;
            case disabled:
              mUnlockStatus = UnlockStatus.Disabled;
              mErrorMessage = "UNLOCK KEY DISABLED";
              break;
            case incorrectphone:
              mNumSelfResets = unlockResponsePojo.selfResets;

              if (mNumSelfResets == 0)
              {
                mUnlockStatus = UnlockStatus.NoSelfReset;
                mErrorMessage = "No remaining key resets, please email android@winters.org.nz with your key and app name to request a total reset.";
              } else
              {
                mUnlockStatus = UnlockStatus.SelfReset;
                mErrorMessage = "This device has a different unique id to the one registered on your unlock key.\nThis can happen on a device reset or rom upgrade, or phone upgrade.\n" + "You can reset the key to this device, do you want to do this?\nYou have " + mNumSelfResets
                    + " changes you can do yourself.";
              }
              break;
            default:
              break;
             
           }
        }
        
      
      } catch (Exception e)
      {
        // Log.d("CHECK", "ERROR: " + e.getMessage());
        mUnlockStatus = UnlockStatus.Error;
        mErrorMessage = e.getMessage();
        if (!TextUtils.isEmpty(mErrorMessage) && mErrorMessage.contains("winters"))
        {
          mErrorMessage = "Network Error, Check Connection!";
        }else
        {
          mErrorMessage = "Unknown Error!";
        }
      }
      stopSelf();
      return null;
    }

    private String readStream(InputStream in)
    {

      try
      {
        String out = "";
        byte[] buffer = new byte[2000];
        int read;
        read = in.read(buffer);
        while (read >= 0)
        {
          if (read > 0)
          {
            out = out + new String(buffer);
          }
          buffer = new byte[2000];
          read = in.read(buffer);
        }
        // out = out + new String(buffer);
        return out;
      } catch (IOException e)
      {
        // TODO Auto-generated catch block
        e.printStackTrace();
        return "";
      }
    }

    // private String mErrorMessage = "";

    protected void onPostExecute(Void v)
    {
      Message message = new Message();
      message.what = mUnlockStatus.ordinal();
      message.obj = mErrorMessage;
      if (mResponseHandler != null)
      {
        mResponseHandler.dispatchMessage(message);
      }
    }
  }

  public static String MD5_Hash(String s)
  {
    MessageDigest m = null;
    try
    {
      m = MessageDigest.getInstance("MD5");
    } catch (NoSuchAlgorithmException e)
    {
      e.printStackTrace();
    }

    m.update(s.getBytes(), 0, s.length());
    String hash = new BigInteger(1, m.digest()).toString(16);
    while (hash.length() < 32)
    {
      hash = "0" + hash;
    }
    return hash;
  }

}
