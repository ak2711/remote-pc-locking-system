package com.webonise.gardenIt.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class UserDashboardModel {

    private User user;
    private int status;
    private String message;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public class User {
        private int id;
        private String email;
        private String name;
        @SerializedName("phone_number")
        private String phoneNumber;
        @SerializedName("created_at")
        private String createdAt;
        @SerializedName("updated_at")
        private String updatedAt;
        private List<Gardens> gardens;
        private Links links;
        @SerializedName("supported_gardens")
        private List<Gardens> supportedGardens;

        public Links getLinks() {
            return links;
        }

        public void setLinks(Links links) {
            this.links = links;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPhoneNumber() {
            return phoneNumber;
        }

        public void setPhoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
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

        public List<Gardens> getGardens() {
            return gardens;
        }

        public void setGardens(List<Gardens> gardens) {
            this.gardens = gardens;
        }

        public List<Gardens> getSupportedGardens() {
            return supportedGardens;
        }

        public void setSupportedGardens(List<Gardens> supportedGardens) {
            this.supportedGardens = supportedGardens;
        }

        public class Links {
            @SerializedName("store_link")
            private String storeLink;

            public String getStoreLink() {
                return storeLink;
            }

            public void setStoreLink(String storeLink) {
                this.storeLink = storeLink;
            }
        }
        public class Gardens {

            private int id;
            @SerializedName("user_id")
            private int userId;
            private String name;
            @SerializedName("garden_type")
            private String gardenType;
            private String description;
            private String latitude;
            private String longitude;
            private String address;
            @SerializedName("created_at")
            private String createdAt;
            @SerializedName("updated_at")
            private String updatedAt;
            private List<Plants> plants;

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

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

            public String getGardenType() {
                return gardenType;
            }

            public void setGardenType(String gardenType) {
                this.gardenType = gardenType;
            }

            public String getDescription() {
                return description;
            }

            public void setDescription(String description) {
                this.description = description;
            }

            public String getLatitude() {
                return latitude;
            }

            public void setLatitude(String latitude) {
                this.latitude = latitude;
            }

            public String getLongitude() {
                return longitude;
            }

            public void setLongitude(String longitude) {
                this.longitude = longitude;
            }

            public String getAddress() {
                return address;
            }

            public void setAddress(String address) {
                this.address = address;
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

            public List<Plants> getPlants() {
                return plants;
            }

            public void setPlants(List<Plants> plants) {
                this.plants = plants;
            }

            public class Plants {
                private int id;
                @SerializedName("garden_id")
                private int gardenId;
                private String name;
                private String description;
                @SerializedName("created_at")
                private String createdAt;
                @SerializedName("updated_at")
                private String updatedAt;
                private List<Images> images;
                private List<Logs> logs;

                public List<Logs> getLogs() {
                    return logs;
                }

                public void setLogs(List<Logs> logs) {
                    this.logs = logs;
                }

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

                public String getName() {
                    return name;
                }

                public void setName(String name) {
                    this.name = name;
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
                    private Image image;
                    private String caption;
                    @SerializedName("created_at")
                    private String createdAt;
                    @SerializedName("updated_at")
                    private String updatedAt;
                    @SerializedName("request_id")
                    private String requestId;

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

                    public Image getImage() {
                        return image;
                    }

                    public void setImage(Image image) {
                        this.image = image;
                    }

                    public String getCaption() {
                        return caption;
                    }

                    public void setCaption(String caption) {
                        this.caption = caption;
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

                    public String getRequestId() {
                        return requestId;
                    }

                    public void setRequestId(String requestId) {
                        this.requestId = requestId;
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

                public class Logs {
                    private int id;
                    private String content;
                    @SerializedName("loggable_id")
                    private int loggableId;
                    @SerializedName("loggable_type")
                    private String loggableType;
                    @SerializedName("created_at")
                    private String createdAt;
                    @SerializedName("updated_at")
                    private String updatedAt;
                    private List<Images> images;

                    public String getLoggableType() {
                        return loggableType;
                    }

                    public void setLoggableType(String loggableType) {
                        this.loggableType = loggableType;
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

                    public int getId() {
                        return id;
                    }

                    public void setId(int id) {
                        this.id = id;
                    }

                    public String getContent() {
                        return content;
                    }

                    public void setContent(String content) {
                        this.content = content;
                    }

                    public int getLoggableId() {
                        return loggableId;
                    }

                    public void setLoggableId(int loggableId) {
                        this.loggableId = loggableId;
                    }



                    public List<Images> getImages() {
                        return images;
                    }

                    public void setImages(List<Images> images) {
                        this.images = images;
                    }

                    public class Images{
                        private int id;
                        @SerializedName("issue_id")
                        private int issueId;
                        @SerializedName("plant_id")
                        private int plantId;
                        private Image image;
                        private String caption;
                        @SerializedName("created_at")
                        private String createdAt;
                        @SerializedName("updated_at")
                        private String updatedAt;
                        @SerializedName("request_id")
                        private String requestId;
                        @SerializedName("log_id")
                        private String logId;

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

                        public Image getImage() {
                            return image;
                        }

                        public void setImage(Image image) {
                            this.image = image;
                        }

                        public String getCaption() {
                            return caption;
                        }

                        public void setCaption(String caption) {
                            this.caption = caption;
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

                        public String getRequestId() {
                            return requestId;
                        }

                        public void setRequestId(String requestId) {
                            this.requestId = requestId;
                        }

                        public String getLogId() {
                            return logId;
                        }

                        public void setLogId(String logId) {
                            this.logId = logId;
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
        }
    }
}
