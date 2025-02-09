package org.example.application.encryption;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class HmacUtil {
    public static byte[] generateHmac(byte[] data, String secret) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKey = new SecretKeySpec(secret.getBytes(), "HmacSHA256");
        mac.init(secretKey);
        return mac.doFinal(data);
    }
}

