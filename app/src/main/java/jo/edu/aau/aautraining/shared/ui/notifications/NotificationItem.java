package jo.edu.aau.aautraining.shared.ui.notifications;

public class NotificationItem {
    private int id;
    private String notificationText;
    private boolean isSeen;

    public NotificationItem(int id, String notificationText, boolean isSeen) {
        this.id = id;
        this.notificationText = notificationText;
        this.isSeen = isSeen;
    }

    public String getNotificationText() {
        return notificationText;
    }

    public void setNotificationText(String notificationText) {
        this.notificationText = notificationText;
    }

    public boolean isSeen() {
        return isSeen;
    }

    public void setSeen(boolean seen) {
        isSeen = seen;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
