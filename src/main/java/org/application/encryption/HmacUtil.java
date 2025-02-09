package org.application.encryption;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class HmacUtil {
    private static final String HMAC_ALGORITHM = "HmacSHA256";

    public static byte[] generateHmac(byte[] data, byte[] secret) throws NoSuchAlgorithmException, InvalidKeyException {
        Mac mac = Mac.getInstance(HMAC_ALGORITHM);
        SecretKeySpec secretKey = new SecretKeySpec(secret, HMAC_ALGORITHM);
        mac.init(secretKey);
        return mac.doFinal(data);
    }

    public static byte[] generateHmac(byte[] data, String secret) throws NoSuchAlgorithmException, InvalidKeyException {
        return generateHmac(data, secret.getBytes(StandardCharsets.UTF_8));
    }
}
