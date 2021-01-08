package jo.edu.aau.aautraining.shared.ui.notifications;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class NotificationViewModel extends ViewModel {
    MutableLiveData<List<NotificationItem>> notificationList;

    public NotificationViewModel() {
        notificationList = new MutableLiveData<>();
        notificationList.setValue(new ArrayList<>());
    }

    public MutableLiveData<List<NotificationItem>> getNotificationList() {
        return notificationList;
    }

    public void addNotification(NotificationItem notificationItem) {
        Objects.requireNonNull(notificationList.getValue()).add(notificationItem);
    }
}