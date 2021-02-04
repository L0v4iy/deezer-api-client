package com.L0v4iy.deezer.crypto;

import org.apache.commons.codec.binary.Hex;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class AudioDecrypter
{
    private static String BLOWFISH_KEY = "g4el58wc0zvf9na1";

    public static byte[] decryptTrack(byte[] encrypted, String trackId)
    {
        try {
            return decrypter(encrypted, trackId);
        } catch (NoSuchAlgorithmException | IOException | InvalidKeyException | BadPaddingException | IllegalBlockSizeException | NoSuchPaddingException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static byte[] decrypter(byte[] encrypted, String trackId)
            throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, IOException
    {
        byte[] blowfishKeyBytes = BLOWFISH_KEY.getBytes();

        MessageDigest messageDigest = MessageDigest.getInstance("MD5");
        messageDigest.update(trackId.getBytes());
        String hash = Hex.encodeHexString(messageDigest.digest());

        ByteArrayOutputStream keyBuilder = new ByteArrayOutputStream( );
        byte[] byteHash = hash.getBytes();
        for (int i = 0; i < 16; i++)
        {
            int append = byteHash[i] ^ byteHash[i + 16] ^ blowfishKeyBytes[i];
            keyBuilder.write(append);
        }
        byte[] key = keyBuilder.toByteArray();

        int inputLen = encrypted.length;

        SecretKeySpec keySpec = new SecretKeySpec(key, "Blowfish");
        Cipher cipher = Cipher.getInstance("Blowfish");
        cipher.init(Cipher.DECRYPT_MODE, keySpec);

        ByteArrayOutputStream decrypted = new ByteArrayOutputStream();
        int iterator = 0;
        while (true)
        {
            byte[] chunk = null;
            if (decrypted.size() == inputLen -1) break;
            // take 2048 bytes per step
            if ((iterator + 1) * 2048 > inputLen)
                chunk = Arrays.copyOfRange(encrypted, iterator * 2048, inputLen -1);
            else
                chunk = Arrays.copyOfRange(encrypted, iterator * 2048, (iterator + 1) * 2048);

            // every 3-rd iterator
            if (iterator % 3 == 0 && chunk.length == 2048)
            {
                // decrypt blowfish
                chunk = cipher.doFinal(chunk);
            }
            decrypted.write(chunk);
            iterator++;

        }
        return decrypted.toByteArray();

    }
}
