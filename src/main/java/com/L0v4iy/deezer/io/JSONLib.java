package com.L0v4iy.deezer.io;

import org.json.JSONObject;

public class JSONLib
{
    public static String parseJSON(String jsonString, String[] path)
    {
        int counter = 0;
        int pathLen = path.length;
        JSONObject json = new JSONObject(jsonString);
        for (String dir : path)
        {
            counter++;
            if (json.has(dir))
            {
                String a = json.get(dir).toString();
                if (counter == pathLen)
                {
                    if (a.equals("[]")) return null;
                    return a;
                }
                json = new JSONObject(a);
            }
        }
        return null;
    }
}
