package jo.edu.aau.aautraining.trainer.ui.trainee.schedule;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import jo.edu.aau.aautraining.R;
import jo.edu.aau.aautraining.shared.AppConstants;
import jo.edu.aau.aautraining.shared.MyDatePicker;
import jo.edu.aau.aautraining.shared.MyFragment;
import jo.edu.aau.aautraining.shared.MySharedPreference;
import jo.edu.aau.aautraining.trainer.TrainersMainActivity;
import jo.edu.aau.aautraining.trainer.ui.trainee.TraineeModel;

public class TraineeScheduleAddFragment extends MyFragment {

    private EditText nameEditText,dateEditText,placeEditText;
    private String traineeName;
    private int trainingId;

    public TraineeScheduleAddFragment() {

    }
    public static TraineeScheduleAddFragment newInstance() {
        TraineeScheduleAddFragment fragment = new TraineeScheduleAddFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root =  inflater.inflate(R.layout.trainee_schedule_add_fragment, container, false);
        nameEditText = root.findViewById(R.id.trainee_schedule_add_trainee_name);
        dateEditText = root.findViewById(R.id.trainee_schedule_add_date);
        dateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyDatePicker.getInstance(getContext())
                        .setTitleResId(R.string.select_date)
                        .selectDateFor(dateEditText);
            }
        });
        placeEditText = root.findViewById(R.id.trainee_schedule_add_place);
        root.findViewById(R.id.trainee_schedule_add_save_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveNewSchedule();
            }
        });
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getArguments() != null) {
            TraineeScheduleAddFragmentArgs fragmentArgs = TraineeScheduleAddFragmentArgs.fromBundle(getArguments());
            trainingId = fragmentArgs.getTrainingId();
            traineeName = fragmentArgs.getTraineeName();
            nameEditText.setText(traineeName);
        }
    }

    private void saveNewSchedule() {
        String dateString = dateEditText.getText().toString();
        if(dateString.isEmpty()){
            dateEditText.setError(getResources().getString(R.string.enter_schedule_date));
            return;
        }else{
            dateEditText.setError(null);
        }
        String place = placeEditText.getText().toString().trim();
        if(place.isEmpty()){
            placeEditText.setError(getResources().getString(R.string.enter_schedule_place));
            return;
        }else{
            placeEditText.setError(null);
        }
        showProgressView();
        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                AppConstants.API_ADD_TRAINEE_SCHEDULE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        try {
                            String response = handleApiResponse(s);
                            JSONObject jsonObject = new JSONObject(response);
                            if(jsonObject.getBoolean("inserted")){
                                Navigation.findNavController(getView()).navigateUp();
                            }else{
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

            @Override
            protected Map<String, String> getParams() {
                HashMap<String,String> params = new HashMap<>();
                params.put("training_id", String.valueOf(trainingId));
                params.put("training_date",dateEditText.getText().toString());
                params.put("training_place",placeEditText.getText().toString().trim());
                return params;
            }
        };
        addVolleyStringRequest(stringRequest);
    }
}