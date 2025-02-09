<<<<<<<< HEAD:src/main/java/org/example/application/encryption/HmacUtil.java
package org.example.application.encryption;
========
package org.encryption;
>>>>>>>> 0c91afaf11321c3615c5e84241311176c786fa2c:src/main/java/org/encryption/HmacUtil.java

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

