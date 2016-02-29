package com.webonise.gardenIt.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class UserDetailsUpdateRequestModel {
    private String name;
    private String email;
    @SerializedName("phone_number")
    private String phoneNumber;
    @SerializedName("user_id")
    private int userId;
    //The name of the variable is plant_image instead of profile_image cause we are too lazy to
    // modify
    // and create new parameter name.
    @SerializedName("plant_image")
    private List<PlantImage> plantImage;

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

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

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
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
