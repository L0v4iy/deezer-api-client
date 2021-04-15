deezer-api-client
=================
"by zeloon"\
Java wrapper for deezer api - http://developers.deezer.com/api/ supports
all GET methods which don't need OAuth authentication

"by L0v4iy"\
Also supports post requests, and get requests which required an authorization  https://www.deezer.com/ajax/gw-light.php?

Gratitude to Chimera.py developer.

### How to use

```

// init:
    DeezerArl deezerArl = new DeezerArl(
            "your deezer ARL (use after authorizing on deezer in chromium browser: show code->application->Cookies->https://www.deezer.com->arl)"
    );
    HttpClientAuth clientAuth = new HttpClientAuth(
            deezerArl,
            null // (proxy not tested yet)
    );

    HttpResourceController controller = new HttpResourceController(clientAuth);

    DeezerClient deezerClient = new DeezerClient(controller);
    DeezerApiWrapper apiWrapper = new DeezerApiWrapper(controller);

// init ready
// lets get track by id
// id you can take from deezer client 

    String query = "track title " + "artist";
    Tracks tracks = deezerClient.search(new Search(query));

// take 1-st track

    Track track = tracks.getData().get(0);
    String trackId = track.getId().toString();

// id gotten
// now getting track link

    TrackData data = apiWrapper.getTrackDataById(trackId);
    
// Qualities:
// 'MP3_128':   '1'
// 'MP3_320':   '3'
// 'FLAC':      '9'
// '360_RA1':   '13'
// '360_RA2':   '14'
// '360_RA3':   '15'
    String uri = apiWrapper.getTrackUri(data, Quality.MP3_128);

// found track uri
// now get the track's byte array and decrypt it
//  "getStream(uri)" located below

    InputStream audioStream = getStream(uri);
    AudioLoader audioLoader = new AudioLoader();
// load and decrypt audio track using streams
    ByteArrayOutputStream output = audioLoader.loadStreamAudioTrack(audioStream, trackId);
// think 5 seconds will be enough
    Thread.sleep(5000);
// output stream is completed
    writeIntoFile(output.toByteArray(), "audioOut");
}

// hope lib 'll be useful
```

↓ To get data from generated uri you can use this ↓
 
```
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
```

More easy ways to understand

```
    public void streamLoadingTest()
    {
        // will load in some time
        init();
        String trackId = "106471752";
        String uri = "http://e-cdn-proxy-e.deezer.com/mobile/1/9d0be9ce4850adb4eac7f49badd83a48d9228bd210db417980538ed813bae68afb321cf24727148f715ce86d8ed7fe5d5768f437bc53d462619fdd6d26a4708a6c6a6bc08e037da335f5b080e8d3b214";
        InputStream audioStream = getStream(uri);
        AudioLoader audioLoader = new AudioLoader();
        audioLoader.loadAudioTrackStream(audioStream, trackId);
    }
```

```
public void serialLoadingTest()
    {
// like in old time
        init();
        String trackId = "106471752";
        String uri = "http://e-cdn-proxy-e.deezer.com/mobile/1/9d0be9ce4850adb4eac7f49badd83a48d9228bd210db417980538ed813bae68afb321cf24727148f715ce86d8ed7fe5d5768f437bc53d462619fdd6d26a4708a6c6a6bc08e037da335f5b080e8d3b214";
        InputStream audioStream = getStream(uri);
        AudioLoader audioLoader = new AudioLoader();
        audioLoader.loadAudioTrackSerial(audioStream, trackId);
    }
```