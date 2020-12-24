package jo.edu.aau.aautraining.supervisor.ui.students.profile;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.kofigyan.stateprogressbar.StateProgressBar;
import com.yanzhikai.pictureprogressbar.PictureProgressBar;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import jo.edu.aau.aautraining.R;
import jo.edu.aau.aautraining.shared.AppConstants;
import jo.edu.aau.aautraining.shared.MyFragment;
import jo.edu.aau.aautraining.shared.MySharedPreference;

public class SupervisorStudentProfileFragment extends MyFragment implements View.OnClickListener {

    String[] descriptionData = {"Start", "Training", "Trainer Report", "Complete", "Success"};
    private SupervisorStudentProfileViewModel profileViewModel;
    private PictureProgressBar pictureProgressBar;
    private int studentId = 0, trainingId = 0;
    private StateProgressBar stateProgressBar;

    public static SupervisorStudentProfileFragment newInstance() {
        return new SupervisorStudentProfileFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        profileViewModel = ViewModelProviders.of(this).get(SupervisorStudentProfileViewModel.class);
        View root = inflater.inflate(R.layout.supervisor_student_profile_fragment, container, false);

        final TextView nameTextView = root.findViewById(R.id.student_profile_name_tv);
        profileViewModel.getStudentName().observe(getViewLifecycleOwner(), nameTextView::setText);
        final TextView facultyNameTV = root.findViewById(R.id.student_profile_faculty_name_tv);
        profileViewModel.getFacultyName().observe(getViewLifecycleOwner(), facultyNameTV::setText);
        final TextView majorTitleTV = root.findViewById(R.id.student_profile_major_tv);
        profileViewModel.getStudentMajor().observe(getViewLifecycleOwner(), majorTitleTV::setText);
        final TextView numberTextView = root.findViewById(R.id.student_profile_number_tv);
        profileViewModel.getStudentUniNo().observe(getViewLifecycleOwner(), numberTextView::setText);
        final TextView companyTextView = root.findViewById(R.id.student_profile_company_tv);
        profileViewModel.getCompanyName().observe(getViewLifecycleOwner(), companyTextView::setText);

        final TextView fieldTextView = root.findViewById(R.id.student_profile_field_tv);
        profileViewModel.getTrainingField().observe(getViewLifecycleOwner(), fieldTextView::setText);

        final TextView startDateTextView = root.findViewById(R.id.student_profile_start_date_tv);
        profileViewModel.getStartDate().observe(getViewLifecycleOwner(), startDateTextView::setText);

        final TextView endDateTextView = root.findViewById(R.id.student_profile_end_date_tv);
        profileViewModel.getFinishDate().observe(getViewLifecycleOwner(), endDateTextView::setText);
        final TextView attendDaysTextView = root.findViewById(R.id.student_profile_attend_days_tv);
        profileViewModel.getAttendDaysCount().observe(getViewLifecycleOwner(), daysCount -> attendDaysTextView.setText(String.valueOf(daysCount)));
        final TextView absenceDaysTextView = root.findViewById(R.id.student_profile_absence_days_tv);
        profileViewModel.getAttendDaysCount().observe(getViewLifecycleOwner(), daysCount -> absenceDaysTextView.setText(String.valueOf(daysCount)));


        final TextView requiredHoursTextView = root.findViewById(R.id.student_profile_required_hours_tv);
        profileViewModel.getRequiredHours().observe(getViewLifecycleOwner(), requiredHoursTextView::setText);

        final TextView passedHoursTextView = root.findViewById(R.id.student_profile_passed_hours_tv);
        profileViewModel.getPassHours().observe(getViewLifecycleOwner(), passedHoursTextView::setText);

        final TextView hoursStatusTextView = root.findViewById(R.id.student_profile_status_hours_tv);
        profileViewModel.getRemainHours().observe(getViewLifecycleOwner(), remainHoursString -> {
            String s = String.format(
                    Locale.US,
                    "%sH passed / %sH To Complete",
                    profileViewModel.getPassHours().getValue(),
                    remainHoursString);
            hoursStatusTextView.setText(s);
        });


        pictureProgressBar = root.findViewById(R.id.student_profile_pictureprogoressbar);
        pictureProgressBar.setProgress(30);

        stateProgressBar = root.findViewById(R.id.student_profile_stateprogoressbar);
        stateProgressBar.setStateDescriptionData(descriptionData);
        stateProgressBar.setStateDescriptionSize(12);


        root.findViewById(R.id.student_profile_send_msg_btn).setOnClickListener(this);
        root.findViewById(R.id.student_profile_schedule_btn).setOnClickListener(this);
        root.findViewById(R.id.student_profile_contact_trainer_btn).setOnClickListener(this);
        AppCompatButton trainerRatingButton = root.findViewById(R.id.student_profile_trainer_rating_btn);
        trainerRatingButton.setOnClickListener(this);
        AppCompatButton finishTrainingButton = root.findViewById(R.id.student_profile_finish_training_btn);
        finishTrainingButton.setOnClickListener(this);
        AppCompatButton supervisorRating = root.findViewById(R.id.student_profile_supervisor_rating_btn);
        supervisorRating.setOnClickListener(this);
        profileViewModel.getTrainingStatus().observe(getViewLifecycleOwner(), status -> {
            switch (status) {
                case 0:
                    stateProgressBar.setCurrentStateNumber(StateProgressBar.StateNumber.ONE);
                    trainerRatingButton.setVisibility(View.GONE);
                    finishTrainingButton.setVisibility(View.GONE);
                    supervisorRating.setVisibility(View.GONE);
                    break;
                case 1:
                    stateProgressBar.setCurrentStateNumber(StateProgressBar.StateNumber.TWO);
                    trainerRatingButton.setVisibility(View.GONE);
                    finishTrainingButton.setVisibility(View.GONE);
                    supervisorRating.setVisibility(View.GONE);
                    break;
                case 2:
                    stateProgressBar.setCurrentStateNumber(StateProgressBar.StateNumber.THREE);
                    trainerRatingButton.setVisibility(View.VISIBLE);
                    finishTrainingButton.setVisibility(View.VISIBLE);
                    supervisorRating.setVisibility(View.GONE);
                    break;
                case 3:
                    stateProgressBar.setCurrentStateNumber(StateProgressBar.StateNumber.FOUR);
                    trainerRatingButton.setVisibility(View.VISIBLE);
                    finishTrainingButton.setVisibility(View.GONE);
                    supervisorRating.setVisibility(View.VISIBLE);
                    break;
                case 4:
                    stateProgressBar.setCurrentStateNumber(StateProgressBar.StateNumber.FIVE);
                    trainerRatingButton.setVisibility(View.VISIBLE);
                    finishTrainingButton.setVisibility(View.GONE);
                    supervisorRating.setVisibility(View.VISIBLE);
                    stateProgressBar.setAllStatesCompleted(true);
                    break;
                case 5:
                    trainerRatingButton.setVisibility(View.VISIBLE);
                    finishTrainingButton.setVisibility(View.GONE);
                    supervisorRating.setVisibility(View.VISIBLE);
                    descriptionData = new String[]{"Start", "Training", "Final Report", "Complete", "Fail"};
                    stateProgressBar.setStateDescriptionData(descriptionData);
                    stateProgressBar.setAllStatesCompleted(true);
                    stateProgressBar.setForegroundColor(Color.RED);
                    break;
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
            SupervisorStudentProfileFragmentArgs profileFragmentArgs = SupervisorStudentProfileFragmentArgs.fromBundle(getArguments());
            studentId = profileFragmentArgs.getStudentId();
            trainingId = profileFragmentArgs.getTrainingId();
            getStudentInfo();
        }

    }

    private void getStudentInfo() {
        showProgressView();
        String url = AppConstants.API_GET_STUDENT_INFO + "?student_id=" + studentId + "&training_id=" + trainingId;
        StringRequest stringRequest = new StringRequest(
                Request.Method.GET,
                url,
                s -> {

                    try {
                        String response = handleApiResponse(s);
                        if (!response.isEmpty()) {
                            JSONObject profileJsonObject = new JSONObject(response);
                            String name = profileJsonObject.getString("name");
                            profileViewModel.setStudentName(name);
                            profileViewModel.setFacultyName(profileJsonObject.getString("faculty_name"));
                            profileViewModel.setStudentMajor(profileJsonObject.getString("major_title"));
                            profileViewModel.setStudentUniNo(profileJsonObject.getString("uni_no"));
                            profileViewModel.setCompanyName(profileJsonObject.getString("company_name"));
                            profileViewModel.setTrainingField(profileJsonObject.getString("training_field"));
                            profileViewModel.setTrainerId(profileJsonObject.getInt("trainer_id"));
                            profileViewModel.setAttendDaysCount(profileJsonObject.getInt("attend_days_count"));
                            profileViewModel.setAbsenceDaysCount(profileJsonObject.getInt("absence_days_count"));
                            int trainingStatus = profileJsonObject.getInt("training_status");
                            profileViewModel.setTrainingStatus(trainingStatus);
                            String passedHours = profileJsonObject.getString("passed_hours");
                            profileViewModel.setPassHours(passedHours);
                            String requiredHours = profileJsonObject.getString("required_hours");
                            profileViewModel.setRequiredHours(requiredHours);
                            String remainHours = profileJsonObject.getString("remain_hours");
                            profileViewModel.setRemainHours(remainHours);
                            double passedValue = Integer.parseInt(passedHours.split(":")[0]) * 60 + Integer.parseInt(passedHours.split(":")[1]);
                            double requiredValue = Integer.parseInt(requiredHours.split(":")[0]) * 60 + Integer.parseInt(requiredHours.split(":")[1]);
                            int passedPercent = (int) ((passedValue / requiredValue) * 100);
                            if (passedPercent > 100) passedPercent = 100;
                            pictureProgressBar.setProgress(passedPercent);
                            profileViewModel.setStartDate(profileJsonObject.getString("start_date"));
                            profileViewModel.setFinishDate(profileJsonObject.getString("finish_date"));

                        } else {
                            showSnackbar(R.string.error);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        showSnackbar(R.string.error);
                    }

                    hideProgressView();
                }, error -> {
            hideProgressView();
            showSnackbar(R.string.error);
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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.student_profile_send_msg_btn:
                Bundle bundle = new Bundle();
                bundle.putString("contact_name", profileViewModel.getStudentName().getValue());
                Navigation.createNavigateOnClickListener(R.id.action_supervisor_nav_student_profile_to_supervisor_nav_chat, bundle).onClick(view);
                break;
            case R.id.student_profile_schedule_btn:
                Bundle bundle5 = new Bundle();
                bundle5.putInt("training_id", trainingId);
                bundle5.putString("student_name", profileViewModel.getStudentName().getValue());
                Navigation.createNavigateOnClickListener(R.id.action_supervisor_nav_student_profile_to_supervisor_nav_student_schedule, bundle5).onClick(view);

                break;
            case R.id.student_profile_contact_trainer_btn:
                Bundle bundle2 = new Bundle();
                bundle2.putInt("trainer_id", profileViewModel.getTrainerId().getValue());
                Navigation.createNavigateOnClickListener(R.id.action_supervisor_nav_student_profile_to_supervisor_nav_contact_trainer, bundle2).onClick(view);
                break;
            case R.id.student_profile_trainer_rating_btn:
                Bundle bundle3 = new Bundle();
                bundle3.putInt("training_id", trainingId);
                Navigation.createNavigateOnClickListener(R.id.action_supervisor_nav_student_profile_to_supervisor_nav_trainer_rating, bundle3).onClick(view);
                break;
            case R.id.student_profile_finish_training_btn:
                Bundle bundle4 = new Bundle();
                bundle4.putInt("training_id", trainingId);
                Navigation.createNavigateOnClickListener(R.id.action_supervisor_nav_student_profile_to_supervisor_nav_finish_training, bundle4).onClick(view);
                break;
            case R.id.student_profile_supervisor_rating_btn:
                Bundle bundle6 = new Bundle();
                bundle6.putInt("training_id", trainingId);
                Navigation.createNavigateOnClickListener(R.id.action_supervisor_nav_student_profile_to_supervisor_nav_rating, bundle6).onClick(view);
                break;
        }
    }
}