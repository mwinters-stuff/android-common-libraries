package nz.org.winters.android.common.appbilling;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

import nz.org.winters.android.common.appbilling.util.Base64;
import android.content.Context;

public class UtilFunctions
{
  public static String getPhoneHash(Context context, String extra)
  {
    DeviceUuidFactory uf = new DeviceUuidFactory(context);
    UUID uuid = uf.getDeviceUuid();
    String uniqueId = Long.toHexString(uuid.getLeastSignificantBits()) + Long.toHexString(uuid.getMostSignificantBits());
    
    String md5ID = MD5_Hash(uniqueId) + extra;
    
    String encoded = Base64.encode(md5ID.getBytes());
    return encoded;
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
    while(hash.length() < 32)
    {
      hash = "0" + hash;
    }
    return hash;
  }  
}
