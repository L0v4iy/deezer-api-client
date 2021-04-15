package com.L0v4iy.deezer;

import com.L0v4iy.deezer.client.DeezerApiWrapper;
import com.L0v4iy.deezer.crypto.AudioDecrypter;
import com.L0v4iy.deezer.crypto.AudioLoader;
import com.L0v4iy.deezer.domain.Track;
import com.L0v4iy.deezer.domain.Tracks;
import com.L0v4iy.deezer.domain.internal.search.Search;
import com.L0v4iy.deezer.io.FileSystemResourceConnection;
import com.L0v4iy.deezer.io.controller.HttpResourceController;
import com.L0v4iy.deezer.io.dto.DeezerArl;
import com.L0v4iy.deezer.io.dto.HttpClientAuth;
import com.L0v4iy.deezer.service.dto.TrackData;
import com.L0v4iy.deezer.client.DeezerClient;

import com.L0v4iy.deezer.service.entity.Quality;
import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
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

    public GrabberTest()
    {
        init();
    }

    @SneakyThrows
    @Test
    public void GetGrabbingUri()
    {
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
        AudioLoader audioLoader = new AudioLoader();
// load and decrypt audio track using streams
        ByteArrayOutputStream output = audioLoader.loadAudioTrackStream(audioStream, trackId);
// think 5 seconds will be enough
        Thread.sleep(5000);
// output stream is completed
        writeIntoFile(output.toByteArray(), "audioOut");
    }

// hope lib 'll be useful

    private InputStream getStream(String uri) throws IOException {
        CloseableHttpResponse response = apiWrapper.getResourceController().getHttpClient().execute(new HttpGet(uri));
        try {
            return response.getEntity().getContent();
        } catch (IOException | UnsupportedOperationException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    private void writeIntoFile(byte[] data, String name)
    {
        try (FileOutputStream fos = new FileOutputStream(name+".mp3")) {
            assert data != null;
            fos.write(data);
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    @Test
    @SneakyThrows
    public void streamLoadingTest()
    {
        // will load in some time
        String trackId = "106471752";
        String uri = "http://e-cdn-proxy-e.deezer.com/mobile/1/9d0be9ce4850adb4eac7f49badd83a48d9228bd210db417980538ed813bae68afb321cf24727148f715ce86d8ed7fe5d5768f437bc53d462619fdd6d26a4708a6c6a6bc08e037da335f5b080e8d3b214";
        InputStream audioStream = getStream(uri);
        AudioLoader audioLoader = new AudioLoader();
        audioLoader.loadAudioTrackStream(audioStream, trackId);
    }

    @Test
    @SneakyThrows
    public void serialLoadingTest()
    {
        String trackId = "106471752";
        String uri = "http://e-cdn-proxy-e.deezer.com/mobile/1/9d0be9ce4850adb4eac7f49badd83a48d9228bd210db417980538ed813bae68afb321cf24727148f715ce86d8ed7fe5d5768f437bc53d462619fdd6d26a4708a6c6a6bc08e037da335f5b080e8d3b214";
        InputStream audioStream = getStream(uri);
        AudioLoader audioLoader = new AudioLoader();
        audioLoader.loadAudioTrackSerial(audioStream, trackId);
    }

    @Test
    public void decryptTrack() throws IOException, BadPaddingException, IllegalBlockSizeException
    {
        String md5 = "b4c5333fb3d99ec3a13e506f6c6b4d47";
        String q = "1";
        String sid = "72598936";
        String mv = "4";
        //byte[] encrypted = "helloWorld".getBytes();
        byte[] encrypted = FileUtils.readFileToByteArray(new File("audioEnc.mp3"));

        byte[] data = new AudioDecrypter(sid).decryptTrack(encrypted);

        assert data != null;
        FileUtils.writeByteArrayToFile(new File("audioOut.mp3"), data);
    }

    @Test
    @SneakyThrows
    public void tryToReadResponse()
    {
        String uri = "http://api.deezer.com/track/106471752";
        FileSystemResourceConnection controller = new FileSystemResourceConnection();
        String response = controller.getData(uri);

        TrackData dto = convertJson(response, TrackData.class);

    }

    private <T> T convertJson(final String content, Class<T> targetClass) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(content, targetClass);
    }
}
