package cz.honzakasik.geography.learning.countryinfotabs.gallery.galleryimage;

import android.content.Context;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

import cz.honzakasik.geography.common.utils.PropUtils;

public class GalleryImageMetadataParser {

    private static final String[] PUBLIC_DOMAIN_LICENSES = {"CC PD"};

    private ObjectMapper objectMapper;
    private Context context;

    private GalleryImageMetadata metadata;

    private GalleryImageMetadataParser(Builder builder) throws IOException {
        this.objectMapper = builder.objectMapper;
        this.context = builder.context;
        this.metadata = parse(builder.inputStream);
    }

    public GalleryImageMetadata getMetadata() {
        return metadata;
    }

    private GalleryImageMetadata parse(InputStream inputStream) throws IOException {
        final JsonNode rootNode = objectMapper.readValue(inputStream, JsonNode.class);

        return new GalleryImageMetadata.Builder()
                .sourceURL(rootNode.get(PropUtils.get("resources.country.photo.metadata.json.sourceurl")).asText())
                .originalFilename(rootNode.get(PropUtils.get("resources.country.photo.metadata.json.originalfilename")).asText())
                .description(getLocalizedDescriptionNode(rootNode).asText())
                .author(rootNode.get(PropUtils.get("resources.country.photo.metadata.json.author")).asText())
                .license(rootNode.get(PropUtils.get("resources.country.photo.metadata.json.license")).asText())
                .publicDomain(isPublicDomain(rootNode.get(PropUtils.get("resources.country.photo.metadata.json.license")).asText()))
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
