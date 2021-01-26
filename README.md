deezer-api-client
=================
"by zeloon"\
Java wrapper for deezer api - http://developers.deezer.com/api/ supports
all GET methods which don't need OAuth authentication

"by L0v4iy"\
Also supports post requests, and get requests which required an authorization  https://www.deezer.com/ajax/gw-light.php?

Gratitude to Chimera.py developer.

### How to use

```java

// init:
    DeezerArl deezerArl = new DeezerArl(
            "your deezer ARL (use after authorizing on deezer in chromium browser: show code->application->Cookies->https://www.deezer.com->arl)"
    );
    HttpClientAuth clientAuth = new HttpClientAuth(
            deezerArl,
            null // (proxy not tested yet)
    );

    HttpResourceController controller = new HttpResourceController(clientAuth);

    deezerClient = new DeezerClient(controller);
    apiWrapper = new DeezerApiWrapper(controller);

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
    String uri = apiWrapper.getTrackUri(data);

// done
```

