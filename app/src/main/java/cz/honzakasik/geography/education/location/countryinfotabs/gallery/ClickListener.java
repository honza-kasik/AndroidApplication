package cz.honzakasik.geography.education.location.countryinfotabs.gallery;

import android.view.View;

public interface ClickListener {
    void onClick(View view, int position);

    void onLongClick(View view, int position);
}
