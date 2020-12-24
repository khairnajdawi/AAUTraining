package jo.edu.aau.aautraining.supervisor.ui.students.rating;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SupervisorRatingViewModel extends ViewModel {
    private MutableLiveData<String> reason, notes;
    private MutableLiveData<Integer> rating;

    public SupervisorRatingViewModel() {
        rating = new MutableLiveData<>();
        reason = new MutableLiveData<>();
        notes = new MutableLiveData<>();
    }

    public MutableLiveData<String> getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason.setValue(reason);
    }

    public MutableLiveData<String> getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes.setValue(notes);
    }

    public MutableLiveData<Integer> getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating.setValue(rating);
    }
}