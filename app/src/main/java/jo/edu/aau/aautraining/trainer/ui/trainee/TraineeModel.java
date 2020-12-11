package jo.edu.aau.aautraining.trainer.ui.trainee;


import androidx.lifecycle.MutableLiveData;

public class TraineeModel{
    private MutableLiveData<String> studentName;
    private MutableLiveData<String> studentImageLink;
    private MutableLiveData<Integer> studentId;
    private MutableLiveData<Integer> trainingId;
    public TraineeModel() {
        studentName = new MutableLiveData<>();
        studentImageLink = new MutableLiveData<>();
        studentId = new MutableLiveData<>();
        trainingId = new MutableLiveData<>();
    }

    public MutableLiveData<String> getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName.setValue(studentName);
    }

    public MutableLiveData<String> getStudentImageLink() {
        return studentImageLink;
    }

    public void setStudentImageLink(String studentImageLink) {
        this.studentImageLink.setValue(studentImageLink);
    }

    public MutableLiveData<Integer> getStudentId() {
        return studentId;
    }

    public void setStudentId(Integer studentId) {
        this.studentId.setValue(studentId);
    }

    public MutableLiveData<Integer> getTrainingId() {
        return trainingId;
    }

    public void setTrainingId(Integer trainingId) {
        this.trainingId.setValue(trainingId);
    }
}
