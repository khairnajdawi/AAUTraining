package jo.edu.aau.aautraining.student.ui.profile;

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
import jo.edu.aau.aautraining.shared.MyFragment;
import jo.edu.aau.aautraining.shared.MySharedPreference;
import jo.edu.aau.aautraining.student.StudentMainActivity;

public class ProfileFragment extends MyFragment {

    private ProfileViewModel profileViewModel;
    private PictureProgressBar pictureProgressBar;
    private int studentId=0,trainingId=0;
    private StateProgressBar stateProgressBar;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        profileViewModel =
                ViewModelProviders.of(this).get(ProfileViewModel.class);
        View root = inflater.inflate(R.layout.student_fragment_profile, container, false);

        final TextView nameTextView = root.findViewById(R.id.student_profile_name_tv);
        profileViewModel.getStudentName().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                nameTextView.setText(s);
                StudentMainActivity activity = (StudentMainActivity) getActivity();
                activity.setHeaderName(s);
            }
        });
        final TextView facultyNameTV = root.findViewById(R.id.student_profile_faculty_name_tv);
        profileViewModel.getFacultyName().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                facultyNameTV.setText(s);
            }
        });
        final TextView majorTitleTV = root.findViewById(R.id.student_profile_major_tv);
        profileViewModel.getStudentMajor().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                majorTitleTV.setText(s);
            }
        });
        final TextView numberTextView = root.findViewById(R.id.student_profile_number_tv);
        profileViewModel.getStudentUniNo().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                numberTextView.setText(s);
            }
        });
        final TextView companyTextView = root.findViewById(R.id.student_profile_company_tv);
        profileViewModel.getCompanyName().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                companyTextView.setText(s);
            }
        });

        final TextView fieldTextView = root.findViewById(R.id.student_profile_field_tv);
        profileViewModel.getTrainingField().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                fieldTextView.setText(s);
            }
        });

        final TextView startDateTextView = root.findViewById(R.id.student_profile_start_date_tv);
        profileViewModel.getStartDate().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                startDateTextView.setText(s);
            }
        });

        final TextView endDateTextView = root.findViewById(R.id.student_profile_end_date_tv);
        profileViewModel.getFinishDate().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                endDateTextView.setText(s);
            }
        });

        final TextView requiredHoursTextView = root.findViewById(R.id.student_profile_required_hours_tv);
        profileViewModel.getRequiredHours().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s ) {
                requiredHoursTextView.setText(s);
            }
        });

        final TextView passedHoursTextView = root.findViewById(R.id.student_profile_passed_hours_tv);
        profileViewModel.getPassHours().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s ) {
                passedHoursTextView.setText(s);
            }
        });
        final TextView hoursStatusTextView = root.findViewById(R.id.student_profile_status_hours_tv);
        profileViewModel.getRemainHours().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String remainHoursString ) {
                String s = String.format(
                        Locale.US,
                        "%sH passed / %sH To Complete",
                        profileViewModel.getPassHours().getValue(),
                        remainHoursString);
                hoursStatusTextView.setText(s);
            }
        });


        pictureProgressBar = root.findViewById(R.id.student_profile_pictureprogoressbar);
        pictureProgressBar.setProgress(30);


        String[] descriptionData = {"Start", "Training", "Trainer Report", "Complete", "Success"};
        stateProgressBar = root.findViewById(R.id.student_profile_stateprogoressbar);
        stateProgressBar.setStateDescriptionData(descriptionData);
        stateProgressBar.setStateDescriptionSize(12);
        stateProgressBar.setCurrentStateNumber(StateProgressBar.StateNumber.TWO);
        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (getArguments() != null) {
            ProfileFragmentArgs profileFragmentArgs = ProfileFragmentArgs.fromBundle(getArguments());
            studentId = profileFragmentArgs.getStudentId();
            trainingId = profileFragmentArgs.getTrainingId();
            getStudentInfo();
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


    }

    private void getStudentInfo() {
        StudentMainActivity activity = (StudentMainActivity) getActivity();
        showProgressView();
        String url = AppConstants.API_GET_STUDENT_INFO + "?student_id=" + studentId + "&training_id=" + trainingId;
        StringRequest stringRequest = new StringRequest(
                Request.Method.GET,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {

                        try {
                            String response = handleApiResponse(s);
                            if (!response.isEmpty()) {
                                JSONObject profileJsonObject = new JSONObject(response);
                                String name=profileJsonObject.getString("name");
                                activity.setHeaderName(name);
                                profileViewModel.setStudentName(name);
                                profileViewModel.setFacultyName(profileJsonObject.getString("faculty_name"));
                                profileViewModel.setStudentMajor(profileJsonObject.getString("major_title"));
                                profileViewModel.setStudentUniNo(profileJsonObject.getString("uni_no"));
                                profileViewModel.setCompanyName(profileJsonObject.getString("company_name"));
                                profileViewModel.setTrainingField(profileJsonObject.getString("training_field"));
                                int trainingStatus = profileJsonObject.getInt("training_status");
                                switch (trainingStatus){
                                    case 0:
                                        stateProgressBar.setCurrentStateNumber(StateProgressBar.StateNumber.ONE);
                                        break;
                                    case 1:
                                        stateProgressBar.setCurrentStateNumber(StateProgressBar.StateNumber.TWO);
                                        break;
                                    default:
                                        stateProgressBar.setCurrentStateNumber(StateProgressBar.StateNumber.THREE);
                                        break;
                                }
                                String passedHours = profileJsonObject.getString("passed_hours");
                                profileViewModel.setPassHours(passedHours);
                                String requiredHours = profileJsonObject.getString("required_hours");
                                profileViewModel.setRequiredHours(requiredHours);
                                String remainHours = profileJsonObject.getString("remain_hours");
                                profileViewModel.setRemainHours(remainHours);
                                double passedValue = Integer.parseInt(passedHours.split(":")[0])*60 + Integer.parseInt(passedHours.split(":")[1]);
                                double requiredValue = Integer.parseInt(requiredHours.split(":")[0])*60 + Integer.parseInt(requiredHours.split(":")[1]);
                                int passedPercent = (int) ((passedValue / requiredValue)*100);
                                if(passedPercent>100) passedPercent=100;
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
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                MySharedPreference mySharedPreference = new MySharedPreference(getContext());
                headers.put("Authorization", "Bearer " + mySharedPreference.getToken());
                return headers;
            }
        };
        activity.addVolleyStringRequest(stringRequest);
    }
}