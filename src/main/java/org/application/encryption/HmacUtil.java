package org.application.encryption;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class HmacUtil {
    // Define o algoritmo HMAC a ser utilizado
    private static final String HMAC_ALGORITHM = "HmacSHA256";

    /**
     * Gera um HMAC-SHA256 a partir de um array de bytes de dados e uma chave secreta.
     *
     * @param data  Os dados a serem autenticados.
     * @param secret A chave secreta em formato de array de bytes.
     * @return O código HMAC gerado.
     * @throws NoSuchAlgorithmException Se o algoritmo HMAC-SHA256 não estiver disponível.
     * @throws InvalidKeyException Se a chave fornecida for inválida.
     */
    public static byte[] generateHmac(byte[] data, byte[] secret) throws NoSuchAlgorithmException, InvalidKeyException {
        Mac mac = Mac.getInstance(HMAC_ALGORITHM); // Obtém uma instância do algoritmo HMAC-SHA256
        SecretKeySpec secretKey = new SecretKeySpec(secret, HMAC_ALGORITHM); // Cria uma chave secreta específica para o algoritmo
        mac.init(secretKey); // Inicializa o objeto Mac com a chave secreta
        return mac.doFinal(data); // Calcula o HMAC e retorna o resultado
    }

    /**
     * Gera um HMAC-SHA256 a partir de um array de bytes de dados e uma chave secreta representada como String.
     *
     * @param data  Os dados a serem autenticados.
     * @param secret A chave secreta em formato String.
     * @return O código HMAC gerado.
     * @throws NoSuchAlgorithmException Se o algoritmo HMAC-SHA256 não estiver disponível.
     * @throws InvalidKeyException Se a chave fornecida for inválida.
     */
    public static byte[] generateHmac(byte[] data, String secret) throws NoSuchAlgorithmException, InvalidKeyException {
        return generateHmac(data, secret.getBytes(StandardCharsets.UTF_8)); // Converte a chave String para bytes UTF-8 e chama o outro método
    }
}