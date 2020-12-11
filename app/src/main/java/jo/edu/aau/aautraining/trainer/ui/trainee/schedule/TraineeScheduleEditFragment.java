package jo.edu.aau.aautraining.trainer.ui.trainee.schedule;

import androidx.lifecycle.ViewModelProviders;

import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

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

public class TraineeScheduleEditFragment extends MyFragment {

    private TraineeScheduleEditViewModel mViewModel;
    private EditText traineeNameEditText
            ,trainingDateEditText
            ,trainingPlaceEditText
            ,startTimeEditText
            ,endTimeEditText
            ,gainedSkillEditText
            ,notesEditText;
    private Spinner attendedSpinner;
    public static TraineeScheduleEditFragment newInstance() {
        return new TraineeScheduleEditFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        mViewModel = ViewModelProviders.of(this).get(TraineeScheduleEditViewModel.class);

        View root =  inflater.inflate(R.layout.trainee_schedule_edit_fragment, container, false);

        traineeNameEditText =root.findViewById(R.id.trainee_schedule_edit_trainee_name);
        mViewModel.getTraineeName().observe(getViewLifecycleOwner(),traineeNameEditText::setText);

       trainingDateEditText = root.findViewById(R.id.trainee_schedule_edit_date);
        mViewModel.getTrainingDate().observe(getViewLifecycleOwner(),trainingDateEditText::setText);
        trainingDateEditText.setOnClickListener(view -> MyDatePicker.getInstance(getContext()).selectDateFor(trainingDateEditText));

        trainingPlaceEditText = root.findViewById(R.id.trainee_schedule_edit_place);
        mViewModel.getTrainingPlace().observe(getViewLifecycleOwner(),trainingPlaceEditText::setText);


        startTimeEditText = root.findViewById(R.id.trainee_schedule_edit_start_time);
        mViewModel.getStartTime().observe(getViewLifecycleOwner(),startTimeEditText::setText);
        startTimeEditText.setOnClickListener(view -> MyTimePicker.getInstance(getContext()).pickTimeFor(startTimeEditText));


        endTimeEditText = root.findViewById(R.id.trainee_schedule_edit_end_time);
        mViewModel.getEndTime().observe(getViewLifecycleOwner(),endTimeEditText::setText);
        endTimeEditText.setOnClickListener(view -> {
            MyTimePicker.getInstance(getContext()).pickTimeFor(endTimeEditText);
        });

        gainedSkillEditText = root.findViewById(R.id.trainee_schedule_edit_gained_skills);
        mViewModel.getGaindSkills().observe(getViewLifecycleOwner(),gainedSkillEditText::setText);

        notesEditText = root.findViewById(R.id.trainee_schedule_edit_notes);
        mViewModel.getNotes().observe(getViewLifecycleOwner(),notesEditText::setText);

        attendedSpinner = root.findViewById(R.id.trainee_schedule_edit_attended_spinner);
        mViewModel.getAttended().observe(getViewLifecycleOwner(), attendedSpinner::setSelection);
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(
                getContext(),android.R.layout.simple_spinner_dropdown_item,
                getResources().getStringArray(R.array.attending));
        attendedSpinner.setAdapter(arrayAdapter);

        attendedSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                if(position==1){
                    startTimeEditText.setEnabled(true);
                    endTimeEditText.setEnabled(true);
                }else{
                    startTimeEditText.setText("");
                    startTimeEditText.setEnabled(false);
                    endTimeEditText.setText("");
                    endTimeEditText.setEnabled(false);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        root.findViewById(R.id.trainee_schedule_edit_save_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tryUpdateSchedule();
            }
        });
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if(getArguments()!=null){
            TraineeScheduleEditFragmentArgs fragmentArgs = TraineeScheduleEditFragmentArgs.fromBundle(getArguments());
            int scheduleId = fragmentArgs.getScheduleId();
            mViewModel.setScheduleId(scheduleId);
            getScheduleInfo();
            String traineeName = fragmentArgs.getTraineeName();
            mViewModel.setTraineeName(traineeName);
        }
    }

    private void getScheduleInfo() {
        showProgressView();
        StringRequest stringRequest = new StringRequest(
                Request.Method.GET,
                AppConstants.API_GET_TRAINEE_SCHEDULE_INFO+"?schedule_id="+mViewModel.getScheduleId().getValue(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        String response = handleApiResponse(s);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String training_date = jsonObject.getString("training_date");
                            mViewModel.setTrainingDate(training_date);
                            String training_place = jsonObject.getString("training_place");
                            mViewModel.setTrainingPlace(training_place);
                            String start_time = jsonObject.getString("start_time");
                            mViewModel.setStartTime(start_time);
                            String end_time = jsonObject.getString("end_time");
                            mViewModel.setEndTime(end_time);
                            String gained_skills = jsonObject.getString("gained_skills");
                            mViewModel.setGaindSkills(gained_skills);
                            String notes = jsonObject.getString("notes");
                            mViewModel.setNotes(notes);
                            int attended = jsonObject.getInt("attended");
                            mViewModel.setAttended(attended);
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
        ){
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

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        menu.add(1, 1, 1, getResources().getString(R.string.add)).setIcon(R.drawable.calendar_delete_48)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        super.onCreateOptionsMenu(menu, inflater);
    }
    @Override
    public boolean onOptionsItemSelected(@NotNull MenuItem item) {
        if(item.getItemId()==1) {
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

    private void tryDeleteSchedule() {
        showProgressView();
        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                AppConstants.API_DELETE_TRAINEE_SCHEDULE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        try {
                            String response = handleApiResponse(s);
                            JSONObject jsonObject = new JSONObject(response);
                            if(jsonObject.getBoolean("deleted")){
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
                params.put("schedule_id", String.valueOf(mViewModel.getScheduleId().getValue()));
                return params;
            }
        };
        addVolleyStringRequest(stringRequest);
    }

    private void tryUpdateSchedule() {
        if(trainingPlaceEditText.getText().toString().trim().isEmpty()){
            trainingPlaceEditText.setError(getResources().getString(R.string.enter_training_place));
            trainingPlaceEditText.requestFocus();
            return;
        }else{
            trainingPlaceEditText.setError(null);
        }
        //check if attended
        if(attendedSpinner.getSelectedItemPosition()==1){
            // attended, check start and end time
            //check if start time text is empty
            if(startTimeEditText.getText().toString().trim().isEmpty()){
                startTimeEditText.setError(getResources().getString(R.string.enter_start_time));
                startTimeEditText.requestFocus();
                return;
            }else{
                startTimeEditText.setError(null);
            }
            //check end time text is empty
            if(endTimeEditText.getText().toString().trim().isEmpty()){
                endTimeEditText.setError(getResources().getString(R.string.enter_end_time));
                endTimeEditText.requestFocus();
                return;
            }else{
                endTimeEditText.setError(null);
            }
        }

        showProgressView();
        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                AppConstants.API_EDIT_TRAINEE_SCHEDULE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        try {
                            String response = handleApiResponse(s);
                            JSONObject jsonObject = new JSONObject(response);
                            if(jsonObject.getBoolean("updated")){
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
                params.put("schedule_id", String.valueOf(mViewModel.getScheduleId().getValue()));
                params.put("training_id", String.valueOf(mViewModel.getScheduleId().getValue()));
                params.put("training_date",trainingDateEditText.getText().toString());
                params.put("training_place",trainingPlaceEditText.getText().toString());
                params.put("attended",String.valueOf(attendedSpinner.getSelectedItemPosition()));
                params.put("start_time",startTimeEditText.getText().toString());
                params.put("end_time",endTimeEditText.getText().toString());
                params.put("gained_skills",gainedSkillEditText.getText().toString());
                params.put("notes",notesEditText.getText().toString());
                return params;
            }
        };
        addVolleyStringRequest(stringRequest);
    }


}