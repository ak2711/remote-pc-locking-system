package com.webonise.gardenIt.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ServiceListModel {
    private int status;
    private List<Requests> requests;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public List<Requests> getRequests() {
        return requests;
    }

    public void setRequests(List<Requests> requests) {
        this.requests = requests;
    }

    public class Requests {
        private int id;
        @SerializedName("garden_id")
        private int gardenId;
        private String title;
        private String description;
        @SerializedName("created_at")
        private String createdAt;
        @SerializedName("updated_at")
        private String updatedAt;
        @SerializedName("user_id")
        private int userId;
        private String status;
        private List<Images> images;
        @SerializedName("available_timings")
        private String availableTimings;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getGardenId() {
            return gardenId;
        }

        public void setGardenId(int gardenId) {
            this.gardenId = gardenId;
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

        public String getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(String createdAt) {
            this.createdAt = createdAt;
        }

        public String getUpdatedAt() {
            return updatedAt;
        }

        public void setUpdatedAt(String updatedAt) {
            this.updatedAt = updatedAt;
        }

        public int getUserId() {
            return userId;
        }

        public void setUserId(int userId) {
            this.userId = userId;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public List<Images> getImages() {
            return images;
        }

        public void setImages(List<Images> images) {
            this.images = images;
        }

        public class Images {
            private int id;
            @SerializedName("issue_id")
            private int issueId;
            @SerializedName("plant_id")
            private int plantId;
            private String caption;
            @SerializedName("request_id")
            private int requestId;
            @SerializedName("created_at")
            private String createdAt;
            @SerializedName("updated_at")
            private String updatedAt;
            private Image image;

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public int getIssueId() {
                return issueId;
            }

            public void setIssueId(int issueId) {
                this.issueId = issueId;
            }

            public int getPlantId() {
                return plantId;
            }

            public void setPlantId(int plantId) {
                this.plantId = plantId;
            }

            public String getCaption() {
                return caption;
            }

            public void setCaption(String caption) {
                this.caption = caption;
            }

            public int getRequestId() {
                return requestId;
            }

            public void setRequestId(int requestId) {
                this.requestId = requestId;
            }

            public String getCreatedAt() {
                return createdAt;
            }

            public void setCreatedAt(String createdAt) {
                this.createdAt = createdAt;
            }

            public String getUpdatedAt() {
                return updatedAt;
            }

            public void setUpdatedAt(String updatedAt) {
                this.updatedAt = updatedAt;
            }

            public Image getImage() {
                return image;
            }

            public void setImage(Image image) {
                this.image = image;
            }

            public class Image {
                private String url;

                public String getUrl() {
                    return url;
                }

                public void setUrl(String url) {
                    this.url = url;
                }
            }
        }
    }
}
