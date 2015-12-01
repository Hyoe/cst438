package com.example.franciscogutierrez.tacotruck2;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Button loginButton = (Button) findViewById(R.id.login_button);

        final EditText etUsername = (EditText) findViewById(R.id.login_et_username);
        final EditText etPassword = (EditText) findViewById(R.id.login_et_password);

        final TextView loginStatus = (TextView) findViewById(R.id.login_status);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = etUsername.getText().toString();
                String password = etPassword.getText().toString();

                //check if fields are not empty
                if (username.isEmpty() || password.isEmpty()) {
                    loginStatus.setText("Fields cannot be empty.");
                } else {
                    //fields are not empty, call database
                    String myURL = "http://cst438-1139.appspot.com/test?function=doLogin&username=" + username + "&password=" + password;

                    try {
                        String[] url = new String[]{myURL};
                        String output = new GetData().execute(url).get();
                        JSONObject jObject = new JSONObject(output);
                        String status = jObject.getString("status");

                        if (status.equals("incorrectUsernamePassword")) {
                            loginStatus.setText("Incorrect Username/Password");
                        } else {
                            //good login, save variables and return to main activity
                            loginStatus.setText("Login Successful");
                            Intent intent = new Intent();
                            intent.putExtra("username", jObject.getString("username"));
                            intent.putExtra("firstName", jObject.getString("firstName"));
                            intent.putExtra("lastName", jObject.getString("lastName"));
                            intent.putExtra("userType", jObject.getString("userType"));
                            setResult(RESULT_OK, intent);
                            finish();
                        }

                    } catch (Exception e) {

                    }
                }


            }
        });
    }
}
