package com.sk.ishop.infrastructure;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.example.sk.ishop.R;
import com.sk.ishop.db.AppDataBase;

/**
 * Created by sk on 17/12/2017.
 */

public class BaseActivity extends AppCompatActivity {

    protected SharedPreferences preferences;
    protected AppDataBase db;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        preferences = getSharedPreferences(Variables.LOGGED_IN_USER,MODE_PRIVATE);
        db = AppDataBase.getINSTACE(this);
    }

    /**
     * what happens when the connection to the server fails.
     *
     * pop up a toast that notify the user
     */
    protected void failed(){
        Toast.makeText(this, R.string.server_connection_error,Toast.LENGTH_LONG).show();
    }
}
