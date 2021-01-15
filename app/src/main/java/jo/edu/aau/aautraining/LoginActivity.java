package jo.edu.aau.aautraining;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import jo.edu.aau.aautraining.shared.AppConstants;
import jo.edu.aau.aautraining.shared.MyAppCompatActivity;
import jo.edu.aau.aautraining.shared.MySharedPreference;
import jo.edu.aau.aautraining.student.StudentMainActivity;
import jo.edu.aau.aautraining.supervisor.SupervisorMainActivity;
import jo.edu.aau.aautraining.trainer.TrainersMainActivity;

public class LoginActivity extends MyAppCompatActivity {

    EditText usernameEditText, passwordEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        usernameEditText = findViewById(R.id.usernameEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        Button loginButton = findViewById(R.id.loginButton);
        loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                tryLogin();
            }
        });
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        MySharedPreference mySharedPreference = new MySharedPreference(this);
        String userName = mySharedPreference.getUserName();
        if (!userName.isEmpty()) {
            usernameEditText.setText(userName);
        }
        String token = mySharedPreference.getToken();
        if (!token.isEmpty()) {
            tryLoginWithToken(token);
        }
    }

    private void tryLogin() {
        // read user name from edit text, trim to remove extra spaces
        String userName = usernameEditText.getText().toString().trim();
        //check if empty string (no input in edit text)
        if (userName.isEmpty()) {
            // display error on EditText
            usernameEditText.setError(getResources().getString(R.string.enter_username));
            return;
        }
        // read password from edit text
        String password = passwordEditText.getText().toString().trim();
        //check if empty string (no input in edit text)
        if (password.isEmpty()) {
            // display error on EditText
            passwordEditText.setError(getResources().getString(R.string.enter_password));
            return;
        }

        showProgressView();
        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                AppConstants.API_LOGIN_URL,
                response -> {
                    handleLoginResponse(response);
                    hideProgressView();
                }, error -> {
            hideProgressView();
            showSnackbar(R.string.error);
        }
        ) {
            @Override
            protected Map<String, String> getParams() {
                HashMap<String, String> params = new HashMap<>();
                params.put("user_name", userName);
                params.put("password", password);
                return params;
            }
        };
        addVolleyStringRequest(stringRequest);
    }

    private void updateFirebaseLogin() {

        String TAG = "FIREBASE_TOKEN";
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                        return;
                    }
                    // Get new FCM registration token
                    String token = task.getResult();
                    StringRequest stringRequest = new StringRequest(
                            Request.Method.POST,
                            AppConstants.UPDATE_FIREBASE_TOKEN,
                            null, null
                    ) {
                        @Override
                        protected Map<String, String> getParams() {
                            HashMap<String, String> params = new HashMap<>();
                            params.put("firebase_token", token);
                            return params;
                        }

                        @Override
                        public Map<String, String> getHeaders() {
                            HashMap<String, String> headers = new HashMap<>();
                            String token = mySharedPreference.getToken();
                            headers.put("Authorization", "Bearer " + token);
                            return headers;
                        }
                    };
                    RequestQueue requestQueue = Volley.newRequestQueue(LoginActivity.this);
                    requestQueue.add(stringRequest);
                });
    }

    private void tryLoginWithToken(String token) {
        showProgressView();
        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                AppConstants.API_TOKEN_LOGIN_URL,
                response -> {
                    handleLoginResponse(response);
                }, error -> {
            hideProgressView();
            showSnackbar(R.string.error);
        }
        ) {

            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + token);
                return headers;
            }
        };
        addVolleyStringRequest(stringRequest);
    }

    private void handleLoginResponse(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            if (jsonObject.getBoolean("valid")) {
                if (jsonObject.getBoolean("valid_login")) {
                    if (jsonObject.getBoolean("is_active")) {
                        updateFirebaseLogin();
                        String token = jsonObject.getString("token");
                        MySharedPreference mySharedPreference = new MySharedPreference(this);
                        mySharedPreference.setToken(token);
                        mySharedPreference.setUserName(usernameEditText.getText().toString());
                        String userRole = jsonObject.getString("user_role");
                        if (userRole.equalsIgnoreCase("student")) {
                            JSONObject infoJsonObject = jsonObject.getJSONObject("extra_info");
                            if (infoJsonObject.getBoolean("has_training")) {
                                Intent i = new Intent(LoginActivity.this, StudentMainActivity.class);
                                i.putExtra("studentId", infoJsonObject.getInt("student_id"));
                                i.putExtra("supervisorId", infoJsonObject.getInt("supervisor_id"));
                                i.putExtra("trainerId", infoJsonObject.getInt("trainer_id"));
                                i.putExtra("trainingId", infoJsonObject.getInt("training_id"));
                                startActivity(i);
                                finish();
                                return;
                            } else {
                                showSnackbar(R.string.student_has_no_training);
                            }
                        } else if (userRole.equalsIgnoreCase("trainer")) {
                            Intent i = new Intent(LoginActivity.this, TrainersMainActivity.class);
                            JSONObject infoJsonObject = jsonObject.getJSONObject("extra_info");
                            i.putExtra("trainer_id", infoJsonObject.getInt("trainer_id"));
                            i.putExtra("img_link", infoJsonObject.getString("img_link"));
                            i.putExtra("full_name", infoJsonObject.getString("full_name"));
                            startActivity(i);
                            finish();
                            return;
                        } else if (userRole.equalsIgnoreCase("supervisor")) {
                            Intent i = new Intent(LoginActivity.this, SupervisorMainActivity.class);
                            JSONObject infoJsonObject = jsonObject.getJSONObject("extra_info");
                            i.putExtra("supervisor_id", infoJsonObject.getInt("supervisor_id"));
                            i.putExtra("img_link", infoJsonObject.getString("img_link"));
                            i.putExtra("full_name", infoJsonObject.getString("full_name"));
                            startActivity(i);
                            finish();
                            return;
                        }
                    } else {
                        showSnackbar(R.string.user_inactive);
                    }
                } else {
                    showSnackbar(R.string.invalid_login);
                }
            } else {
                showSnackbar(R.string.error);
            }
            hideProgressView();
        } catch (JSONException e) {
            e.printStackTrace();
            showSnackbar(R.string.error);
        }
    }
}