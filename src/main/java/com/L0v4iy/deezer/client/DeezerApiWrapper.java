package com.L0v4iy.deezer.client;

import com.L0v4iy.deezer.io.controller.ResourceController;
import com.L0v4iy.deezer.service.DeezerApi;
import com.L0v4iy.deezer.service.dto.TrackData;
import com.L0v4iy.deezer.io.ConnectionResources;
import com.L0v4iy.deezer.io.JSONLib;
import com.L0v4iy.deezer.crypto.LinkGenerator;
import lombok.extern.java.Log;
import org.apache.http.client.utils.URIBuilder;
import org.json.JSONObject;

import java.io.IOException;

@Log
public class DeezerApiWrapper extends DeezerApi
{

    public DeezerApiWrapper(ResourceController resourceController)
    {
        super(resourceController);
    }

    public String getTrackUri(TrackData trackData, String audioQuality)
    {
        String data = trackData.getTrackData();
        String id = JSONLib.parseJSON(data, new String[]{"results", "DATA", "SNG_ID"});
        String md5Origin = JSONLib.parseJSON(data, new String[]{"results", "DATA", "MD5_ORIGIN"});
        String mediaVersion = JSONLib.parseJSON(data, new String[]{"results", "DATA", "MEDIA_VERSION"});
        return getTrackUri(id, md5Origin, mediaVersion, audioQuality);
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

    /**
     * dont even know for what i did this, but anyway...
     * @param trackId track id
     * @return data
     */
    protected TrackData getTrackDataMobileById(String trackId)
    {
        URIBuilder query = new URIBuilder();
        query.addParameter("api_version", "1.0");
        query.addParameter("api_token", "null");
        query.addParameter("method", "deezer.ping");

        String r = null;
        try {
            String uri = ConnectionResources.PRIVATE_API_URL + query;
            r = getResourceController().postData(uri, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String sid = JSONLib.parseJSON(r, new String[]{"results", "SESSION"});

        query.addParameter("api_key", ConnectionResources.MOBILE_API_KEY);
        query.setParameter("method", "song_getData");
        query.addParameter("output", "3");
        query.addParameter("input", "3");
        query.addParameter("sid", sid);
        query.addParameter("sid", sid);
        JSONObject data = new JSONObject();
        data.put("SNG_ID", trackId);
        String path = "1.0/gateway.php";

        String postURI = ConnectionResources.PUBLIC_API_URL + path + query;
        String rawData = null;
        try {
            System.out.println(postURI);
            rawData = getResourceController().postData(postURI, data);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (JSONLib.parseJSON(rawData, new String[]{"error"}) != null)
        {
            log.warning("error on getting track data");

        }
        return new TrackData(rawData);
    }

}
