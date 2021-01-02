package jo.edu.aau.aautraining.trainer.ui.trainee.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.kofigyan.stateprogressbar.StateProgressBar;
import com.yanzhikai.pictureprogressbar.PictureProgressBar;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Time;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import jo.edu.aau.aautraining.R;
import jo.edu.aau.aautraining.shared.AppConstants;
import jo.edu.aau.aautraining.shared.MyAppCompatActivity;
import jo.edu.aau.aautraining.shared.MyFragment;
import jo.edu.aau.aautraining.shared.MySharedPreference;
import jo.edu.aau.aautraining.trainer.TrainersMainActivity;

public class TraineeProfileFragment extends MyFragment implements View.OnClickListener {

    private TraineeProfileViewModel traineeProfileViewModel;
    private PictureProgressBar pictureProgressBar;
    private int traineeId = 0, trainingId = 0;
    private StateProgressBar stateProgressBar;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        traineeProfileViewModel =
                ViewModelProviders.of(this).get(TraineeProfileViewModel.class);
        View root = inflater.inflate(R.layout.trainer_trainee_profile_fragment, container, false);

        final TextView nameTextView = root.findViewById(R.id.trainee_profile_name_tv);
        traineeProfileViewModel.getTraineeName().observe(getViewLifecycleOwner(), nameTextView::setText);
        final TextView facultyNameTV = root.findViewById(R.id.trainee_profile_faculty_name_tv);
        traineeProfileViewModel.getFacultyName().observe(getViewLifecycleOwner(), facultyNameTV::setText);
        final TextView majorTitleTV = root.findViewById(R.id.trainee_profile_major_tv);
        traineeProfileViewModel.getTraineeMajor().observe(getViewLifecycleOwner(), majorTitleTV::setText);
        final TextView numberTextView = root.findViewById(R.id.trainee_profile_number_tv);
        traineeProfileViewModel.getTraineeUniNo().observe(getViewLifecycleOwner(), numberTextView::setText);
        final TextView supervisorTextView = root.findViewById(R.id.trainee_profile_supervisor_tv);
        traineeProfileViewModel.getSupervisorName().observe(getViewLifecycleOwner(), supervisorTextView::setText);
        final TextView fieldTextView = root.findViewById(R.id.trainee_profile_field_tv);
        traineeProfileViewModel.getTrainingField().observe(getViewLifecycleOwner(), fieldTextView::setText);
        final TextView startDateTextView = root.findViewById(R.id.trainee_profile_start_date_tv);
        traineeProfileViewModel.getStartDate().observe(getViewLifecycleOwner(), startDateTextView::setText);
        final TextView endDateTextView = root.findViewById(R.id.trainee_profile_end_date_tv);
        traineeProfileViewModel.getFinishDate().observe(getViewLifecycleOwner(), endDateTextView::setText);
        final TextView passedHoursTextView = root.findViewById(R.id.trainee_profile_passed_hours_tv);
        traineeProfileViewModel.getPassedHours().observe(getViewLifecycleOwner(), passedHoursTextView::setText);
        final TextView requiredHoursTextView = root.findViewById(R.id.trainee_profile_required_hours_tv);
        traineeProfileViewModel.getRequiredHours().observe(getViewLifecycleOwner(), requiredHoursTextView::setText);
        final TextView trainingHoursTextView = root.findViewById(R.id.trainee_profile_training_hourse_tv);
        traineeProfileViewModel.getTrainingHoursText().observe(getViewLifecycleOwner(), trainingHoursTextView::setText);

        pictureProgressBar = root.findViewById(R.id.trainee_profile_pictureprogoressbar);

        String[] descriptionData = {"Start", "InProgress", "Complete"};
        stateProgressBar = root.findViewById(R.id.trainee_profile_stateprogoressbar);
        stateProgressBar.setStateDescriptionData(descriptionData);
        stateProgressBar.setCurrentStateNumber(StateProgressBar.StateNumber.ONE);

