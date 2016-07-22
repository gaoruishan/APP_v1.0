
package com.cmcc.hyapps.andyou.util;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;

/**
 * @author kuloud
 */
public class AESUtil {
    private static final String KEY = "38250326";

    private static final byte[] IV = {
            0x12, 0x34, 0x56, 0x78, (byte) 0x90, (byte) 0xAB, (byte) 0xCD, (byte) 0xEF
    };

    public static byte[] CBCEncrypt(byte[] data) {

        try {
            DESKeySpec dks = new DESKeySpec(KEY.getBytes());

            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            SecretKey secretKey = keyFactory.generateSecret(dks);

            Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
            // Cipher cipher = Cipher.getInstance("DES/CBC/NoPadding");

            IvParameterSpec param = new IvParameterSpec(IV);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, param);

            byte encryptedData[] = cipher.doFinal(data);

            return encryptedData;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static byte[] CBCDdecryption(byte[] data) {

        try {
            DESKeySpec dks = new DESKeySpec(KEY.getBytes());

            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            SecretKey secretKey = keyFactory.generateSecret(dks);

            Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
            // Cipher cipher = Cipher.getInstance("DES/CBC/NoPadding");

            IvParameterSpec param = new IvParameterSpec(IV);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, param);

            byte encryptedData[] = cipher.doFinal(data);

            return encryptedData;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
