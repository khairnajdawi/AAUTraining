package jo.edu.aau.aautraining.trainer.ui.profile;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.collection.LruCache;
import androidx.lifecycle.ViewModelProviders;

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
import jo.edu.aau.aautraining.trainer.TrainersMainActivity;

public class ProfileFragment extends MyFragment {

    private ProfileViewModel mViewModel;

    public static ProfileFragment newInstance() {
        return new ProfileFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.trainer_profile_fragment, container, false);
        mViewModel = ViewModelProviders.of(this).get(ProfileViewModel.class);

        final TextView nameTextView = root.findViewById(R.id.trainer_profile_name);
        mViewModel.getName().observe(getViewLifecycleOwner(), nameTextView::setText);

        final TextView companyTextView = root.findViewById(R.id.trainer_profile_company);
        mViewModel.getCompanyName().observe(getViewLifecycleOwner(), companyTextView::setText);

        final TextView specialityTextView = root.findViewById(R.id.trainer_profile_specialty);
        mViewModel.getSpecialty().observe(getViewLifecycleOwner(), specialityTextView::setText);

        final TextView jobTitleTextView = root.findViewById(R.id.trainer_profile_job_title);
        mViewModel.getJobTitle().observe(getViewLifecycleOwner(), jobTitleTextView::setText);

        final TextView mobileTextView = root.findViewById(R.id.trainer_profile_mobile);
        mViewModel.getMobile().observe(getViewLifecycleOwner(), mobileTextView::setText);

        final TextView emailTextView = root.findViewById(R.id.trainer_profile_email);
        mViewModel.getEmail().observe(getViewLifecycleOwner(), emailTextView::setText);


        final NetworkImageView imageView = root.findViewById(R.id.trainer_profile_imageView);
        mViewModel.getImageLink().observe(getViewLifecycleOwner(),imageLink -> {
            if(!imageLink.isEmpty()){
                imageView.setImageUrl(imageLink,  mImageLoader);
                imageView.setErrorImageResId(R.drawable.training);
            }
        });

        return  root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getTrainerInfo();
    }

    private void getTrainerInfo() {
        TrainersMainActivity activity = (TrainersMainActivity) getActivity();
        int trainerId=activity.getTrainerId();
        showProgressView();

        StringRequest stringRequest = new StringRequest(
                Request.Method.GET,
                AppConstants.API_GET_TRAINER_INFO+"?trainer_id="+trainerId,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        System.out.println("Trainer Profile "+trainerId+" : " + s);
                        try {
                            String response = activity.handleApiResponse(s);
                            if (!response.isEmpty()) {
                                JSONObject profileJsonObject = new JSONObject(response);
                                mViewModel.setName(profileJsonObject.getString("name"));
                                mViewModel.setCompanyName(profileJsonObject.getString("company_name"));
                                mViewModel.setSpecialty(profileJsonObject.getString("specialty"));
                                mViewModel.setEmail(profileJsonObject.getString("email"));
                                mViewModel.setMobile(profileJsonObject.getString("mobile"));
                                mViewModel.setJobTitle(profileJsonObject.getString("job_title"));
                                mViewModel.setImageLink(AppConstants.API_BASE_URL+ profileJsonObject.getString("img_link"));
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