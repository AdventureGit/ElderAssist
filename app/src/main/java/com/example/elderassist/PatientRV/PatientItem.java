package com.example.elderassist.PatientRV;

public class PatientItem {
    String name;
    String patientCode;
    int image;

    public PatientItem(int image, String name, String patientCode) {
        this.image = image;
        this.name = name;
        this.patientCode = patientCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPatientCode() {
        return patientCode;
    }

    public void setPatientCode(String patientCode) {
        this.patientCode = patientCode;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }
}