        root.findViewById(R.id.trainee_profile_send_msg_btn).setOnClickListener(this);
        root.findViewById(R.id.trainee_profile_schedule_btn).setOnClickListener(this);
        root.findViewById(R.id.trainee_profile_supervisor_btn).setOnClickListener(this);
        root.findViewById(R.id.trainee_profile_finish_btn).setOnClickListener(this);

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            TraineeProfileFragmentArgs traineeProfileFragmentArgs = TraineeProfileFragmentArgs.fromBundle(getArguments());
            trainingId = traineeProfileFragmentArgs.getTrainingId();
            traineeId = traineeProfileFragmentArgs.getTraineeId();
            getTraineeInfo();
        }

    }

    private void getTraineeInfo() {
        MyAppCompatActivity activity = (MyAppCompatActivity) getActivity();
        showProgressView();
        String url = AppConstants.API_GET_TRAINEE_INFO + "?trainee_id=" + traineeId + "&training_id=" + trainingId;
        StringRequest stringRequest = new StringRequest(
                Request.Method.GET,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {

                        try {
                            String response = activity.handleApiResponse(s);
                            if (!response.isEmpty()) {
                                JSONObject profileJsonObject = new JSONObject(response);
                                String name = profileJsonObject.getString("name");
                                traineeProfileViewModel.setTraineeName(name);
                                traineeProfileViewModel.setFacultyName(profileJsonObject.getString("faculty_name"));
                                traineeProfileViewModel.setTraineeMajor(profileJsonObject.getString("major_title"));
                                traineeProfileViewModel.setTraineeUniNo(profileJsonObject.getString("uni_no"));
                                traineeProfileViewModel.setSupervisorName(profileJsonObject.getString("company_name"));
                                traineeProfileViewModel.setTrainingField(profileJsonObject.getString("training_field"));

                                int trainingStatus = profileJsonObject.getInt("training_status");
                                traineeProfileViewModel.setTrainingStatus(trainingStatus);
                                String requiredHours = profileJsonObject.getString("required_hours");
                                traineeProfileViewModel.setRequiredHours(requiredHours);
                                String passedHours = profileJsonObject.getString("passed_hours");
                                String remainHours = profileJsonObject.getString("remain_hours");
                                String trainingHoursText = String.format(Locale.US, "%sH passed / %sH remain", passedHours, remainHours);
                                switch (trainingStatus) {
                                    case 0:
                                        stateProgressBar.setCurrentStateNumber(StateProgressBar.StateNumber.ONE);
                                        break;
                                    case 1:
                                        stateProgressBar.setCurrentStateNumber(StateProgressBar.StateNumber.TWO);
                                        break;
                                    default:
                                        stateProgressBar.setCurrentStateNumber(StateProgressBar.StateNumber.THREE);
                                        //hide finish training button
                                        getView().findViewById(R.id.trainee_profile_finish_btn).setVisibility(View.GONE);
                                        trainingHoursText = String.format(Locale.US, "%sH passed", passedHours);
                                        break;
                                }


                                double passedValue = Integer.parseInt(passedHours.split(":")[0]) * 60 + Integer.parseInt(passedHours.split(":")[1]);
                                double requiredValue = Integer.parseInt(requiredHours.split(":")[0]) * 60 + Integer.parseInt(requiredHours.split(":")[1]);
                                int passedPercent = (int) ((passedValue / requiredValue) * 100);
                                if (passedPercent > 100) passedPercent = 100;
                                pictureProgressBar.setProgress(passedPercent);
                                traineeProfileViewModel.setTrainingHoursText(trainingHoursText);
                                traineeProfileViewModel.setPassedHours(passedHours);
                                traineeProfileViewModel.setStartDate(profileJsonObject.getString("start_date"));
                                traineeProfileViewModel.setFinishDate(profileJsonObject.getString("finish_date"));

                                traineeProfileViewModel.setSupervisorId(profileJsonObject.getInt("supervisor_id"));
                                traineeProfileViewModel.setSupervisorName(profileJsonObject.getString("supervisor_name"));
                            } else {
                                activity.showSnackbar(R.string.error);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            activity.showSnackbar(R.string.error);
                        }

                        hideProgressView();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                hideProgressView();
                activity.showSnackbar(R.string.error);
            }
        }
        ) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                MySharedPreference mySharedPreference = new MySharedPreference(getContext());
                headers.put("Authorization", "Bearer " + mySharedPreference.getToken());
                return headers;
            }
        };
        activity.addVolleyStringRequest(stringRequest);
    }

    @Override
    public void onClick(View view) {
        TrainersMainActivity activity = (TrainersMainActivity) getActivity();
        switch (view.getId()) {
            case R.id.trainee_profile_send_msg_btn:
                TrainersMainActivity mainActivity = (TrainersMainActivity) getActivity();
                Bundle bundle = new Bundle();
                bundle.putString("from_role", "Trainer");
                bundle.putString("to_role", "Student");
                bundle.putInt("from_id", mainActivity.getTrainerId());
                bundle.putInt("to_id", traineeId);
                bundle.putString("contact_name", traineeProfileViewModel.getTraineeName().getValue());
                Navigation.createNavigateOnClickListener(R.id.action_trainer_nav_trainee_profile_to_trainer_nav_trainee_chat, bundle).onClick(view);
                break;
            case R.id.trainee_profile_schedule_btn:
                Bundle bundle2 = new Bundle();
                bundle2.putInt("training_id", trainingId);
                bundle2.putInt("trainee_id", traineeId);
                bundle2.putInt("training_status", traineeProfileViewModel.getTrainingStatus().getValue());
                bundle2.putString("trainee_name", traineeProfileViewModel.getTraineeName().getValue());
                Navigation.createNavigateOnClickListener(R.id.action_trainer_nav_trainee_profile_to_trainer_nav_trainee_schedule, bundle2).onClick(view);
                break;
            case R.id.trainee_profile_supervisor_btn:
                Bundle bundle3 = new Bundle();
                bundle3.putInt("supervisor_id", traineeProfileViewModel.getSupervisorId().getValue());
                Navigation.createNavigateOnClickListener(R.id.action_trainer_nav_trainee_profile_to_trainer_nav_supervisor, bundle3).onClick(view);
                break;
            case R.id.trainee_profile_finish_btn:
                Bundle bundle4 = new Bundle();
                bundle4.putInt("training_id", trainingId);
                Navigation.createNavigateOnClickListener(R.id.action_trainer_nav_trainee_profile_to_trainer_nav_finish_training, bundle4).onClick(view);
                break;
        }
    }
}