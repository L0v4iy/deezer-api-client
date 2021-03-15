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
        byte[] decryptedTrack = AudioDecrypter.decryptTrack(IOUtils.toByteArray(audioStream), trackId);

// write in a file

        try (FileOutputStream fos = new FileOutputStream("audio.mp3")) {
            assert decryptedTrack != null;
            fos.write(decryptedTrack);
        }
    }
    
// hope lib 'll be useful
```

↓ To get data from generated uri you can use this ↓
 
```
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
```

