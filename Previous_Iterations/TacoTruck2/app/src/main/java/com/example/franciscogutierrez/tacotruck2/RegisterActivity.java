package com.example.franciscogutierrez.tacotruck2;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import org.json.JSONObject;


public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Button registerButton = (Button) findViewById(R.id.register_button);

        final EditText etUsername = (EditText) findViewById(R.id.register_et_username);
        final EditText etPassword1 = (EditText) findViewById(R.id.register_et_password1);
        final EditText etPassword2 = (EditText) findViewById(R.id.register_et_password2);
        final EditText etFirstName = (EditText) findViewById(R.id.register_et_firstname);
        final EditText etLastName = (EditText) findViewById(R.id.register_et_lastname);

        final TextView registerStatus = (TextView) findViewById(R.id.register_status);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //get all fields as strings
                String username = etUsername.getText().toString();
                String password1 = etPassword1.getText().toString();
                String password2 = etPassword2.getText().toString();
                String firstName = etFirstName.getText().toString();
                String lastName = etLastName.getText().toString();

                //check if all fields are not null
                if (username.isEmpty() || password1.isEmpty() || password2.isEmpty() || firstName.isEmpty() || lastName.isEmpty()) {
                    registerStatus.setText("Fields cannot be empty.");
                } else if (!password1.equals(password2)) {
                    //check if password is the same for both password fields
                    registerStatus.setText("Passwords do not match.");
                } else {
                    //everything is okay, build url string
                    RadioButton rb = (RadioButton) findViewById(R.id.radioRegularUser);
                    String userType = "";
                    //usertype == 0 is for regular user, usertype == 1 is for truck driver
                    if (rb.isChecked()) {
                        userType = "0";
                    } else {
                        userType = "1";
                    }
                    password1 = Util.encryptPassword(password1);
                    String myURL = "http://cst438-1139.appspot.com/test?function=doRegister&username=" + username + "&password=" + password1 + "&firstName=" + firstName + "&lastName=" + lastName + "&type=" + userType;


                    //on successful register, log user in, and finish() activity
                    try {
                        String[] url = new String[]{myURL};
                        String output = new GetData().execute(url).get();
                        JSONObject jObject = new JSONObject(output);
                        String status = jObject.getString("status");

                        //status can be: usernameTaken, successfulRegistration, databaseError
                        if (status.equals("usernameTaken")) {
                            registerStatus.setText("That Username is already taken.");
                        } else if (status.equals("successfulRegistration")) {
                            registerStatus.setText("Registration Successful");
                            Intent intent = new Intent();
                            intent.putExtra("username", username);
                            intent.putExtra("firstName", firstName);
                            intent.putExtra("lastName", lastName);
                            intent.putExtra("userType", userType);
                            setResult(RESULT_OK, intent);
                            finish();
                        } else {
                            //database error
                            registerStatus.setText("Database Error, Please try later.");
                        }
                    } catch (Exception e) {

                    }
                }
            }
        });
    }
}

