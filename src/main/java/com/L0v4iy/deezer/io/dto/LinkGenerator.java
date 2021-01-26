package com.L0v4iy.deezer.io.dto;

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

public class LinkGenerator
{
    public static String generateLink(String md5Origin, String audioQuality, String trackId, String mediaVersion)
    {
        try {
            return generator(md5Origin, audioQuality, trackId, mediaVersion);
        } catch (IOException | NoSuchPaddingException | NoSuchAlgorithmException  | BadPaddingException | IllegalBlockSizeException| InvalidKeyException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String generator(String md5o, String audioQ, String trackId, String mediaVer)
            throws IOException, NoSuchPaddingException, NoSuchAlgorithmException, BadPaddingException, IllegalBlockSizeException, InvalidKeyException
    {
        byte[] md5Origin = md5o.getBytes();
        byte[] audioQuality = audioQ.getBytes();
        byte[] id = trackId.getBytes();
        byte[] mediaVersion = mediaVer.getBytes();
        // delimiter
        byte del = (byte) 0xa4;

        ByteArrayOutputStream step1Stream = new ByteArrayOutputStream( );
        step1Stream.write(md5Origin);
        step1Stream.write(del);
        step1Stream.write(audioQuality);
        step1Stream.write(del);
        step1Stream.write(id);
        step1Stream.write(del);
        step1Stream.write(mediaVersion);
        byte[] step1 = step1Stream.toByteArray();

        MessageDigest messageDigest = MessageDigest.getInstance("MD5");
        messageDigest.update(step1);
        String step1Hash = Hex.encodeHexString(messageDigest.digest());

        ByteArrayOutputStream step2Stream = new ByteArrayOutputStream( );
        step2Stream.write(step1Hash.getBytes());
        step2Stream.write(del);
        step2Stream.write(step1);
        step2Stream.write(del);
        byte[] step2 = step2Stream.toByteArray();

        // AES encrypting
        String AESKey = "jo6aey6haid2Teih";
        SecretKeySpec sKeySpec = new SecretKeySpec(AESKey.getBytes(), "AES");
        Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding");

        ByteArrayOutputStream step3Stream = new ByteArrayOutputStream( );
        cipher.init(Cipher.ENCRYPT_MODE, sKeySpec);
        for (int i = 0; i < step2.length / 16; i++)
        {
            byte[] block = Arrays.copyOfRange(step2, i*16, (i+1)*16);
            byte[] encrypted = cipher.doFinal(block);
            step3Stream.write(encrypted);
        }
        String cyphered = Hex.encodeHexString(step3Stream.toByteArray());
        byte[] step3 = cyphered.getBytes();

        return String.format("http://e-cdn-proxy-%s.deezer.com/mobile/1/%s", md5o.charAt(0), new String(step3, "UTF-8"));
    }
}
