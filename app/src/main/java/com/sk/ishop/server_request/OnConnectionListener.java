package com.sk.ishop.server_request;

import org.json.JSONObject;

/**
 * Created by sk on 13/12/2017.
 */

public interface OnConnectionListener {

    /**
     * used if the connection was successful
     *
     * @param data the answer of the server wrapped in a Json object (according to sever protocol)
     */
    void onSuccess(JSONObject data);


    /**
     * used if the connection had failed.
     * the data that has return doesn't match the needed format (indication of technical issue)
     */
    void onFailure();
}
