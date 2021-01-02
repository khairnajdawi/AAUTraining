package jo.edu.aau.aautraining.supervisor.ui.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import jo.edu.aau.aautraining.R;
import jo.edu.aau.aautraining.shared.AppConstants;
import jo.edu.aau.aautraining.shared.MyAppCompatActivity;
import jo.edu.aau.aautraining.shared.MyFragment;
import jo.edu.aau.aautraining.shared.MySharedPreference;
import jo.edu.aau.aautraining.supervisor.SupervisorMainActivity;

public class ProfileFragment extends MyFragment {

    private ProfileViewModel mViewModel;

    public static ProfileFragment newInstance() {
        return new ProfileFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mViewModel = ViewModelProviders.of(this).get(ProfileViewModel.class);
        View root = inflater.inflate(R.layout.supervisor_profile_fragment, container, false);

        final TextView nameTextView = root.findViewById(R.id.supervisor_profile_name);
        mViewModel.getName().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {

                nameTextView.setText(s);
            }
        });

        final TextView rankTextView = root.findViewById(R.id.supervisor_profile_rank);
        mViewModel.getScientificRank().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                rankTextView.setText(s);
            }
        });

        final TextView facultyTextView = root.findViewById(R.id.supervisor_profile_faculty);
        mViewModel.getFacultyName().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                facultyTextView.setText(s);
            }
        });


        final TextView departmentTextView = root.findViewById(R.id.supervisor_profile_department);
        mViewModel.getDepartmentName().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                departmentTextView.setText(s);
            }
        });

        final TextView jobTextView = root.findViewById(R.id.supervisor_profile_job_title);
        mViewModel.getJobTitle().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                jobTextView.setText(s);
            }
        });

        final TextView mobileTextView = root.findViewById(R.id.supervisor_profile_mobile);
        mViewModel.getMobile().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                mobileTextView.setText(s);
            }
        });

        final TextView emailTextView = root.findViewById(R.id.supervisor_profile_email);
        mViewModel.getEmail().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                emailTextView.setText(s);
            }
        });
        final NetworkImageView imageView = root.findViewById(R.id.supervisor_profile_imageView);
        mViewModel.getImageLink().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                imageView.setImageUrl(s, mImageLoader);
                imageView.setErrorImageResId(R.drawable.professor_icon);
            }
        });
        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        SupervisorMainActivity activity = (SupervisorMainActivity) getActivity();
        getSupervisorInfo(activity.getSupervisorId());
    }

    private void getSupervisorInfo(int supervisorId) {
        showProgressView();

        StringRequest stringRequest = new StringRequest(
                Request.Method.GET,
                AppConstants.API_GET_SUPERVISOR_INFO + "?supervisor_id=" + supervisorId,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {

                        try {
                            String response = handleApiResponse(s);
                            if (!response.isEmpty()) {
                                JSONObject profileJsonObject = new JSONObject(response);
                                mViewModel.setName(profileJsonObject.getString("name"));
                                mViewModel.setScientificRank(profileJsonObject.getString("scientific_rank"));
                                mViewModel.setFacultyName(profileJsonObject.getString("faculty"));
                                mViewModel.setDepartmentName(profileJsonObject.getString("department"));
                                mViewModel.setEmail(profileJsonObject.getString("email"));
                                mViewModel.setMobile(profileJsonObject.getString("mobile"));
                                mViewModel.setJobTitle(profileJsonObject.getString("job_title"));
                                mViewModel.setImageLink(AppConstants.APP_BASE_URL + profileJsonObject.getString("img_link"));
//                                Bundle bundle=new Bundle();
//                                bundle.putString("contact_name",profileJsonObject.getString("name"));
//                                getView().findViewById(R.id.student_supervisor_contactbtn).setOnClickListener(
//                                        Navigation.createNavigateOnClickListener(R.id.action_student_nav_contact_supervisor_to_student_nav_chat,bundle)
//                                );
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
        addVolleyStringRequest(stringRequest);
    }
}