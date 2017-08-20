package me.esca.activities;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.IOException;
import ca.mimic.oauth2library.OAuth2Client;
import ca.mimic.oauth2library.OAuthResponse;
import me.esca.R;
import me.esca.utils.security.authentication.Message;


/**
 * Created by Me on 18/08/2017.
 */
public class LogInActivity extends Activity {

    private EditText usernameEditText;
    private EditText passwordEditText;
    private Button logInButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.log_in_layout);
        usernameEditText = (EditText) findViewById(R.id.usernameEditText);
        passwordEditText = (EditText) findViewById(R.id.passwordEditText);
        logInButton = (Button) findViewById(R.id.logInButton);

        logInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SendCredentialsAndGetAccessToken().execute();
            }
        });

    }

    private class SendCredentialsAndGetAccessToken extends AsyncTask<Void, Message, Message>{

        private String username;
        private String password;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            username = usernameEditText.getText().toString().trim();
            password = passwordEditText.getText().toString().trim();
        }

        @Override
        protected Message doInBackground(Void... params) {

            OAuth2Client.Builder builder = new OAuth2Client.Builder("android-escaAndroid", "123456",
                    "http://escaws.herokuapp.com/oauth/token")
                    .grantType("password")
                    .scope("write")
                    .username(username)
                    .password(password);

            try {
                OAuthResponse response = builder.build().requestAccessToken();
                String accessToken = response.getAccessToken();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Message message) {
            super.onPostExecute(message);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
