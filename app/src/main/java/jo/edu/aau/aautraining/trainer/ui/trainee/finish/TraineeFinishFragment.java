package jo.edu.aau.aautraining.trainer.ui.trainee.finish;

import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RatingBar;
import android.widget.Spinner;

import jo.edu.aau.aautraining.R;

public class TraineeFinishFragment extends Fragment {

    private TraineeFinishViewModel mViewModel;
    private Spinner reasonSpinner;
    private RatingBar attendRatingBar, learnWellRatingBar, learnAbilityRatingBar, gainedSkillsRatingBar, applyingSkillRatingBar;

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
        attendRatingBar.setMax(5);
        attendRatingBar.setStepSize(1.0f);
        learnWellRatingBar = root.findViewById(R.id.trainee_finish_ratingbar_learning_well);
        learnWellRatingBar.setMax(5);
        learnAbilityRatingBar = root.findViewById(R.id.trainee_finish_ratingbar_learning_ability);
        learnAbilityRatingBar.setMax(5);
        gainedSkillsRatingBar = root.findViewById(R.id.trainee_finish_ratingbar_gained_skills);
        gainedSkillsRatingBar.setMax(5);
        applyingSkillRatingBar = root.findViewById(R.id.trainee_finish_ratingbar_applying_skills);
        applyingSkillRatingBar.setMax(5);

        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(TraineeFinishViewModel.class);
    }

}