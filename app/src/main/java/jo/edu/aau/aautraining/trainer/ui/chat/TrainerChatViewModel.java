package jo.edu.aau.aautraining.trainer.ui.chat;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class TrainerChatViewModel extends ViewModel {
    private MutableLiveData<String> contactName;
    private MutableLiveData<Integer> contactId;
    private int contactType=0;

    public TrainerChatViewModel() {
        contactName = new MutableLiveData<>();
        contactId = new MutableLiveData<>();
    }

    public MutableLiveData<String> getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName.setValue(contactName);
    }

    public MutableLiveData<Integer> getContactId() {
        return contactId;
    }

    public void setContactId(Integer contactId) {
        this.contactId.setValue(contactId);
    }

    public int getContactType() {
        return contactType;
    }

    public void setContactType(int contactType) {
        this.contactType = contactType;
    }
}