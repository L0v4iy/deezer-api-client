package com.L0v4iy.deezer.io.dto;

public class HttpClientAuth
{
    private final DeezerArl deezerArl;
    private final Proxy proxy;

    public HttpClientAuth(DeezerArl deezerArl, Proxy proxy) {
        this.deezerArl = deezerArl;
        this.proxy = proxy;
    }

    public DeezerArl getDeezerArl() {
        return deezerArl;
    }

    public Proxy getProxy() {
        return proxy;
    }
}
