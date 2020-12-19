package jo.edu.aau.aautraining.supervisor.ui.schedule;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ScheduleControlViewModel extends ViewModel {
    private MutableLiveData<String> studentName, scheduleDate, scheduleTime;

    public ScheduleControlViewModel() {
        studentName = new MutableLiveData<>();
        scheduleDate = new MutableLiveData<>();
        scheduleTime = new MutableLiveData<>();
    }

    public MutableLiveData<String> getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName.setValue(studentName);
    }

    public MutableLiveData<String> getScheduleDate() {
        return scheduleDate;
    }

    public void setScheduleDate(String scheduleDate) {
        this.scheduleDate.setValue(scheduleDate);
    }

    public MutableLiveData<String> getScheduleTime() {
        return scheduleTime;
    }

    public void setScheduleTime(String scheduleTime) {
        this.scheduleTime.setValue(scheduleTime);
    }
}