package jo.edu.aau.aautraining.trainer.ui.trainee.supervisor;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.collection.LruCache;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
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

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        supervisorViewModel =
                ViewModelProviders.of(this).get(SupervisorViewModel.class);
        View root = inflater.inflate(R.layout.trainer_supervisor_fragment, container, false);

        final TextView nameTextView = root.findViewById(R.id.trainer_supervisor_name);
        supervisorViewModel.getName().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                nameTextView.setText(s);
            }
        });

        final TextView rankTextView = root.findViewById(R.id.trainer_supervisor_rank);
        supervisorViewModel.getScientificRank().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                rankTextView.setText(s);
            }
        });

        final TextView facultyTextView = root.findViewById(R.id.trainer_supervisor_faculty);
        supervisorViewModel.getFacultyName().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                facultyTextView.setText(s);
            }
        });


        final TextView departmentTextView = root.findViewById(R.id.trainer_supervisor_department);
        supervisorViewModel.getDepartmentName().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                departmentTextView.setText(s);
            }
        });

        final TextView jobTextView = root.findViewById(R.id.trainer_supervisor_job_title);
        supervisorViewModel.getJobTitle().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                jobTextView.setText(s);
            }
        });

        final TextView mobileTextView = root.findViewById(R.id.trainer_supervisor_mobile);
        supervisorViewModel.getMobile().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                mobileTextView.setText(s);
            }
        });

        final TextView emailTextView = root.findViewById(R.id.trainer_supervisor_email);
        supervisorViewModel.getEmail().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                emailTextView.setText(s);
            }
        });
        RequestQueue mRequestQueue = Volley.newRequestQueue(getContext());
        ImageLoader mImageLoader = new ImageLoader(mRequestQueue, new ImageLoader.ImageCache() {

            private final LruCache<String, Bitmap> mCache = new LruCache<String, Bitmap>(10);

            public Bitmap getBitmap(String url) {
                return mCache.get(url);
            }

            @Override
            public void putBitmap(String url, Bitmap bitmap) {
                mCache.put(url, bitmap);
            }

        });
        final NetworkImageView imageView = root.findViewById(R.id.trainer_supervisor_imageView);
        supervisorViewModel.getImageLink().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                imageView.setImageUrl(s, mImageLoader);
                imageView.setErrorImageResId(R.drawable.professor_icon);
            }
        });
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            int supervisorId = SupervisorFragmentArgs.fromBundle(getArguments()).getSupervisorId();
            getSupervisorInfo(supervisorId);
        }
    }

    private void getSupervisorInfo(int supervisorId) {
        MyAppCompatActivity activity = (MyAppCompatActivity) getActivity();
        showProgressView();

        StringRequest stringRequest = new StringRequest(
                Request.Method.GET,
                AppConstants.API_GET_SUPERVISOR_INFO + "?supervisor_id=" + supervisorId,
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
                                supervisorViewModel.setImageLink(AppConstants.API_BASE_URL + profileJsonObject.getString("img_link"));
                                Bundle bundle = new Bundle();
                                bundle.putInt("contact_id", supervisorId);
                                bundle.putInt("contact_type", 4);
                                bundle.putString("contact_name", profileJsonObject.getString("name"));
                                getView().findViewById(R.id.trainer_supervisor_contactbtn).setOnClickListener(
                                        Navigation.createNavigateOnClickListener(R.id.action_trainer_nav_supervisor_to_trainer_nav_chat, bundle)
                                );

                                SupervisorFragmentDirections.actionTrainerNavSupervisorToTrainerNavChat(supervisorId, profileJsonObject.getString("name"), 4);
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