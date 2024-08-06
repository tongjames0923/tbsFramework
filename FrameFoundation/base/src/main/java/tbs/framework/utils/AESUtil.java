package tbs.framework.utils;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.util.Base64;

public class AESUtil {

    private static final String AES = "AES";
    private static final String AES_CIPHER = "AES/CBC/PKCS5Padding";

    public static String encrypt(String key, String data) throws Exception {
        SecretKey secretKey = new SecretKey() {
            public String getAlgorithm() {
                return AES;
            }

            public byte[] getEncoded() {
                return key.getBytes();
            }

            public String getFormat() {
                return "RAW";
            }
        };
        byte[] keyBytes = new byte[16];
        byte[] keysB = key.getBytes();
        for (int i = 0; i < 16; i++) {
            if (i >= keysB.length) {
                keyBytes[i] = 0x00;
            } else {
                keyBytes[i] = keysB[i];
            }
        }
        IvParameterSpec iv = new IvParameterSpec(keyBytes);
        Cipher cipher = Cipher.getInstance(AES_CIPHER);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv);
        byte[] encryptedData = cipher.doFinal(data.getBytes());
        return Base64.getEncoder().encodeToString(encryptedData);
    }

    public static String decrypt(String key, String encryptedData) throws Exception {
        SecretKey secretKey = new SecretKey() {
            public String getAlgorithm() {
                return AES;
            }

            public byte[] getEncoded() {
                return key.getBytes();
            }

            public String getFormat() {
                return "RAW";
            }
        };
        byte[] keyBytes = new byte[16];
        byte[] keysB = key.getBytes();
        for (int i = 0; i < 16; i++) {
            if (i >= keysB.length) {
                keyBytes[i] = 0x00;
            } else {
                keyBytes[i] = keysB[i];
            }
        }
        IvParameterSpec iv = new IvParameterSpec(keyBytes);
        Cipher cipher = Cipher.getInstance(AES_CIPHER);
        cipher.init(Cipher.DECRYPT_MODE, secretKey, iv);
        byte[] original = cipher.doFinal(Base64.getDecoder().decode(encryptedData));
        return new String(original);
    }
}
