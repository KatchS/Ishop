package com.sk.ishop.infrastructure;

import android.app.Fragment;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by sk on 17/12/2017.
 */

public class BaseFragment extends Fragment {

    /**
     * validate the format of a given email address
     *
     * @param emailAddress a String represent an email address
     * @return true if the String is in a right format, false otherwise
     */
    public static boolean emailValidate(String emailAddress){
        String ePattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(ePattern);
        java.util.regex.Matcher m = p.matcher(emailAddress);
        return m.matches();
    }

    /**
     * validate the format of a given password
     *
     * @param password a String represent the password
     * @return true if the String is in a right format, false otherwise
     */
    public static boolean passwordValidate(String password){
        if(password.length() > 5){
            return true;
        }
        return false;
    }

    /**
     * make an Json object with the given data (prepare the data to be sent to the server)
     *
     * @param email the email that was given by the user
     * @param password the password that was given by the user
     * @param action which action the server suppose to execute
     * @return an Json object that is ready to be sent to the server (according to API)
     */
    protected JSONObject prepareJsonObject(String email, String password, int action) {
        JSONObject output = new JSONObject();
        try {
            output.put("action",action);
            output.put("email", email);
            output.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return output;
    }
}
