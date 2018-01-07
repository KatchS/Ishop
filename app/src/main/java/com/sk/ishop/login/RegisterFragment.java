package com.sk.ishop.login;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.example.sk.ishop.R;
import com.sk.ishop.infrastructure.BaseFragment;
import com.sk.ishop.server_request.HttpRequest;

/**
 * Created by sk on 17/12/2017.
 */

public class RegisterFragment extends BaseFragment {

    private final String TAG = " RegisterFragment";

    private EditText email,password,confPass;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.activity_login_fragment_register, container,false);

        email = (EditText) view.findViewById(R.id.register_fragment_email_edit_text);
        password = (EditText) view.findViewById(R.id.register_fragment_password_edit_text);
        confPass = (EditText) view.findViewById(R.id.register_fragment_confirm_password_edit_text);

        view.findViewById(R.id.register_fragment_register_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View button) {
                boolean valid = true;
                String pass = password.getText().toString();
                String mail = email.getText().toString();

                // check all the info at once, so all the errors will be given together for a better user experience
                if(!emailValidate(mail)){
                    valid = false;
                    // the email has invalid format
                    email.setError(getString(R.string.email_validate_error));
                }
                if(!passwordValidate(pass)){
                    valid = false;
                    // password is to short
                    password.setError(getString(R.string.password_validate_error));
                }
                if(!pass.equals(confPass.getText().toString())){
                    valid = false;
                    // the confirmed password did not match the original one
                    confPass.setError(getString(R.string.confirm_password_error));
                }

                if(valid){
                    new HttpRequest(prepareJsonObject(mail, pass, 3), ((LoginActivity) getActivity()), true).start();
                }
            }
        });
        return view;
    }

    public void emailAlreadyExist(){
        email.setError(getString(R.string.email_taken));
    }
}
