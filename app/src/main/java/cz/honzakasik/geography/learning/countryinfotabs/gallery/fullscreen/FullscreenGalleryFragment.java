package cz.honzakasik.geography.learning.countryinfotabs.gallery.fullscreen;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Locale;

import cz.honzakasik.geography.R;
import cz.honzakasik.geography.learning.countryinfotabs.gallery.galleryimage.GalleryImage;

public class FullscreenGalleryFragment extends DialogFragment {

    private Logger logger = LoggerFactory.getLogger(FullscreenGalleryFragment.class);

    private List<GalleryImage> images;
    private ViewPager viewPager;
    private TextView countLabel;
    private TextView authorLabel;
    private TextView descriptionLabel;

    private int selectedPosition = 0;

    public FullscreenGalleryFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance) {
        View view = inflater.inflate(R.layout.fragment_image_slider, container, false);
        viewPager = (ViewPager) view.findViewById(R.id.image_slider_viewpager);
        countLabel = (TextView) view.findViewById(R.id.image_slider_count_label);
        authorLabel = (TextView) view.findViewById(R.id.image_slider_meta_author);
        descriptionLabel = (TextView) view.findViewById(R.id.image_slider_meta_description);

        images = (List<GalleryImage>) getArguments().getSerializable("images");
        selectedPosition = getArguments().getInt("position");

        logger.info("position: {}", selectedPosition);
        logger.info("images size: {}" , images.size());

        FullscreenGalleryAdapter fullscreenAdapter = new FullscreenGalleryAdapter(getContext(), images);
        viewPager.setAdapter(fullscreenAdapter);
        viewPager.addOnPageChangeListener(viewPagerPageChangeListener);

        setCurrentItem(selectedPosition);

        return view;
    }

    private void setCurrentItem(int position) {
        viewPager.setCurrentItem(position, false);
        displayMetaInfo(selectedPosition);
    }

    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageSelected(int position) {
            displayMetaInfo(position);
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }
    };

    private void displayMetaInfo(int position) {
        Locale currentLocale = getResources().getConfiguration().locale;
        String xOfYString = getString(R.string.x_of_y);
        countLabel.setText(String.format(currentLocale, xOfYString, (position + 1), images.size()));

        GalleryImage image = images.get(position);
        if (image.isPublicDomain()) {
            authorLabel.setText(getString(R.string.image_public_domain));
        } else {
            authorLabel.setText(String.format(getString(R.string.image_author_placeholder), image.getAuthor(), image.getLicense()));
        }
        descriptionLabel.setText(image.getDescription());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
    }

}