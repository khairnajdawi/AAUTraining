package jo.edu.aau.aautraining.supervisor.ui.students.rating;

import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.willy.ratingbar.RotationRatingBar;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import jo.edu.aau.aautraining.R;
import jo.edu.aau.aautraining.shared.AppConstants;
import jo.edu.aau.aautraining.shared.MyFragment;
import jo.edu.aau.aautraining.shared.MySharedPreference;
import jo.edu.aau.aautraining.supervisor.SupervisorMainActivity;
import jo.edu.aau.aautraining.supervisor.ui.students.StudentModel;

public class SupervisorRatingFragment extends MyFragment {

    private SupervisorRatingViewModel mViewModel;

    public static SupervisorRatingFragment newInstance() {
        return new SupervisorRatingFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mViewModel = ViewModelProviders.of(this).get(SupervisorRatingViewModel.class);
        View root = inflater.inflate(R.layout.supervisor_rating_fragment, container, false);

        EditText reasonEditText = root.findViewById(R.id.supervisor_rating_reason);
        mViewModel.getReason().observe(getViewLifecycleOwner(), reasonEditText::setText);
        EditText notesEditText = root.findViewById(R.id.supervisor_rating_notes);
        mViewModel.getNotes().observe(getViewLifecycleOwner(), notesEditText::setText);
        RotationRatingBar ratingBar = root.findViewById(R.id.supervisor_rating_ratingbar);
        mViewModel.getRating().observe(getViewLifecycleOwner(), ratingBar::setRating);

        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (getArguments() != null) {
            int trainingId = SupervisorRatingFragmentArgs.fromBundle(getArguments()).getTrainingId();
            getRating(trainingId);
        }
    }

    private void getRating(int trainingId) {
        showProgressView();
        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                AppConstants.SUPERVISOR_GET_RATING,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        String response = handleApiResponse(s);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            int finishReason = jsonObject.getInt("finish_reason");
                            String[] reasonsList = getResources().getStringArray(R.array.finish_reason_array);
                            mViewModel.setReason(reasonsList[finishReason - 1]);

                            int rating = jsonObject.getInt("rating");
                            mViewModel.setRating(rating);

                            String notes = jsonObject.getString("notes");
                            mViewModel.setNotes(notes);

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
            protected Map<String, String> getParams() {
                HashMap<String, String> params = new HashMap<>();
                params.put("training_id", String.valueOf(trainingId));
                return params;
            }

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