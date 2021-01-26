package com.L0v4iy.deezer.io.controller;

import com.L0v4iy.deezer.io.dto.HttpClientAuth;
import org.apache.http.impl.client.CloseableHttpClient;
import org.json.JSONObject;

import java.io.IOException;

public interface ResourceController
{
    String getData(String uri) throws IOException;
    String postData(String uri, JSONObject data) throws IOException;

    String callPrivateApi(String method, JSONObject args);
    String callPublicApi(String method, JSONObject args);

    CloseableHttpClient getHttpClient();
    HttpClientAuth getClientAuth();
}
