package jo.edu.aau.aautraining.supervisor.ui.schedule;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import jo.edu.aau.aautraining.R;
import jo.edu.aau.aautraining.shared.AppConstants;
import jo.edu.aau.aautraining.shared.MyConfirmation;
import jo.edu.aau.aautraining.shared.MyDatePicker;
import jo.edu.aau.aautraining.shared.MyFragment;
import jo.edu.aau.aautraining.shared.MySharedPreference;
import jo.edu.aau.aautraining.shared.MyTimePicker;

public class ScheduleEditFragment extends MyFragment {

    private ScheduleControlViewModel mViewModel;
    private EditText studentNameET, dateET, timeET;
    private int scheduleId;

    public static ScheduleEditFragment newInstance() {
        return new ScheduleEditFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        mViewModel = ViewModelProviders.of(this).get(ScheduleControlViewModel.class);
        View root = inflater.inflate(R.layout.supervisor_schedule_edit_fragment, container, false);
        studentNameET = root.findViewById(R.id.supervisor_schedule_edit_student_name);
        mViewModel.getStudentName().observe(getViewLifecycleOwner(), studentNameET::setText);
        dateET = root.findViewById(R.id.supervisor_schedule_edit_date);
        mViewModel.getScheduleDate().observe(getViewLifecycleOwner(), dateET::setText);
        mViewModel.getScheduleDate().observe(getViewLifecycleOwner(), dateET::setText);
        dateET.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyDatePicker.getInstance(getContext())
                        .setTitleResId(R.string.select_date)
                        .selectDateFor(dateET);
            }
        });
        timeET = root.findViewById(R.id.supervisor_schedule_edit_time);
        mViewModel.getScheduleTime().observe(getViewLifecycleOwner(), timeET::setText);
        timeET.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyTimePicker.getInstance(getContext()).pickTimeFor(timeET);
            }
        });
        root.findViewById(R.id.supervisor_schedule_edit_save_btn).setOnClickListener(new View.OnClickListener() {
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
                AppConstants.SUPERVISOR_EDIT_SCHEDULE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        try {
                            String response = handleApiResponse(s);
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.getBoolean("updated")) {
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
                params.put("schedule_id", String.valueOf(scheduleId));
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
        ScheduleEditFragmentArgs fragmentArgs = ScheduleEditFragmentArgs.fromBundle(getArguments());
        scheduleId = fragmentArgs.getScheduleId();
        mViewModel.setStudentName(fragmentArgs.getStudentName());
        getScheduleInfo();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        menu.add(1, 1, 1, getResources().getString(R.string.add)).setIcon(R.drawable.calendar_delete_48)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NotNull MenuItem item) {
        if (item.getItemId() == 1) {
            new MyConfirmation(getContext(), getResources().getString(R.string.confirm_delete_schedule))
                    .askConfirmation(new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            tryDeleteSchedule();
                        }
                    });
        }
        return super.onOptionsItemSelected(item);
    }

    private void getScheduleInfo() {
        showProgressView();
        StringRequest stringRequest = new StringRequest(
                Request.Method.GET,
                AppConstants.SUPERVISOR_GET_SCHEDULE_INFO + "?schedule_id=" + scheduleId,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        String response = handleApiResponse(s);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            mViewModel.setScheduleDate(jsonObject.getString("sch_date"));
                            mViewModel.setScheduleTime(jsonObject.getString("sch_time"));
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
        };
        addVolleyStringRequest(stringRequest);
    }


    private void tryDeleteSchedule() {
        showProgressView();
        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                AppConstants.SUPERVISOR_DELETE_SCHEDULE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        try {
                            String response = handleApiResponse(s);
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.getBoolean("deleted")) {
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
                params.put("schedule_id", String.valueOf(scheduleId));
                return params;
            }
        };
        addVolleyStringRequest(stringRequest);
    }

}