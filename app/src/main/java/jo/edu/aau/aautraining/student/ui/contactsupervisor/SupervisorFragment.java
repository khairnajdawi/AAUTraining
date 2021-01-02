package jo.edu.aau.aautraining.student.ui.contactsupervisor;

import android.graphics.Bitmap;
import android.media.Image;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.collection.LruCache;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import jo.edu.aau.aautraining.R;
import jo.edu.aau.aautraining.shared.AppConstants;
import jo.edu.aau.aautraining.shared.MyAppCompatActivity;
import jo.edu.aau.aautraining.shared.MyFragment;
import jo.edu.aau.aautraining.shared.MySharedPreference;

public class SupervisorFragment extends MyFragment {

    private SupervisorViewModel supervisorViewModel;
    private int studentId;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        supervisorViewModel =
                ViewModelProviders.of(this).get(SupervisorViewModel.class);
        View root = inflater.inflate(R.layout.student_fragment_supervisor, container, false);

        final TextView nameTextView = root.findViewById(R.id.student_supervisor_name);
        supervisorViewModel.getName().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                //SupervisorFragmentDirections.actionStudentNavContactSupervisorToStudentNavChat().setContactName(s);
                nameTextView.setText(s);
            }
        });

        final TextView rankTextView = root.findViewById(R.id.student_supervisor_rank);
        supervisorViewModel.getScientificRank().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                rankTextView.setText(s);
            }
        });

        final TextView facultyTextView = root.findViewById(R.id.student_supervisor_faculty);
        supervisorViewModel.getFacultyName().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                facultyTextView.setText(s);
            }
        });


        final TextView departmentTextView = root.findViewById(R.id.student_supervisor_department);
        supervisorViewModel.getDepartmentName().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                departmentTextView.setText(s);
            }
        });

        final TextView jobTextView = root.findViewById(R.id.student_supervisor_job_title);
        supervisorViewModel.getJobTitle().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                jobTextView.setText(s);
            }
        });

        final TextView mobileTextView = root.findViewById(R.id.student_supervisor_mobile);
        supervisorViewModel.getMobile().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                mobileTextView.setText(s);
            }
        });

        final TextView emailTextView = root.findViewById(R.id.student_supervisor_email);
        supervisorViewModel.getEmail().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                emailTextView.setText(s);
            }
        });
        final NetworkImageView imageView = root.findViewById(R.id.student_supervisor_imageView);
        supervisorViewModel.getImageLink().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                imageView.setImageUrl(s,  mImageLoader);
                imageView.setErrorImageResId(R.drawable.professor_icon);
            }
        });
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if(getArguments()!=null){
            int supervisorId = SupervisorFragmentArgs.fromBundle(getArguments()).getSupervisorId();
            studentId = SupervisorFragmentArgs.fromBundle(getArguments()).getStudentId();
            getSupervisorInfo(supervisorId);
        }
    }

    private void getSupervisorInfo(int supervisorId) {
        MyAppCompatActivity activity = (MyAppCompatActivity) getActivity();
        showProgressView();

        StringRequest stringRequest = new StringRequest(
                Request.Method.GET,
                AppConstants.API_GET_SUPERVISOR_INFO+"?supervisor_id="+supervisorId,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {

                        try {
                            String response = activity.handleApiResponse(s);
                            if (!response.isEmpty()) {
                                JSONObject profileJsonObject = new JSONObject(response);
                                supervisorViewModel.setName(profileJsonObject.getString("name"));
                                supervisorViewModel.setScientificRank(profileJsonObject.getString("scientific_rank"));
                                supervisorViewModel.setFacultyName(profileJsonObject.getString("faculty"));
                                supervisorViewModel.setDepartmentName(profileJsonObject.getString("department"));
                                supervisorViewModel.setEmail(profileJsonObject.getString("email"));
                                supervisorViewModel.setMobile(profileJsonObject.getString("mobile"));
                                supervisorViewModel.setJobTitle(profileJsonObject.getString("job_title"));
                                supervisorViewModel.setImageLink(AppConstants.APP_BASE_URL + profileJsonObject.getString("img_link"));
                                Bundle bundle = new Bundle();
                                bundle.putString("contact_name", profileJsonObject.getString("name"));
                                bundle.putString("from_role", "Student");
                                bundle.putInt("from_id", studentId);
                                bundle.putString("to_role", "Supervisor");
                                bundle.putInt("to_id", supervisorId);
                                getView().findViewById(R.id.student_supervisor_contactbtn).setOnClickListener(
                                        Navigation.createNavigateOnClickListener(R.id.action_student_nav_contact_supervisor_to_student_nav_chat, bundle)
                                );
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
}