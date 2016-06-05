package habanerohoy.hackbanero.com.elhabanerohoy;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.parse.CountCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

public class LoginActivity extends AppCompatActivity {

    private LoginButton loginButton;
    private CallbackManager callbackManager;
    private Context mainContext;
    private String email, name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mainContext = this;
        callbackManager = CallbackManager.Factory.create();

        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser != null) {
            // Send logged in users to Welcome.class
            Intent intent = new Intent(LoginActivity.this,MainActivity.class);
            startActivity(intent);
            finish();
        } else {

        }

        loginButton = (LoginButton) findViewById(R.id.login_button);

        // Other app specific specialization

        loginButton.setReadPermissions(Arrays.asList(
                "public_profile", "email"));
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {

            //codigo para llamar el id, nombre y email
            @Override
            public void onSuccess(LoginResult loginResult) {
                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                try{
                                    email = object.getString("email");
                                    name = object.getString("name");

                                }  catch (JSONException ex) {
                                    ex.printStackTrace();
                                    Log.e("error",ex.getMessage());
                                }
                                CheckUserOnPArse();

                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields","id,name,email");
                request.setParameters(parameters);
                request.executeAsync();
            }


            @Override
            public void onCancel() {
                Log.i("error","login attempt canceled.");
            }

            @Override
            public void onError(FacebookException e) {
                Log.i("error","login attempt failed.");
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        //para cambiar la ventana al momento de oprimir el boton de login y pasar los datos a registro

    }

    private void SaveNewUserOnParse(){

        ParseObject usuario = new ParseObject("Usuarios");
        usuario.put("nombre", name);
        usuario.put("email", email);

        usuario.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {

                if (e == null) {
                    ChacheUserAndEnter();

                } else {
                    Toast.makeText(mainContext,"error",Toast.LENGTH_SHORT).show();
                }


            }
        });

    }

    private void CheckUserOnPArse(){

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Usuarios");
        query.whereEqualTo("email", email);
        query.countInBackground(new CountCallback() {
            public void done(int count, ParseException e) {
                if (e == null) {
                    // The count request succeeded. Log the count
                    if(count == 0){
                        SaveNewUserOnParse();
                    }else{
                        ChacheUserAndEnter();
                    }
                } else {
                    // The request failed
                }
            }
        });

    }

    private void ChacheUserAndEnter(){
        SharedPreferences prefs = getSharedPreferences("Preferencias",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("email", email);
        editor.putString("nombre", name);
        editor.commit();

        Intent i = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(i);
    }

}
