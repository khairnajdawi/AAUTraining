package jo.edu.aau.aautraining.supervisor.ui.students.schedule;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

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
import jo.edu.aau.aautraining.shared.MyFragment;
import jo.edu.aau.aautraining.shared.MySharedPreference;
import jo.edu.aau.aautraining.trainer.ui.trainee.schedule.TraineeScheduleEditFragmentArgs;

public class StudentTrainingScheduleInfoFragment extends MyFragment {

    private StudentTrainingScheduleInfoViewModel mViewModel;

    public static StudentTrainingScheduleInfoFragment newInstance() {
        return new StudentTrainingScheduleInfoFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mViewModel = ViewModelProviders.of(this).get(StudentTrainingScheduleInfoViewModel.class);
        View root = inflater.inflate(R.layout.student_training_schedule_info_fragment, container, false);

        EditText studentNameEditText = root.findViewById(R.id.student_training_schedule_student_name);
        mViewModel.getStudentName().observe(getViewLifecycleOwner(), studentNameEditText::setText);

        EditText trainingDateEditText = root.findViewById(R.id.student_training_schedule_date);
        mViewModel.getTrainingDate().observe(getViewLifecycleOwner(), trainingDateEditText::setText);

        EditText trainingPlaceEditText = root.findViewById(R.id.student_training_schedule_place);
        mViewModel.getTrainingPlace().observe(getViewLifecycleOwner(), trainingPlaceEditText::setText);


        EditText startTimeEditText = root.findViewById(R.id.student_training_schedule_start_time);
        mViewModel.getStartTime().observe(getViewLifecycleOwner(), startTimeEditText::setText);

        EditText endTimeEditText = root.findViewById(R.id.student_training_schedule_end_time);
        mViewModel.getEndTime().observe(getViewLifecycleOwner(), endTimeEditText::setText);

        EditText gainedSkillEditText = root.findViewById(R.id.student_training_schedule_gained_skills);
        mViewModel.getGaindSkills().observe(getViewLifecycleOwner(), gainedSkillEditText::setText);

        EditText notesEditText = root.findViewById(R.id.student_training_schedule_notes);
        mViewModel.getNotes().observe(getViewLifecycleOwner(), notesEditText::setText);

        EditText attendEditText = root.findViewById(R.id.student_training_schedule_attended);
        mViewModel.getAttended().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer selection) {
                String[] attendList = getResources().getStringArray(R.array.attending);
                attendEditText.setText(attendList[selection]);
            }
        });

        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getArguments() != null) {
            StudentTrainingScheduleInfoFragmentArgs fragmentArgs = StudentTrainingScheduleInfoFragmentArgs.fromBundle(getArguments());
            int scheduleId = fragmentArgs.getScheduleId();
            mViewModel.setScheduleId(scheduleId);
            getScheduleInfo();
            String studentName = fragmentArgs.getStudentName();
            mViewModel.setStudentName(studentName);
        }
    }

    private void getScheduleInfo() {
        showProgressView();
        StringRequest stringRequest = new StringRequest(
                Request.Method.GET,
                AppConstants.API_GET_TRAINEE_SCHEDULE_INFO + "?schedule_id=" + mViewModel.getScheduleId().getValue(),
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