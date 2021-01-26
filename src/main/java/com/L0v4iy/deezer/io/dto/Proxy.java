package com.L0v4iy.deezer.io.dto;

public class Proxy
{
    private final String host;
    private final Integer port;

    public Proxy(String host, Integer port) {
        this.host = host;
        this.port = port;
    }

    public String getHost() {
        return host;
    }

    public Integer getPort() {
        return port;
    }
}
