package cz.honzakasik.geography.learning.countryinfotabs.gallery.galleryimage;

public class GalleryImageMetadata {

    private String author;
    private String license;
    private String sourceURL;
    private String description;
    private String originalFilename;
    private boolean publicDomain;

    private GalleryImageMetadata(String author, String license, String sourceURL, String description, String originalFilename, boolean publicDomain) {
        this.author = author;
        this.license = license;
        this.sourceURL = sourceURL;
        this.description = description;
        this.originalFilename = originalFilename;
        this.publicDomain = publicDomain;
    }

    public String getAuthor() {
        return author;
    }

    public String getLicense() {
        return license;
    }

    public String getSourceURL() {
        return sourceURL;
    }

    public String getDescription() {
        return description;
    }

    public String getOriginalFilename() {
        return originalFilename;
    }

    public boolean isPublicDomain() {
        return publicDomain;
    }

    public static final class Builder {

        private String author;
        private String license;
        private String sourceURL;
        private String description;
        private String originalFilename;
        private boolean publicDomain;

        public Builder author(String author) {
            this.author = author;
            return this;
        }

        public Builder license(String license) {
            this.license = license;
            return this;
        }

        public Builder sourceURL(String sourceURL) {
            this.sourceURL = sourceURL;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder originalFilename(String originalFilename) {
            this.originalFilename = originalFilename;
            return this;
        }

        public Builder publicDomain(boolean publicDomain) {
            this.publicDomain = publicDomain;
            return this;
        }

        public GalleryImageMetadata build() {
            return new GalleryImageMetadata(author, license, sourceURL, description, originalFilename, publicDomain);
        }
    }

}
