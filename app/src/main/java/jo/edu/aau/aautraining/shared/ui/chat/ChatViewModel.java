package jo.edu.aau.aautraining.shared.ui.chat;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ChatViewModel extends ViewModel {
    public String text;
    public String time;
    private MutableLiveData<String> contactName;

    public ChatViewModel() {
        contactName = new MutableLiveData<>();
    }

    public LiveData<String> getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName.setValue(contactName);
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}