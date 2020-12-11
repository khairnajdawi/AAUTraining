package jo.edu.aau.aautraining.student.ui.chat;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class StudentChatViewModel extends ViewModel {
    public String text;
    private MutableLiveData<String> contactName;
    public StudentChatViewModel() {
        contactName = new MutableLiveData<>();
    }
    public LiveData<String> getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName.setValue(contactName);
    }
}