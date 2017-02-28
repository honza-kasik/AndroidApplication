package cz.honzakasik.geography.learning.flags;

import android.content.Context;
import android.graphics.Picture;
import android.graphics.drawable.PictureDrawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import cz.honzakasik.geography.R;
import cz.honzakasik.geography.common.location.country.Country;
import cz.honzakasik.geography.common.tasks.LoadFlagImageTask;
import cz.honzakasik.geography.common.tasks.PostExecuteTask;

public class FlagListItem extends RelativeLayout {

    private Country country;

    private ImageView flagImageView;
    private ProgressBar progressBar;

    public FlagListItem(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        this.flagImageView = (ImageView) this.findViewById(R.id.flag_image_view);
        this.progressBar = (ProgressBar) this.findViewById(R.id.flag_progress_bar);
    }

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
        new LoadFlagImageTask(getContext(), new PostExecuteTask<Picture>() {
            @Override
            public void run(Picture result) {
                flagImageView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
                flagImageView.setImageDrawable(new PictureDrawable(result));
                flagImageView.setVisibility(VISIBLE);
                progressBar.setVisibility(INVISIBLE);
            }
        }).execute(this.country);
    }
}
