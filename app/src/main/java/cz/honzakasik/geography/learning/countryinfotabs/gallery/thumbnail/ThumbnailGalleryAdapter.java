package cz.honzakasik.geography.learning.countryinfotabs.gallery.thumbnail;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.signature.StringSignature;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import cz.honzakasik.geography.R;
import cz.honzakasik.geography.learning.countryinfotabs.gallery.galleryimage.GalleryImage;

public class ThumbnailGalleryAdapter extends RecyclerView.Adapter<ThumbnailGalleryAdapter.ViewHolder> {

    private Logger logger = LoggerFactory.getLogger(ThumbnailGalleryAdapter.class);

    private List<GalleryImage> images = new ArrayList<>();
    private Context context;

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView thumbnail;

        public ViewHolder(View view) {
            super(view);
            thumbnail = (ImageView) view.findViewById(R.id.thumbnail);
        }
    }

    public ThumbnailGalleryAdapter(Context context, List<GalleryImage> images) {
        this.context = context;
        this.images = images;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.gallery_thumbnail, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        GalleryImage image = images.get(position);

        logger.info("Loading image '{}'", image.getImagePath());
        Glide.with(context).load(image.getImagePath().toString())
                .thumbnail(0.5f)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.RESULT)
                .signature(new StringSignature(image.getMetadata().getOriginalFilename()))
                .into(holder.thumbnail);
    }

    @Override
    public int getItemCount() {
        return images.size();
    }

}