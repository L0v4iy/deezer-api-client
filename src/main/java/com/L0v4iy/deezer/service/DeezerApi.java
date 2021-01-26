package com.L0v4iy.deezer.service;

import com.L0v4iy.deezer.io.controller.ResourceController;
import lombok.Getter;
import org.json.JSONObject;

public abstract class DeezerApi
{
    @Getter
    private ResourceController resourceController;

    /**
     * @param resourceController implementation of resource controller
     */
    protected DeezerApi(ResourceController resourceController)
    {
        this.resourceController = resourceController;
    }

    /**
     * do request to deezer api
     * @param method method to use on deezer api
     * @param data $_POST data
     * @return response
     */
    protected String callPrivateApi(String method, JSONObject data)
    {
        return resourceController.callPrivateApi(method, data);
    }

    protected String callPublicApi(String method, JSONObject data)
    {
        return resourceController.callPublicApi(method, data);
    }

}
