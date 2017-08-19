package me.esca.activities;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.oauth2.client.DefaultOAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.token.grant.client.ClientCredentialsResourceDetails;
import org.springframework.security.oauth2.client.token.grant.password.ResourceOwnerPasswordResourceDetails;
import org.springframework.security.oauth2.common.AuthenticationScheme;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import me.esca.R;
import me.esca.model.Recipe;
import me.esca.utils.security.authentication.Message;

import static java.util.Arrays.asList;
import static me.esca.services.escaWS.Utils.ALL_RECIPES_URL;
import static me.esca.services.escaWS.Utils.MAIN_DOMAIN_NAME;


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

                ResourceOwnerPasswordResourceDetails resourceDetails = new ResourceOwnerPasswordResourceDetails();
            resourceDetails.setUsername(username);
            resourceDetails.setPassword(password);
            resourceDetails.setTokenName("token");
            resourceDetails.setAccessTokenUri("http://escaws.herokuapp.com/oauth/token");
            resourceDetails.setClientId("android-escaAndroid");
            resourceDetails.setClientSecret("123456");
            resourceDetails.setGrantType("authorization_code");
            resourceDetails.setScope(asList("write"));
            resourceDetails.setAuthenticationScheme(AuthenticationScheme.header);


            DefaultOAuth2ClientContext clientContext = new DefaultOAuth2ClientContext();

            OAuth2RestTemplate restTemplate = new OAuth2RestTemplate(resourceDetails, clientContext);
//            List<HttpMessageConverter<?>> messageConvertersList = new ArrayList<>();
//            messageConvertersList.add(new MappingJackson2HttpMessageConverter());
//            restTemplate.setMessageConverters(messageConvertersList);

            restTemplate.getAccessToken();

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
