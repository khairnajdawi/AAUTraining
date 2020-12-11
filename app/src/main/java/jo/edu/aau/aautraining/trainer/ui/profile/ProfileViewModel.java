package jo.edu.aau.aautraining.trainer.ui.profile;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ProfileViewModel extends ViewModel {
    private MutableLiveData<String> name,specialty,mobile,email,jobTitle,companyName,imageLink;

    public ProfileViewModel() {
        name=new MutableLiveData<>();
        specialty=new MutableLiveData<>();
        mobile=new MutableLiveData<>();
        email=new MutableLiveData<>();
        jobTitle=new MutableLiveData<>();
        companyName=new MutableLiveData<>();
        imageLink=new MutableLiveData<>();
    }

    public MutableLiveData<String> getName() {
        return name;
    }

    public void setName(String name) {
        this.name.setValue(name);
    }

    public MutableLiveData<String> getSpecialty() {
        return specialty;
    }

    public void setSpecialty(String specialty) {
        this.specialty.setValue(specialty);
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

    public MutableLiveData<String> getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle.setValue(jobTitle);
    }

    public MutableLiveData<String> getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName.setValue(companyName);
    }

    public MutableLiveData<String> getImageLink() {
        return imageLink;
    }

    public void setImageLink(String imageLink) {
        this.imageLink.setValue(imageLink);
    }
}