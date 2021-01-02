package jo.edu.aau.aautraining.shared;

public class MessageModel {
    private String msg;
    private String time;
    private String fromOrTo;

    public MessageModel() {
    }

    public MessageModel(String msg, String time, String fromOrTo) {
        this.msg = msg;
        this.time = time;
        this.fromOrTo = fromOrTo;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getFromOrTo() {
        return fromOrTo;
    }

    public void setFromOrTo(String fromOrTo) {
        this.fromOrTo = fromOrTo;
    }
}
