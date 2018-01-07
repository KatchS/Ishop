package com.sk.ishop.server_request;

import android.os.Handler;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by sk on 13/12/2017.
 */

public class HttpRequest extends Thread {

    private Handler handler;
    private JSONObject data;
    private OnConnectionListener listener;
    private boolean toLogin;

    private final String TAG = "HttpRequest";

    private final String SERVER_ADDRESS = "http://35.205.179.103/ishop/";

    public HttpRequest(JSONObject data, OnConnectionListener listener, boolean toLogin){
        handler = new Handler();
        this.data = data;
        this.listener = listener;
        this.toLogin = toLogin;
    }

    @Override
    public void run() {
        HttpURLConnection connection = null;
        try {
            URL url = new URL(SERVER_ADDRESS + (toLogin ? "login" : "product"));

            connection = preparedConnection(url);

            connection.getOutputStream().write(data.toString().getBytes());
            Log.i(TAG, "run: the data has been sent");

            InputStream in = connection.getInputStream();
            final JSONObject dataFromResponse = getResponse(in);

            doInHanlder(dataFromResponse);

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            // a problem with the returned data
            doInHanlder();
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            if(connection != null)
                connection.disconnect();
        }
    }

    /**
     * notify the listener that the connection had succeeded inside a handler
     *
     * @param dataFromResponse the Json object to be sent to onSuccess
     */
    private void doInHanlder(final JSONObject dataFromResponse) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                listener.onSuccess(dataFromResponse);
            }
        });
    }

    /**
     * notify the listener that the connection had failed inside a handler
     */
    private void doInHanlder() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                listener.onFailure();
            }
        });
    }

    /**
     * creates an HttpURLConnection object and set its properties accordingly
     *
     * @param url the address to which the HttpURLConnection will refer to
     * @return an adjusted HttpURLConnection object
     * @throws IOException in case the connection was interrupted
     */
    private HttpURLConnection preparedConnection(URL url) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        connection.setDoInput(true);
        connection.setRequestProperty("Content-Type","application/json");
        Log.i(TAG, "preparedConnection: a connection has been prepared");
        return connection;
    }

    /**
     * handle the response form the server, creates a Json object from the returned data
     *
     * @param in the InputStream that will be used to read the massage
     * @return an Json object contain the data from the response
     * @throws IOException in case the connection interrupted
     * @throws JSONException in case a Json object cannot be created from the received data
     */
    private JSONObject getResponse(InputStream in) throws IOException, JSONException {
        byte[] buffer = new byte[128];
        int actuallyRead;
        StringBuilder sb = new StringBuilder();
        while((actuallyRead = in.read(buffer)) != -1){
            sb.append(new String(buffer,0,actuallyRead));
        }
        Log.i(TAG, "getResponse: a response has been received");
        return new JSONObject(sb.toString());
    }
}
