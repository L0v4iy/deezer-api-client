package com.L0v4iy.deezer.io.controller;

import com.L0v4iy.deezer.io.ConnectionResources;
import com.L0v4iy.deezer.io.JSONLib;
import com.L0v4iy.deezer.io.dto.HttpClientAuth;
import com.L0v4iy.deezer.io.dto.Proxy;
import lombok.extern.java.Log;
import org.apache.http.*;
import org.apache.http.client.CookieStore;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.json.JSONObject;

import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * at first
 * prepare for active searching audio tracks
 * then
 * use
 */
@Log
public class HttpResourceController implements ResourceController
{

    private String apiToken = null;
    private boolean logedIn = false;

    private final HttpClientAuth clientAuth;
    private CloseableHttpClient httpClient;
    private static Logger apacheLogger = java.util.logging.Logger.getLogger("org.apache.http.client.protocol.ResponseProcessCookies");

    /**
     * @param clientAuth configuration class
     */
    public HttpResourceController(HttpClientAuth clientAuth)
    {
        apacheLogger.setLevel(Level.OFF);

        this.clientAuth = clientAuth;
        // create ARL cookie
        CookieStore cookieStore = new BasicCookieStore();
        BasicClientCookie arlCookie = createCookie("arl", clientAuth.getDeezerArl().getARL());
        cookieStore.addCookie(arlCookie);

        RequestConfig globalConfig = RequestConfig.custom()
                .setCookieSpec(CookieSpecs.DEFAULT)
                .build();
        HttpClientContext localContext = HttpClientContext.create();
        localContext.setCookieStore(cookieStore);
        httpClient = HttpClients.custom()
                .setDefaultRequestConfig(globalConfig)
                .setDefaultCookieStore(cookieStore).build();

        if (clientAuth.getProxy() != null) {
            Proxy p = clientAuth.getProxy();
            HttpHost proxy = new HttpHost(p.getHost(),p.getPort());
            httpClient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY,proxy);
        }
        // to wake up
        postLogin();
    }

    /**
     * @param request method completes request
     * @param targetClass $_GET method or $_POST
     * @param <T> $_GET method or $_POST
     * @return completed request
     */
    private <T> T configureRequest(HttpRequestBase request, Class<T> targetClass)
    {
        request.addHeader(HttpHeaders.USER_AGENT, ConnectionResources.USER_AGENT);
        request.addHeader(HttpHeaders.CONTENT_LANGUAGE, "en-US");
        request.addHeader(HttpHeaders.CACHE_CONTROL, "max-age=0");
        request.addHeader(HttpHeaders.ACCEPT, "*/*");
        request.addHeader(HttpHeaders.ACCEPT_CHARSET, "utf-8,ISO-8859-1;q=0.7,*;q=0.3");
        request.addHeader(HttpHeaders.ACCEPT_LANGUAGE, "en-US,en;q=0.9,en-US;q=0.8,en;q=0.7");
        request.addHeader(HttpHeaders.CONNECTION, "keep-alive");

        return targetClass.cast(request);
    }

    /**
     * request executor
     * @param request completed request
     * @return response
     * @throws IOException if something wrong
     */
    private String executeRequest(HttpRequestBase request) throws IOException
    {
        CloseableHttpResponse response = getHttpClient().execute(request);

        Scanner sc = new Scanner(response.getEntity().getContent());
        StringBuilder result = new StringBuilder();
        while(sc.hasNext()) {
            result.append(sc.nextLine());
        }
        response.close();
        return result.toString();
    }

    @Override
    public String getData(String uri) throws IOException
    {
        return executeRequest(getResponseData(uri));
    }

    @Override
    public String postData(String uri, JSONObject data) throws IOException
    {
        return executeRequest(postResponseData(uri, data));
    }

    private HttpGet getResponseData(String uri)
    {
        HttpGet request = new HttpGet(uri);
        return configureRequest(request, HttpGet.class);
    }

    private HttpPost postResponseData(String uri, JSONObject data)
    {
        HttpPost request = new HttpPost(uri);
        if (data != null)
        {
            HttpEntity stringEntity = new StringEntity(data.toString(), ContentType.APPLICATION_JSON);
            request.setEntity(stringEntity);
        }
        return configureRequest(request, HttpPost.class);
    }

    /**
     * method to interact with deezer
     * @param method deezer API method
     * @param args $_POST args
     * @return $_POST response
     */
    @Override
    public String callPrivateApi(String method, JSONObject args)
    {
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        URIBuilder query = new URIBuilder();
        query.addParameter("api_version", "1.0");

        String apiToken = "null";
        if (!method.equals("deezer.getUserData"))
            apiToken = getApiToken();

        query.addParameter("api_token", apiToken);

        query.addParameter("input", "3");
        query.addParameter("method", method);
        // Why use this?
        query.addParameter("cid", generateCID());

        String uri = String.format(ConnectionResources.PRIVATE_API_URL,query);

        HttpPost request = postResponseData(uri, args);
        String result = null;
        try {
            result = executeRequest(request);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (result == null) return null;
        String error = JSONLib.parseJSON(result, new String[]{"error"});
        if (error != null)
        {
            log.info("Error on calling private api");
            log.info(error);
        }
        return result;
    }

    @Override
    public String callPublicApi(String method, JSONObject args)
    {
        return null;
    }

    /**
     * @return api token
     */
    public String getApiToken()
    {
        // TODO: 026 26.01.21 may be it s not a good idea
        // as I see token not change during session
        // what token would be in the different session i dont know
        if (apiToken != null)
        {
            return apiToken;
        }
        String method = "deezer.getUserData";
        String result = callPrivateApi(method, null);
        String formLogin = JSONLib.parseJSON(result, new String[]{"results", "checkFormLogin"});
        String form = JSONLib.parseJSON(result, new String[]{"results", "checkForm"});
        if (form != null)
        {
            // good on init
            apiToken = form;
            return apiToken;
        }
        if (formLogin != null)
        {
            // bad on init
            apiToken = formLogin;
            return apiToken;
        }
        log.warning("Found no api token");
        return null;
    }

    /**
     * @return cid for 1-st initialization
     * required 1 time
     */
    private String generateCID()
    {
        StringBuilder builder = new StringBuilder();
        Random rand = new Random();
        for (int i = 0; i< 9; i++)
        {
            int x = rand.nextInt(10);
            builder.append(x);
        }
        return builder.toString();
    }

    private BasicClientCookie createCookie(String key, String val)
    {
        BasicClientCookie cookie = new BasicClientCookie(key, val);

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, 100);
        Date date = calendar.getTime();

        cookie.setExpiryDate(date);
        cookie.setDomain(".deezer.com");
        cookie.setPath("/");
        cookie.setVersion(1);
        cookie.setComment("");
        cookie.setValue(val);
        return cookie;
    }


    /**
     * log in by post request
     * check if everything is correct and ready to grab
     */
    private void postLogin()
    {
        // required to collect cookies
        HttpGet responce = getResponseData("https://www.deezer.com/");
        try {
            executeRequest(responce);
        } catch (IOException e) {
            e.printStackTrace();
        }

        String method = "deezer.getUserData";
        String result = callPrivateApi(method, null);

        String login = JSONLib.parseJSON(result, new String[]{"results", "USER", "USER_ID"});
        assert login != null;
        if (!login.equals("0"))
        {
            System.out.println("LogIn success");
            logedIn = true;
            return;
        }
        System.out.println("Error on LogIn");
    }

    public CloseableHttpClient getHttpClient()
    {
        return httpClient;
    }

    public HttpClientAuth getClientAuth() {
        return clientAuth;
    }

    public boolean isLogedIn() {
        return logedIn;
    }
}