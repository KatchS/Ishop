package com.sk.ishop.login;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.example.sk.ishop.R;
import com.sk.ishop.infrastructure.BaseFragment;
import com.sk.ishop.server_request.HttpRequest;

/**
 * Created by sk on 13/12/2017.
 */

public class LoginFragment extends BaseFragment implements View.OnClickListener {

    private final String TAG = "LoginFragment";

    private EditText email,password;
    private View error;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_login_fragment_login,container,false);

        email = view.findViewById(R.id.fragment_login_email_edit_text);
        password = view.findViewById(R.id.fragment_login_password_edit_text);
        error = view.findViewById(R.id.login_fragment_error_text_view);
        view.findViewById(R.id.fragment_login_login_button).setOnClickListener(this);
        view.findViewById(R.id.fragment_login_register_button).setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.fragment_login_login_button:
                login();
                break;
            case R.id.fragment_login_register_button:
                ((LoginActivity)getActivity()).startRegisterFragment();
                break;
        }
    }

    private void login() {
        boolean valid = true;
        String mail = email.getText().toString();
        String pass = password.getText().toString();

        // check all the info at once, so all the errors will be given together for a better user experience
        if(!emailValidate(mail)){
            valid = false;
            email.setError(getString(R.string.email_validate_error));
        }
        if(!passwordValidate(pass)){
            valid = false;
            password.setError(getString(R.string.password_validate_error));
        }

        if(valid){
            new HttpRequest(prepareJsonObject(mail, pass, 1), ((LoginActivity) getActivity()), true).start();
        }
    }

    /**
     * change the visibility of error text view on the screen
     * @param show true if you want to show, false otherwise
     */
    public void showError(boolean show){
        error.setVisibility(show ? View.VISIBLE : View.GONE);
    }

}
