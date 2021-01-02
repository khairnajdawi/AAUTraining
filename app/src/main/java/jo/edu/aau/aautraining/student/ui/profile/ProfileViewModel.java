package jo.edu.aau.aautraining.student.ui.profile;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class ProfileViewModel extends AndroidViewModel {

    private MutableLiveData<String> studentName,
            facultyName,
            studentUniNo,
            studentMajor,
            companyName,
            trainingField,
            startDate,
            finishDate;
    private MutableLiveData<String> requiredHours,passHours,remainHours;
    private MutableLiveData<Integer> trainingStatus;

    public ProfileViewModel(Application application) {
        super(application);
        studentName = new MutableLiveData<>();
        facultyName = new MutableLiveData<>();
        studentUniNo = new MutableLiveData<>();
        studentMajor = new MutableLiveData<>();
        companyName = new MutableLiveData<>();
        trainingField = new MutableLiveData<>();
        startDate = new MutableLiveData<>();
        finishDate = new MutableLiveData<>();
        requiredHours = new MutableLiveData<>();
        passHours = new MutableLiveData<>();
        remainHours = new MutableLiveData<>();
        trainingStatus = new MutableLiveData<>();
    }

    public MutableLiveData<Integer> getTrainingStatus() {
        return trainingStatus;
    }

    public void setTrainingStatus(Integer trainingStatus) {
        this.trainingStatus.setValue(trainingStatus);
    }

    public MutableLiveData<String> getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName.setValue(companyName);
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

    public MutableLiveData<String> getPassHours() {
        return passHours;
    }

    public void setPassHours(String passHours) {
        this.passHours.setValue(passHours);
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

    public MutableLiveData<String> getStudentUniNo() {
        return studentUniNo;
    }

    public void setStudentUniNo(String studentUniNo) {
        this.studentUniNo.setValue(studentUniNo);
    }

    public MutableLiveData<String> getStudentMajor() {
        return studentMajor;
    }

    public void setStudentMajor(String studentMajor) {
        this.studentMajor.setValue(studentMajor);
    }

    public LiveData<String> getStudentName() {
        return studentName;
    }
    public void setStudentName(String studentName){
        this.studentName.setValue(studentName);
    }
}