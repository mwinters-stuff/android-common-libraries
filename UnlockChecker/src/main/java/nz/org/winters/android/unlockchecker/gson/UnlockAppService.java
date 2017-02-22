package nz.org.winters.android.unlockchecker.gson;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;

/**
 * Created by mathew on 4/11/13.
 */
public interface UnlockAppService
{
  @POST("/unlock")
  UnlockResponsePojo postUnlockRequest(@Body UnlockPojo unlockPojo);

  @POST("/trial")
  TrialResponsePojo postTrialRequest(@Body TrialPojo trialPojo);

  @POST("/trial")
  void postTrialRequest(@Body TrialPojo trialPojo,  Callback<TrialResponsePojo> cb);

}
