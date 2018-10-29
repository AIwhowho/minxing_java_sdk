package com.minxing.client.utils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;

public class HMACSHA1 {
	private static final String HMAC_SHA1_ALGORITHM = "HmacSHA1";

	public static String getSignature(String data,String key) {  
		byte[] rawHmac = null;
		try {
			byte[] keyBytes=key.getBytes();  
			SecretKeySpec signingKey = new SecretKeySpec(keyBytes, HMAC_SHA1_ALGORITHM);     
			Mac mac = Mac.getInstance(HMAC_SHA1_ALGORITHM);     
			mac.init(signingKey);     
			rawHmac = mac.doFinal(data.getBytes());
		} catch(Exception e) {
			e.printStackTrace();
		}
		return new String(Base64.encodeBase64(rawHmac));
	}  
	
	public static void main(String[] args) throws Exception {
		String h = HMACSHA1.getSignature("http://example.com/api/v1/conversations/ocu_messages?timestamp=1403160595265", "ece9d330a116b878a119aa3deb4e30e2");
		System.out.println(h);
	}
}
