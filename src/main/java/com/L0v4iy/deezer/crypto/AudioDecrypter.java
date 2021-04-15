package com.L0v4iy.deezer.crypto;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class AudioDecrypter
{
    private static String BLOWFISH_KEY = "g4el58wc0zvf9na1";
    private static String IV_KEY = "0001020304050607";

    private Cipher cipher;

    public AudioDecrypter(String trackId)
    {
        try
        {
            configureCipher(trackId);
        } catch (NoSuchAlgorithmException | DecoderException | InvalidAlgorithmParameterException | NoSuchPaddingException | InvalidKeyException e)
        {
            e.printStackTrace();
        }
    }

    private void configureCipher(String trackId) throws NoSuchAlgorithmException, DecoderException, NoSuchPaddingException, InvalidAlgorithmParameterException, InvalidKeyException
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
        SecretKeySpec keySpec = new SecretKeySpec(key, "Blowfish");
        byte[] iv = Hex.decodeHex(IV_KEY);
        IvParameterSpec ivSpec = new IvParameterSpec(iv);
        this.cipher = Cipher.getInstance("Blowfish/CBC/NoPadding");
        this.cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
    }

    public byte[] decryptTrack(byte[] encrypted)
            throws BadPaddingException, IllegalBlockSizeException, IOException
    {

        int inputLen = encrypted.length;
        ByteArrayOutputStream decrypted = new ByteArrayOutputStream();
        int iterator = 0;
        while (true)
        {
            byte[] chunk;
            if (decrypted.size() == inputLen)
            {
                break;
            }
            // take 2048 bytes per step
            if ((iterator + 1) * 2048 >= inputLen)
                chunk = Arrays.copyOfRange(encrypted, (iterator) * 2048, inputLen);
            else
                chunk = Arrays.copyOfRange(encrypted, iterator * 2048, (iterator + 1) * 2048);

            // every 3-rd iterator
            if (iterator % 3 == 0 && chunk.length == 2048)
            {
                // decrypt blowfishs
                chunk = decryptChunk(chunk);
            }
            decrypted.write(chunk);
            iterator++;

        }
        return decrypted.toByteArray();
    }

    public byte[] decryptChunk(byte[] chunk) throws BadPaddingException, IllegalBlockSizeException
    {
        // decrypt blowfishs
        return cipher.doFinal(chunk);
    }
}
