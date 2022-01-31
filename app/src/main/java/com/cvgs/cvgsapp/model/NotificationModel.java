package com.cvgs.cvgsapp.model;

public class NotificationModel {
    String id_notification, description, details, date_notify, datetime_notify;
    boolean is_read;

    public NotificationModel(String id_notification, String description, String details, String date_notify, String datetime_notify, boolean is_read) {
        this.id_notification = id_notification;
        this.description = description;
        this.details = details;
        this.date_notify = date_notify;
        this.datetime_notify = datetime_notify;
        this.is_read = is_read;
    }

    public String getId_notification() {
        return id_notification;
    }

    public String getDescription() {
        return description;
    }

    public String getDetails() {
        return details;
    }

    public String getDate_notify() {
        return date_notify;
    }

    public String getDatetime_notify() {
        return datetime_notify;
    }

    public boolean is_read() {
        return is_read;
    }
}
