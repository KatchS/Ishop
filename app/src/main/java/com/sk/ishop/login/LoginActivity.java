package com.sk.ishop.login;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.sk.ishop.MainActivity;
import com.example.sk.ishop.R;
import com.sk.ishop.infrastructure.BaseActivity;
import com.sk.ishop.server_request.OnConnectionListener;

import org.json.JSONException;
import org.json.JSONObject;

import static com.sk.ishop.infrastructure.Variables.LOGGED_USER;

public class LoginActivity extends BaseActivity implements OnConnectionListener {

    private final String TAG = "LoginActivity";

    private LoginFragment loginFragment;
    private RegisterFragment registerFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getSupportActionBar().hide();

        db.close(); // the connection is not needed in the login section

        loginFragment = new LoginFragment();
        getFragmentManager()
                .beginTransaction()
                .add(R.id.activity_login_frame_layout,loginFragment)
                .commit();

    }


    @Override
    public void onSuccess(JSONObject data) {
        try {
            boolean status = data.getBoolean("status");
            if(status) {
                // login or registration succeed
                loginFragment.showError(false);
                acceptLoginInfo(data.getLong("user_id"));
            }else{
                int action = data.getInt("action");
                switch(action){
                    case 1:
                        // login failed, the data did not match the servers data
                        loginFragment.showError(true);
                        break;
                    case 3:
                        // registration failed, email already exist in the DB
                        registerFragment.emailAlreadyExist();
                        break;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onFailure() {
        failed();
    }

    /**
     * puts the data from the requested server in the SharedPreferences file and move to MainActivity
     *
     * @param userID a String represent the user ID
     */
    protected void acceptLoginInfo(long userID){
        preferences.edit().putLong(LOGGED_USER,userID).apply();
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    public void startRegisterFragment(){
        registerFragment = new RegisterFragment();
        getFragmentManager()
                .beginTransaction()
                .addToBackStack(null)
                .replace(R.id.activity_login_frame_layout,registerFragment)
                .commit();
    }
}
