package com.L0v4iy.deezer.client;

import com.L0v4iy.deezer.io.controller.ResourceController;
import com.L0v4iy.deezer.service.DeezerApi;
import com.L0v4iy.deezer.service.dto.TrackData;
import com.L0v4iy.deezer.io.ConnectionResources;
import com.L0v4iy.deezer.io.JSONLib;
import com.L0v4iy.deezer.crypto.LinkGenerator;
import com.L0v4iy.deezer.service.entity.Quality;
import lombok.extern.java.Log;
import org.json.JSONObject;

import java.io.IOException;

@Log
public class DeezerApiWrapper extends DeezerApi
{

    public DeezerApiWrapper(ResourceController resourceController)
    {
        super(resourceController);
    }

    public String getTrackUri(TrackData trackData, Quality audioQuality)
    {
        String data = trackData.getTrackData();
        String id = JSONLib.parseJSON(data, new String[]{"results", "DATA", "SNG_ID"});
        String md5Origin = JSONLib.parseJSON(data, new String[]{"results", "DATA", "MD5_ORIGIN"});
        String mediaVersion = JSONLib.parseJSON(data, new String[]{"results", "DATA", "MEDIA_VERSION"});
        return getTrackUri(id, md5Origin, mediaVersion, audioQuality.key);
    }

    public String getTrackUri(String id, String md5Origin, String mediaVersion, String audioQuality)
    {
        return LinkGenerator.generateLink(md5Origin, audioQuality, id, mediaVersion);
    }

    /**
     *
     * @param isrc audio track "International Standard Recording Code"
     * @return track data
     */
    public TrackData getTrackDataByISRC(String isrc)
    {
        String uri = ConnectionResources.PUBLIC_API_URL + "/track/isrc:" + isrc;
        String res = null;
        try {
             res = getResourceController().getData(uri);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (res == null) return null;
        if (JSONLib.parseJSON(res, new String[]{"error"}) != null)
        {
            log.warning("error on getting track data");

        }
        String id = JSONLib.parseJSON(res, new String[]{"id"});
        return getTrackDataById(id);
    }

    /**
     * make request to private api using trackId
     * @param trackId track id
     * @return full response from api
     */
    public TrackData getTrackDataById(String trackId)
    {
        JSONObject object = new JSONObject();
        object.put("SNG_ID", trackId);
        // get track data •
        // case
        // or get track data mobile
        String res = callPrivateApi("deezer.pageTrack", object);
        if (JSONLib.parseJSON(res, new String[]{"error"}) != null)
        {
            log.warning("error on getting track data");
        }
        return new TrackData(res);
    }

}
