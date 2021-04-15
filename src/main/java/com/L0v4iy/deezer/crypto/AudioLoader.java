package com.L0v4iy.deezer.crypto;

import org.apache.commons.io.IOUtils;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.concurrent.Executor;

public class AudioLoader
{
    private volatile ByteArrayOutputStream decryptedOutputStream;

    public ByteArrayOutputStream loadAudioTrackStream(InputStream encryptedStream, String trackId)
    {
        AudioDecrypter decrypter = buildDecrypter(trackId);

        decryptedOutputStream = new ByteArrayOutputStream();
        Runnable delegate = () ->
        {
            try
            {
                decryptDelegate(encryptedStream, decrypter);
            } catch (IOException e)
            {
                e.printStackTrace();
            }
        };
        Executor executor = (runnable) -> {
            new Thread(runnable).start();
        };
        executor.execute(delegate);
        //delegate.run();
        return decryptedOutputStream;
    }

    public byte[] loadAudioTrackSerial(InputStream encryptedStream, String trackId)
    {
    try
    {
        AudioDecrypter decrypter = buildDecrypter(trackId);
        byte[] loaded = IOUtils.toByteArray(encryptedStream);
        encryptedStream.close();
        return decrypter.decryptTrack(loaded);
    } catch (IOException | IllegalBlockSizeException | BadPaddingException e)
    {
        e.printStackTrace();
    }
    return null;
}

    private void decryptDelegate(InputStream encryptedStream, AudioDecrypter decrypter) throws IOException
    {
        try {
            BufferedInputStream bufferedEncrypted = new BufferedInputStream(encryptedStream);
            byte[] chunk;
            int iterator = 0;

            while ((chunk = pupCopy(bufferedEncrypted, 2048)) != null) {

                if (iterator % 3 == 0 && chunk.length == 2048)
                {
                    // decrypt blowfishs
                    chunk = decrypter.decryptChunk(chunk);
                }
                decryptedOutputStream.write(chunk);
                iterator++;
            }
            System.out.println("downloaded and decrypted");
            encryptedStream.close();
        }
        catch (BadPaddingException | IllegalBlockSizeException e)
        {
            e.printStackTrace();
        }

    }

    private ByteArrayOutputStream copiedBuffer = new ByteArrayOutputStream();
    /**
     * @param input InputStream
     * @param length byte size required to receive
     * @return byte array in correct size
     * @throws IOException exc.
     */
    public byte[] pupCopy(InputStream input, int length) throws IOException {
        byte[] buffer = new byte[length];
        long count = 1;
        int n;

        if (copiedBuffer.size() < length)
        {
            for(count = 0L; -1 != (n = input.read(buffer)); count += n) {
                copiedBuffer.write(buffer, 0, n);
                if (copiedBuffer.size() >= length)
                {
                    break;
                }
            }
        }

        byte[] copied = copiedBuffer.toByteArray();
        buffer = Arrays.copyOfRange(copied, 0, length);

        if (copiedBuffer.size() < length)
        {
            if (count <= 0) return null;
            // last uncompleted chunk
            copiedBuffer.reset();
            return buffer;
        }

        byte[] other = Arrays.copyOfRange(copied, length, copiedBuffer.size());
        copiedBuffer.reset();
        copiedBuffer.write(other);
        return buffer;

    }
    private AudioDecrypter buildDecrypter(String trackId)
    {
        return new AudioDecrypter(trackId );
    }
}
