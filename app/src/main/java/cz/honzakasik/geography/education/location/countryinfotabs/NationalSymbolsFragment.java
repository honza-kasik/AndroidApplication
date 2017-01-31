package cz.honzakasik.geography.education.location.countryinfotabs;

import android.graphics.Picture;
import android.graphics.drawable.PictureDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import cz.honzakasik.geography.App;
import cz.honzakasik.geography.R;
import cz.honzakasik.geography.audioplayer.AudioPlayerView;
import cz.honzakasik.geography.common.location.country.Country;
import cz.honzakasik.geography.common.tasks.LoadFlagImageTask;
import cz.honzakasik.geography.common.tasks.PostExecuteTask;
import cz.honzakasik.geography.common.utils.PropUtils;
import cz.honzakasik.geography.education.location.CountryInfoActivity;

public class NationalSymbolsFragment extends Fragment {

    private Logger logger = LoggerFactory.getLogger(NationalSymbolsFragment.class);

    private AudioPlayerView playerView;
    private TextView anthemLabel;
    private ImageView flagImageView;
    private ProgressBar flagProgressBar;
    private RelativeLayout flagWrapper;

    private LruCache<Country, Picture> flagCache;

    public NationalSymbolsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance) {
        return inflater.inflate(R.layout.fragment_ci_national_symbols, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        this.flagCache = ((App)getContext().getApplicationContext()).getGlobalFlagCache();
        this.anthemLabel = (TextView) getView().findViewById(R.id.national_symbols_anthem_label);
        this.flagImageView = (ImageView) getView().findViewById(R.id.flagView);
        this.flagProgressBar = (ProgressBar) getView().findViewById(R.id.flag_progress_bar);
        this.flagWrapper = (RelativeLayout) getView().findViewById(R.id.flag_wrapper);

        Country country = ((CountryInfoActivity) getActivity()).getCountry();

        setFlagImage(country);
        try {
            showPlayerAndSetSource(country);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setFlagImage(Country country) {
        flagImageView.setVisibility(View.INVISIBLE);
        flagProgressBar.setVisibility(View.VISIBLE);
        Picture flag = flagCache.get(country);
        if (flag == null) {
            new LoadFlagImageTask(getContext(), new PostExecuteTask<Picture>() {
                @Override
                public void run(Picture picture) {
                    flagImageView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
                    flagProgressBar.setVisibility(View.INVISIBLE);
                    flagImageView.setVisibility(View.VISIBLE);
                    flagImageView.setImageDrawable(new PictureDrawable(picture));
                }
            }).execute(country);
        } else {
            flagImageView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
            flagProgressBar.setVisibility(View.INVISIBLE);
            flagImageView.setVisibility(View.VISIBLE);
            flagImageView.setImageDrawable(new PictureDrawable(flag));
        }
        //Different states have different height flags - need to reset layout params
        flagWrapper.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT)
        );
    }

    private void showPlayerAndSetSource(Country country) throws IOException {
        playerView = (AudioPlayerView) getView().findViewById(R.id.anthem_player);
        try {
            playerView.initializePlayer(getFileUriAccordingToCountry(country), getActivity());
        } catch (IllegalStateException e) {
            playerView.setVisibility(View.INVISIBLE);
            anthemLabel.setVisibility(View.INVISIBLE);
            logger.error("Player initialization ended in illegal state!", e);
        }
    }

    private Uri getFileUriAccordingToCountry(Country country) throws IOException {
        String alpha2Code = country.getIso2().toLowerCase();
        String rootPath = PropUtils.get("resources.country.anthem.path");
        String dirPath = rootPath + File.separator + alpha2Code;
        String[] files = getContext().getAssets().list(dirPath);

        logger.info("Found {} files in '{}': {}.", files.length, dirPath, Arrays.toString(files));

        if (files.length != 1) {
            logger.error("One file is expected!" +
                    "Count of files in " + dirPath + "' : '" + files.length + "'.");
            return null;
        }

        String fullPath = dirPath + File.separator + files[0];
        String fileUri = PropUtils.get("resources.country.photo.uri.prefix") + File.separator + fullPath;
        logger.info("Obtained uri '{}'.", fileUri);
        return Uri.parse(fileUri);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        playerView.close();
    }

    @Override
    public void onPause() {
        super.onPause();
        playerView.close();
    }
}
