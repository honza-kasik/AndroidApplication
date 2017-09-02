package cz.honzakasik.geography.learning.countryinfotabs.gallery.galleryimage;

import java.io.Serializable;
import java.net.URI;

/**
 * Class representing image displayed in gallery
 */
public class GalleryImage implements Serializable {

    private String name;
    private URI imagePath;
    private MediaMetadata metadata;

    private GalleryImage(Builder builder) {
        this.name = builder.name;
        this.imagePath = builder.imagePath;
        this.metadata = builder.metadata;
    }

    public String getName() {
        return name;
    }

    public URI getImagePath() {
        return imagePath;
    }

    public MediaMetadata getMetadata() {
        return metadata;
    }

    public static final class Builder {

        private String name;
        private URI imagePath;
        private MediaMetadata metadata;

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder imagePath(URI imagePath) {
            this.imagePath = imagePath;
            return this;
        }

        public Builder metadata(MediaMetadata metadata) {
            this.metadata = metadata;
            return this;
        }

        public GalleryImage build() {
            return new GalleryImage(this);
        }
    }
}
