package cz.honzakasik.geography.education.location.countryinfotabs.gallery;

import java.io.Serializable;
import java.net.URI;

class GalleryImage implements Serializable {

    private String name;
    private String description;
    private String author;
    private String license;

    private URI imagePath;

    private boolean publicDomain;

    private GalleryImage(Builder builder) {
        this.name = builder.name;
        this.description = builder.description;
        this.author = builder.author;
        this.license = builder.license;
        this.imagePath = builder.imagePath;
        this.publicDomain = builder.publicDomain;
    }

    public String getName() {
        return name;
    }

    public URI getImagePath() {
        return imagePath;
    }

    public String getDescription() {
        return description;
    }

    public String getAuthor() {
        return author;
    }

    public String getLicense() {
        return license;
    }

    public boolean isPublicDomain() {
        return publicDomain;
    }

    static final class Builder {

        private String name;
        private String description;
        private String author;
        private String license;
        private URI imagePath;
        private boolean publicDomain;

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder author(String author) {
            this.author = author;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder imagePath(URI imagePath) {
            this.imagePath = imagePath;
            return this;
        }

        public Builder publicDomain(boolean value) {
            this.publicDomain = value;
            return this;
        }

        public Builder license(String license) {
            this.license = license;
            return this;
        }

        public GalleryImage build() {
            return new GalleryImage(this);
        }

    }
}
