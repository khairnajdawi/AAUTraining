package jo.edu.aau.aautraining.trainer.ui.trainee;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.StringRequest;
import com.github.tibolte.agendacalendarview.models.CalendarEvent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jo.edu.aau.aautraining.R;
import jo.edu.aau.aautraining.shared.AppConstants;
import jo.edu.aau.aautraining.shared.MyAppCompatActivity;
import jo.edu.aau.aautraining.shared.MyFragment;
import jo.edu.aau.aautraining.shared.MySharedPreference;
import jo.edu.aau.aautraining.trainer.TrainersMainActivity;

public class TrainerTraineeFragment extends MyFragment {

    private TrainerTraineeViewModel mViewModel;
    private RecyclerView recyclerView;
    private TraineeAdapter traineeAdapter;
    private TrainersMainActivity mainActivity;

    public static TrainerTraineeFragment newInstance() {
        return new TrainerTraineeFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mViewModel = ViewModelProviders.of(this).get(TrainerTraineeViewModel.class);
        View root =  inflater.inflate(R.layout.trainer_trainee_fragment, container, false);
        recyclerView = root.findViewById(R.id.trainer_trainee_recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(),1));
        traineeAdapter = new TraineeAdapter();
        recyclerView.setAdapter(traineeAdapter);

        mViewModel.getTraineeList().observe(getViewLifecycleOwner(), new Observer<ArrayList<TraineeModel>>() {
            @Override
            public void onChanged(ArrayList<TraineeModel> traineeModels) {
                traineeAdapter.notifyDataSetChanged();
            }
        });
        mainActivity = (TrainersMainActivity) getActivity();

        return root;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private class TraineeViewHolder extends RecyclerView.ViewHolder{
        private TextView nameTextView;
        private NetworkImageView imageView;
        public TraineeViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.trainee_item_textview);
            imageView=itemView.findViewById(R.id.trainee_item_imageview);
        }
    }
    private class TraineeAdapter extends RecyclerView.Adapter<TraineeViewHolder>{

        @NonNull
        @Override
        public TraineeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new TraineeViewHolder(
                    View.inflate(parent.getContext(),R.layout.trainee_item_layout,null)
            );
        }

        @Override
        public void onBindViewHolder(@NonNull TraineeViewHolder holder, int position) {
            TraineeModel trainee = mainActivity.getTraineesList().get(position);
            holder.nameTextView.setText(trainee.getStudentName().getValue());
            String imageLink = trainee.getStudentImageLink().getValue();
            if(! imageLink.isEmpty()){
                holder.imageView.setImageUrl(imageLink,  mImageLoader);
                holder.imageView.setErrorImageResId(R.drawable.student_logo_color);
            }else{
                holder.imageView.setImageResource(R.drawable.student_logo_color);
            }
            holder.itemView.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Bundle bundle = new Bundle();
                            bundle.putInt("training_id",trainee.getTrainingId().getValue());
                            bundle.putInt("trainee_id",trainee.getStudentId().getValue());
                            //TrainerTraineeFragmentDirections.actionTrainerNavTraineeListToTrainerNavTraineeProfile(String.valueOf(trainee.getTrainingId().getValue())).setTrainingId(String.valueOf(trainee.getTrainingId().getValue()));
                            Navigation.createNavigateOnClickListener(R.id.action_trainer_nav_trainee_list_to_trainer_nav_trainee_profile,bundle).onClick(view);
                        }
                    }
            );
        }

        @Override
        public int getItemCount() {
            return mainActivity.getTraineesList().size();
        }
    }
}