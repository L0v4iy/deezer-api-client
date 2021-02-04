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

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

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
        String uri = apiWrapper.getTrackUri(data, "1");
        System.out.println(uri);

// done
    }

    @Test
    public void tryRandom() throws IOException {
        String md5 = "b4c5333fb3d99ec3a13e506f6c6b4d47";
        String q = "1";
        String sid = "72598936";
        String mv = "4";
        //byte[] encrypted = FileUtils.readFileToByteArray(new File("audio.mp3"));
        byte[] encrypted = FileUtils.readFileToByteArray(new File("audio.mp3"));

        byte[] data = AudioDecrypter.decryptTrack(encrypted, sid);

        assert data != null;
        FileUtils.writeByteArrayToFile(new File("audioOut.mp3"), data);
    }
}
