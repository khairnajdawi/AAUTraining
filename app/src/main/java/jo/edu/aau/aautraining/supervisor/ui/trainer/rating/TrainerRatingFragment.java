package jo.edu.aau.aautraining.supervisor.ui.trainer.rating;

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
import com.willy.ratingbar.RotationRatingBar;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import jo.edu.aau.aautraining.R;
import jo.edu.aau.aautraining.shared.AppConstants;
import jo.edu.aau.aautraining.shared.MyFragment;
import jo.edu.aau.aautraining.shared.MySharedPreference;

public class TrainerRatingFragment extends MyFragment {

    private TrainerRatingViewModel mViewModel;
    private RotationRatingBar attendRatingBar, learnWellRatingBar, learnAbilityRatingBar, gainedSkillsRatingBar, applyingSkillRatingBar;
    private EditText recommendationEditText, reasonEditText;
    private int trainingId;

    public static TrainerRatingFragment newInstance() {
        return new TrainerRatingFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mViewModel = ViewModelProviders.of(this).get(TrainerRatingViewModel.class);
        View root = inflater.inflate(R.layout.supervisor_trainer_rating, container, false);

        RotationRatingBar averageRatingBar = root.findViewById(R.id.trainer_rating_ratingbar_avg);
        mViewModel.getAverageRating().observe(getViewLifecycleOwner(), averageRatingBar::setRating);

        attendRatingBar = root.findViewById(R.id.trainer_rating_ratingbar_attend);
        mViewModel.getAttendRating().observe(getViewLifecycleOwner(), rating -> attendRatingBar.setRating(rating));
        learnWellRatingBar = root.findViewById(R.id.trainer_rating_ratingbar_learning_well);
        mViewModel.getLearnWellRating().observe(getViewLifecycleOwner(), rating -> learnWellRatingBar.setRating(rating));
        learnAbilityRatingBar = root.findViewById(R.id.trainer_rating_ratingbar_learning_ability);
        mViewModel.getLearnAbilityRating().observe(getViewLifecycleOwner(), rating -> learnAbilityRatingBar.setRating(rating));
        gainedSkillsRatingBar = root.findViewById(R.id.trainer_rating_ratingbar_gained_skills);
        mViewModel.getGainedSkillsRating().observe(getViewLifecycleOwner(), rating -> gainedSkillsRatingBar.setRating(rating));
        applyingSkillRatingBar = root.findViewById(R.id.trainer_rating_ratingbar_applying_skills);
        mViewModel.getApplyingSkillRating().observe(getViewLifecycleOwner(), rating -> applyingSkillRatingBar.setRating(rating));

        reasonEditText = root.findViewById(R.id.trainer_rating_finish_reason);
        mViewModel.getFinishReason().observe(getViewLifecycleOwner(), reason -> {
            String[] reasonList = getResources().getStringArray(R.array.finish_reason_array);
            reasonEditText.setText(reasonList[reason - 1]);
        });
        recommendationEditText = root.findViewById(R.id.trainer_rating_recommendation);
        mViewModel.getRecommendation().observe(getViewLifecycleOwner(), recommendationEditText::setText);
        return root;
    }


    private void getTrainerRating() {
        showProgressView();
        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                AppConstants.SUPERVISOR_GET_TRAINER_REPORT + "?training_id=" + trainingId,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        String response = handleApiResponse(s);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            mViewModel.setApplyingSkillRating(jsonObject.getInt("apply_skills_rating"));
                            mViewModel.setAttendRating(jsonObject.getInt("attend_rating"));
                            mViewModel.setGainedSkillsRating(jsonObject.getInt("gained_skills_rating"));
                            mViewModel.setLearnWellRating(jsonObject.getInt("wellness_rating"));
                            mViewModel.setLearnAbilityRating(jsonObject.getInt("ability_rating"));
                            mViewModel.setFinishReason(jsonObject.getInt("finish_reason"));
                            mViewModel.setRecommendation(jsonObject.getString("recommendation"));
                            mViewModel.setAverageRating((float) jsonObject.getDouble("trainer_rating"));
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

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (getArguments() != null) {
            trainingId = TrainerRatingFragmentArgs.fromBundle(getArguments()).getTrainingId();
        }
        getTrainerRating();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}