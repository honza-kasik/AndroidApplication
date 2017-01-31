package cz.honzakasik.geography.education.location.countryinfotabs.gallery;

import java.io.Serializable;

public class GalleryImage implements Serializable {

    private String name;

    /***
     * A file path, or a uri or url
     */
    private String image;

    public GalleryImage(String name, String image) {
        this.name = name;
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }
}
