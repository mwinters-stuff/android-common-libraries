package nz.org.winters.android.unlockchecker.gson;

public class UnlockPojo
{
  @Override
  public String toString()
  {
    return "UnlockPojo [app_key=" + app_key + ", phoneid=" + phoneid + ", unlockkey=" + unlockkey + ", package_name=" + package_name + ", request_id=" + request_id + ", selfReset=" + selfReset + "]";
  }
  public String app_key;
  public String phoneid;
  public String unlockkey;
  public String package_name;
  public String request_id;
  public boolean selfReset;

}
