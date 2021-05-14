package com.rsa.greasemechanic.Models;

public class ModelStaff {

    private String staffImage;
    private String staffName;
    private String staffDOB;
    private String staffExperience;

    public ModelStaff() {
        //Empty Constructor required.
    }

    public ModelStaff(String staffImage, String staffName, String staffDOB, String staffExperience) {
        this.staffImage = staffImage;
        this.staffName = staffName;
        this.staffDOB = staffDOB;
        this.staffExperience = staffExperience;
    }

    public String getStaffImage() {
        return staffImage;
    }

    public String getStaffName() {
        return staffName;
    }

    public String getStaffDOB() {
        return staffDOB;
    }

    public String getStaffExperience() {
        return staffExperience;
    }
}
