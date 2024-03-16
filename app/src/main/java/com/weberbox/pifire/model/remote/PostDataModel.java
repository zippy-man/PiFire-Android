package com.weberbox.pifire.model.remote;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@SuppressWarnings("unused")
public class PostDataModel {

    @SerializedName("pellets_action")
    @Expose
    private PelletsAction pelletsAction;
    @SerializedName("start_timer")
    @Expose
    private TimerAction startTimer;
    @SerializedName("pause_timer")
    @Expose
    private Boolean pauseTimer;
    @SerializedName("stop_timer")
    @Expose
    private Boolean stopTimer;

    public TimerAction getStartTimer() {
        return startTimer;
    }

    public void setStartTimer(TimerAction startTimer) {
        this.startTimer = startTimer;
    }

    public PostDataModel withStartTimer(TimerAction startTimer) {
        this.startTimer = startTimer;
        return this;
    }

    public Boolean getPauseTimer() {
        return pauseTimer;
    }

    public void setPauseTimer(Boolean pauseTimer) {
        this.pauseTimer = pauseTimer;
    }

    public PostDataModel withPauseTimer(Boolean pauseTimer) {
        this.pauseTimer = pauseTimer;
        return this;
    }

    public Boolean getStopTimer() {
        return stopTimer;
    }

    public void setStopTimer(Boolean stopTimer) {
        this.stopTimer = stopTimer;
    }

    public PostDataModel withStopTimer(Boolean stopTimer) {
        this.stopTimer = stopTimer;
        return this;
    }

    public PelletsAction getPelletsAction() {
        return pelletsAction;
    }

    public void setPelletsPelletsAction(PelletsAction pelletsAction) {
        this.pelletsAction = pelletsAction;
    }

    public PostDataModel withPelletsAction(PelletsAction pelletsAction) {
        this.pelletsAction = pelletsAction;
        return this;
    }

    public static class PelletsAction {

        @SerializedName("profile")
        @Expose
        private String profile;
        @SerializedName("brand_name")
        @Expose
        private String brandName;
        @SerializedName("wood_type")
        @Expose
        private String woodType;
        @SerializedName("rating")
        @Expose
        private Integer rating;
        @SerializedName("comments")
        @Expose
        private String comments;
        @SerializedName("add_and_load")
        @Expose
        private Boolean addAndLoad;
        @SerializedName("delete_wood")
        @Expose
        private String deleteWood;
        @SerializedName("new_wood")
        @Expose
        private String newWood;
        @SerializedName("delete_brand")
        @Expose
        private String deleteBrand;
        @SerializedName("new_brand")
        @Expose
        private String newBrand;
        @SerializedName("log_item")
        @Expose
        private String logItem;

        public String getProfile() {
            return profile;
        }

        public void setProfile(String profile) {
            this.profile = profile;
        }

        public PelletsAction withProfile(String profile) {
            this.profile = profile;
            return this;
        }

        public String getBrandName() {
            return brandName;
        }

        public void setBrandName(String brandName) {
            this.brandName = brandName;
        }

        public PelletsAction withBrandName(String brandName) {
            this.brandName = brandName;
            return this;
        }

        public String getWoodType() {
            return woodType;
        }

        public void setWoodType(String woodType) {
            this.woodType = woodType;
        }

        public PelletsAction withWoodType(String woodType) {
            this.woodType = woodType;
            return this;
        }

        public Integer getRating() {
            return rating;
        }

        public void setRating(Integer rating) {
            this.rating = rating;
        }

        public PelletsAction withRating(Integer rating) {
            this.rating = rating;
            return this;
        }

        public String getComments() {
            return comments;
        }

        public void setComments(String comments) {
            this.comments = comments;
        }

        public PelletsAction withComments(String comments) {
            this.comments = comments;
            return this;
        }

        public Boolean getAddAndLoad() {
            return addAndLoad;
        }

        public void setComments(Boolean addAndLoad) {
            this.addAndLoad = addAndLoad;
        }

        public PelletsAction withAddAndLoad(Boolean addAndLoad) {
            this.addAndLoad = addAndLoad;
            return this;
        }

