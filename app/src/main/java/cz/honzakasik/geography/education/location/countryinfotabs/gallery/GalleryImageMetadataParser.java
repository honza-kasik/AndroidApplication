package cz.honzakasik.geography.education.location.countryinfotabs.gallery;

import android.content.Context;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

import cz.honzakasik.geography.common.utils.PropUtils;

public class GalleryImageMetadataParser {

    private static final String[] PUBLIC_DOMAIN_LICENSES = {"CC PD"};

    private String author;
    private String license;
    private String sourceURL;
    private String description;
    private String originalFilename;
    private boolean publicDomain;
    private ObjectMapper objectMapper;
    private Context context;

    private GalleryImageMetadataParser(Builder builder) throws IOException {
        this.objectMapper = builder.objectMapper;
        this.context = builder.context;
        parse(builder.inputStream);
    }

    private void parse(InputStream inputStream) throws IOException {
        final JsonNode countryJsonNode = objectMapper.readValue(inputStream, JsonNode.class);

        this.sourceURL = countryJsonNode.get(PropUtils.get("resources.country.photo.metadata.json.sourceurl")).asText();
        this.originalFilename = countryJsonNode.get(PropUtils.get("resources.country.photo.metadata.json.originalfilename")).asText();
        this.description = getLocalizedDescriptionNode(countryJsonNode).asText();
        this.author = countryJsonNode.get(PropUtils.get("resources.country.photo.metadata.json.author")).asText();
        this.license = countryJsonNode.get(PropUtils.get("resources.country.photo.metadata.json.license")).asText();
        this.publicDomain = isPublicDomain(this.license);
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

        private InputStream inputStream;
        private ObjectMapper objectMapper;
        private Context context;

        public Builder(Context context) {
            this.context = context;
        }

        public Builder inputStream(InputStream inputStream) {
            this.inputStream = inputStream;
            return this;
        }

        public Builder objectMapper(ObjectMapper objectMapper) {
            this.objectMapper = objectMapper;
            return this;
        }

        public GalleryImageMetadataParser build() throws IOException {
            if (objectMapper == null) {
                objectMapper = new ObjectMapper();
            }
            return new GalleryImageMetadataParser(this);
        }

    }
}
