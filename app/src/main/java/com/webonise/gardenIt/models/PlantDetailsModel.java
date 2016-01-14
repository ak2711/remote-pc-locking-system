package com.webonise.gardenIt.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PlantDetailsModel {

    private int status;
    private String message;
    private Plant plant;

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

    public Plant getPlant() {
        return plant;
    }

    public void setPlant(Plant plant) {
        this.plant = plant;
    }

    public class Plant {
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
