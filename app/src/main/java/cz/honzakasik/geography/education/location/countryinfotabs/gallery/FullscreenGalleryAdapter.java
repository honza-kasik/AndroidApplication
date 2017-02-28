package cz.honzakasik.geography.education.location.countryinfotabs.gallery;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.List;

import cz.honzakasik.geography.R;

public class FullscreenGalleryAdapter extends PagerAdapter {

    private Context context;
    private List<GalleryImage> images;

    public FullscreenGalleryAdapter(Context context, List<GalleryImage> images) {
        this.context = context;
        this.images = images;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.image_slider_fullscreen_preview, container, false);

        ImageView imageViewPreview = (ImageView) view.findViewById(R.id.image_preview);

        GalleryImage image = images.get(position);

        Glide.with(context).load(image.getImagePath().toString())
                .thumbnail(0.5f)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imageViewPreview);

        container.addView(view);

        return view;
    }

    @Override
    public int getCount() {
        return images.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object obj) {
        return view == obj;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }
}
