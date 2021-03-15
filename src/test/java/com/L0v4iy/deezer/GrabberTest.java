package com.L0v4iy.deezer;

import com.L0v4iy.deezer.client.DeezerApiWrapper;
import com.L0v4iy.deezer.crypto.AudioDecrypter;
import com.L0v4iy.deezer.domain.Track;
import com.L0v4iy.deezer.domain.Tracks;
import com.L0v4iy.deezer.domain.internal.search.Search;
import com.L0v4iy.deezer.io.controller.HttpResourceController;
import com.L0v4iy.deezer.io.dto.DeezerArl;
import com.L0v4iy.deezer.io.dto.HttpClientAuth;
import com.L0v4iy.deezer.service.dto.TrackData;
import com.L0v4iy.deezer.client.DeezerClient;

import com.L0v4iy.deezer.service.entity.Quality;
import lombok.SneakyThrows;
import org.apache.commons.io.IOUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.junit.Test;

import java.io.*;

public class GrabberTest
{

    /**
     * to make authorized requests directly
     */
    private DeezerApiWrapper apiWrapper;
    /**
     * common requests
     */
    private DeezerClient deezerClient;

    private void init()
    {
        DeezerArl deezerArl = new DeezerArl(
                // dont worry, its wrong
                "25cc77197827cc9e999gge7f16a4372f6364257a8d6de529758c5293a65f72a9fee6f69a3a5983a406a40gh6fda39d390067c54f8fd6cg5hkd7a647a33b0a5f4d1742d59f11jkb3fc7656c0f29e88r5yd2e73b9ec41e3ad78a1fe35edf8as912"
        );
        HttpClientAuth clientAuth = new HttpClientAuth(
                deezerArl,
                null
        );
        HttpResourceController controller = new HttpResourceController(clientAuth);

        deezerClient = new DeezerClient(controller);
        apiWrapper = new DeezerApiWrapper(controller);
    }

    @SneakyThrows
    @Test
    public void GetGrabbingUri()
    {
        init();
// init ready
// lets get track by id
// id you can take from deezer client

        String query = "Nemix The Core";
        Tracks tracks = deezerClient.search(new Search(query));

// take 1-st track

        Track track = tracks.getData().get(0);
        String trackId = track.getId().toString();

// id gotten
// now getting track link

        TrackData data = apiWrapper.getTrackDataById(trackId);
        String uri = apiWrapper.getTrackUri(data, Quality.MP3_128);
        System.out.println(uri);

// found track uri
// now get the track's byte array and decrypt it

        InputStream audioStream = getStream(uri);
        byte[] decryptedTrack = AudioDecrypter.decryptTrack(IOUtils.toByteArray(audioStream), trackId);

// write in a file

        try (FileOutputStream fos = new FileOutputStream("audio.mp3")) {
            assert decryptedTrack != null;
            fos.write(decryptedTrack);
        }
    }

// hope lib 'll be useful

    private InputStream getStream(String uri) throws IOException {
        CloseableHttpResponse response = apiWrapper.getResourceController().getHttpClient().execute(new HttpGet(uri));
        // however you can add audio decrypter into here, this lib produced for lavalink it use same methods, hope will work
        try {
            return new ByteArrayInputStream(
                    IOUtils.toByteArray(response.getEntity().getContent())
            );
        } finally {
            response.close();
        }
    }
    

    @Test
    public void tryRandom()
    {
        String md5 = "b4c5333fb3d99ec3a13e506f6c6b4d47";
        String q = "1";
        String sid = "72598936";
        String mv = "4";
        byte[] encrypted = "helloWorld".getBytes();
        byte[] data = AudioDecrypter.decryptTrack(encrypted, sid);
    }
}
