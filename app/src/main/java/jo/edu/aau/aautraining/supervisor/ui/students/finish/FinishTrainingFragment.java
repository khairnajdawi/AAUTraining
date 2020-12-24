package jo.edu.aau.aautraining.supervisor.ui.students.finish;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.Navigation;

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
import jo.edu.aau.aautraining.trainer.TrainersMainActivity;
import jo.edu.aau.aautraining.trainer.ui.trainee.TraineeModel;
import jo.edu.aau.aautraining.trainer.ui.trainee.finish.TraineeFinishFragmentArgs;

public class FinishTrainingFragment extends MyFragment {

    private Spinner reasonSpinner;
    private RotationRatingBar ratingBar;
    private EditText notesEditText;
    private int trainingId;

    public static FinishTrainingFragment newInstance() {
        return new FinishTrainingFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.supervisor_finish_fragment, container, false);
        reasonSpinner = root.findViewById(R.id.student_finish_reason_spinner);
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(
                getContext(), android.R.layout.simple_spinner_dropdown_item,
                getResources().getStringArray(R.array.finish_reason_array));
        reasonSpinner.setAdapter(arrayAdapter);

        ratingBar = root.findViewById(R.id.student_finish_ratingbar);

        notesEditText = root.findViewById(R.id.student_finish_notes);

        root.findViewById(R.id.student_finish_save_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tryFinishTraining();
            }
        });

        return root;
    }

    private void tryFinishTraining() {
        String recommendation = notesEditText.getText().toString().trim();
        if (recommendation.isEmpty()) {
            notesEditText.setError(getResources().getString(R.string.enter_recommendation));
            return;
        } else {
            notesEditText.setError(null);
        }
        showProgressView();
        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                AppConstants.SUPERVISOR_FINISH_STUDENT_TRAINING,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        String response = handleApiResponse(s);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.getBoolean("updated")) {
                                SupervisorMainActivity activity = (SupervisorMainActivity) getActivity();
                                for (StudentModel studentModel :
                                        activity.getStudentsList()) {
                                    if (studentModel.getTrainingId().getValue() == trainingId) {
                                        studentModel.setTrainingStatus(3);
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
                params.put("rating", String.valueOf(ratingBar.getRating()));
                params.put("notes", notesEditText.getText().toString());
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
            trainingId = TraineeFinishFragmentArgs.fromBundle(getArguments()).getTrainingId();
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}