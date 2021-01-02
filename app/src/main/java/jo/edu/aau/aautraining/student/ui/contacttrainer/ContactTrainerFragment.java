package jo.edu.aau.aautraining.student.ui.contacttrainer;

import androidx.collection.LruCache;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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
import jo.edu.aau.aautraining.student.StudentMainActivity;

public class ContactTrainerFragment extends MyFragment {

    private ContactTrainerViewModel mViewModel;
    private int studentId;

    public static ContactTrainerFragment newInstance() {
        return new ContactTrainerFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mViewModel = ViewModelProviders.of(this).get(ContactTrainerViewModel.class);
        View view=  inflater.inflate(R.layout.student_contact_trainer_fragment, container, false);

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
        final NetworkImageView networkImageView=view.findViewById(R.id.student_trainer_imageView);
        mViewModel.getImageLink().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                networkImageView.setImageUrl(s,  mImageLoader);
                networkImageView.setErrorImageResId(R.drawable.training);
            }
        });

        final TextView nameTextView=view.findViewById(R.id.student_trainer_name);
        mViewModel.getName().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                nameTextView.setText(s);
                //ContactTrainerFragmentDirections.actionStudentNavContactTrainerToStudentNavChat().setContactName(s);
            }
        });

        final TextView specialtyTextView=view.findViewById(R.id.student_trainer_specialty);
        mViewModel.getSpecialty().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                specialtyTextView.setText(s);
            }
        });
        final TextView jobTextView=view.findViewById(R.id.student_trainer_job_title);
        mViewModel.getJobTitle().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                jobTextView.setText(s);
            }
        });
        final TextView companyTextView=view.findViewById(R.id.student_trainer_company);
        mViewModel.getCompanyName().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                companyTextView.setText(s);
            }
        });
        final TextView mobileTextView=view.findViewById(R.id.student_trainer_mobile);
        mViewModel.getMobile().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                mobileTextView.setText(s);
            }
        });
        final TextView emailTextView=view.findViewById(R.id.student_trainer_email);
        mViewModel.getEmail().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                emailTextView.setText(s);
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.student_trainer_contactbtn).setOnClickListener(
                Navigation.createNavigateOnClickListener(R.id.action_student_nav_contact_trainer_to_student_nav_chat)
        );
        if(getArguments()!=null){
            int trainerId=ContactTrainerFragmentArgs.fromBundle(getArguments()).getTrainerId();
            studentId = ContactTrainerFragmentArgs.fromBundle(getArguments()).getStudentId();
            getTrainerInfo(trainerId);
        }
    }

    private void getTrainerInfo(int trainerId) {
        MyAppCompatActivity activity = (MyAppCompatActivity) getActivity();
        showProgressView();

        StringRequest stringRequest = new StringRequest(
                Request.Method.GET,
                AppConstants.API_GET_TRAINER_INFO+"?trainer_id="+trainerId,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {

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
                                mViewModel.setImageLink(AppConstants.APP_BASE_URL + profileJsonObject.getString("img_link"));
                                Bundle bundle = new Bundle();
                                bundle.putString("contact_name", profileJsonObject.getString("name"));
                                bundle.putString("from_role", "Student");
                                bundle.putInt("from_id", studentId);
                                bundle.putString("to_role", "Trainer");
                                bundle.putInt("to_id", trainerId);
                                getView().findViewById(R.id.student_trainer_contactbtn).setOnClickListener(
                                        Navigation.createNavigateOnClickListener(R.id.action_student_nav_contact_trainer_to_student_nav_chat, bundle)
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