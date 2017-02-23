package nz.org.winters.android.unlockchecker.gson;

public class TrialPojo
{
  @Override
  public String toString()
  {
    return "TrialPojo [package_name=" + package_name + ", phoneid=" + phoneid + ", app_key=" + app_key + ", request_id=" + request_id + "]";
  }
  public String package_name;
  public String phoneid;
  public String app_key;
  public String request_id;
}
