package com.L0v4iy.deezer.io.controller;

import com.L0v4iy.deezer.io.dto.HttpClientAuth;
import lombok.extern.java.Log;
import org.apache.http.impl.client.CloseableHttpClient;
import org.json.JSONObject;

import java.io.IOException;

@Log
public class HttpResourceController extends HttpClient
{

    public HttpResourceController(HttpClientAuth clientAuth) {
        super(clientAuth);
    }

    @Override
    public String getData(String uri) throws IOException {
        return super.getData(uri);
    }

    @Override
    public String postData(String uri, JSONObject data) throws IOException {
        return super.postData(uri, data);
    }

    @Override
    public String callPrivateApi(String method, JSONObject args) {
        super.reloadExpiredSession();
        return super.callPrivateApi(method, args);
    }

    @Override
    public String callPublicApi(String method, JSONObject args) {
        super.reloadExpiredSession();
        return super.callPublicApi(method, args);
    }

    @Override
    public CloseableHttpClient getHttpClient() {
        return super.getHttpClient();
    }

    @Override
    public HttpClientAuth getClientAuth() {
        return super.getClientAuth();
    }

}
