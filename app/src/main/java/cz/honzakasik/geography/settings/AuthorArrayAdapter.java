package cz.honzakasik.geography.settings;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import cz.honzakasik.geography.R;
import cz.honzakasik.geography.learning.countryinfotabs.gallery.galleryimage.MediaMetadata;

/**
 * Array adapter for list of media authors used in settings
 */
public class AuthorArrayAdapter extends ArrayAdapter<MediaMetadata> {

    private LayoutInflater inflater;

    public AuthorArrayAdapter(Context context, List<MediaMetadata> objects) {
        super(context, 0, objects);
        this.inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        final ViewHolder viewHolder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.list_item_author, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            //no need to inflate new view
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.populate(getItem(position));

        return convertView;
    }

    /**
     * Common pattern to ensure that {@link View#findViewById(int)} will not be called too many
     * times thus making scrolling in list faster
     * {@see https://developer.android.com/training/improving-layouts/smooth-scrolling.html}
     */
    private static final class ViewHolder {

        private final String authorPlaceholder;

        private final TextView mediaName;
        private final TextView author;
        private final TextView license;

        public ViewHolder(View rootView) {
            this.authorPlaceholder = rootView.getContext().getString(R.string.work_author_placeholder);
            this.mediaName = (TextView) rootView.findViewById(R.id.list_item_author_media_name);
            this.author = (TextView) rootView.findViewById(R.id.list_item_author_author);
            this.license = (TextView) rootView.findViewById(R.id.list_item_author_license);
        }

        /**
         * Populates view with provided metadata
         * @param metadata metadata used to populate view
         */
        public void populate(MediaMetadata metadata) {
            this.mediaName.setText(Html.fromHtml("<a href=" + metadata.getSourceURL() + ">" + metadata.getOriginalFilename() + "</a>"));
            this.mediaName.setMovementMethod(LinkMovementMethod.getInstance());
            if (metadata.isPublicDomain() && metadata.getAuthor().isEmpty()) {
                this.author.setText(R.string.work_public_domain);
            } else {
                this.author.setText(String.format(authorPlaceholder, metadata.getAuthor()));
            }
            this.license.setText(metadata.getLicense());
        }
    }
}
