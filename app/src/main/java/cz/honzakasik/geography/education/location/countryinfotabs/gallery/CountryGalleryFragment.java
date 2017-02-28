package cz.honzakasik.geography.education.location.countryinfotabs.gallery;

import android.content.res.AssetManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.net.URI;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import cz.honzakasik.geography.R;
import cz.honzakasik.geography.common.location.country.Country;
import cz.honzakasik.geography.common.utils.PropUtils;
import cz.honzakasik.geography.education.location.CountryInfoActivity;

/**
 * Class representing whole gallery of images. In this class, images from file assets are loaded and
 * then displayed
 */
public class CountryGalleryFragment extends Fragment {

    private final Logger logger = LoggerFactory.getLogger(CountryGalleryFragment.class);

    private List<GalleryImage> images;

    public CountryGalleryFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance) {
        return inflater.inflate(R.layout.fragment_ci_gallery, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        RecyclerView recyclerView = (RecyclerView) getView().findViewById(R.id.country_gallery_recycler_view);
        recyclerView.addOnItemTouchListener(new ThumbnailTouchListener(getActivity().getApplicationContext(), recyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("images", (Serializable) images);
                bundle.putInt("position", position);

                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                FullscreenGalleryFragment newFragment = new FullscreenGalleryFragment();
                newFragment.setArguments(bundle);
                newFragment.show(ft, "slideshow");
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
        Country country = ((CountryInfoActivity) getActivity()).getCountry();


        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(), 2);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        images = new LinkedList<>();
        ThumbnailGalleryAdapter adapter = new ThumbnailGalleryAdapter(getContext().getApplicationContext(), images);
        recyclerView.setAdapter(adapter);

        try {
            images.addAll(getImagesAccordingToCountry(country));
        } catch (IOException e) {
            logger.error("Unable to load photos!", e);
        }
    }

    /**
     * Loads list of images with metadata according to iso-2 of country
     * @param country images of this country will be load
     * @return list of images with metadata of given country
     * @throws IOException
     */
    private List<GalleryImage> getImagesAccordingToCountry(@NonNull Country country)
            throws IOException {
        String alpha2Code = country.getIso2().toLowerCase();
        String rootPath = PropUtils.get("resources.country.photo.path");
        AssetManager assetManager = getContext().getAssets();
        String[] files = assetManager.list(rootPath + File.separator + alpha2Code);

        List<GalleryImage> images = new LinkedList<>();

        final ImageNameValidator nameValidator = new ImageNameValidator();
        final ObjectMapper objectMapper = new ObjectMapper();
        final GalleryImageMetadataParser.Builder metadataParserBuilder = new GalleryImageMetadataParser.Builder(getContext()).objectMapper(objectMapper);
        for (String file : files) {
            if (nameValidator.isExpectedImageFormat(file)) {
                final GalleryImage.Builder builder = new GalleryImage.Builder();

                final String countryRelativeFolder =  rootPath + File.separator + alpha2Code;
                final String countryFolderURI = PropUtils.get("resources.country.photo.uri.prefix") +
                        File.separator + countryRelativeFolder;
                //get photo uri
                final URI fileURI = URI.create(countryFolderURI + File.separator + file);
                logger.info("Creating new GalleryImage with uri '{}'.", fileURI.toString());
                builder.imagePath(fileURI);

                //load metadata
                final String metadataFileName = FilenameUtils.removeExtension(file) +
                        PropUtils.get("resources.country.photo.metadata.suffix");
                final GalleryImageMetadataParser parser = metadataParserBuilder
                        .inputStream(getContext().getAssets().open(countryRelativeFolder + File.separator + metadataFileName))
                        .build();

                builder.description(parser.getDescription())
                        .author(parser.getAuthor())
                        .license(parser.getLicense())
                        .publicDomain(parser.isPublicDomain());

                images.add(builder.build());
            }
        }
        return images;
    }

}
