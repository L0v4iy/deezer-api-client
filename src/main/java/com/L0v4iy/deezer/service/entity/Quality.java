package com.L0v4iy.deezer.service.entity;

import java.util.HashMap;
import java.util.Map;

public enum Quality
{
    MP3_128("1"),
    MP3_320("3"),
    FLAC("9"),
    q360_RA1("13"),
    q360_RA2("14"),
    q360_RA3("15");

    private static final Map<String, Quality> map = new HashMap<>();

    static
    {
        for (Quality quality : Quality.values())
        {
            map.put(quality.key, quality);
        }

    }

    public final String key;

    Quality(final String key)
    {
        this.key = key;
    }

    public static Quality getQuality(String key)
    {
        return map.get(key);
    }
}
