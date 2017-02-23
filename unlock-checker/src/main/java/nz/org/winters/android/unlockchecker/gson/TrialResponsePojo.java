package nz.org.winters.android.unlockchecker.gson;

import java.util.Date;

public class TrialResponsePojo
{
  @Override
  public String toString()
  {
    return "TrialResponsePojo [request_id=" + request_id + ", message=" + message + ", date=" + date + ", response=" + response + "]";
  }
  public String request_id;
  public String message;
  public Date date;
  public enum ResponseType{fail,start,continuing, expired};
  public ResponseType response;
}
