package tbs.framework.utils.impls;

import cn.hutool.core.lang.UUID;
import tbs.framework.utils.UuidUtil;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SimpleUuidUtil extends UuidUtil {

    /**
     * 将字节数组转换为十六进制字符串。
     *
     * @param bytes 字节数组
     * @return 十六进制字符串
     */
    public static String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder(2 * bytes.length);
        for (byte b : bytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

    private String md5(String uuid, int len) {
        byte[] hash = uuid.getBytes();
        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance("SHA-512");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        return bytesToHex(digest.digest(hash)).substring(0, len);
    }

    @Override
    public String uuid() {
        return md5(UUID.fastUUID().toString(), 16);
    }
}
