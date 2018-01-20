package com.ashishlakhmani.dit_sphere.classes;


public class MessageObject {

    private String object_id;
    private String student_id;
    private String message;
    private String date;
    private String sendStatus;

    public MessageObject(String object_id, String student_id, String message, String date, String sendStatus) {
        this.object_id = object_id;
        this.student_id = student_id;
        this.message = message;
        this.date = date;
        this.sendStatus = sendStatus;
    }

    public String getObject_id() {
        return object_id;
    }

    public String getStudent_id() {
        return student_id;
    }

    public String getMessage() {
        return message;
    }

    public String getDate() {
        return date;
    }

    public String getSendStatus() {
        return sendStatus;
    }


    public void setSendStatus(String sendStatus) {
        this.sendStatus = sendStatus;
    }
}
