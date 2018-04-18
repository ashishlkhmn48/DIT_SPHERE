package com.ashishlakhmani.dit_sphere.classes;

import java.io.Serializable;
import java.util.List;

public class FacultyData implements Serializable{
    private String name;
    private String contactNum;
    private String emailId;
    private String branch;
    private String location;
    private String imageUrl;
    private List<String> specialization;

    public FacultyData(String name, String contactNum, String emailId, String branch, String location, String imageUrl, List<String> specialization) {
        this.name = name;
        this.contactNum = contactNum;
        this.emailId = emailId;
        this.branch = branch;
        this.location = location;
        this.imageUrl = imageUrl;
        this.specialization = specialization;
    }

    public String getName() {
        return name;
    }

    public String getContactNum() {
        return contactNum;
    }

    public String getEmailId() {
        return emailId;
    }

    public String getBranch() {
        return branch;
    }

    public String getLocation() {
        return location;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public List<String> getSpecialization() {
        return specialization;
    }

    @Override
    public String toString() {
        return emailId;
    }
}
