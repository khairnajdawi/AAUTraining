package jo.edu.aau.aautraining.supervisor.ui.students;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;

import java.util.ArrayList;

import jo.edu.aau.aautraining.R;
import jo.edu.aau.aautraining.shared.MyFragment;
import jo.edu.aau.aautraining.supervisor.SupervisorMainActivity;
import jo.edu.aau.aautraining.trainer.TrainersMainActivity;
import jo.edu.aau.aautraining.trainer.ui.trainee.TraineeModel;
import jo.edu.aau.aautraining.trainer.ui.trainee.TrainerTraineeFragment;

public class SupervisorStudentsFragment extends MyFragment {

    private SupervisorStudentsViewModel mViewModel;
    private SupervisorMainActivity mainActivity;
    private RecyclerView recyclerView;
    private StudentAdapter studentAdapter;

    public static SupervisorStudentsFragment newInstance() {
        return new SupervisorStudentsFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mViewModel = ViewModelProviders.of(this).get(SupervisorStudentsViewModel.class);
        View root = inflater.inflate(R.layout.supervisor_students_fragment, container, false);
        recyclerView = root.findViewById(R.id.supervisor_students_recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 1));
        studentAdapter = new StudentAdapter();
        recyclerView.setAdapter(studentAdapter);

        mainActivity = (SupervisorMainActivity) getActivity();

        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    private class StudentViewHolder extends RecyclerView.ViewHolder {
        private TextView nameTextView;
        private NetworkImageView imageView;

        public StudentViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.trainee_item_textview);
            imageView = itemView.findViewById(R.id.trainee_item_imageview);
        }
    }

    private class StudentAdapter extends RecyclerView.Adapter<StudentViewHolder> {

        @NonNull
        @Override
        public StudentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new StudentViewHolder(
                    View.inflate(parent.getContext(), R.layout.trainee_item_layout, null)
            );
        }

        @Override
        public void onBindViewHolder(@NonNull StudentViewHolder holder, int position) {
            StudentModel studentModel = mainActivity.getStudentsList().get(position);
            holder.nameTextView.setText(studentModel.getStudentName().getValue());
            String imageLink = studentModel.getStudentImageLink().getValue();
            if (!imageLink.isEmpty()) {
                holder.imageView.setImageUrl(imageLink, mImageLoader);
                holder.imageView.setErrorImageResId(R.drawable.student_logo_color);
            } else {
                holder.imageView.setImageResource(R.drawable.student_logo_color);
            }
            holder.itemView.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Bundle bundle = new Bundle();
                            bundle.putInt("training_id", studentModel.getTrainingId().getValue());
                            bundle.putInt("student_id", studentModel.getStudentId().getValue());
                            Navigation.createNavigateOnClickListener(R.id.action_supervisor_nav_student_list_to_supervisor_nav_student_profile, bundle).onClick(view);
                        }
                    }
            );
        }

        @Override
        public int getItemCount() {
            return mainActivity.getStudentsList().size();
        }
    }
}