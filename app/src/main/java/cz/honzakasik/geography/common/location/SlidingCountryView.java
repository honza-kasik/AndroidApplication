package cz.honzakasik.geography.common.location;

import android.content.Context;
import android.graphics.Picture;
import android.graphics.drawable.PictureDrawable;
import android.os.AsyncTask;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.RelativeSizeSpan;
import android.text.style.SuperscriptSpan;
import android.util.AttributeSet;
import android.util.LruCache;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.caverock.androidsvg.SVG;
import com.caverock.androidsvg.SVGParseException;
import com.neovisionaries.i18n.CurrencyCode;
import com.neovisionaries.i18n.LanguageCode;
import com.wunderlist.slidinglayer.SlidingLayer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

import cz.honzakasik.geography.R;
import cz.honzakasik.geography.common.location.country.Country;
import cz.honzakasik.geography.common.utils.PropUtils;
import cz.honzakasik.geography.common.utils.ResHelper;

@Deprecated
public class SlidingCountryView extends SlidingLayer {

    private Logger logger = LoggerFactory.getLogger(SlidingCountryView.class);

    private TextView countryNameTextView;
    private TextView populationTextView;
    private TextView areaTextView;
    private TextView currencyTextView;
    private TextView languageTextView;

    private TextView populationLabelTextView;
    private TextView areaLabelTextView;
    private TextView currencyLabelTextView;
    private TextView languageLabelTextView;

    private ImageView flagImageView;
    private ProgressBar flagProgressBar;

    private LruCache<Country, Picture> flagCache;

    public SlidingCountryView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.flagCache = new LruCache<>(128);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        flagImageView = (ImageView) this.findViewById(R.id.flagView);
        flagProgressBar = (ProgressBar) this.findViewById(R.id.flag_progress_bar);

        countryNameTextView = (TextView) this.findViewById(R.id.countryName);

        populationTextView = (TextView) this.findViewById(R.id.country_view_population);
        areaTextView = (TextView) this.findViewById(R.id.country_view_area);
        currencyTextView = (TextView) this.findViewById(R.id.country_view_currency);
        languageTextView = (TextView) this.findViewById(R.id.country_view_language);

        initializeLabelsTextFields();
    }

    private void initializeLabelsTextFields() {
        populationLabelTextView = (TextView) this.findViewById(R.id.country_view_population_label);
        areaLabelTextView = (TextView) this.findViewById(R.id.country_view_area_label);
        currencyLabelTextView = (TextView) this.findViewById(R.id.country_view_currency_label);
        languageLabelTextView = (TextView) this.findViewById(R.id.country_view_language_label);

        populationLabelTextView.setText(R.string.country_view_population_label);
        areaLabelTextView.setText(R.string.country_view_area_label);
        currencyLabelTextView.setText(R.string.country_view_currency_label);
        languageLabelTextView.setText(R.string.country_view_language_label);
    }

    public void setCountryData(Country country) {
        //name
        countryNameTextView.setText(ResHelper.getLocalizedCountryName(country, getContext()));
        //population
        populationTextView.setText(String.format(getResources()
                .getConfiguration().locale, "%d", country.getPopulation()));
        //area
        areaTextView.setText(getLocalizedArea(country));
        //currency
        currencyLabelTextView.setText(getResources()
                .getQuantityText(R.plurals.country_view_currency_label,
                        country.getCurrencies().size()));
        currencyTextView.setText(getCurrenciesNames(country));
        //language
        languageLabelTextView.setText(getResources()
                .getQuantityText(R.plurals.country_view_language_label,
                        country.getLanguages().size()));
        languageTextView.setText(getLanguageNames(country));
        //flag
        setFlagImage(country);
    }

    private String getCurrenciesNames(Country country) {
        List<CurrencyCode> currencyCodes = country.getCurrencies();
        StringBuilder sb = new StringBuilder();
        int i = 0;
        for (CurrencyCode currencyCode : currencyCodes) {
            sb.append(getResources().getString(ResHelper.getResId(
                    currencyCode.toString().toLowerCase() +
                            PropUtils.get("resources.country.string.currency.suffix"),
                    R.string.class)));
            i++;
            if (i < currencyCodes.size()) {
                sb.append(PropUtils.get("resources.country.string.currency.separator"));
            }
        }
        return sb.toString();
    }

    private String getLanguageNames(Country country) {
        List<LanguageCode> languageCodes = country.getLanguages();
        StringBuilder sb = new StringBuilder();
        int i = 0;
        for (LanguageCode languageCode : languageCodes) {
            logger.info("Appending {} to languages from {}", languageCode.toString(), languageCodes.toString());
            sb.append(getResources()
                    .getString(ResHelper
                            .getResId(
                                    languageCode
                                            .getAlpha3()
                                            .toString()
                                            .toLowerCase() +
                                            PropUtils.get("resources.country.string.language.suffix"),
                                    R.string.class)));
            i++;
            if (i < languageCodes.size()) {
                sb.append(PropUtils.get("resources.country.string.language.separator"));
            }
        }
        return sb.toString();
    }

    private Spannable getLocalizedArea(Country country) {
        String number = String.format(getResources().getConfiguration().locale,
                "%d",
                country.getArea());
        String unit = getResources().getString(R.string.country_view_area_km2);
        int unitLength = unit.length();
        int numberLength = number.length();
        int start = numberLength + 1 + unitLength - 1;
        int end = numberLength + 1 + unitLength;
        SpannableStringBuilder sb = new SpannableStringBuilder(number + " " + unit);
        sb.setSpan(new SuperscriptSpan(), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        sb.setSpan(new RelativeSizeSpan(0.75f), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return sb;
    }

    private void setFlagImage(Country country) {
        flagImageView.setVisibility(INVISIBLE);
        flagProgressBar.setVisibility(VISIBLE);
        Picture flag = flagCache.get(country);
        if (flag == null) {
            new LoadFlagTask(getContext()).execute(country);
        } else {
            flagImageView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
            flagProgressBar.setVisibility(INVISIBLE);
            flagImageView.setVisibility(VISIBLE);
            flagImageView.setImageDrawable(new PictureDrawable(flag));
        }
    }

    public void openCountryPreview(Country country) {
        openPreview(true);
        setCountryData(country);
    }


    /**
     * Load the flag from the svg source image
     */
    private class LoadFlagTask extends AsyncTask<Country, Integer, Picture> {
        private Context context;

        public LoadFlagTask(Context context) {
            this.context = context;
        }

        protected Picture doInBackground(Country... country) {
            String path = PropUtils.get("resources.games.flags.path") +
                    country[0].getIso2().toLowerCase() +
                    "." +
                    PropUtils.get("resources.games.flags.file.suffix");
            logger.debug("Parsing flag image for {} from '{}'.", country[0].getName(), path);
            try {
                SVG svg = SVG.getFromAsset(context.getAssets(), path);
                Picture picture = svg.renderToPicture();
                flagCache.put(country[0], picture);
                return picture;
            } catch (SVGParseException | IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(Picture picture) {
            flagImageView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
            flagProgressBar.setVisibility(INVISIBLE);
            flagImageView.setVisibility(VISIBLE);
            flagImageView.setImageDrawable(new PictureDrawable(picture));
        }
    }

}
