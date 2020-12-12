package jo.edu.aau.aautraining;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.Nullable;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import jo.edu.aau.aautraining.shared.AppConstants;
import jo.edu.aau.aautraining.shared.MyAppCompatActivity;
import jo.edu.aau.aautraining.shared.MySharedPreference;
import jo.edu.aau.aautraining.student.StudentMainActivity;
import jo.edu.aau.aautraining.trainer.TrainersMainActivity;

public class LoginActivity extends MyAppCompatActivity {

    EditText usernameEditText, passwordEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        usernameEditText = findViewById(R.id.usernameEditText);
        passwordEditText = findViewById(R.id.passwordEditText);

        findViewById(R.id.loginButton).setOnClickListener(new View.OnClickListener() {
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
        String userName=mySharedPreference.getUserName();
        if(!userName.isEmpty()){
            usernameEditText.setText(userName);
        }
        String token=mySharedPreference.getToken();
        if(!token.isEmpty()){
            tryLoginWithToken(token);
        }
    }

    private void tryLogin() {
        // read user name from edit text, trim to remove extra spaces
        String userName = usernameEditText.getText().toString().trim();
        //check if empty string (no input in edit text)
        if (userName.isEmpty()) {
            // display error on edittext
            usernameEditText.setError(getResources().getString(R.string.enter_username));
            return;
        }
        // read password afroma edita text
        String password = passwordEditText.getText().toString().trim();
        //check if empty string (no input in edit text)
        if (password.isEmpty()) {
            // display error on edittext
            usernameEditText.setError(getResources().getString(R.string.enter_password));
            return;
        }

        showProgressView();
        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                AppConstants.API_LOGIN_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        System.out.println("Login response : "+response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.getBoolean("valid")) {
                                if (jsonObject.getBoolean("valid_login")) {
                                    if (jsonObject.getBoolean("is_active")) {
                                        String token = jsonObject.getString("token");
                                        MySharedPreference mySharedPreference = new MySharedPreference(LoginActivity.this);
                                        mySharedPreference.setToken(token);
                                        mySharedPreference.setUserName(userName);
                                        String userRole = jsonObject.getString("user_role");
                                        if (userRole.equalsIgnoreCase("student")) {
                                            JSONObject infoJsonObject = jsonObject.getJSONObject("extra_info");
                                            Intent i = new Intent(LoginActivity.this, StudentMainActivity.class);
                                            i.putExtra("studentId",infoJsonObject.getInt("student_id"));
                                            i.putExtra("supervisorId",infoJsonObject.getInt("supervisor_id"));
                                            i.putExtra("trainerId",infoJsonObject.getInt("trainer_id"));
                                            i.putExtra("trainingId",infoJsonObject.getInt("training_id"));
                                            startActivity(i);
                                            finish();
                                            return;
                                        }else
                                        if (userRole.equalsIgnoreCase("trainer")) {
                                            Intent i = new Intent(LoginActivity.this, TrainersMainActivity.class);
                                            JSONObject infoJsonObject = jsonObject.getJSONObject("extra_info");
                                            i.putExtra("trainer_id",infoJsonObject.getInt("trainer_id"));
                                            i.putExtra("img_link",infoJsonObject.getString("img_link"));
                                            i.putExtra("full_name",infoJsonObject.getString("full_name"));
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
                        } catch (JSONException e) {
                            e.printStackTrace();
                            showSnackbar(R.string.error);
                        }

                        hideProgressView();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                hideProgressView();
                showSnackbar(R.string.error);
                //Log.e("VolleyError", error.getLocalizedMessage());
            }
        }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<>();
                params.put("user_name", userName);
                params.put("password", password);
                return params;
            }
        };
        addVolleyStringRequest(stringRequest);
    }

    private void tryLoginWithToken(String token) {
        showProgressView();
        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                AppConstants.API_TOKEN_LOGIN_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        System.out.println("Login Response " + response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.getBoolean("valid")) {
                                if (jsonObject.getBoolean("valid_login")) {
                                    if (jsonObject.getBoolean("is_active")) {
                                        String userRole = jsonObject.getString("user_role");
                                        if (userRole.equalsIgnoreCase("student")) {
                                            JSONObject infoJsonObject = jsonObject.getJSONObject("extra_info");
                                            Intent i = new Intent(LoginActivity.this, StudentMainActivity.class);
                                            i.putExtra("studentId",infoJsonObject.getInt("student_id"));
                                            i.putExtra("supervisorId",infoJsonObject.getInt("supervisor_id"));
                                            i.putExtra("trainerId",infoJsonObject.getInt("trainer_id"));
                                            i.putExtra("trainingId",infoJsonObject.getInt("training_id"));
                                            startActivity(i);
                                            finish();
                                            return;
                                        }else
                                        if (userRole.equalsIgnoreCase("trainer")) {
                                            Intent i = new Intent(LoginActivity.this, TrainersMainActivity.class);
                                            JSONObject infoJsonObject = jsonObject.getJSONObject("extra_info");
                                            i.putExtra("trainer_id", infoJsonObject.getInt("trainer_id"));
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
                        } catch (JSONException e) {
                            e.printStackTrace();
                            showSnackbar(R.string.error);
                        }

                        hideProgressView();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                hideProgressView();
                showSnackbar(R.string.error);
                Log.e("VolleyError", error.getLocalizedMessage());
            }
        }
        ) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer "+token);
                return headers;
            }
        };
        addVolleyStringRequest(stringRequest);
    }

}