        public String getDeleteWood() {
            return deleteWood;
        }

        public void setDeleteWood(String deleteWood) {
            this.deleteWood = deleteWood;
        }

        public PelletsAction withDeleteWood(String deleteWood) {
            this.deleteWood = deleteWood;
            return this;
        }

        public String getNewWood() {
            return newWood;
        }

        public void setNewWood(String newWood) {
            this.newWood = newWood;
        }

        public PelletsAction withNewWood(String newWood) {
            this.newWood = newWood;
            return this;
        }

        public String getDeleteBrand() {
            return deleteBrand;
        }

        public void setDeleteBrand(String deleteBrand) {
            this.deleteBrand = deleteBrand;
        }

        public PelletsAction withDeleteBrand(String deleteBrand) {
            this.deleteBrand = deleteBrand;
            return this;
        }

        public String getNewBrand() {
            return newBrand;
        }

        public void setNewBrand(String newBrand) {
            this.newBrand = newBrand;
        }

        public PelletsAction withNewBrand(String newBrand) {
            this.newBrand = newBrand;
            return this;
        }

        public String getLogItem() {
            return logItem;
        }

        public void setLogItem(String logItem) {
            this.logItem = logItem;
        }

        public PelletsAction withLogItem(String logItem) {
            this.logItem = logItem;
            return this;
        }

    }

    public static class Timer {
        @SerializedName("start_timer")
        @Expose
        private TimerAction startTimer;
        @SerializedName("pause_timer")
        @Expose
        private Boolean pauseTimer;
        @SerializedName("stop_timer")
        @Expose
        private Boolean stopTimer;

        public TimerAction getStartTimer() {
            return startTimer;
        }

        public void setStartTimer(TimerAction startTimer) {
            this.startTimer = startTimer;
        }

        public Timer withStartTimer(TimerAction startTimer) {
            this.startTimer = startTimer;
            return this;
        }

        public Boolean getPauseTimer() {
            return pauseTimer;
        }

        public void setPauseTimer(Boolean pauseTimer) {
            this.pauseTimer = pauseTimer;
        }

        public Timer withPauseTimer(Boolean pauseTimer) {
            this.pauseTimer = pauseTimer;
            return this;
        }

        public Boolean getStopTimer() {
            return stopTimer;
        }

        public void setStopTimer(Boolean stopTimer) {
            this.stopTimer = stopTimer;
        }

        public Timer withStopTimer(Boolean stopTimer) {
            this.stopTimer = stopTimer;
            return this;
        }
    }

    public static class TimerAction {

        @SerializedName("hours_range")
        @Expose
        private Integer hoursRange;
        @SerializedName("minutes_range")
        @Expose
        private Integer minutesRange;
        @SerializedName("timer_shutdown")
        @Expose
        private Boolean timerShutdown;
        @SerializedName("timer_keep_warm")
        @Expose
        private Boolean timerKeepWarm;

        public Integer getHours() {
            return hoursRange;
        }

        public void setHours(Integer hoursRange) {
            this.hoursRange = hoursRange;
        }

        public TimerAction withHours(Integer hoursRange) {
            this.hoursRange = hoursRange;
            return this;
        }

        public Integer getMinutes() {
            return minutesRange;
        }

        public void setMinutes(Integer minutesRange) {
            this.minutesRange = minutesRange;
        }

        public TimerAction withMinutes(Integer minutesRange) {
            this.minutesRange = minutesRange;
            return this;
        }

        public Boolean getShutdown() {
            return timerShutdown;
        }

        public void setShutdown(Boolean timerShutdown) {
            this.timerShutdown = timerShutdown;
        }

        public TimerAction withShutdown(Boolean timerShutdown) {
            this.timerShutdown = timerShutdown;
            return this;
        }

        public Boolean getKeepWarm() {
            return timerKeepWarm;
        }

        public void setKeepWarm(Boolean timerKeepWarm) {
            this.timerKeepWarm = timerKeepWarm;
        }

        public TimerAction withKeepWarm(Boolean timerKeepWarm) {
            this.timerKeepWarm = timerKeepWarm;
            return this;
        }

    }

}
