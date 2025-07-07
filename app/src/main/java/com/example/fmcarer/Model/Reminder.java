package com.example.fmcarer.Model;

public class Reminder {
    public String reminderId;
    public String childId;
    public String title;
    public String description;
    public String time;
    public boolean isRepeat;
    public String repeatType;

    // Bắt buộc constructor rỗng cho Firebase
    public Reminder() {}

    public Reminder(String reminderId, String childId, String title, String description, String time, boolean isRepeat, String repeatType) {
        this.reminderId = reminderId;
        this.childId = childId;
        this.title = title;
        this.description = description;
        this.time = time;
        this.isRepeat = isRepeat;
        this.repeatType = repeatType;
    }

    // Getter & Setter
    public String getReminderId() {
        return reminderId;
    }

    public void setReminderId(String reminderId) {
        this.reminderId = reminderId;
    }

    public String getChildId() {
        return childId;
    }

    public void setChildId(String childId) {
        this.childId = childId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public boolean isRepeat() {
        return isRepeat;
    }

    public void setRepeat(boolean repeat) {
        isRepeat = repeat;
    }

    public String getRepeatType() {
        return repeatType;
    }

    public void setRepeatType(String repeatType) {
        this.repeatType = repeatType;
    }
}
