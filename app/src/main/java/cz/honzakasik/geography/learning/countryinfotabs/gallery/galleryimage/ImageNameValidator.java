package cz.honzakasik.geography.learning.countryinfotabs.gallery.galleryimage;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ImageNameValidator {

    private Pattern pattern;
    private Matcher matcher;

    private static final String IMAGE_PATTERN =
            "([^\\s]+(\\.(?i)(jpg|png|gif|bmp))$)";

    public ImageNameValidator() {
        pattern = Pattern.compile(IMAGE_PATTERN);
    }

    /**
     * Validate image with regular expression
     *
     * @param image image for validation
     * @return true valid image, false invalid image
     */
    public boolean isExpectedImageFormat(final String image) {

        matcher = pattern.matcher(image);
        return matcher.matches();
    }
}

