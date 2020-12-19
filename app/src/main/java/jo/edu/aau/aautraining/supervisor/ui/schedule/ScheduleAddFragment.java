package jo.edu.aau.aautraining.supervisor.ui.schedule;

import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import jo.edu.aau.aautraining.R;
import jo.edu.aau.aautraining.shared.AppConstants;
import jo.edu.aau.aautraining.shared.MyDatePicker;
import jo.edu.aau.aautraining.shared.MyFragment;
import jo.edu.aau.aautraining.shared.MySharedPreference;
import jo.edu.aau.aautraining.shared.MyTimePicker;

public class ScheduleAddFragment extends MyFragment {

    private ScheduleControlViewModel mViewModel;
    private EditText studentNameET, dateET, timeET;
    private int trainingId;
    private String studentName;

    public static ScheduleAddFragment newInstance() {
        return new ScheduleAddFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mViewModel = ViewModelProviders.of(this).get(ScheduleControlViewModel.class);
        View root = inflater.inflate(R.layout.supervisor_schedule_add_fragment, container, false);
        studentNameET = root.findViewById(R.id.supervisor_schedule_add_student_name);
        mViewModel.getStudentName().observe(getViewLifecycleOwner(), studentNameET::setText);
        dateET = root.findViewById(R.id.supervisor_schedule_add_date);
        dateET.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyDatePicker.getInstance(getContext())
                        .setTitleResId(R.string.select_date)
                        .selectDateFor(dateET);
            }
        });
        timeET = root.findViewById(R.id.supervisor_schedule_add_time);
        timeET.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyTimePicker.getInstance(getContext()).pickTimeFor(timeET);
            }
        });
        root.findViewById(R.id.supervisor_schedule_add_save_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                trySaveSchedule();
            }
        });
        return root;
    }

    private void trySaveSchedule() {
        if (dateET.getText().toString().isEmpty()) {
            showSnackbar(R.string.select_date);
            return;
        }
        if (timeET.getText().toString().isEmpty()) {
            showSnackbar(R.string.select_time);
            return;
        }
        showProgressView();
        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                AppConstants.SUPERVISOR_ADD_SCHEDULE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        try {
                            String response = handleApiResponse(s);
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.getBoolean("inserted")) {
                                Navigation.findNavController(getView()).navigateUp();
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
                HashMap<String, String> params = new HashMap<>();
                params.put("training_id", String.valueOf(trainingId));
                params.put("sch_date", dateET.getText().toString());
                params.put("sch_time", timeET.getText().toString().trim());
                return params;
            }
        };
        addVolleyStringRequest(stringRequest);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ScheduleAddFragmentArgs fragmentArgs = ScheduleAddFragmentArgs.fromBundle(getArguments());
        trainingId = fragmentArgs.getTrainingId();
        mViewModel.setStudentName(fragmentArgs.getStudentName());
    }

}