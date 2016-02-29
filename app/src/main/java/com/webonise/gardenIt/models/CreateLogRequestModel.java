package com.webonise.gardenIt.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CreateLogRequestModel {
    private String content;
    @SerializedName("plant_id")
    private int plantId;
    @SerializedName("garden_id")
    private int gardenId;
    @SerializedName("phone_number")
    private String phoneNumber;
    @SerializedName("plant_image")
    private List<PlantImage> plantImage;

    public int getGardenId() {
        return gardenId;
    }

    public void setGardenId(int gardenId) {
        this.gardenId = gardenId;
    }

    public int getPlantId() {
        return plantId;
    }

    public void setPlantId(int plantId) {
        this.plantId = plantId;
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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
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
