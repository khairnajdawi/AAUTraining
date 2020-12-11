package jo.edu.aau.aautraining.trainer.ui.trainee;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;

public class TrainerTraineeViewModel extends ViewModel {

    private MutableLiveData<ArrayList<TraineeModel>> traineeList;

    public TrainerTraineeViewModel() {
        traineeList = new MutableLiveData<>();
        traineeList.setValue(new ArrayList<>());
    }

    public MutableLiveData<ArrayList<TraineeModel>> getTraineeList() {
        return traineeList;
    }

    public void setTraineeList(MutableLiveData<ArrayList<TraineeModel>> traineeList) {
        this.traineeList = traineeList;
    }
    public void AddTrainee(TraineeModel trainee){
        traineeList.getValue().add(trainee);
    }

    public void clearTraineeList() {
        traineeList.setValue(new ArrayList<>());
    }
}