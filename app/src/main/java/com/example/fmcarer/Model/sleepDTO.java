package com.example.fmcarer.Model;

public class sleepDTO {
    private String date;
    private String sleepTime;
    private String wakeUpTime;
    private String duration; // Duration in string format (e.g., "2 hours 30 minutes")

    // Default constructor
    public sleepDTO() {
    }

    public sleepDTO(String date, String sleepTime, String wakeUpTime, String duration) {
        this.date = date;
        this.sleepTime = sleepTime;
        this.wakeUpTime = wakeUpTime;
        this.duration = duration;
    }

    // Getters
    public String getDate() {
        return date;
    }

    public String getSleepTime() {
        return sleepTime;
    }

    public String getWakeUpTime() {
        return wakeUpTime;
    }

    public String getDuration() {
        return duration;
    }

    // New method to calculate the duration in hours
    public float getDurationInHours() {
        String[] sleepParts = sleepTime.split(":");
        String[] wakeParts = wakeUpTime.split(":");

        int sleepHour = Integer.parseInt(sleepParts[0]);
        int sleepMinute = Integer.parseInt(sleepParts[1]);
        int wakeHour = Integer.parseInt(wakeParts[0]);
        int wakeMinute = Integer.parseInt(wakeParts[1]);

        // Calculate sleep duration in minutes
        int sleepTotalMinutes = sleepHour * 60 + sleepMinute;
        int wakeTotalMinutes = wakeHour * 60 + wakeMinute;

        // Adjust for overnight sleep
        if (wakeTotalMinutes < sleepTotalMinutes) {
            wakeTotalMinutes += 24 * 60; // Add a full day's worth of minutes
        }

        int durationMinutes = wakeTotalMinutes - sleepTotalMinutes;
        return durationMinutes / 60f; // Return duration in hours
    }
}
