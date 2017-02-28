package cz.honzakasik.geography.learning.countryinfotabs;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.RelativeSizeSpan;
import android.text.style.SuperscriptSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.neovisionaries.i18n.CurrencyCode;
import com.neovisionaries.i18n.LanguageCode;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.List;

import cz.honzakasik.geography.R;
import cz.honzakasik.geography.common.location.country.Country;
import cz.honzakasik.geography.common.location.countryinfo.CountryDataAdapter;
import cz.honzakasik.geography.common.location.countryinfo.LabelValuePair;
import cz.honzakasik.geography.common.utils.PropUtils;
import cz.honzakasik.geography.common.utils.ResHelper;
import cz.honzakasik.geography.learning.location.CountryInfoActivity;

public class BasicCountryInfoFragment extends Fragment {

    private Logger logger = LoggerFactory.getLogger(BasicCountryInfoFragment.class);

    private RecyclerView recyclerView;

    public BasicCountryInfoFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance) {
        return inflater.inflate(R.layout.fragment_ci_basic_informations, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        recyclerView = (RecyclerView) getView().findViewById(R.id.country_info_recycler_view);
        setCountryData(((CountryInfoActivity)getActivity()).getCountry());
    }

    private List<LabelValuePair> getDataSet(Country country) {
        List<LabelValuePair> data = new LinkedList<>();
        data.add(new LabelValuePair<>(
                getResources().getString(R.string.country_view_capital_city_label),
                getLocalizedCountryCapital(country)
        ));
        data.add(new LabelValuePair<>(
                getResources().getString(R.string.country_view_population_label),
                String.format(getResources().getConfiguration().locale, "%d", country.getPopulation()))
        );
        data.add(new LabelValuePair<>(
                getResources().getString(R.string.country_view_area_label),
                getLocalizedArea(country)
        ));
        data.add(new LabelValuePair<>(
                getResources().getQuantityText(R.plurals.country_view_currency_label,
                                country.getCurrencies().size()),
                getCurrenciesNames(country)
        ));
        data.add(new LabelValuePair<>(
                getResources().getQuantityText(R.plurals.country_view_language_label,
                                country.getLanguages().size()),
                getLanguageNames(country)
        ));
        return data;
    }

    public void setCountryData(Country country) {
        String countryName = ResHelper.getLocalizedCountryName(country, getContext());
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(countryName);

        RecyclerView.Adapter adapter = new CountryDataAdapter(getDataSet(country), this.getContext());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
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

    private String getLocalizedCountryCapital(Country country) {
        return getResources().getString(
                ResHelper.getResId(country.getIso2().toLowerCase() + "_capital", R.string.class));
    }

}
