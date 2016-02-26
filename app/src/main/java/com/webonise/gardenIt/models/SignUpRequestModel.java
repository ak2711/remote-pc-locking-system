package com.webonise.gardenIt.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SignUpRequestModel {
    private String name;
    private String email;
    private String password;
    @SerializedName("phone_number")
    private String phoneNumber;
    @SerializedName("referred_by")
    private String referredBy;

    //The name of the variable is plant_image instead of profile_image cause we are too lazy to modify
    // and create new parameter name.
    @SerializedName("plant_image")
    private List<PlantImage> plantImage;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getReferredBy() {
        return referredBy;
    }

    public void setReferredBy(String referredBy) {
        this.referredBy = referredBy;
    }

    public List<PlantImage> getPlantImage() {
        return plantImage;
    }

    public void setPlantImage(List<PlantImage> plantImage) {
        this.plantImage = plantImage;
    }

    public class PlantImage {
        private String image;

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }
    }
}
