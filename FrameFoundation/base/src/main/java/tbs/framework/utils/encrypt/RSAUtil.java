package tbs.framework.utils.encrypt;

import javax.crypto.Cipher;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/**
 * RSA加密工具类
 *
 * @author abstergo
 */
public class RSAUtil {

    private static final String RSA = "RSA";

    private static KeyPair generateKeyPair() throws Exception {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(RSA);
        keyPairGenerator.initialize(2048);
        return keyPairGenerator.genKeyPair();
    }

    public static String encrypt(String data, String publicKey) throws Exception {
        Cipher cipher = Cipher.getInstance(RSA);
        cipher.init(Cipher.ENCRYPT_MODE, getPublicKey(publicKey));
        byte[] encryptedData = cipher.doFinal(data.getBytes());
        return Base64.getEncoder().encodeToString(encryptedData);
    }

    public static String decrypt(String encryptedData, String privateKey) throws Exception {
        Cipher cipher = Cipher.getInstance(RSA);
        cipher.init(Cipher.DECRYPT_MODE, getPrivateKey(privateKey));
        byte[] original = cipher.doFinal(Base64.getDecoder().decode(encryptedData));
        return new String(original);
    }

    private static PublicKey getPublicKey(String key) throws Exception {
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(Base64.getDecoder().decode(key));
        KeyFactory keyFactory = KeyFactory.getInstance(RSA);
        return keyFactory.generatePublic(keySpec);
    }

    private static PrivateKey getPrivateKey(String key) throws Exception {
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(key));
        KeyFactory keyFactory = KeyFactory.getInstance(RSA);
        return keyFactory.generatePrivate(keySpec);
    }
}