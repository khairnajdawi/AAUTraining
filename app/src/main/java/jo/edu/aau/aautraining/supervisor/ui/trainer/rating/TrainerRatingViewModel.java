package jo.edu.aau.aautraining.supervisor.ui.trainer.rating;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class TrainerRatingViewModel extends ViewModel {
    private MutableLiveData<Float> averageRating;
    private MutableLiveData<Integer> finishReason, attendRating, learnWellRating, learnAbilityRating, gainedSkillsRating, applyingSkillRating;
    private MutableLiveData<String> recommendation;

    public TrainerRatingViewModel() {
        finishReason = new MutableLiveData<>();
        attendRating = new MutableLiveData<>();
        learnWellRating = new MutableLiveData<>();
        learnAbilityRating = new MutableLiveData<>();
        gainedSkillsRating = new MutableLiveData<>();
        applyingSkillRating = new MutableLiveData<>();
        recommendation = new MutableLiveData<>();
        averageRating = new MutableLiveData<>();
    }

    public MutableLiveData<Float> getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(Float averageRating) {
        this.averageRating.setValue(averageRating);
    }

    public MutableLiveData<Integer> getFinishReason() {
        return finishReason;
    }

    public void setFinishReason(Integer finishReason) {
        this.finishReason.setValue(finishReason);
    }

    public MutableLiveData<Integer> getAttendRating() {
        return attendRating;
    }

    public void setAttendRating(Integer attendRating) {
        this.attendRating.setValue(attendRating);
    }

    public MutableLiveData<Integer> getLearnWellRating() {
        return learnWellRating;
    }

    public void setLearnWellRating(Integer learnWellRating) {
        this.learnWellRating.setValue(learnWellRating);
    }

    public MutableLiveData<Integer> getLearnAbilityRating() {
        return learnAbilityRating;
    }

    public void setLearnAbilityRating(Integer learnAbilityRating) {
        this.learnAbilityRating.setValue(learnAbilityRating);
    }

    public MutableLiveData<Integer> getGainedSkillsRating() {
        return gainedSkillsRating;
    }

    public void setGainedSkillsRating(Integer gainedSkillsRating) {
        this.gainedSkillsRating.setValue(gainedSkillsRating);
    }

    public MutableLiveData<Integer> getApplyingSkillRating() {
        return applyingSkillRating;
    }

    public void setApplyingSkillRating(Integer applyingSkillRating) {
        this.applyingSkillRating.setValue(applyingSkillRating);
    }

    public MutableLiveData<String> getRecommendation() {
        return recommendation;
    }

    public void setRecommendation(String recommendation) {
        this.recommendation.setValue(recommendation);
    }
}