package jo.edu.aau.aautraining.trainer.ui.trainee.profile;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class TraineeProfileViewModel extends AndroidViewModel {

    private MutableLiveData<String> traineeName,
            facultyName,
            traineeUniNo,
            traineeMajor,
            supervisorName,
            trainingField,
            startDate,
            finishDate,
            trainingHoursText;
    private MutableLiveData<Integer> trainingId, supervisorId, trainingStatus;
    private MutableLiveData<String> passedHours, requiredHours, remainHours;

    public TraineeProfileViewModel(Application application) {
        super(application);
        traineeName = new MutableLiveData<>();
        facultyName = new MutableLiveData<>();
        traineeUniNo = new MutableLiveData<>();
        traineeMajor = new MutableLiveData<>();
        supervisorName = new MutableLiveData<>();
        trainingField = new MutableLiveData<>();
        startDate = new MutableLiveData<>();
        finishDate = new MutableLiveData<>();

        passedHours = new MutableLiveData<>();
        requiredHours = new MutableLiveData<>();
        trainingHoursText = new MutableLiveData<>();

        trainingId = new MutableLiveData<>();
        supervisorId = new MutableLiveData<>();
        trainingStatus = new MutableLiveData<>();
    }

    public MutableLiveData<String> getTrainingHoursText() {
        return trainingHoursText;
    }

    public void setTrainingHoursText(String trainingHoursText) {
        this.trainingHoursText.setValue(trainingHoursText);
    }

    public MutableLiveData<String> getPassedHours() {
        return passedHours;
    }

    public void setPassedHours(String passedHours) {
        this.passedHours.setValue(passedHours);
    }


    public MutableLiveData<Integer> getTrainingId() {
        return trainingId;
    }

    public void setTrainingId(Integer trainingId) {
        this.trainingId.setValue(trainingId);
    }

    public MutableLiveData<Integer> getSupervisorId() {
        return supervisorId;
    }

    public void setSupervisorId(Integer supervisorId) {
        this.supervisorId.setValue(supervisorId);
    }

    public MutableLiveData<String> getSupervisorName() {
        return supervisorName;
    }

    public void setSupervisorName(String supervisorName) {
        this.supervisorName.setValue(supervisorName);
    }

    public MutableLiveData<String> getTrainingField() {
        return trainingField;
    }

    public void setTrainingField(String trainingField) {
        this.trainingField.setValue(trainingField);
    }

    public MutableLiveData<String> getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate.setValue(startDate);
    }

    public MutableLiveData<String> getFinishDate() {
        return finishDate;
    }

    public void setFinishDate(String finishDate) {
        this.finishDate.setValue(finishDate);
    }

    public MutableLiveData<String> getRequiredHours() {
        return requiredHours;
    }

    public void setRequiredHours(String requiredHours) {
        this.requiredHours.setValue(requiredHours);
    }

    public MutableLiveData<String> getRemainHours() {
        return remainHours;
    }

    public void setRemainHours(String remainHours) {
        this.remainHours.setValue(remainHours);
    }

    public MutableLiveData<String> getFacultyName() {
        return facultyName;
    }

    public void setFacultyName(String facultyName) {
        this.facultyName.setValue(facultyName);
    }

    public MutableLiveData<String> getTraineeUniNo() {
        return traineeUniNo;
    }

    public void setTraineeUniNo(String traineeUniNo) {
        this.traineeUniNo.setValue(traineeUniNo);
    }

    public MutableLiveData<String> getTraineeMajor() {
        return traineeMajor;
    }

    public void setTraineeMajor(String traineeMajor) {
        this.traineeMajor.setValue(traineeMajor);
    }

    public LiveData<String> getTraineeName() {
        return traineeName;
    }

    public void setTraineeName(String traineeName) {
        this.traineeName.setValue(traineeName);
    }

    public MutableLiveData<Integer> getTrainingStatus() {
        return trainingStatus;
    }

    public void setTrainingStatus(int trainingStatus) {
        this.trainingStatus.setValue(trainingStatus);
    }
}