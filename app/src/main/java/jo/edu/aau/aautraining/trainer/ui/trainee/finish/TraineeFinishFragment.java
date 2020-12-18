package jo.edu.aau.aautraining.trainer.ui.trainee.finish;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.willy.ratingbar.RotationRatingBar;
import com.willy.ratingbar.ScaleRatingBar;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import jo.edu.aau.aautraining.R;
import jo.edu.aau.aautraining.shared.AppConstants;
import jo.edu.aau.aautraining.shared.MyFragment;
import jo.edu.aau.aautraining.shared.MySharedPreference;
import jo.edu.aau.aautraining.trainer.TrainersMainActivity;
import jo.edu.aau.aautraining.trainer.ui.trainee.TraineeModel;

public class TraineeFinishFragment extends MyFragment {

    private TraineeFinishViewModel mViewModel;
    private Spinner reasonSpinner;
    private RotationRatingBar attendRatingBar, learnWellRatingBar, learnAbilityRatingBar, gainedSkillsRatingBar, applyingSkillRatingBar;
    private EditText recommendationEditText;
    private int trainingId;

    public static TraineeFinishFragment newInstance() {
        return new TraineeFinishFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.trainee_finish_fragment, container, false);
        reasonSpinner = root.findViewById(R.id.trainee_finish_reason_spinner);
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(
                getContext(), android.R.layout.simple_spinner_dropdown_item,
                getResources().getStringArray(R.array.finish_reason_array));
        reasonSpinner.setAdapter(arrayAdapter);

        attendRatingBar = root.findViewById(R.id.trainee_finish_ratingbar_attend);
        learnWellRatingBar = root.findViewById(R.id.trainee_finish_ratingbar_learning_well);
        learnAbilityRatingBar = root.findViewById(R.id.trainee_finish_ratingbar_learning_ability);
        gainedSkillsRatingBar = root.findViewById(R.id.trainee_finish_ratingbar_gained_skills);
        applyingSkillRatingBar = root.findViewById(R.id.trainee_finish_ratingbar_applying_skills);

        recommendationEditText = root.findViewById(R.id.trainee_finish_recommendation);

        root.findViewById(R.id.trainee_finish_save_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tryFinishTraining();
            }
        });

        return root;
    }

    private void tryFinishTraining() {
        String recommendation = recommendationEditText.getText().toString().trim();
        if (recommendation.isEmpty()) {
            recommendationEditText.setError(getResources().getString(R.string.enter_recommendation));
            return;
        } else {
            recommendationEditText.setError(null);
        }
        showProgressView();
        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                AppConstants.API_TRAINER_FINISH_TRAINING,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        String response = handleApiResponse(s);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.getBoolean("updated")) {
                                TrainersMainActivity activity = (TrainersMainActivity) getActivity();
                                for (TraineeModel trainee :
                                        activity.getTraineesList()) {
                                    if (trainee.getTrainingId().getValue() == trainingId) {
                                        trainee.setTrainingStatus(2);
                                        break;
                                    }
                                }

                                Navigation.findNavController(getView()).navigateUp();
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
            protected Map<String, String> getParams() {
                HashMap<String, String> params = new HashMap<>();
                params.put("training_id", String.valueOf(trainingId));
                params.put("finish_reason", String.valueOf(reasonSpinner.getSelectedItemPosition() + 1));
                params.put("attend_rating", String.valueOf(attendRatingBar.getRating()));
                params.put("wellness_rating", String.valueOf(learnWellRatingBar.getRating()));
                params.put("ability_rating", String.valueOf(learnAbilityRatingBar.getRating()));
                params.put("gained_skills_rating", String.valueOf(gainedSkillsRatingBar.getRating()));
                params.put("apply_skills_rating", String.valueOf(applyingSkillRatingBar.getRating()));
                params.put("recommendation", recommendationEditText.getText().toString());
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
        mViewModel = ViewModelProviders.of(this).get(TraineeFinishViewModel.class);
        if (getArguments() != null) {
            trainingId = TraineeFinishFragmentArgs.fromBundle(getArguments()).getTrainingId();
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}