package nz.org.winters.android.unlockchecker.gson;


public class UnlockResponsePojo
{
  @Override
  public String toString()
  {
    return "UnlockResponsePojo [request_id=" + request_id + ", message=" + message + ", response=" + response + ", selfResets=" + selfResets + "]";
  }
  public String request_id;
  public String message;
  
  public enum ResponseType{fail,happy,nounlocks,extrahappy,expired, invalid, incorrectphone, disabled};
  public ResponseType response;
  public int selfResets;
}

