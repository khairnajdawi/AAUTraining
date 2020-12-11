package jo.edu.aau.aautraining.student.ui.contactsupervisor;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SupervisorViewModel extends ViewModel {

    private MutableLiveData<String> name,scientificRank,facultyName,departmentName,jobTitle,mobile,email,imageLink;

    public SupervisorViewModel() {
        name = new MutableLiveData<>();
        scientificRank = new MutableLiveData<>();
        facultyName = new MutableLiveData<>();
        departmentName = new MutableLiveData<>();
        jobTitle = new MutableLiveData<>();
        mobile = new MutableLiveData<>();
        email = new MutableLiveData<>();
        imageLink = new MutableLiveData<>();
    }

    public LiveData<String> getName() {
        return name;
    }
    public void setName(String name){
        this.name.setValue(name);
    }

    public MutableLiveData<String> getFacultyName() {
        return facultyName;
    }

    public void setFacultyName(String facultyName) {
        this.facultyName.setValue(facultyName);
    }
    public MutableLiveData<String> getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName.setValue(departmentName);
    }

    public MutableLiveData<String> getScientificRank() {
        return scientificRank;
    }

    public void setScientificRank(String scientificRank) {
        this.scientificRank.setValue(scientificRank);
    }


    public MutableLiveData<String> getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle.setValue(jobTitle);
    }

    public MutableLiveData<String> getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile.setValue(mobile);
    }

    public MutableLiveData<String> getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email.setValue(email);
    }
    public MutableLiveData<String> getImageLink(){
        return imageLink;
    }
    public void setImageLink(String imageLink){
        this.imageLink.setValue(imageLink);
    }
}