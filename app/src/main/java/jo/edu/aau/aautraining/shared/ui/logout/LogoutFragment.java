package jo.edu.aau.aautraining.shared.ui.logout;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.Navigation;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import jo.edu.aau.aautraining.LoginActivity;
import jo.edu.aau.aautraining.R;
import jo.edu.aau.aautraining.shared.AppConstants;
import jo.edu.aau.aautraining.shared.MyFragment;
import jo.edu.aau.aautraining.shared.MySharedPreference;

public class LogoutFragment extends MyFragment {


    public LogoutFragment() {
        // Required empty public constructor
    }

    public static LogoutFragment newInstance(String param1, String param2) {
        LogoutFragment fragment = new LogoutFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.shared_logout_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.shared_logout_cancelBtn).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Navigation.findNavController(getView()).navigateUp();
                    }
                }
        );
        view.findViewById(R.id.shared_logout_confirmBtn).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        tryLogout();
                    }
                }
        );
    }

    private void tryLogout() {
        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                AppConstants.API_LOGOUT_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        System.out.println(response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.getBoolean("valid") && jsonObject.getBoolean("logout")) {
                                MySharedPreference mySharedPreference = new MySharedPreference(getContext());
                                mySharedPreference.setToken("");
                                Intent i = new Intent(getContext(), LoginActivity.class);
                                startActivity(i);
                                getActivity().finish();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            showSnackbar();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                showSnackbar();
            }
        }
        ) {
            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<>();
                MySharedPreference mySharedPreference = new MySharedPreference(getContext());
                headers.put("Authorization", "Bearer " + mySharedPreference.getToken());
                return headers;
            }
        };
        addVolleyStringRequest(stringRequest);
    }
}