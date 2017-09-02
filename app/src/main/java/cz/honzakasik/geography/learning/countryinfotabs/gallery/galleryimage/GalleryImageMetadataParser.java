package cz.honzakasik.geography.learning.countryinfotabs.gallery.galleryimage;

import android.content.Context;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

import cz.honzakasik.geography.common.utils.PropUtils;

/**
 * Parser parsing {@link MediaMetadata} from JSON files
 */
public class GalleryImageMetadataParser {

    //Strings recognized as a public domain identifier
    private static final String[] PUBLIC_DOMAIN_LICENSES = {"CC PD"};

    private ObjectMapper objectMapper;
    private Context context;

    private MediaMetadata metadata;

    private GalleryImageMetadataParser(Builder builder) throws IOException {
        this.objectMapper = builder.objectMapper;
        this.context = builder.context;
        this.metadata = parse(builder.inputStream);
    }
    /**
     * Get parsed metadata
     * @return parsed {@link MediaMetadata}
     */
    public MediaMetadata getMetadata() {

        return metadata;
    }

    private MediaMetadata parse(InputStream inputStream) throws IOException {
        final JsonNode rootNode = objectMapper.readValue(inputStream, JsonNode.class);

        final String licenseString = rootNode.get(PropUtils.get("resources.country.photo.metadata.json.license")).asText();
        return new MediaMetadata.Builder()
                .sourceURL(rootNode.get(PropUtils.get("resources.country.photo.metadata.json.sourceurl")).asText())
                .originalFilename(rootNode.get(PropUtils.get("resources.country.photo.metadata.json.originalfilename")).asText())
                .description(getLocalizedDescriptionNode(rootNode).asText())
                .author(rootNode.get(PropUtils.get("resources.country.photo.metadata.json.author")).asText())
                .license(licenseString)
                .publicDomain(isPublicDomain(licenseString))
                .build();
    }

    private JsonNode getLocalizedDescriptionNode(JsonNode countryJsonNode) {
        final String descriptionIdentifier = PropUtils.get("resources.country.photo.metadata.json.description");
        final String defaultDescriptionNodeIdentifier = Locale.ENGLISH.getLanguage();
        final String localisedDescriptionNodeIdentifier = context.getResources().getConfiguration().locale.getLanguage();

        JsonNode descriptionNode = countryJsonNode.get(descriptionIdentifier).get(localisedDescriptionNodeIdentifier);
        if (descriptionNode == null) { //if localised text is not found
            descriptionNode = countryJsonNode.get(descriptionIdentifier).get(defaultDescriptionNodeIdentifier);
        }

        return descriptionNode;
    }

    private boolean isPublicDomain(String licenseString) {
        for (String license : PUBLIC_DOMAIN_LICENSES) {
            if (licenseString.equals(license)) {
                return true;
            }
        }
        return false;
    }

    public static final class Builder {

        private InputStream inputStream;
        private ObjectMapper objectMapper;
        private Context context;

        public Builder(Context context) {
            this.context = context;
        }

        /**
         * Input stream containing parseable JSON file with all required image metadata.
         * NO VALIDATION IS PERFORMED!!
         * @param inputStream Valid JSON with image metadata
         */
        public Builder inputStream(InputStream inputStream) {
            this.inputStream = inputStream;
            return this;
        }

        /**
         * {@link ObjectMapper} instance used to parse JSON from input stream. If not set, new
         * instance will be used
         * @param objectMapper instance used to parse file
         */
        public Builder objectMapper(ObjectMapper objectMapper) {
            this.objectMapper = objectMapper;
            return this;
        }

        /**
         * Build new {@link GalleryImageMetadataParser} instance
         */
        public GalleryImageMetadataParser build() throws IOException {
            if (objectMapper == null) {
                objectMapper = new ObjectMapper();
            }
            return new GalleryImageMetadataParser(this);
        }

    }
}
