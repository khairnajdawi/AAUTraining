package jo.edu.aau.aautraining.supervisor.ui.students.schedule;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class StudentTrainingScheduleInfoViewModel extends ViewModel {
    private MutableLiveData<Integer> scheduleId, attended;
    private MutableLiveData<String> studentName, trainingDate, trainingPlace, startTime, endTime, gaindSkills, notes;

    public StudentTrainingScheduleInfoViewModel() {
        scheduleId = new MutableLiveData<>();
        studentName = new MutableLiveData<>();
        trainingDate = new MutableLiveData<>();
        trainingPlace = new MutableLiveData<>();
        startTime = new MutableLiveData<>();
        endTime = new MutableLiveData<>();
        gaindSkills = new MutableLiveData<>();
        notes = new MutableLiveData<>();
        attended = new MutableLiveData<>();

    }

    public MutableLiveData<Integer> getScheduleId() {
        return scheduleId;
    }

    public void setScheduleId(Integer scheduleId) {
        this.scheduleId.setValue(scheduleId);
    }

    public MutableLiveData<String> getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName.setValue(studentName);
    }

    public MutableLiveData<Integer> getAttended() {
        return attended;
    }

    public void setAttended(Integer attended) {
        this.attended.setValue(attended);
    }

    public MutableLiveData<String> getTrainingDate() {
        return trainingDate;
    }

    public void setTrainingDate(String trainingDate) {
        this.trainingDate.setValue(trainingDate);
    }

    public MutableLiveData<String> getTrainingPlace() {
        return trainingPlace;
    }

    public void setTrainingPlace(String trainingPlace) {
        this.trainingPlace.setValue(trainingPlace);
    }

    public MutableLiveData<String> getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime.setValue(startTime);
    }

    public MutableLiveData<String> getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime.setValue(endTime);
    }

    public MutableLiveData<String> getGaindSkills() {
        return gaindSkills;
    }

    public void setGaindSkills(String gaindSkills) {
        this.gaindSkills.setValue(gaindSkills);
    }

    public MutableLiveData<String> getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes.setValue(notes);
    }
}